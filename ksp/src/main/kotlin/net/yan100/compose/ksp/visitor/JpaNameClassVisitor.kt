/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.ksp.visitor

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import net.yan100.compose.core.camelCaseToSnakeCase
import net.yan100.compose.core.hasText
import net.yan100.compose.core.nonText
import net.yan100.compose.ksp.*
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.ksp.core.annotations.MetaGenerated
import net.yan100.compose.ksp.core.annotations.MetaName
import net.yan100.compose.ksp.core.annotations.MetaNonNull
import net.yan100.compose.ksp.core.getFirstName
import net.yan100.compose.ksp.data.ContextData
import net.yan100.compose.ksp.dsl.fileDsl
import net.yan100.compose.ksp.functions.companionObjectBuilder
import org.hibernate.annotations.Comment
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.jetbrains.annotations.NotNull
import java.time.LocalDate

class JpaNameClassVisitor : KSTopDownVisitor<ContextData, Unit>() {
  val accessAnnotation = AnnotationSpec.builder(Access::class)
    .addMember("jakarta.persistence.AccessType.PROPERTY")
    .build()
  private val jvmTransient = AnnotationSpec.builder(Transient::class)
  private val jpaTransient = AnnotationSpec.builder(jakarta.persistence.Transient::class)

  data class JpaProperty(
    var name: String,
    var title: String?,
    var desc: String?,
    var nullable: Boolean,
    var requireDelegate: Boolean,
    var basicType: Boolean,
    var shadow: Boolean
  ) {
    init {
      requireDelegate = (basicType && !nullable) // 满足此条件则必须进行委托
      if (title.nonText()) {
        title = desc?.lines()?.firstOrNull()?.trim()?.replace("#", "")?.trim()?.also { desc = desc?.lines()?.drop(1)?.joinToString("\n")?.trim() }
      }
    }
  }

  /** ## 当前类是否为 jpa 类 */
  @OptIn(KspExperimental::class)
  private fun KSClassDeclaration.isJpaHandle(prefix: String = "super"): Boolean {
    val lower = this.simpleName.getShortName().lowercase()
    val annotations = getAnnotationsByType(MetaDef::class).toList()
    return lower.startsWith(prefix) && annotations.isNotEmpty()
  }

  @OptIn(KspExperimental::class)
  private fun KSPropertyDeclaration.getColumnName(): String {
    return getAnnotationsByType(Column::class).firstOrNull()?.name
      ?: (getAnnotationsByType(MetaName::class).getFirstName() ?: shName.camelCaseToSnakeCase)
  }

  @OptIn(KspExperimental::class)
  private fun KSPropertyDeclaration.isAnnotatedNonNull(): Boolean {
    val isJpaNonNull = isAnnotationPresent(jakarta.validation.constraints.NotNull::class)
    val isJbrNonNull = isAnnotationPresent(NotNull::class)
    val isValidNonNull = isAnnotationPresent(jakarta.validation.constraints.NotNull::class)
    val isAnnoNonNull = isAnnotationPresent(MetaNonNull::class)
    return isAnnoNonNull || isValidNonNull || isJpaNonNull || isJbrNonNull
  }

  private val propertyIgnoreAnnotations =
    listOf(
      Schema::class,
      Column::class,
      Comment::class,
      NotNull::class,
      MetaName::class,
      MetaNonNull::class,
      Nullable::class,
      org.jetbrains.annotations.Nullable::class,
    )
  private lateinit var log: KSPLogger

  override fun defaultHandler(node: KSNode, data: ContextData) {
    log = data.log
  }

  @OptIn(KspExperimental::class)
  fun findSuperName(classDeclaration: KSClassDeclaration): String? {
    val ab = classDeclaration.getAllSuperTypes().map { it.declaration }.filter { it.isAnnotationPresent(MetaDef::class) }
    val x =
      ab.lastOrNull()?.let {
        val anno = it.getAnnotationsByType(MetaName::class).lastOrNull()
        if (anno?.name.hasText()) anno?.name else if (anno?.value.hasText()) anno?.value else it.simpleName.asString().replace("Super", "").camelCaseToSnakeCase
      }
    return x
  }

