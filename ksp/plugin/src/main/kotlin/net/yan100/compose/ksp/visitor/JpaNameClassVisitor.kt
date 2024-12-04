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
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import net.yan100.compose.core.Id
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.core.toSnakeCase
import net.yan100.compose.ksp.toolkit.*
import net.yan100.compose.ksp.toolkit.dsl.fileDsl
import net.yan100.compose.ksp.toolkit.kotlinpoet.ClassNames
import net.yan100.compose.ksp.toolkit.models.DeclarationContext
import net.yan100.compose.meta.annotations.MetaAutoManagement
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.meta.annotations.MetaName
import net.yan100.compose.meta.annotations.MetaSkipGeneration
import net.yan100.compose.meta.getFirstName
import org.jetbrains.annotations.NotNull
import kotlin.properties.Delegates

private data class JpaProperty(
  var name: String,
  var title: String? = null,
  var desc: String? = null,
  var nullable: Boolean,
  var requireDelegate: Boolean,
  var basicType: Boolean,
  var shadow: Boolean
) {
  init {
    requireDelegate = ((!nullable)) // 满足此条件则必须进行委托
  }
}

class JpaNameClassVisitor(
  private val listenerSpec: AnnotationSpec?
) : KSTopDownVisitor<DeclarationContext<KSClassDeclaration>, Unit>() {

  private val accessAnnotation = AnnotationSpec.builder(ClassNames.Jakarta.Persistence.Access).addMember("jakarta.persistence.AccessType.PROPERTY").build()
  private val jpaTransient = AnnotationSpec.builder(ClassNames.Jakarta.Persistence.Transient)
  private val jvmTransient = AnnotationSpec.builder(Transient::class)

  private lateinit var log: KSPLogger

  override fun defaultHandler(node: KSNode, data: DeclarationContext<KSClassDeclaration>) {
    log = data.log
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

  // 需在生成后过滤掉的属性
  private val propertyIgnoreAnnotations =
    listOf(
      MetaName::class,
      MetaDef::class,
      MetaSkipGeneration::class,
      MetaAutoManagement::class
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
    metaDefIsShadow: Boolean,
    ctx: DeclarationContext<KSClassDeclaration>
  ): List<Pair<KSPropertyDeclaration, PropertySpec>> {
    val allProperties = classDeclaration.getAllProperties()
      .filter { it.isOpen() }
      .toMutableList()

    return allProperties
      .filter { it.isPublic() && !it.type.resolve().isError && !it.isAnnotationPresent(MetaSkipGeneration::class) }
      .map { destProperty ->
        val jpaProperty = JpaProperty(
          name = destProperty.simpleNameGetShortNameStr,
          nullable = !destProperty.isAnnotatedNonNull() && destProperty.type.resolve().isMarkedNullable,
          requireDelegate = destProperty.isDelegated(),
          basicType = destProperty.isBasicType(),
          shadow = metaDefIsShadow
        )

        val propertyType = try {
          destProperty.type.toTypeName().copy(jpaProperty.nullable)
        } catch (e: IllegalArgumentException) {
          throw IllegalArgumentException(
            "type ${destProperty.parentDeclaration?.qualifiedNameAsString} property: ${destProperty.simpleNameAsString} is error type",
            e
          )
        }

        destProperty to PropertySpec.builder(destProperty.simpleNameAsString, propertyType)
          .addKdoc(destProperty.docString ?: "")
          .addOverrideModifier()
          .addOpeneModifier()
          .also { b ->
            val isBasicType = jpaProperty.basicType
            val isDelegate = jpaProperty.requireDelegate
            val isNotNull = !jpaProperty.nullable
            // 非空的委托基本类型
            if (isNotNull && isDelegate && isBasicType) {
              b.delegate("%T.notNull()", Delegates::class)
              return@also
            }
            if (!isNotNull) {
              b.initializer("null")
              return@also
            }

            // 初始化一些常见集合类型
            when (val qName = destProperty.type.resolve().declaration.realDeclaration.qualifiedNameAsString) {
              List::class.qualifiedName -> {
                jpaProperty.requireDelegate = false
                b.initializer("emptyList()")
              }

              Set::class.qualifiedName -> {
                b.initializer("emptySet()")
                jpaProperty.requireDelegate = false
              }

              Map::class.qualifiedName -> {
                b.initializer("emptyMap()")
                jpaProperty.requireDelegate = false
              }

              Array::class.qualifiedName -> {
                b.initializer("emptyArray()")
                jpaProperty.requireDelegate = false
              }

              "kotlin.collections.MutableList",
              java.util.List::class.qualifiedName,
                -> {
                jpaProperty.requireDelegate = false
                b.initializer("mutableListOf()")
              }

              MutableSet::class.qualifiedName,
              java.util.Set::class.qualifiedName
                -> b.initializer("mutableSetOf()")

              MutableMap::class.qualifiedName,
              java.util.Map::class.qualifiedName
                -> {
                jpaProperty.requireDelegate = false
                b.initializer("mutableMapOf()")
              }

              String::class.qualifiedName,
              java.lang.String::class.qualifiedName -> {
                jpaProperty.requireDelegate = false
                b.delegate("%T.notNull()", Delegates::class)
              }

              else -> {
                log.warn("not resolved type: $qName")
                b.delegate("%T.notNull()", Delegates::class)
              }
            }
          }.also { b ->
            b.addAnnotations(generateJpaColumnPropertyAnnotations(destProperty, jpaProperty))
            b.addAnnotations(generateJpaPropertyAnnotations(destProperty, jpaProperty))
          }
          .mutable(true)
          .build()
      }.sortedBy { it.first.simpleNameAsString }
  }

  /**
   * 生成伴生对象字段
   */
  private fun getConstantProperty(p: KSPropertyDeclaration): PropertySpec {
    val cn = getColumnName(p)
    return PropertySpec.builder(p.simpleNameGetShortNameStr.toSnakeCase().uppercase(), String::class)
      .addConstModifier().initializer("%S", cn)
      .addAnnotation(jvmTransient.useField().build())
      .build()
  }

  /**
   * # jpa 入口
   */
  @OptIn(KspExperimental::class)
  override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: DeclarationContext<KSClassDeclaration>) {
    log = data.log
    val classSimpleName = classDeclaration.simpleNameGetShortNameStr
    val superClassName = ClassName(classDeclaration.packageNameAsString, classDeclaration.simpleNameAsString)
    val destSimpleName = classSimpleName.replaceFirst("Super", "")
    val destClassName = ClassName(classDeclaration.packageNameAsString, destSimpleName)
    val tableName = run {
      classDeclaration.getAnnotationsByType(MetaName::class).getFirstName()
        ?: findSuperName(classDeclaration)
        ?: destClassName.simpleName
    }.toSnakeCase()
    val metaDefIsShadow = classDeclaration.getAnnotationsByType(MetaDef::class).firstOrNull()?.shadow ?: false

    // 定义文件并输出
    fileDsl(classDeclaration.packageName.asString(), destSimpleName) {
      builder.addAnnotation(
        AnnotationSpec.builder(
          Suppress::class
        ).addMember("%S", "Unused").addMember("%S", "RedundantVisibilityModifier").useFile().build()
      )
      classBy(destClassName) {
        opened()
        if (listenerSpec != null) {
          builder.addAnnotation(
            listenerSpec
          )
        } else {
          builder.addAnnotation(
            AnnotationSpec.builder(
              ClassNames.Jakarta.Persistence
                .EntityListeners
            )
              .addMember("%T::class", ClassNames.Net.Yan100.Compose.Rds.Core.Listeners.SnowflakeIdInsertListener)
              .addMember("%T::class", ClassNames.Net.Yan100.Compose.Rds.Core.Listeners.BizCodeInsertListener)
              .build()
          )
        }

        annotateBy(accessAnnotation)
        annotateAllBy(generateClassAnnotations(destClassName, metaDefIsShadow))
        // 继承父类
        when (classDeclaration.classKind) {
          ClassKind.CLASS -> extendsClassBy(superClassName)
          ClassKind.INTERFACE -> extendsInterfaceBy(superClassName)
          else -> {}
        }


        val fieldAndAnnotations = regetProperties(classDeclaration, destSimpleName, metaDefIsShadow, data)
        if (fieldAndAnnotations.isNotEmpty()) {
          // 不生成自动管理的属性
          val managementProperties = fieldAndAnnotations
            .filterNot { (k, _) ->
              k.isAnnotationPresent(MetaAutoManagement::class)
                || k.getter?.isAnnotationPresent(MetaAutoManagement::class) == true
                || k.setter?.isAnnotationPresent(MetaAutoManagement::class) == true
            }
          val secondaryConstructorParameters = managementProperties
            .map { (_, it) ->
              ParameterSpec.builder(it.name, it.type.copy(nullable = it.type.isNullable))
                .also { i ->
                  if (it.type.isNullable) i.defaultValue("null")
                }
                .build()
            }
          // 生成空主构造器和值设置附构造器
          builder.primaryConstructor(FunSpec.constructorBuilder().build())
          builder.addFunction(
            FunSpec.constructorBuilder()
              .callThisConstructor()
              .addParameters(secondaryConstructorParameters)
              .addCode(
                CodeBlock.builder()
                  .also { c ->
                    managementProperties
                      .forEach { (_, p) ->
                        c.addStatement("this.${p.name} = ${p.name}")
                      }
                  }
                  .build())
              .build()
          )

          // 添加 internal id
          val internalIdName = "____database_internal_${destSimpleName.toSnakeCase()}_field_primary_id"
          val idColumnAnnotation = AnnotationSpec.builder(ClassNames.Jakarta.Persistence.Column)
            .addMember("name = %T.ID", IDbNames::class)
          builder.addProperty(
            PropertySpec.builder(internalIdName, Id::class.asTypeName().copy(nullable = true), KModifier.PRIVATE, KModifier.FINAL)
              .initializer("%L", null)
              .mutable(true)
              .addAnnotation(ClassNames.Jakarta.Persistence.Transient)
              .addAnnotation(ClassNames.Jakarta.Persistence.Id)
              .addAnnotation(ClassNames.Kotlin.Jvm.Transient)
              .addAnnotation(
                AnnotationSpec.builder(Deprecated::class)
                  .addMember("%S", "not access internal field")
                  .addMember("level = %T.ERROR", DeprecationLevel::class)
                  .build()
              )
              .addAnnotation(idColumnAnnotation.build()).build()
          )
          builder.addFunction(
            FunSpec.builder("setId")
              .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "DEPRECATION_ERROR").build())
              .addParameter(ParameterSpec.builder("id", Id::class).build())
              .addAnnotation(idColumnAnnotation.build())
              .addModifiers(KModifier.OVERRIDE)
              .addStatement("this.%L = id", internalIdName)
              .build()
          )
          builder.addFunction(
            FunSpec.builder("getId")
              .addModifiers(KModifier.OVERRIDE)
              .addAnnotation(ClassNames.Jakarta.Persistence.Id)
              .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "DEPRECATION_ERROR").build())
              .addStatement("return this.%L ?: %S", internalIdName, "")
              .addAnnotation(idColumnAnnotation.build())
              .returns(Id::class)
              .build()
          )

          // toString()
          builder.addFunction(
            FunSpec.builder("toString")
              .addModifiers(KModifier.OVERRIDE)
              .returns(String::class)
              .addCode(
                CodeBlock.builder()
                  .addStatement("return \"%T(id=\$id,%L)\"", destClassName, fieldAndAnnotations.joinToString(separator = ",") { (_, p) ->
                    "${p.name}=\$${p.name}"
                  })
                  .build()
              )
              .build()
          )
          // hashCode
          builder.addFunction(
            FunSpec.builder("hashCode")
              .addModifiers(KModifier.OVERRIDE)
              .returns(Int::class)
              .addCode(
                CodeBlock.builder()
                  .addStatement("return javaClass.hashCode()")
                  .build()
              )
              .build()
          )
          // equals
          builder.addFunction(
            FunSpec.builder("equals")
              .addModifiers(KModifier.OVERRIDE)
              .addParameter("other", Any::class.asTypeName().copy(nullable = true))
              .addStatement(
                """
                return if (null == other) false
                else if (this === other) true
                else if (%T.getClass(this) != %T.getClass(other)) false
                else if (!isNew && id == (other as %T).id) true
                else false""".trimIndent(),
                ClassNames.Org.Hibernate.Hibernate,
                ClassNames.Org.Hibernate.Hibernate,
                ClassNames.Net.Yan100.Compose.Rds.Core.Entities.IJpaPersistentEntity
              )
              .returns(Boolean::class)
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

  private fun generateJpaPropertyAnnotations(k: KSPropertyDeclaration, pp: JpaProperty): List<AnnotationSpec> {
    val otherAnnotations =
      k.annotations
        .filterNot { a -> propertyIgnoreAnnotations.any { a.isAnnotationByKClass(it) } }
        .filter { a -> a.useSiteTarget == null || a.useSiteTarget == AnnotationUseSiteTarget.FIELD }
        .map { it.toAnnotationSpec(true) }
        .toMutableList()
        .let {
          if (pp.requireDelegate) {
            //it.map { a -> a.toBuilder().useDelegate().build() }
            it.map { a -> a.toBuilder().useGet().build() }
          } else it
        }
        .toMutableList()
        .also { e ->
          k.getter?.annotations
            ?.filterNot { getAnno ->
              getAnno.simpleName == "Column"
            }
            ?.filterNot { getterAnnotation ->
              propertyIgnoreAnnotations.any {
                try {
                  log.info("getter annotation: $getterAnnotation")
                  getterAnnotation.isAnnotationByKClass(it)
                } catch (e: Exception) {
                  log.exception(e)
                  false
                }
              }
            }
            ?.map { it.toAnnotationSpec(true).toBuilder().useGet().build() }
            ?.apply(e::addAll)
          k.setter
            ?.annotations
            ?.filterNot { getAnno ->
              getAnno.simpleName == "Column"
            }
            ?.filterNot { a -> propertyIgnoreAnnotations.any { a.isAnnotationByKClass(it) } }
            ?.map { it.toAnnotationSpec(true).toBuilder().useGet().build() }
            ?.apply(e::addAll)
        }

    if (pp.requireDelegate) {
      otherAnnotations += jvmTransient.useDelegate().build()
      otherAnnotations += jpaTransient.useDelegate().build()
    }
    return otherAnnotations.distinctBy { it.useSiteTarget to it.typeName }
  }

  private fun generateJpaColumnPropertyAnnotations(k: KSPropertyDeclaration, pp: JpaProperty): List<AnnotationSpec> {
    val notGenColumn = listOf(
      "jakarta.persistence.JoinColumn",
      "jakarta.persistence.JoinTable",
      "jakarta.persistence.ManyToOne",
      "jakarta.persistence.OneToMany",
      "jakarta.persistence.ManyToMany",
      "jakarta.persistence.OneToOne",
      "jakarta.persistence.ElementCollection"
    ).any {
      k.getKsAnnotationsByAnnotationClassQualifiedName(it).firstOrNull() != null
        || k.getter?.getKsAnnotationsByAnnotationClassQualifiedName(it)?.firstOrNull() != null
        || k.setter?.getKsAnnotationsByAnnotationClassQualifiedName(it)?.firstOrNull() != null
    }
    if (notGenColumn) return emptyList()

    val meta = AnnotationSpec
      .builder(ClassNames.Jakarta.Persistence.Column)
      .addMember("name = ${k.simpleNameGetShortNameStr.toSnakeCase().uppercase()}")
    return meta.run {
      buildList {
        if (!pp.nullable) addMember("nullable = false")
        if (pp.shadow) {
          addMember("insertable = %L", false)
          addMember("updatable = %L", false)
        }
        if (pp.requireDelegate) {
          add(useDelegate().build())
          add(accessAnnotation.toBuilder().useGet().build())
        }
        add(useGet().build())
      }
    }
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
