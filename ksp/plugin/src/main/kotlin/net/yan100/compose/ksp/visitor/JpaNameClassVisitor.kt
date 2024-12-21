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
import net.yan100.compose.core.RefId
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.core.toSnakeCase
import net.yan100.compose.ksp.toolkit.*
import net.yan100.compose.ksp.toolkit.dsl.fileDsl
import net.yan100.compose.ksp.toolkit.kotlinpoet.Libs
import net.yan100.compose.ksp.toolkit.models.DeclarationContext
import net.yan100.compose.meta.annotations.MetaAutoManagement
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.meta.annotations.MetaName
import net.yan100.compose.meta.annotations.MetaSkipGeneration
import net.yan100.compose.meta.annotations.orm.MetaFormula
import net.yan100.compose.meta.getFirstName
import kotlin.properties.Delegates

private data class JpaProperty @OptIn(KspExperimental::class) constructor(
  val ksPropertyDeclaration: KSPropertyDeclaration,
  val name: String = ksPropertyDeclaration.simpleNameGetShortNameStr,
  val ctx: DeclarationContext<KSClassDeclaration>,
  var title: String? = null,
  var desc: String? = null,
  var nullable: Boolean = ksPropertyDeclaration.type.resolve().isMarkedNullable,
  var requireDelegate: Boolean = !nullable,
  val basicType: Boolean = ksPropertyDeclaration.isBasicType(),
  val shadow: Boolean = ctx.declaration.getAnnotationsByType(MetaDef::class).firstOrNull()?.shadow == true
)

