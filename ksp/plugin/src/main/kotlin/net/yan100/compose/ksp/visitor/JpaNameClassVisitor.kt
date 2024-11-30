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

import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import net.yan100.compose.core.toSnakeCase
import net.yan100.compose.ksp.toolkit.*
import net.yan100.compose.ksp.toolkit.dsl.fileDsl
import net.yan100.compose.ksp.toolkit.kotlinpoet.ClassNames
import net.yan100.compose.ksp.toolkit.models.DeclarationContext
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.meta.annotations.MetaName
import net.yan100.compose.meta.getFirstName
import org.jetbrains.annotations.NotNull


class JpaNameClassVisitor : KSTopDownVisitor<DeclarationContext<KSClassDeclaration>, Unit>() {
  private val accessAnnotation = AnnotationSpec.builder(ClassNames.Jakarta.Persistence.Access).addMember("jakarta.persistence.AccessType.PROPERTY").build()
  private val jpaTransient = AnnotationSpec.builder(ClassNames.Jakarta.Persistence.Transient)
  private val jvmTransient = AnnotationSpec.builder(Transient::class)

  private lateinit var log: KSPLogger

  override fun defaultHandler(node: KSNode, data: DeclarationContext<KSClassDeclaration>) {
    log = data.log
  }

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
      requireDelegate = ((basicType && !nullable) || !nullable) // 满足此条件则必须进行委托
      if (title.isNullOrBlank()) {
        title = desc?.lines()?.firstOrNull()?.trim()?.replace("#", "")?.trim()?.also { desc = desc?.lines()?.drop(1)?.joinToString("\n")?.trim() }
      }
    }
  }

  @OptIn(KspExperimental::class)
  private fun getColumnName(property: KSPropertyDeclaration): String {
    return (property.getAnnotationsByType(MetaName::class).getFirstName() ?: property.simpleNameGetShortNameStr.toSnakeCase())
  }

  @OptIn(KspExperimental::class)
  private fun KSPropertyDeclaration.isAnnotatedNonNull(): Boolean {
    val isJbrNonNull = isAnnotationPresent(NotNull::class)
    return isJbrNonNull
  }

  private val propertyIgnoreAnnotations =
    listOf(
      MetaName::class,
    )

  @OptIn(KspExperimental::class)
  fun findSuperName(classDeclaration: KSClassDeclaration): String? {
    val ab = classDeclaration.getAllSuperTypes().map { it.declaration }.filter { it.isAnnotationPresent(MetaDef::class) }
    val x =
      ab.lastOrNull()?.let {
        val anno = it.getAnnotationsByType(MetaName::class).lastOrNull()
        if (anno?.name?.isBlank() == true) anno.name else if (anno?.value?.isNotBlank() == true) anno.value else it.simpleName.asString()
          .replaceFirst("Super", "")
      }
    return x
  }

  @OptIn(KspExperimental::class)
  private fun regetProperties(
    classDeclaration: KSClassDeclaration,
    destClassName: String,
    metaDefIsShadow: Boolean
  ): List<Pair<KSPropertyDeclaration, PropertySpec>> {
    val currentAllProperties = classDeclaration.getDeclaredProperties().toMutableList()
    val superTypes =
      classDeclaration.superTypes
        .filter { s -> s.resolve().declaration.getAnnotationsByType(MetaDef::class).iterator().hasNext() }
        .mapNotNull { it.resolve().declaration as? KSClassDeclaration }

    val sp =
      superTypes
        .map { regetProperties(it, destClassName, metaDefIsShadow) }
        .flatten()
        .filter {
          val (k) = it
          currentAllProperties.none { a -> a.simpleName.getShortName() == k.simpleName.getShortName() }
        }

    return currentAllProperties
      .filter { it.isPublic() && it.isAbstract() && it.isMutable }
      .let { it + sp.map { e -> e.first } }
      .map { p ->
        val jpaProperty =
          JpaProperty(
            name = p.simpleNameGetShortNameStr,
            title = null,
            desc = null,
            nullable = !p.isAnnotatedNonNull() && p.type.resolve().isMarkedNullable,
            requireDelegate = p.isDelegated(),
            basicType = p.actualDeclaration.isBasicType(),
            shadow = metaDefIsShadow
          )
        log.info("handle class type: ${p.qualifiedNameAsStringStr} basic: ${p.isBasicType()} actual: ${p.actualDeclaration.qualifiedNameAsStringStr} actualBasicType: ${p.actualDeclaration.isBasicType()}  jpa property: $jpaProperty")

        p to PropertySpec.builder(p.simpleNameAsStringStr, p.type.toTypeName().copy(jpaProperty.nullable))
          .addOverrideModifier()
          .addOpeneModifier()
          //.addFinalModifier()
          .also { b ->
            b.addAnnotations(generatePropertyOtherAnnotations(p, jpaProperty))
            if (!jpaProperty.nullable && !jpaProperty.requireDelegate && !p.actualDeclaration.isBasicType()) {
              if (!jpaProperty.basicType) b.addModifiers(KModifier.LATEINIT)
            } else if (jpaProperty.nullable) {
              b.initializer("null")
            }
            if (jpaProperty.requireDelegate) {
              b.delegate("@%T(%S) late()", Suppress::class, "DEPRECATION_ERROR")
            }
          }
          .mutable(true)
          .build()
      }
  }

  private fun getConstantProperty(p: KSPropertyDeclaration): PropertySpec {
    val cn = getColumnName(p)
    return PropertySpec.builder(p.simpleNameGetShortNameStr.toSnakeCase().uppercase(), String::class)
      .addConstModifier().initializer("%S", cn)
      .addAnnotation(jvmTransient.useField().build())
      .build()
  }

  @OptIn(KspExperimental::class)
  override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: DeclarationContext<KSClassDeclaration>) {
    log = data.log

    val classSimpleName = classDeclaration.simpleName.getShortName()
    val superClassName = ClassName(classDeclaration.packageName.asString(), classDeclaration.simpleName.asString())

    val destName = classSimpleName.replaceFirst("Super", "")
    val destClassName = ClassName(classDeclaration.packageName.asString(), destName)
    val tableName = run {
      classDeclaration.getAnnotationsByType(MetaName::class).getFirstName()
        ?: findSuperName(classDeclaration)
        ?: destClassName.simpleName
    }.toSnakeCase()

    val metaDefIsShadow = classDeclaration.getAnnotationsByType(MetaDef::class).firstOrNull()?.shadow ?: false

    fileDsl(classDeclaration.packageName.asString(), destName) {
      builder.addAnnotation(
        AnnotationSpec.builder(
          Suppress::class
        ).addMember("%S", "Unused").addMember("%S", "RedundantVisibilityModifier").useFile().build()
      )

      classBy(destClassName) {
        opened()
        annotateBy(accessAnnotation)
        if (metaDefIsShadow) {
          annotateBy(AnnotationSpec.builder(ClassNames.Org.Hibernate.Annotations.Immutable).build())
        }

        log.info("generate class: $destClassName")

        annotateAllBy(generateClassAnnotations(destClassName, metaDefIsShadow))
        extendsBy(superClassName)

        val fieldAndAnnotations = regetProperties(classDeclaration, destName, metaDefIsShadow)


        if (fieldAndAnnotations.isNotEmpty()) {
          // 生成一个空构造器
          val secondaryConstructorParameters = fieldAndAnnotations.map { (_, it) ->
            ParameterSpec.builder(it.name, it.type.copy(nullable = it.type.isNullable))
              .also { i ->
                if (it.type.isNullable) i.defaultValue("null")
              }
              .build()
          }
          builder.primaryConstructor(FunSpec.constructorBuilder().build())
          builder.addFunction(
            FunSpec.constructorBuilder()
              .callThisConstructor()
              .addParameters(secondaryConstructorParameters)
              .addCode(
                CodeBlock.builder()
                  .also { c ->
                    fieldAndAnnotations.forEach { (_, p) ->
                      c.addStatement("this.${p.name} = ${p.name}")
                    }
                  }
                  .build())
              .build()
          )

          // 生成一个默认 toString()
          builder.addFunction(
            FunSpec.builder("toString")
              .addModifiers(KModifier.OVERRIDE)
              .returns(String::class)
              .addCode(
                CodeBlock.builder()
                  .addStatement("return \"%T(%L) <\${%L}\"", destClassName, fieldAndAnnotations.joinToString { (_, p) ->
                    "${p.name}=\${${p.name}}"
                  }, "super.toString()")
                  .build()
              )
              .build()
          )
        }

        builder.addProperties(fieldAndAnnotations.map { it.second }.toSet())
        builder.addType(generateCompanionObject(tableName, fieldAndAnnotations.map {
          getConstantProperty(it.first)
        }.asSequence()))
      }
    }.writeTo(data.codeGenerator, data.dependencies)
  }

  private fun generatePropertyOtherAnnotations(k: KSPropertyDeclaration, pp: JpaProperty): List<AnnotationSpec> {
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
          k.getter?.annotations
            ?.filterNot { getterAnnotation ->
              propertyIgnoreAnnotations.any {
                try {
                  log.info("getter annotation: $getterAnnotation")
                  getterAnnotation.isAnnotationBy(it)
                } catch (e: Exception) {
                  log.error("annotation")
                  log.exception(e)
                  false
                }
              }
            }
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
      otherAnnotations += AnnotationSpec.builder(ClassNames.Com.Fasterxml.Jackson.Annotation.JsonIgnore).useDelegate().build()
    }
    otherAnnotations += buildColumnAnnotations(k, pp)
    return otherAnnotations
  }

  private fun buildColumnAnnotations(k: KSPropertyDeclaration, pp: JpaProperty): List<AnnotationSpec> {
    val meta = AnnotationSpec.builder(ClassNames.Jakarta.Persistence.Column)
      .addMember("name = ${k.simpleNameGetShortNameStr.toSnakeCase().uppercase()}")
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
    }
    results += meta.useGet().build()
    return results
  }

  private fun generateClassAnnotations(destClassName: ClassName, metaDefIsShadow: Boolean): List<AnnotationSpec> {
    val tableAnnotation = AnnotationSpec.builder(
      if (metaDefIsShadow) ClassNames.Jakarta.Persistence.Table //jakartaSecondaryTableAnnotationClassName
      else ClassNames.Jakarta.Persistence.Table
    ).addMember(
      CodeBlock.builder().add("name = %T.TABLE_NAME", destClassName).build()
    ).build()
    return listOf(
      AnnotationSpec.builder(ClassNames.Jakarta.Persistence.Entity).build(),
      AnnotationSpec.builder(ClassNames.Org.Hibernate.Annotations.DynamicInsert).build(),
      AnnotationSpec.builder(ClassNames.Org.Hibernate.Annotations.DynamicUpdate).build(),
      tableAnnotation
    )
  }

  private fun generateCompanionObject(tableName: String, ppc: Sequence<PropertySpec>): TypeSpec {
    val tableNameConst = PropertySpec.builder("TABLE_NAME", String::class).addConstModifier()
      .addAnnotation(jvmTransient.useField().build())
      .initializer("%S", tableName).build()
    return TypeSpec.companionObjectBuilder().addProperty(tableNameConst).addProperties(ppc.toSet()).build()
  }
}
