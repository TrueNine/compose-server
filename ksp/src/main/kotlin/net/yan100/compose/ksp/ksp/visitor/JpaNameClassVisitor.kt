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
package net.yan100.compose.ksp.ksp.visitor

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import java.io.Serial
import java.time.LocalDate
import net.yan100.compose.core.extensionfunctions.camelCaseToSnakeCase
import net.yan100.compose.core.extensionfunctions.hasText
import net.yan100.compose.core.extensionfunctions.nonText
import net.yan100.compose.ksp.ksp.annotations.*
import net.yan100.compose.ksp.ksp.data.ContextData
import net.yan100.compose.ksp.ksp.dsl.fileDsl
import net.yan100.compose.ksp.ksp.extensionfunctions.*
import net.yan100.compose.ksp.ksp.functions.companionObjectBuilder
import net.yan100.compose.rds.core.entities.IEntity
import org.hibernate.annotations.Comment
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.jetbrains.annotations.NotNull

class JpaNameClassVisitor : KSTopDownVisitor<ContextData, Unit>() {

  data class JpaProperty(var name: String, var title: String?, var desc: String?, var nullable: Boolean, var requireDelegate: Boolean, var basicType: Boolean) {
    init {
      if (basicType && !nullable) requireDelegate = true
      if (title.nonText()) {
        title = desc?.lines()?.firstOrNull()?.trim()?.replace("#", "")?.trim()?.also {
          desc = desc?.lines()?.drop(1)?.joinToString("\n")?.trim()
        }
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
      ?: (getAnnotationsByType(MetaName::class).getFirstName() ?: simpleName.getShortName().camelCaseToSnakeCase)
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
      org.jetbrains.annotations.Nullable::class
    )
  private lateinit var log: KSPLogger
  override fun defaultHandler(node: KSNode, data: ContextData) {
    log = data.log
  }

  @OptIn(KspExperimental::class)
  private fun reGetAllProperty(
    classDeclaration: KSClassDeclaration,
    destClassName: String
  ): List<Pair<KSPropertyDeclaration, PropertySpec>> {
    val sp = classDeclaration.superTypes.filter { s ->
      s.resolve().declaration.getAnnotationsByType(MetaDef::class).iterator().hasNext()
    }.mapNotNull {
      it.resolve().declaration as? KSClassDeclaration
    }.map {
      reGetAllProperty(it, destClassName)
    }.flatten()

    return classDeclaration
      .getDeclaredProperties().toMutableList()
      .filter { it.isPublic() && it.isMutable }
      .let { it + sp.map { e -> e.first } }
      .map { p ->
        val jpaProperty = JpaProperty(
          name = p.simpleName.getShortName(),
          title = null,
          desc = p.docString.cleanedDoc(),
          nullable = !p.isAnnotatedNonNull() && p.type.resolve().isMarkedNullable,
          requireDelegate = p.isDelegated(),
          basicType = p.isBasicType()
        )

        p.getKspAnnotationsByType<Schema>().firstOrNull()?.also {
          val sc = it.toAnnotation<Schema>()
          if (sc.title.hasText()) jpaProperty.title = sc.title
          if (sc.description.hasText()) jpaProperty.desc = sc.description
        }

        p to PropertySpec.builder(p.simpleName.asString(), p.type.toTypeName().copy(jpaProperty.nullable))
          .openedModifier()
          .also { b ->
            b.addAnnotations(generatePropertyAnnotations(p, jpaProperty))
            val comment =
              "## ${jpaProperty.title ?: "${destClassName}.${jpaProperty.name}"}${if (jpaProperty.desc.hasText()) "\n${jpaProperty.desc}" else ""}"
            b.addKdoc(comment)

            if (!jpaProperty.nullable && !jpaProperty.requireDelegate) {
              if (!jpaProperty.basicType) {
                b.addModifiers(KModifier.LATEINIT)
              }
            } else if (jpaProperty.nullable) {
              b.initializer("null")
            }

            if ((jpaProperty.basicType || jpaProperty.requireDelegate) && !jpaProperty.nullable) {
              b.delegate("late()")
            }
          }
          .mutable(true)
          .addKdoc(p.getColumnName())
          .build()
      }
  }

  private fun getConstantProperty(p: KSPropertyDeclaration): PropertySpec {
    val cn = p.getColumnName()
    return PropertySpec.builder(p.simpleName.getShortName().camelCaseToSnakeCase.uppercase(), String::class)
      .constantModifier()
      .initializer("%S", cn)
      .build()
  }

  @OptIn(KspExperimental::class)
  override fun visitClassDeclaration(
    classDeclaration: KSClassDeclaration,
    data: ContextData,
  ) {
    super.visitClassDeclaration(classDeclaration, data)
    val classSimpleName = classDeclaration.simpleName.getShortName()
    if (classDeclaration.isJpaHandle()) {
      val destClassName = classSimpleName.replaceFirst("Super", "")
      val className = ClassName(classDeclaration.packageName.asString(), destClassName)
      val tableName = classDeclaration.getAnnotationsByType(MetaName::class).getFirstName() ?: className.simpleName.camelCaseToSnakeCase

      val fieldAndAnnotations = reGetAllProperty(classDeclaration, destClassName)

      fileDsl(classDeclaration.packageName.asString(), destClassName) {
        classType(className) {
          kdoc {
            h1("generated by a")
            p("测试的生成")
          }

          builder.addType(generateCompanionObject(tableName, fieldAndAnnotations.map { getConstantProperty(it.first) }.asSequence()))
          for (sup in classDeclaration.superTypes) {
            val supC = sup.resolve().declaration as KSClassDeclaration
            val k = (supC).classKind
            if (supC.getAnnotationsByType(MetaDef::class).iterator().hasNext()) break

            when (k) {
              ClassKind.CLASS -> extendsBy(sup.toTypeName())
              ClassKind.INTERFACE -> implBy(sup.toTypeName())
              else -> Unit
            }
          }

          val a = builder.build().superclass as? ClassName
          if (null== a) {
            extendsBy(IEntity::class.asTypeName())
          }
          a?.also { c ->
            val cName = "${c.packageName}.${c.simpleName}"
            if (cName == "kotlin.Any") extendsBy(IEntity::class)
            else {
              val a = Class.forName(cName)
              if (!a.isAssignableFrom(IEntity::class.java)) {
                extendsBy(IEntity::class.asTypeName())
              }
            }
          }


          builder.addAnnotations(generateClassAnnotations(className))
          builder.addProperties(fieldAndAnnotations.map { it.second }.toSet())
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
          if (pp.requireDelegate) {
            it.map { a -> a.toBuilder().useDelegate().build() } +
              it.map { a -> a.toBuilder().useGet().build() } +
              it.map { a -> a.toBuilder().useSet().build() }
          } else it
        }
        .toMutableList()

    if (pp.requireDelegate) {
      otherAnnotations += AnnotationSpec.builder(Transient::class).useDelegate().build()
      otherAnnotations += AnnotationSpec.builder(jakarta.persistence.Transient::class).useDelegate().build()
      otherAnnotations += AnnotationSpec.builder(Schema::class).addMember("hidden = true").useDelegate().build()
      otherAnnotations += AnnotationSpec.builder(JsonIgnore::class).useDelegate().build()
    }
    // column
    val column =
      AnnotationSpec.builder(Column::class)
        .useGet()
        .addMember("name = ${k.simpleName.getShortName().camelCaseToSnakeCase.uppercase()}")
        .addMember("table = TABLE_NAME")
        .also { ab -> if (pp.nullable) ab.addMember("nullable = true") }
    otherAnnotations += column.useGet().build()
    otherAnnotations += column.useSet().build()

    if (pp.nullable) {
      otherAnnotations += AnnotationSpec.builder(jakarta.annotation.Nullable::class).useSet().build()
      otherAnnotations += AnnotationSpec.builder(org.jetbrains.annotations.Nullable::class).useSet().build()
    } else {
      otherAnnotations += AnnotationSpec.builder(org.jetbrains.annotations.NotNull::class).useSet().build()
    }

    // schema
    val schema =
      AnnotationSpec.builder(Schema::class).addMember("name = %S", pp.name).addMember("nullable = ${pp.nullable}").also { s ->
        pp.title?.also { s.addMember("title = %S", it) }
        pp.desc?.also { if (it.hasText()) s.addMember("description = %S", it) }
      }
    otherAnnotations += schema.useGet().build()

    if (pp.title.hasText()) otherAnnotations += AnnotationSpec.builder(Comment::class).useGet().addMember("value = %S", pp.title!!).useGet().build()
    return otherAnnotations
  }

  private fun generateClassAnnotations(destClassName: ClassName): List<AnnotationSpec> {
    val tableAnno =
      AnnotationSpec.builder(jakarta.persistence.Table::class).addMember(CodeBlock.builder().add("name = %T.TABLE_NAME", destClassName).build()).build()
    return listOf(
      AnnotationSpec.builder(Entity::class).build(),
      tableAnno,
      AnnotationSpec.builder(DynamicInsert::class).build(),
      AnnotationSpec.builder(DynamicUpdate::class).build(),
      AnnotationSpec.builder(MetaGenerated::class).addMember("value = %S", "Generated by ${LocalDate.now()}").build()
    )
  }

  private fun generateCompanionObject(tableName: String, ppc: Sequence<PropertySpec>): TypeSpec {
    val tableNameConst = PropertySpec.builder("TABLE_NAME", String::class).constantModifier().initializer("\"$tableName\"").build()
    return companionObjectBuilder()
      .addProperty(PropertySpec.builder("serialVersionUID", Long::class).addAnnotation(Serial::class).privateModifier().initializer("0L").build())
      .addProperty(tableNameConst)
      .addProperties(ppc.toSet())
      .build()
  }
}