class JpaNameClassVisitor(
  private val listenerSpec: AnnotationSpec?
) : KSTopDownVisitor<DeclarationContext<KSClassDeclaration>, Unit>() {

  private val jvmTransient = AnnotationSpec.builder(Transient::class)

  private lateinit var log: KSPLogger

  override fun defaultHandler(node: KSNode, data: DeclarationContext<KSClassDeclaration>) {
    log = data.log
  }

  @OptIn(KspExperimental::class)
  private fun getColumnName(property: JpaProperty): String {
    return (property.ksPropertyDeclaration.getAnnotationsByType(MetaName::class).getFirstName()
      ?: property.ksPropertyDeclaration.simpleNameGetShortNameStr.toSnakeCase())
  }

  // 需在生成后过滤掉的属性
  private val propertyIgnoreAnnotations = listOf(
    MetaName::class, MetaDef::class, MetaSkipGeneration::class, MetaAutoManagement::class
  )

  @OptIn(KspExperimental::class)
  fun findSuperName(classDeclaration: KSClassDeclaration): String? {
    val ab = classDeclaration.getAllSuperTypes().map { it.declaration }.filter { it.isAnnotationPresent(MetaDef::class) }
    val x = ab.lastOrNull()?.let {
      val anno = it.getAnnotationsByType(MetaName::class).lastOrNull()
      if (anno?.name?.isBlank() == true) anno.name else if (anno?.value?.isNotBlank() == true) anno.value else it.simpleName.asString()
        .replaceFirst("Super", "")
    }
    return x
  }

  /**
   * 生成所有属性
   */
  @OptIn(KspExperimental::class)
  private fun regetProperties(
    ctx: DeclarationContext<KSClassDeclaration>
  ): List<Pair<JpaProperty, PropertySpec>> {
    val allProperties =
      ctx.declaration.getAllProperties().filter { it.isOpen() }.filter { it.isPublic() }.filterNot { it.isAnnotationPresent(MetaSkipGeneration::class) }
        .filterNot { it.simpleName.asString() == "id" }
        .filterNot { it.isAnnotationPresent(MetaFormula::class) }
        .toMutableList()
    return allProperties.map { destProperty ->
      val jpaProperty = JpaProperty(
        ctx = ctx,
        ksPropertyDeclaration = destProperty
      )
      val propertyType = destProperty.type.toTypeName().copy(jpaProperty.nullable)
      jpaProperty to PropertySpec.builder(destProperty.simpleNameAsString, propertyType).addKdoc(destProperty.docString ?: "").addOverrideModifier()
        .addOpeneModifier().also { b ->
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
          initNonNilProperty(b, jpaProperty)
        }.also { b ->
          b.addAnnotations(generateJpaColumnPropertyAnnotations(destProperty, jpaProperty))
          b.addAnnotations(generateJpaPropertyAnnotations(destProperty, jpaProperty))
        }.mutable(true)
    }.map { (a, b) ->
      a to b.build()
    }
  }

  /**
   * 初始化集合类型
   */
  private fun initNonNilProperty(builder: PropertySpec.Builder, jpaProperty: JpaProperty) {
    when (val qName = jpaProperty.ksPropertyDeclaration.type.resolve().declaration.realDeclaration.qualifiedNameAsString) {
      List::class.qualifiedName -> {
        jpaProperty.requireDelegate = false
        builder.initializer("emptyList()")
      }

      Set::class.qualifiedName -> {
        builder.initializer("emptySet()")
        jpaProperty.requireDelegate = false
      }

      Map::class.qualifiedName -> {
        builder.initializer("emptyMap()")
        jpaProperty.requireDelegate = false
      }

      Array::class.qualifiedName -> {
        builder.initializer("emptyArray()")
        jpaProperty.requireDelegate = false
      }

      "kotlin.collections.MutableList",
      java.util.List::class.qualifiedName,
        -> {
        jpaProperty.requireDelegate = false
        builder.initializer("mutableListOf()")
      }

      MutableSet::class.qualifiedName, java.util.Set::class.qualifiedName -> builder.initializer("mutableSetOf()")

      MutableMap::class.qualifiedName, java.util.Map::class.qualifiedName -> {
        jpaProperty.requireDelegate = false
        builder.initializer("mutableMapOf()")
      }

      else -> {
        log.warn("not resolved type: $qName")
        jpaProperty.requireDelegate = true
        builder.delegate("%T.notNull()", Delegates::class)
      }
    }
  }

  /**
   * 生成伴生对象字段
   */
  private fun getConstantProperty(p: JpaProperty): PropertySpec {
    val cn = getColumnName(p)
    return PropertySpec.builder(p.ksPropertyDeclaration.simpleNameGetShortNameStr.toSnakeCase().uppercase(), String::class).addConstModifier()
      .initializer("%S", cn)
      .addAnnotation(jvmTransient.useField().build()).build()
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
      classDeclaration.getAnnotationsByType(MetaName::class).getFirstName() ?: findSuperName(classDeclaration) ?: destClassName.simpleName
    }.toSnakeCase()

    // 定义文件并输出
    fileDsl(classDeclaration.packageName.asString(), destSimpleName) {
      builder.addAnnotation(
        AnnotationSpec.builder(
          Suppress::class
        ).addMember("%S", "Unused").addMember("%S", "RedundantVisibilityModifier").useFile().build()
      )
      classBy(destClassName) {
        opened()
        if (listenerSpec != null) builder.addAnnotation(listenerSpec)
        else builder.addAnnotation(
          AnnotationSpec.builder(Libs.jakarta.persistence.EntityListeners.toClassName())
            .addMember("%T::class", Libs.net.yan100.compose.rds.core.listeners.SnowflakeIdInsertListener.toClassName())
            .addMember("%T::class", Libs.net.yan100.compose.rds.core.listeners.BizCodeInsertListener.toClassName()).build()
        )
        annotateAllBy(generateClassAnnotations(destClassName))
        // 继承父类
        when (classDeclaration.classKind) {
          ClassKind.CLASS -> extendsClassBy(superClassName)
          ClassKind.INTERFACE -> extendsInterfaceBy(superClassName)
          else -> {}
        }
        val fieldAndAnnotations = regetProperties(data)
        if (fieldAndAnnotations.isNotEmpty()) {
          // 不生成自动管理的属性
          val managementProperties = fieldAndAnnotations.filterNot { (k, _) ->
            k.ksPropertyDeclaration.isAnnotationPresent(MetaAutoManagement::class)
              || k.ksPropertyDeclaration.getter?.isAnnotationPresent(MetaAutoManagement::class) == true
              || k.ksPropertyDeclaration.setter?.isAnnotationPresent(
              MetaAutoManagement::class
            ) == true
          }
          val secondaryConstructorParameters = managementProperties.map { (_, it) ->
            ParameterSpec.builder(it.name, it.type.copy(nullable = it.type.isNullable)).also { i ->
              if (it.type.isNullable) i.defaultValue("null")
            }.build()
          }
          // 生成空主构造器和值设置附构造器
          builder.primaryConstructor(FunSpec.constructorBuilder().build())
          builder.addFunction(
            FunSpec.constructorBuilder().callThisConstructor().addParameters(secondaryConstructorParameters)
              .addCode(CodeBlock.builder().also { c ->
                managementProperties.forEach { (_, p) ->
                  c.addStatement("this.${p.name} = ${p.name}")
                }
              }.build()).build()
          )
          // 添加 internal id
          val internalIdName = "____database_internal_${destSimpleName.toSnakeCase()}_field_primary_id"
          val idColumnAnnotation = AnnotationSpec.builder(Libs.jakarta.persistence.Column.toClassName()).addMember("name = %T.ID", IDbNames::class)
          builder.addProperty(
            PropertySpec.builder(internalIdName, RefId::class.asTypeName().copy(nullable = true), KModifier.PRIVATE, KModifier.FINAL).initializer("%L", null)
              .mutable(true).addAnnotation(Libs.jakarta.persistence.Transient.toClassName()).addAnnotation(Libs.kotlin.jvm.Transient.toClassName())
              .addAnnotation(
                AnnotationSpec.builder(Deprecated::class).addMember("%S", "not access internal field").addMember("level = %T.ERROR", DeprecationLevel::class)
                  .build()
              )
              .build()
          )
          builder.addProperty(
            PropertySpec.builder("id", RefId::class).mutable(true).addOverrideModifier().addFinalModifier().getter(
              FunSpec.getterBuilder().addAnnotation(Libs.jakarta.persistence.Id.toAnnotationSpec())
                .addAnnotation(JvmSynthetic::class)
                .addAnnotation(Libs.jakarta.persistence.Transient.toAnnotationSpec())
                .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "DEPRECATION_ERROR").build())
                .addStatement("return if (this.%L === null) error(%S) else this.%L!!", internalIdName, "提前获取 id", internalIdName).build()

            ).setter(
              FunSpec.setterBuilder().addAnnotation(Libs.jakarta.persistence.Transient.toAnnotationSpec())
                .addAnnotation(JvmSynthetic::class)
                .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "DEPRECATION_ERROR").build()).addParameter("v", RefId::class)
                .addStatement("this.%L = v", internalIdName).build()
            ).build()
          )
          builder.addFunction(
            FunSpec.builder("setId").addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "DEPRECATION_ERROR").build())
              .addAnnotation(AnnotationSpec.builder(Deprecated::class).addMember("%S", "").addMember("level = %T.HIDDEN", DeprecationLevel::class).build())
              .addAnnotation(idColumnAnnotation.build())
              .addModifiers(KModifier.OPEN)
              .addParameter(ParameterSpec.builder("jvmIdSetValue", RefId::class.asTypeName().copy(nullable = true)).build())
              .addStatement("this.%L = jvmIdSetValue", internalIdName).build()
          )
          builder.addFunction(
            FunSpec.builder("getId").addAnnotation(
              AnnotationSpec.builder(Deprecated::class).addMember("%S", "").addMember("level = %T.ERROR", DeprecationLevel::class).build()
            ).addModifiers(KModifier.OVERRIDE)
              .addAnnotation(Libs.jakarta.persistence.Id.toClassName())
              .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "DEPRECATION_ERROR").build())
              .addStatement("return if (%L == net.yan100.compose.core.getDefaultNullableId()) null else %L", internalIdName, internalIdName)
              .addAnnotation(idColumnAnnotation.build())
              .addModifiers(KModifier.OPEN)
              .returns(Id::class.asTypeName().copy(nullable = true)).build()
          )
          builder.addFunction(
            FunSpec.builder("isNew")
              .addAnnotation(Libs.jakarta.persistence.Transient.toAnnotationSpec())
              .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "DEPRECATION_ERROR").build())
              .addStatement("return %L == null || %L == net.yan100.compose.core.getDefaultNullableId()", internalIdName, internalIdName)
              .returns(Boolean::class)
              .addModifiers(KModifier.OVERRIDE)
              .build()
          )

          // toString()
          builder.addFunction(
            FunSpec.builder("toString").addModifiers(KModifier.OVERRIDE).returns(String::class)
              .addAnnotation(
                AnnotationSpec.builder(Suppress::class)
                  .addMember("%S", "DEPRECATION_ERROR")
                  .build()
              )
              .addStatement(
                "return \"%T(id=\$%L,%L)\"", destClassName, internalIdName, fieldAndAnnotations.joinToString(separator = ",") { (_, p) ->
                  "${p.name}=\$${p.name}"
                })
              .build()
          ).build()
          // hashCode
          builder.addFunction(
            FunSpec.builder("hashCode").addModifiers(KModifier.OVERRIDE).returns(Int::class).addCode(
              CodeBlock.builder().addStatement("return javaClass.hashCode()").build()
            ).build()
          )
          // equals
          builder.addFunction(
            FunSpec.builder("equals").addModifiers(KModifier.OVERRIDE).addParameter("other", Any::class.asTypeName().copy(nullable = true)).addStatement(
              """
                return if (null == other) false
                else if (this === other) true
                else if (%T.getClass(this) != %T.getClass(other)) false
                else if (!isNew && id == (other as %T).id) true
                else false""".trimIndent(),
              Libs.org.hibernate.Hibernate.toClassName(),
              Libs.org.hibernate.Hibernate.toClassName(),
              Libs.net.yan100.compose.rds.core.entities.IJpaPersistentEntity.toClassName()
            ).returns(Boolean::class).build()
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
    val otherAnnotations = k.annotations.filterNot { a -> propertyIgnoreAnnotations.any { a.isAnnotationByKClass(it) } }
      .filter { a -> a.useSiteTarget == null || a.useSiteTarget == AnnotationUseSiteTarget.FIELD }.map { it.toAnnotationSpec(true) }.toMutableList().let {
        if (pp.requireDelegate) {
          //it.map { a -> a.toBuilder().useDelegate().build() }
          it.map { a -> a.toBuilder().useGet().build() }
        } else it
      }.toMutableList().also { e ->
        k.getter?.annotations?.filterNot { getAnno ->
          getAnno.simpleName == "Column"
        }?.filterNot { getterAnnotation ->
          propertyIgnoreAnnotations.any {
            try {
              log.info("getter annotation: $getterAnnotation")
              getterAnnotation.isAnnotationByKClass(it)
            } catch (e: Exception) {
              log.exception(e)
              false
            }
          }
        }?.map { it.toAnnotationSpec(true).toBuilder().useGet().build() }?.apply(e::addAll)
        k.setter?.annotations?.filterNot { getAnno ->
          getAnno.simpleName == "Column"
        }?.filterNot { a -> propertyIgnoreAnnotations.any { a.isAnnotationByKClass(it) } }?.map { it.toAnnotationSpec(true).toBuilder().useGet().build() }
          ?.apply(e::addAll)
      }

    if (pp.requireDelegate) {
      otherAnnotations += jvmTransient.useDelegate().build()
      otherAnnotations += Libs.jakarta.persistence.Transient.toAnnotationSpecBuilder().useDelegate().build()
    }
    return otherAnnotations.distinctBy { it.useSiteTarget to it.typeName }
  }

  private fun generateJpaColumnPropertyAnnotations(k: KSPropertyDeclaration, pp: JpaProperty): List<AnnotationSpec> {
    val notGenColumn = listOf(
      Libs.jakarta.persistence.JoinColumn.qualifiedName,
      Libs.jakarta.persistence.JoinTable.qualifiedName,
      Libs.jakarta.persistence.ManyToOne.qualifiedName,
      Libs.jakarta.persistence.OneToMany.qualifiedName,
      Libs.jakarta.persistence.ManyToMany.qualifiedName,
      Libs.jakarta.persistence.OneToOne.qualifiedName,
      Libs.jakarta.persistence.ElementCollection.qualifiedName,
    ).any {
      k.getKsAnnotationsByAnnotationClassQualifiedName(it).firstOrNull() != null || k.getter?.getKsAnnotationsByAnnotationClassQualifiedName(it)
        ?.firstOrNull() != null || k.setter?.getKsAnnotationsByAnnotationClassQualifiedName(it)?.firstOrNull() != null
    }
    if (notGenColumn) return emptyList()

    val meta =
      AnnotationSpec.builder(Libs.jakarta.persistence.Column.toClassName()).addMember("name = ${k.simpleNameGetShortNameStr.toSnakeCase().uppercase()}")
    return meta.run {
      buildList {
        if (!pp.nullable) addMember("nullable = false")
        if (pp.shadow) {
          addMember("insertable = %L", false)
          addMember("updatable = %L", false)
        }
        add(useGet().build())
      }
    }
  }

  private fun generateClassAnnotations(destClassName: ClassName): List<AnnotationSpec> {
    val tableAnnotation = AnnotationSpec.builder(Libs.jakarta.persistence.Table.toClassName()).addMember(
      CodeBlock.builder().add("name = %T.TABLE_NAME", destClassName).build()
    ).build()
    return listOf(
      AnnotationSpec.builder(Libs.jakarta.persistence.Entity.toClassName()).build(),
      AnnotationSpec.builder(Libs.org.hibernate.annotations.DynamicInsert.toClassName()).build(),
      AnnotationSpec.builder(Libs.org.hibernate.annotations.DynamicUpdate.toClassName()).build(),
      tableAnnotation
    )
  }

  private fun generateCompanionObject(tableName: String, ppc: Sequence<PropertySpec>): TypeSpec {
    val tableNameConst =
      PropertySpec.builder("TABLE_NAME", String::class).addConstModifier().addAnnotation(jvmTransient.useField().build()).initializer("%S", tableName).build()
    return TypeSpec.companionObjectBuilder().addProperty(tableNameConst).addProperties(ppc.toSet()).build()
  }
}
