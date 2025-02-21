package net.yan100.compose.rds.core.converters.jimmer

import net.yan100.compose.rds.core.models.IDbTreeMetadata
import org.babyfish.jimmer.jackson.Converter

/**
 * # 线索树元数据 为 null 转换器
 *
 * > 不应由外部传入元数据信息
 */
class NullDbTreeMetadataConverter : Converter<IDbTreeMetadata, IDbTreeMetadata> {
  override fun output(value: IDbTreeMetadata?): IDbTreeMetadata? {
    return null
  }

  override fun input(jsonValue: IDbTreeMetadata?): IDbTreeMetadata? {
    return null
  }
}
