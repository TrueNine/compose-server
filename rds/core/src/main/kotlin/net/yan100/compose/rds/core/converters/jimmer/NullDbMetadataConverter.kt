package net.yan100.compose.rds.core.converters.jimmer

import net.yan100.compose.rds.core.models.IDbMetadata
import org.babyfish.jimmer.jackson.Converter

/**
 * # 元数据 为 null 转换器
 *
 * > 不应由外部传入元数据信息
 */
class NullDbMetadataConverter : Converter<IDbMetadata, IDbMetadata> {
  override fun output(value: IDbMetadata?): IDbMetadata? {
    return null
  }

  override fun input(jsonValue: IDbMetadata?): IDbMetadata? {
    return null
  }
}