  @OptIn(KspExperimental::class)
  private fun reGetAllProperty(classDeclaration: KSClassDeclaration, destClassName: String): List<Pair<KSPropertyDeclaration, PropertySpec>> {
    val currentAllProperties = classDeclaration.getDeclaredProperties().toMutableList()
    val superTypes =
      classDeclaration.superTypes
        .filter { s -> s.resolve().declaration.getAnnotationsByType(MetaDef::class).iterator().hasNext() }
        .mapNotNull { it.resolve().declaration as? KSClassDeclaration }

    val sp =
      superTypes
        .map { reGetAllProperty(it, destClassName) }
        .flatten()
        .filter {
          val (k) = it
          currentAllProperties.none { a -> a.simpleName.getShortName() == k.simpleName.getShortName() }
        }
    val metaDefIsShadow = classDeclaration.getAnnotationsByType(MetaDef::class).firstOrNull()?.shadow ?: false

    return currentAllProperties
      .filter { it.isPublic() && it.isAbstract() && it.isMutable }
      .let { it + sp.map { e -> e.first } }
      .map { p ->
        val jpaProperty =
          JpaProperty(
            name = p.shName,
            title = null,
            desc = p.docString.cleanedDoc(),
            nullable = !p.isAnnotatedNonNull() && p.type.resolve().isMarkedNullable,
            requireDelegate = p.isDelegated(),
            basicType = p.actualType.isBasicType(),
            shadow = metaDefIsShadow
          )
        log.info("type: ${p.qName} basic: ${p.isBasicType()} actual: ${p.actualType.qName} actualBasicType: ${p.actualType.isBasicType()}  jpa property: $jpaProperty")

        p.getKspAnnotationsByType<Schema>().firstOrNull()?.also {
          val sc = it.toAnnotation<Schema>()
          if (sc.title.hasText()) jpaProperty.title = sc.title
          if (sc.description.hasText()) jpaProperty.desc = sc.description
        }

        p to PropertySpec.builder(p.sName, p.type.toTypeName().copy(jpaProperty.nullable))
          .openedModifier()
          .overrideModifier()
          .also { b ->
            b.addAnnotations(generatePropertyAnnotations(p, jpaProperty))
            if (!jpaProperty.nullable && !jpaProperty.requireDelegate && !p.actualType.isBasicType()) {
              if (!jpaProperty.basicType) b.addModifiers(KModifier.LATEINIT)
            } else if (jpaProperty.nullable) {
              b.initializer("null")
            }
            if (jpaProperty.requireDelegate) b.delegate("late()")
          }
          .mutable(true)
          .build()
      }
  }

  private fun getConstantProperty(p: KSPropertyDeclaration): PropertySpec {
    val cn = p.getColumnName()
    return PropertySpec.builder(p.shName.camelCaseToSnakeCase.uppercase(), String::class)
      .constantModifier().initializer("%S", cn)
      .addAnnotation(jvmTransient.useField().build())
      .build()
  }

