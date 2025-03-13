package net.yan100.compose.oss.common

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper

object S3PolicyCreator {
  fun builder(): S3BuilderChain {
    return S3BuilderChain()
  }

  fun publicBucket(bucketName: String): S3BuilderChain {
    val p = S3PrincipalArgs()
    p.aws.add("*")
    return S3BuilderChain()
      .addStatement(
        S3StatementBuilder.builder()
          .effect(S3Policies.Effect.ALLOW)
          .principal(p)
          .addAction(S3Policies.Bucket.LIST_MUL_UPLOADS)
          .addAction(S3Policies.Bucket.LIST)
          .addAction(S3Policies.Bucket.GET_LOCATION)
          .addResource(bucketName)
      )
      .addStatement(
        S3StatementBuilder.builder()
          .principal(p)
          .effect(S3Policies.Effect.ALLOW)
          .addAction(S3Policies.Object.GET)
          .addAction(S3Policies.Object.LIST_MUL_UPLOAD_PARTS)
          .addAction(S3Policies.Object.PUT)
          .addAction(S3Policies.Object.ABORT_MUL_UPLOAD)
          .addAction(S3Policies.Object.DEL)
          .addResource("$bucketName/*")
      )
  }

  fun privateBucket(bucketName: String): S3BuilderChain {
    val p = S3PrincipalArgs()
    p.aws.add("*")
    return S3BuilderChain()
      .addStatement(
        S3StatementBuilder.builder()
          .principal(p)
          .effect(S3Policies.Effect.ALLOW)
          .addAction(S3Policies.Bucket.LIST)
          .addAction(S3Policies.Bucket.GET_LOCATION)
          .addResource(bucketName)
      )
      .addStatement(
        S3StatementBuilder.builder()
          .principal(p)
          .effect(S3Policies.Effect.ALLOW)
          .addAction(S3Policies.Object.GET)
          .addResource("$bucketName/*")
      )
  }

  fun publicReadonlyBucket(bucketName: String): S3BuilderChain {
    val p = S3PrincipalArgs()
    p.aws.add("*")
    return S3BuilderChain()
      .addStatement(
        S3StatementBuilder.builder()
          .principal(p)
          .effect(S3Policies.Effect.ALLOW)
          .addAction(S3Policies.Bucket.LIST)
          .addAction(S3Policies.Bucket.GET_LOCATION)
          .addResource(bucketName)
      )
      .addStatement(
        S3StatementBuilder.builder()
          .principal(p)
          .effect(S3Policies.Effect.ALLOW)
          .addAction(S3Policies.Object.GET)
          .addResource("$bucketName/*")
      )
  }

  class S3BuilderChain {
    private val RULE = S3Args()
    private val objectMapper = ObjectMapper()

    init {
      RULE.version = "2012-10-17"
    }

    fun addStatement(builder: S3StatementBuilder): S3BuilderChain {
      RULE.statement.add(builder.statement())
      return this
    }

    fun version(version: String?): S3BuilderChain {
      RULE.version = version
      return this
    }

    fun json(): String {
      try {
        return objectMapper.writeValueAsString(RULE)
      } catch (e: JsonProcessingException) {
        throw RuntimeException(e)
      }
    }
  }

  class S3StatementBuilder private constructor() {
    private val S = S3StatementArgs()

    fun statement(): S3StatementArgs {
      return S
    }

    fun principal(principal: S3PrincipalArgs?): S3StatementBuilder {
      S.principal = principal
      return this
    }

    fun addResource(resource: String): S3StatementBuilder {
      S.resource.add("arn:aws:s3:::$resource")
      return this
    }

    fun addAction(action: String): S3StatementBuilder {
      S.action.add("s3:$action")
      return this
    }

    fun effect(effect: String?): S3StatementBuilder {
      S.effect = effect
      return this
    }

    fun build(): S3StatementBuilder {
      return this
    }

    companion object {
      fun builder(): S3StatementBuilder {
        return S3StatementBuilder()
      }
    }
  }
}
