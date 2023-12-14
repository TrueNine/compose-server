package net.yan100.compose.core.map

import net.yan100.compose.core.models.AuthRequestInfo
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.factory.Mappers



@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface RequestInfoMap {
  companion object {
    val INSTANCE = Mappers.getMapper(RequestInfoMap::class.java)
  }
  @Mapping(target = "enabled", ignore = true)
  @Mapping(target = "nonLocked", ignore = true)
  @Mapping(target = "nonExpired", ignore = true)
  @Mapping(target = "encryptedPassword", ignore = true)
  fun clearAuthedInfo(info: AuthRequestInfo): AuthRequestInfo
}