  @OptIn(KspExperimental::class)
  override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: ContextData) {
    super.visitClassDeclaration(classDeclaration, data)
    val classSimpleName = classDeclaration.simpleName.getShortName()
    val superClassName = ClassName(classDeclaration.packageName.asString(), classDeclaration.simpleName.asString())
    if (classDeclaration.isJpaHandle()) {
      val destName = classSimpleName.replaceFirst("Super", "")
      val destClassName = ClassName(classDeclaration.packageName.asString(), destName)
      val tableName =
        classDeclaration.getAnnotationsByType(MetaName::class).getFirstName()
          ?: findSuperName(classDeclaration)
          ?: destClassName.simpleName.camelCaseToSnakeCase

      fileDsl(classDeclaration.packageName.asString(), destName) {
        classBy(destClassName) {
          annotateBy(accessAnnotation)
          log.info("generate class: $destClassName")
          annotateAllBy(generateClassAnnotations(destClassName))
          extendsBy(superClassName)
          opened()
          val fieldAndAnnotations = reGetAllProperty(classDeclaration, destName)
          builder.addProperties(fieldAndAnnotations.map { it.second }.toSet())
          builder.addType(generateCompanionObject(tableName, fieldAndAnnotations.map {
            getConstantProperty(it.first)
          }.asSequence()))
        }
      }.writeTo(data.codeGenerator, Dependencies.ALL_FILES)
    }
  }

  private fun generatePropertyAnnotations(k: KSPropertyDeclaration, pp: JpaProperty): List<AnnotationSpec> {
    val otherAnnotations =
      k.annotations
        .filterNot { a -> propertyIgnoreAnnotations.any { a.isAnnotationBy(it) } }
        .map { it.toAnnotationSpec() }
        .toMutableList()
        .let {
          val defaults = it.map { a -> a.toBuilder().useGet().build() }
          if (pp.requireDelegate) {
            it.map { a -> a.toBuilder().useDelegate().build() } + defaults
          } else defaults
        }
        .toMutableList()
        .also { e ->
          k.getter
            ?.annotations
            ?.filterNot { a -> propertyIgnoreAnnotations.any { a.isAnnotationBy(it) } }
            ?.map { it.toAnnotationSpec().toBuilder().useGet().build() }
            ?.apply(e::addAll)
          k.setter
            ?.annotations
            ?.filterNot { a -> propertyIgnoreAnnotations.any { a.isAnnotationBy(it) } }
            ?.map { it.toAnnotationSpec().toBuilder().useGet().build() }
            ?.apply(e::addAll)
        }

    if (pp.requireDelegate) {
      otherAnnotations += jvmTransient.useDelegate().build()
      otherAnnotations += jpaTransient.useDelegate().build()
      otherAnnotations += AnnotationSpec.builder(JsonIgnore::class).useDelegate().build()
    }
    otherAnnotations += buildColumnAnnotations(k, pp)
    return otherAnnotations
  }

  private fun buildColumnAnnotations(k: KSPropertyDeclaration, pp: JpaProperty): List<AnnotationSpec> {
    val meta = AnnotationSpec.builder(Column::class).addMember("name = ${k.shName.camelCaseToSnakeCase.uppercase()}")
    if (!pp.nullable) meta.addMember("nullable = false")
    if (pp.shadow) {
      meta.addMember("insertable = false")
      meta.addMember("updatable = false")
    }
    val results = mutableListOf<AnnotationSpec>()
    if (pp.requireDelegate) {
      results += meta.useDelegate().build()
      results += accessAnnotation.toBuilder().useGet().build()
      results += accessAnnotation.toBuilder().useSet().build()
    } //else results += meta.useField().build()
    results += meta.useGet().build()
    return results
  }

  private fun generateClassAnnotations(destClassName: ClassName): List<AnnotationSpec> {
    val tableAnno =
      AnnotationSpec.builder(jakarta.persistence.Table::class).addMember(CodeBlock.builder().add("name = %T.TABLE_NAME", destClassName)
        .build()).build()
    return listOf(
      AnnotationSpec.builder(Entity::class).build(),
      tableAnno,
      AnnotationSpec.builder(DynamicInsert::class).build(),
      AnnotationSpec.builder(DynamicUpdate::class).build(),
      AnnotationSpec.builder(MetaGenerated::class).addMember("value = %S", "Generated by ${LocalDate.now()}").build(),
    )
  }

  private fun generateCompanionObject(tableName: String, ppc: Sequence<PropertySpec>): TypeSpec {
    val tableNameConst = PropertySpec.builder("TABLE_NAME", String::class).constantModifier()
      .addAnnotation(jvmTransient.useField().build())
      .initializer("%S", tableName).build()
    return companionObjectBuilder().addProperty(tableNameConst).addProperties(ppc.toSet()).build()
  }
}
