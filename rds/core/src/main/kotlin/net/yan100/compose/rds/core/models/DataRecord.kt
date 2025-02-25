package net.yan100.compose.rds.core.models

import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.RefId

@Deprecated("已提上删除议程")
@Schema(title = "表行对象序列化模型")
data class DataRecord(
  var id: RefId? = null,
  var modelHash: Int? = null,
  var lang: String? = null,
  var namespace: String? = null,
  var entityJson: String? = null,
)
