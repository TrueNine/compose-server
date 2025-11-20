package io.github.truenine.composeserver.ksp.plugin.visitor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.isOpen
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.AnnotationUseSiteTarget
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import io.github.truenine.composeserver.ksp.addConstModifier
import io.github.truenine.composeserver.ksp.addFinalModifier
import io.github.truenine.composeserver.ksp.addOpeneModifier
import io.github.truenine.composeserver.ksp.addOverrideModifier
import io.github.truenine.composeserver.ksp.dsl.fileDsl
import io.github.truenine.composeserver.ksp.getKsAnnotationsByAnnotationClassQualifiedName
import io.github.truenine.composeserver.ksp.isAnnotationByKClass
import io.github.truenine.composeserver.ksp.isBasicType
import io.github.truenine.composeserver.ksp.kotlinpoet.Libs
import io.github.truenine.composeserver.ksp.meta.annotations.MetaAutoManagement
import io.github.truenine.composeserver.ksp.meta.annotations.MetaDef
import io.github.truenine.composeserver.ksp.meta.annotations.MetaName
import io.github.truenine.composeserver.ksp.meta.annotations.MetaSkipGeneration
import io.github.truenine.composeserver.ksp.meta.annotations.orm.MetaFormula
import io.github.truenine.composeserver.ksp.meta.getFirstName
import io.github.truenine.composeserver.ksp.models.DeclarationContext
import io.github.truenine.composeserver.ksp.packageNameAsString
import io.github.truenine.composeserver.ksp.qualifiedNameAsString
import io.github.truenine.composeserver.ksp.realDeclaration
import io.github.truenine.composeserver.ksp.simpleName
import io.github.truenine.composeserver.ksp.simpleNameAsString
import io.github.truenine.composeserver.ksp.simpleNameGetShortNameStr
import io.github.truenine.composeserver.ksp.useDelegate
import io.github.truenine.composeserver.ksp.useField
import io.github.truenine.composeserver.ksp.useFile
import io.github.truenine.composeserver.ksp.useGet
import kotlin.properties.Delegates

private sealed class PropertyProcessingResult {
  data class Success(val annotations: List<AnnotationSpec>) : PropertyProcessingResult()

  data class Skip(val reason: String) : PropertyProcessingResult()
}

private data class JpaProperty
@OptIn(KspExperimental::class)
constructor(
  val ksPropertyDeclaration: KSPropertyDeclaration,
  val name: String = ksPropertyDeclaration.simpleNameGetShortNameStr,
  val ctx: DeclarationContext<KSClassDeclaration>,
  var title: String? = null,
  var desc: String? = null,
  var nullable: Boolean = ksPropertyDeclaration.type.resolve().isMarkedNullable,
  var requireDelegate: Boolean = !nullable,
  val basicType: Boolean = ksPropertyDeclaration.isBasicType(),
  val shadow: Boolean = ctx.declaration.getAnnotationsByType(MetaDef::class).firstOrNull()?.shadow == true,
)

class JpaNameClassVisitor(private val listenerSpec: AnnotationSpec?) : KSTopDownVisitor<DeclarationContext<KSClassDeclaration>, Unit>() {
  fun String.toSnakeCase(): String {
    if (length <= 1 || isBlank()) return lowercase()
    return buildString {
      var prevIsLower = false
      this@toSnakeCase.forEachIndexed { i, c ->
        if (c.isUpperCase()) {
          if (prevIsLower) append('_')
          append(c.lowercaseChar())
          prevIsLower = false
        } else {
          append(c.lowercaseChar())
          prevIsLower = true
        }
      }
    }
  }

