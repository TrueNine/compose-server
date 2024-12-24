package net.yan100.compose.client.generator

import net.yan100.compose.client.domain.TypescriptScope
import net.yan100.compose.client.domain.entries.TypescriptFile
import net.yan100.compose.client.domain.entries.TypescriptName
import net.yan100.compose.client.templates.UtilsTemplate
import net.yan100.compose.client.toTypescriptEnum
import net.yan100.compose.meta.client.ClientApiStubs
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind


class TypescriptGenerator(
  private val stubsProvider: () -> ClientApiStubs
) {
  private val stubs get() = stubsProvider().copy()
  private val clientDefinitions get() = stubs.definitions


  internal fun renderEnum(typescriptEnum: TypescriptScope.Enum): TypescriptFile {
    return TypescriptFile.SingleEnum(typescriptEnum)
  }

  internal fun renderEnumsToFiles(enumDefinitions: List<ClientType> = emptyList()): List<TypescriptFile> {
    return enumDefinitions.filter { it.typeKind == TypeKind.ENUM_CLASS }.map { enumClientType ->
      val constants = enumClientType.resolveEnumConstants()
      renderEnum(
        enumClientType.toTypescriptEnum().copy(
          constants = constants
        )
      )
    }
  }

  fun renderExecutor(): TypescriptFile {
    return TypescriptFile.SingleTypeUtils(
      fileName = TypescriptName.Name("Executor"),
      code = UtilsTemplate.renderExecutor(),
      usedNames = listOf("HTTPMethod", "BodyType").map(TypescriptName::Name)
    )
  }
}
