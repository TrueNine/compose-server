package net.yan100.compose.client

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.client.ts.TsReturnTypeController
import net.yan100.compose.meta.client.ClientApi
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind
import net.yan100.compose.testtookit.assertNotEmpty
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import kotlin.test.*
import jakarta.annotation.Resource as JakartaResource

@SpringBootTest
class StubVerifyTest {
  lateinit var mapper: ObjectMapper @JakartaResource set

  @Test
  fun `ensure super types use input generic`() {
    val api = getClientApi()
    val targetName1 = TsReturnTypeController.ExtendsType::class.qualifiedName
    assertFailsWith<ClassNotFoundException> { Class.forName(targetName1) }
    val targetName = TsReturnTypeController.ExtendsType::class.java.name
    log.info("targetClassName: {}", targetName)
    val extendsType = api.definitions.find { it.typeName == targetName }
    assertNotNull(extendsType)
    assertEquals(1, extendsType.argumentLocations.size)
    assertEquals(3, extendsType.superTypes.size, "所有父类，只建议忽略无泛型的泛型")

    log.info("extendsType: {}", extendsType)
  }

  @Test
  fun `test get all enums`() {
    val api = getClientApi()
    val allEnums = api.definitions.filter { it.typeKind == TypeKind.ENUM_CLASS }
    assertNotEquals(0, allEnums.size)
    allEnums.forEach {
      val enumClass = Class.forName(it.typeName)
      assertNotNull(enumClass)
      assertNotNull(it.enumConstants)
      val enumConstants = enumClass.enumConstants
      assertEquals(enumConstants.size, it.enumConstants.size)

      // 比较枚举值是否一致
      it.enumConstants.forEach { (name, ordinal) ->
        val enumConstant = enumClass.getEnumConstants().find {
          name == (it as Enum<*>).name && ordinal == it.ordinal
        }
        assertNotNull(enumConstant)
      }
    }
  }

  @Test
  fun `ensure all define super types`() {
    val api = getClientApi()
    assertTrue {
      api.definitions.any {
        it.superTypes.isNotEmpty()
      }
    }
    fun a(defs: List<ClientType>) {
      defs.map { it.superTypes }.flatten().forEach { d ->
        assertNull(d.typeKind)
        assertEquals(0, d.properties.size)
        a(d.superTypes)
      }
    }
    a(api.definitions)
  }

  @Test
  fun `test get all definitions classes`() {
    val api = getClientApi()
    val names = api.definitions.filterNot { it.isAlias == true }.map { it.typeName }
    names.forEach {
      val javaClass = Class.forName(it)
      assertNotNull(javaClass)
    }
  }

  @Test
  fun `ensure definitions not repeat`() {
    val api = getClientApi()

    val dSize1 = api.definitions.size
    val dSize2 = api.definitions.toSet().size
    assertTrue { dSize1 == dSize2 }

    api.definitions.forEach {
      assertNotNull(it.typeKind)
      assertNotNull(it.typeName)
    }
  }

  @Test
  fun `verify jimmer entity to immutable`() {
    val api = getClientApi()
    val jimmerEntity = api.definitions.find { it.typeName.contains("JimmerEntity") }
    assertNotNull(jimmerEntity)
    assertEquals(TypeKind.IMMUTABLE, jimmerEntity.typeKind)
  }

  @Test
  fun `ensure typealias type`() {
    val api = getClientApi()
    val aliases = api.definitions.filter { it.isAlias == true }
    assertNotEquals(0, aliases.size)
    aliases.forEach {
      assertTrue { it.isAlias == true }
      assertEquals(TypeKind.TYPEALIAS, it.typeKind)
      assertNotNull(it.aliasForTypeName)
    }
  }

  @Test
  fun `ensure generated embeddable`() {
    val defs = getClientApi().definitions
    val embs = defs.filter { it.typeKind == TypeKind.EMBEDDABLE }
    assertNotEquals(0, embs.size)
  }

  @Test
  fun `verify stub client converted to client api`() {
    val api = getClientApi()
    assertNotEmpty { api.services }
    assertNotEmpty { api.definitions }
  }

  @Test
  fun `test parse to client api`() {
    val file = getStubFile()
    val api = mapper.readValue(file.inputStream, ClientApi::class.java)
    assertNotNull(api)
  }


  @Test
  fun `ensure stub file is generated`() {
    val resolver = PathMatchingResourcePatternResolver()
    val resources = resolver.getResources("classpath:META-INF/compose-client/*-client-ts.stub.json")
    assertNotNull(resources)
    assertTrue { resources.isNotEmpty() }
    assertEquals(1, resources.size)
  }

  private fun getStubFile(): Resource {
    val resolver = PathMatchingResourcePatternResolver()
    val resources = resolver.getResources("classpath:META-INF/compose-client/*-client-ts.stub.json")
    assertNotNull(resources)
    assertNotNull(resources[0])
    return resources[0]
  }

  private fun getClientApi(): ClientApi {
    val file = getStubFile()
    val api = mapper.readValue(file.inputStream, ClientApi::class.java)
    assertNotNull(api)
    return api
  }
}