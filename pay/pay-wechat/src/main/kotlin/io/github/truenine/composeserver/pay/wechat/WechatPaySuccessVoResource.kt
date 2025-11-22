package io.github.truenine.composeserver.pay.wechat

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "Notification data")
data class WechatPaySuccessVoResource(
  @Schema(title = "Encryption algorithm type", description = "Algorithm used to encrypt the result data; currently only AEAD_AES_256_GCM is supported")
  var algorithm: String? = null,
  @Schema(title = "Cipher text") @JsonProperty("ciphertext") var cipherText: String? = null,
  @Schema(title = "Associated data") @JsonProperty("associated_data") var associatedDate: String? = null,
  @Schema(title = "Original type", description = "Original callback type, typically 'transaction'")
  @JsonProperty("original_type")
  var originalType: String? = null,
  @Schema(title = "Nonce", description = "Random string used for encryption") @JsonProperty("nonce") var randomString: String? = null,
)