  companion object {
    private val PROPERTY_IGNORE_ANNOTATIONS = listOf(MetaName::class, MetaDef::class, MetaSkipGeneration::class, MetaAutoManagement::class)

    private val JPA_RELATION_ANNOTATIONS =
      listOf(
        Libs.jakarta.persistence.JoinColumn.qualifiedName,
        Libs.jakarta.persistence.JoinTable.qualifiedName,
        Libs.jakarta.persistence.ManyToOne.qualifiedName,
        Libs.jakarta.persistence.OneToMany.qualifiedName,
        Libs.jakarta.persistence.ManyToMany.qualifiedName,
        Libs.jakarta.persistence.OneToOne.qualifiedName,
        Libs.jakarta.persistence.ElementCollection.qualifiedName,
      )

    private val COLLECTION_TYPE_INITIALIZERS =
      mapOf(
        List::class.qualifiedName to "emptyList()",
        Set::class.qualifiedName to "emptySet()",
        Map::class.qualifiedName to "emptyMap()",
        Array::class.qualifiedName to "emptyArray()",
        "kotlin.collections.MutableList" to "mutableListOf()",
        java.util.List::class.qualifiedName to "mutableListOf()",
        MutableSet::class.qualifiedName to "mutableSetOf()",
        java.util.Set::class.qualifiedName to "mutableSetOf()",
        MutableMap::class.qualifiedName to "mutableMapOf()",
        java.util.Map::class.qualifiedName to "mutableMapOf()",
      )
  }

  private val jvmTransient = AnnotationSpec.builder(Transient::class)
  private lateinit var log: KSPLogger

  override fun defaultHandler(node: KSNode, data: DeclarationContext<KSClassDeclaration>) {
    log = data.log
  }

  @OptIn(KspExperimental::class)
  private fun getColumnName(property: JpaProperty): String =
    property.ksPropertyDeclaration.getAnnotationsByType(MetaName::class).getFirstName()
      ?: property.ksPropertyDeclaration.simpleNameGetShortNameStr.toSnakeCase()

  @OptIn(KspExperimental::class)
  private fun findSuperName(classDeclaration: KSClassDeclaration): String? =
    classDeclaration
      .getAllSuperTypes()
      .map { it.declaration }
      .filter { it.isAnnotationPresent(MetaDef::class) }
      .lastOrNull()
      ?.let {
        val anno = it.getAnnotationsByType(MetaName::class).lastOrNull()
        when {
          anno?.name?.isBlank() == true -> anno.name
          anno?.value?.isNotBlank() == true -> anno.value
          else -> it.simpleName.asString().replaceFirst("Super", "")
        }
      }

  /** 生成所有属性 */
  @OptIn(KspExperimental::class)
  private fun regetProperties(ctx: DeclarationContext<KSClassDeclaration>): List<Pair<JpaProperty, PropertySpec>> =
    ctx.declaration
      .getAllProperties()
      .filter { it.isOpen() && it.isPublic() }
      .filterNot { it.isAnnotationPresent(MetaSkipGeneration::class) }
      .filterNot { it.simpleName.asString() == "id" }
      .filterNot { it.isAnnotationPresent(MetaFormula::class) }
      .map { destProperty -> createPropertyPair(ctx, destProperty) }
      .toList()

  private fun createPropertyPair(ctx: DeclarationContext<KSClassDeclaration>, destProperty: KSPropertyDeclaration): Pair<JpaProperty, PropertySpec> {
    val jpaProperty = JpaProperty(ctx = ctx, ksPropertyDeclaration = destProperty)
    val propertyType = destProperty.type.toTypeName().copy(jpaProperty.nullable)

    return jpaProperty to
      PropertySpec.builder(destProperty.simpleNameAsString, propertyType)
        .addKdoc(destProperty.docString ?: "")
        .addOverrideModifier()
        .addOpeneModifier()
        .apply {
          initializeProperty(this, jpaProperty)
          addAnnotations(generateJpaColumnPropertyAnnotations(destProperty, jpaProperty))
          addAnnotations(generateJpaPropertyAnnotations(destProperty, jpaProperty))
        }
        .mutable(true)
        .build()
  }

  private fun initializeProperty(builder: PropertySpec.Builder, jpaProperty: JpaProperty) {
    val isBasicType = jpaProperty.basicType
    val isDelegate = jpaProperty.requireDelegate
    val isNotNull = !jpaProperty.nullable

    when {
      isNotNull && isDelegate && isBasicType -> {
        builder.delegate("%T.notNull()", Delegates::class)
      }

      !isNotNull -> {
        builder.initializer("null")
      }

      else -> {
        initNonNilProperty(builder, jpaProperty)
      }
    }
  }

