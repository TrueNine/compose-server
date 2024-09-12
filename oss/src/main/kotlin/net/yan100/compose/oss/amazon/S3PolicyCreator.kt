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
package net.yan100.compose.oss.amazon

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.oss.model.S3Args
import net.yan100.compose.oss.model.S3Policies
import net.yan100.compose.oss.model.S3PrincipalArgs
import net.yan100.compose.oss.model.S3StatementArgs

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

  fun publicBucketAndReadOnly(bucketName: String): S3BuilderChain {
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
    private val MAPPER = ObjectMapper()

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
        return MAPPER.writeValueAsString(RULE)
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