  /** 初始化集合类型 */
  private fun initNonNilProperty(builder: PropertySpec.Builder, jpaProperty: JpaProperty) {
    val qName = jpaProperty.ksPropertyDeclaration.type.resolve().declaration.realDeclaration.qualifiedNameAsString

    COLLECTION_TYPE_INITIALIZERS[qName]?.let { initializer ->
      builder.initializer(initializer)
      jpaProperty.requireDelegate = false
      return
    }

    log.warn("not resolved type: $qName")
    jpaProperty.requireDelegate = true
    builder.delegate("%T.notNull()", Delegates::class)
  }

  /** 生成伴生对象字段 */
  private fun getConstantProperty(p: JpaProperty): PropertySpec {
    val cn = getColumnName(p)
    return PropertySpec.builder(p.ksPropertyDeclaration.simpleNameGetShortNameStr.toSnakeCase().uppercase(), String::class)
      .addConstModifier()
      .initializer("%S", cn)
      .addAnnotation(jvmTransient.useField().build())
      .build()
  }

  /** # jpa 入口 */
  @OptIn(KspExperimental::class)
  override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: DeclarationContext<KSClassDeclaration>) {
    log = data.log
    val classSimpleName = classDeclaration.simpleNameGetShortNameStr
    val superClassName = ClassName(classDeclaration.packageNameAsString, classDeclaration.simpleNameAsString)
    val destSimpleName = classSimpleName.replaceFirst("Super", "")
    val destClassName = ClassName(classDeclaration.packageNameAsString, destSimpleName)
    val tableName =
      run { classDeclaration.getAnnotationsByType(MetaName::class).getFirstName() ?: findSuperName(classDeclaration) ?: destClassName.simpleName }.toSnakeCase()

    // 定义文件并输出
    fileDsl(classDeclaration.packageName.asString(), destSimpleName) {
        builder.addAnnotation(
          AnnotationSpec.builder(Suppress::class).addMember("%S", "Unused").addMember("%S", "RedundantVisibilityModifier").useFile().build()
        )
        classBy(destClassName) {
          opened()
          if (listenerSpec != null) builder.addAnnotation(listenerSpec)
          else
            builder.addAnnotation(
              AnnotationSpec.builder(Libs.jakarta.persistence.EntityListeners.toClassName())
                .addMember("%T::class", Libs.io.github.truenine.composeserver.rds.listeners.SnowflakeIdInsertListener.toClassName())
                .addMember("%T::class", Libs.io.github.truenine.composeserver.rds.listeners.BizCodeInsertListener.toClassName())
                .build()
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
            val managementProperties =
              fieldAndAnnotations.filterNot { (k, _) ->
                k.ksPropertyDeclaration.isAnnotationPresent(MetaAutoManagement::class) ||
                  k.ksPropertyDeclaration.getter?.isAnnotationPresent(MetaAutoManagement::class) == true ||
                  k.ksPropertyDeclaration.setter?.isAnnotationPresent(MetaAutoManagement::class) == true
              }
            val secondaryConstructorParameters =
              managementProperties.map { (_, it) ->
                ParameterSpec.builder(it.name, it.type.copy(nullable = it.type.isNullable)).also { i -> if (it.type.isNullable) i.defaultValue("null") }.build()
              }
            // 生成空主构造器和值设置附构造器
            builder.primaryConstructor(FunSpec.constructorBuilder().build())
            builder.addFunction(
              FunSpec.constructorBuilder()
                .callThisConstructor()
                .addParameters(secondaryConstructorParameters)
                .addCode(CodeBlock.builder().also { c -> managementProperties.forEach { (_, p) -> c.addStatement("this.${p.name} = ${p.name}") } }.build())
                .build()
            )
            // 添加 internal id
            val internalIdName = "____database_internal_${destSimpleName.toSnakeCase()}_field_primary_id"
            val idColumnAnnotation = AnnotationSpec.builder(Libs.jakarta.persistence.Column.toClassName()).addMember("name = %S", "id")
            builder.addProperty(
              PropertySpec.builder(internalIdName, Long::class.asTypeName().copy(nullable = true), KModifier.PRIVATE, KModifier.FINAL)
                .initializer("%L", null)
                .mutable(true)
                .addAnnotation(Libs.jakarta.persistence.Transient.toClassName())
                .addAnnotation(Libs.kotlin.jvm.Transient.toClassName())
                .addAnnotation(
                  AnnotationSpec.builder(Deprecated::class)
                    .addMember("%S", "not access internal field")
                    .addMember("level = %T.ERROR", DeprecationLevel::class)
                    .build()
                )
                .build()
            )
            builder.addProperty(
              PropertySpec.builder("id", Long::class)
                .mutable(true)
                .addOverrideModifier()
                .addFinalModifier()
                .getter(
                  FunSpec.getterBuilder()
                    .addAnnotation(Libs.jakarta.persistence.Id.toAnnotationSpec())
                    .addAnnotation(JvmSynthetic::class)
                    .addAnnotation(Libs.jakarta.persistence.Transient.toAnnotationSpec())
                    .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "DEPRECATION_ERROR").build())
                    .addStatement("return if (this.%L === null) error(%S) else this.%L!!", internalIdName, "提前获取 id", internalIdName)
                    .build()
                )
                .setter(
                  FunSpec.setterBuilder()
                    .addAnnotation(Libs.jakarta.persistence.Transient.toAnnotationSpec())
                    .addAnnotation(JvmSynthetic::class)
                    .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "DEPRECATION_ERROR").build())
                    .addParameter("v", Long::class)
                    .addStatement("this.%L = v", internalIdName)
                    .build()
                )
                .build()
            )
            builder.addFunction(
              FunSpec.builder("setId")
                .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "DEPRECATION_ERROR").build())
                .addAnnotation(AnnotationSpec.builder(Deprecated::class).addMember("%S", "").addMember("level = %T.HIDDEN", DeprecationLevel::class).build())
                .addAnnotation(idColumnAnnotation.build())
                .addModifiers(KModifier.OPEN)
                .addParameter(ParameterSpec.builder("jvmIdSetValue", Long::class.asTypeName().copy(nullable = true)).build())
                .addStatement("this.%L = jvmIdSetValue", internalIdName)
                .build()
            )
            builder.addFunction(
              FunSpec.builder("getId")
                .addAnnotation(AnnotationSpec.builder(Deprecated::class).addMember("%S", "").addMember("level = %T.ERROR", DeprecationLevel::class).build())
                .addModifiers(KModifier.OVERRIDE)
                .addAnnotation(Libs.jakarta.persistence.Id.toClassName())
                .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "DEPRECATION_ERROR").build())
                .addStatement("return if (%L == io.github.truenine.composeserver.getDefaultNullableId()) null else %L", internalIdName, internalIdName)
                .addAnnotation(idColumnAnnotation.build())
                .addModifiers(KModifier.OPEN)
                .returns(Long::class.asTypeName().copy(nullable = true))
                .build()
            )
            builder.addFunction(
              FunSpec.builder("isNew")
                .addAnnotation(Libs.jakarta.persistence.Transient.toAnnotationSpec())
                .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "DEPRECATION_ERROR").build())
                .addStatement("return %L == null || %L == io.github.truenine.composeserver.getDefaultNullableId()", internalIdName, internalIdName)
                .returns(Boolean::class)
                .addModifiers(KModifier.OVERRIDE)
                .build()
            )

            // toString()
            builder
              .addFunction(
                FunSpec.builder("toString")
                  .addModifiers(KModifier.OVERRIDE)
                  .returns(String::class)
                  .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "DEPRECATION_ERROR").build())
                  .addStatement(
                    "return \"%T(id=$%L,%L)\"",
                    destClassName,
                    internalIdName,
                    fieldAndAnnotations.joinToString(separator = ",") { (_, p) -> "${p.name}=$${p.name}" },
                  )
                  .build()
              )
              .build()
            // hashCode
            builder.addFunction(
              FunSpec.builder("hashCode")
                .addModifiers(KModifier.OVERRIDE)
                .returns(Int::class)
                .addCode(CodeBlock.builder().addStatement("return javaClass.hashCode()").build())
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
                  else false
                  """
                    .trimIndent(),
                  Libs.org.hibernate.Hibernate.toClassName(),
                  Libs.org.hibernate.Hibernate.toClassName(),
                  Libs.io.github.truenine.composeserver.rds.entities.IJpaPersistentEntity.toClassName(),
                )
                .returns(Boolean::class)
                .build()
            )
          }

          builder.addProperties(fieldAndAnnotations.map { it.second }.toSet())
          builder.addType(generateCompanionObject(tableName, fieldAndAnnotations.map { getConstantProperty(it.first) }.asSequence()))
        }
      }
      .writeTo(data.codeGenerator, data.dependencies)
  }

  private fun generateJpaPropertyAnnotations(k: KSPropertyDeclaration, pp: JpaProperty): List<AnnotationSpec> {
    val otherAnnotations =
      k.annotations
        .filterNot { a -> PROPERTY_IGNORE_ANNOTATIONS.any { a.isAnnotationByKClass(it) } }
        .filter { a -> a.useSiteTarget == null || a.useSiteTarget == AnnotationUseSiteTarget.FIELD }
        .map { it.toAnnotationSpec(true) }
        .toMutableList()
        .let {
          if (pp.requireDelegate) {
            // it.map { a -> a.toBuilder().useDelegate().build() }
            it.map { a -> a.toBuilder().useGet().build() }
          } else it
        }
        .toMutableList()
        .also { e ->
          k.getter
            ?.annotations
            ?.filterNot { getAnno -> getAnno.simpleName == "Column" }
            ?.filterNot { getterAnnotation ->
              PROPERTY_IGNORE_ANNOTATIONS.any {
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
            ?.filterNot { getAnno -> getAnno.simpleName == "Column" }
            ?.filterNot { a -> PROPERTY_IGNORE_ANNOTATIONS.any { a.isAnnotationByKClass(it) } }
            ?.map { it.toAnnotationSpec(true).toBuilder().useGet().build() }
            ?.apply(e::addAll)
        }

    if (pp.requireDelegate) {
      otherAnnotations += jvmTransient.useDelegate().build()
      otherAnnotations += Libs.jakarta.persistence.Transient.toAnnotationSpecBuilder().useDelegate().build()
    }
    return otherAnnotations.distinctBy { it.useSiteTarget to it.typeName }
  }

  private fun generateJpaColumnPropertyAnnotations(k: KSPropertyDeclaration, pp: JpaProperty): List<AnnotationSpec> {
    val notGenColumn =
      listOf(
          Libs.jakarta.persistence.JoinColumn.qualifiedName,
          Libs.jakarta.persistence.JoinTable.qualifiedName,
          Libs.jakarta.persistence.ManyToOne.qualifiedName,
          Libs.jakarta.persistence.OneToMany.qualifiedName,
          Libs.jakarta.persistence.ManyToMany.qualifiedName,
          Libs.jakarta.persistence.OneToOne.qualifiedName,
          Libs.jakarta.persistence.ElementCollection.qualifiedName,
        )
        .any {
          k.getKsAnnotationsByAnnotationClassQualifiedName(it).firstOrNull() != null ||
            k.getter?.getKsAnnotationsByAnnotationClassQualifiedName(it)?.firstOrNull() != null ||
            k.setter?.getKsAnnotationsByAnnotationClassQualifiedName(it)?.firstOrNull() != null
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
    val tableAnnotation =
      AnnotationSpec.builder(Libs.jakarta.persistence.Table.toClassName())
        .addMember(CodeBlock.builder().add("name = %T.TABLE_NAME", destClassName).build())
        .build()
    return listOf(
      AnnotationSpec.builder(Libs.jakarta.persistence.Entity.toClassName()).build(),
      AnnotationSpec.builder(Libs.org.hibernate.annotations.DynamicInsert.toClassName()).build(),
      AnnotationSpec.builder(Libs.org.hibernate.annotations.DynamicUpdate.toClassName()).build(),
      tableAnnotation,
    )
  }

  private fun generateCompanionObject(tableName: String, ppc: Sequence<PropertySpec>): TypeSpec {
    val tableNameConst =
      PropertySpec.builder("TABLE_NAME", String::class).addConstModifier().addAnnotation(jvmTransient.useField().build()).initializer("%S", tableName).build()
    return TypeSpec.companionObjectBuilder().addProperty(tableNameConst).addProperties(ppc.toSet()).build()
  }
}
