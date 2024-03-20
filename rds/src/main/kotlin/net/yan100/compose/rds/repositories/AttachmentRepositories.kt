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
package net.yan100.compose.rds.repositories

import net.yan100.compose.rds.entities.attachment.Attachment
import net.yan100.compose.rds.entities.attachment.LinkedAttachment
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface IAttachmentRepo : IRepo<Attachment> {
  @Query(
    """
    select new kotlin.Pair(a.baseUrl,a.saveName)
    from Attachment a
    where a.id = :id
  """
  )
  fun findBaseUrlAndSaveNamePairById(id: Long): Pair<String, String>?

  @Query("""select a.metaName from Attachment a where a.id = :id""")
  fun findMetaNameById(id: String): String?

  @Query("""select a.saveName from Attachment a where a.id = :id""")
  fun findSaveNameById(id: String): String?

  /** 根据id查找附件的全路径 */
  @Query(
    """
    select b.baseUrl||a.metaName
    from Attachment a
    inner join Attachment b ON a.urlId = b.id
    where a.id = :id
"""
  )
  fun findFullPathById(@Param("id") id: String): String?

  @Query(
    """
    select a.baseUrl||a.metaName
    from Attachment a
    where a.metaName LIKE concat(:metaName,'%%') 
    """
  )
  fun findAllFullUrlByMetaNameStartingWith(metaName: String, page: Pageable): Page<String>

  /** ## 根据 baseUrl 查询其下的所有 附件 */
  @Query(
    """
    from Attachment a
    inner join Attachment b ON a.urlId = b.id
    where b.attType = net.yan100.compose.rds.core.typing.AttachmentTyping.BASE_URL
    and b.baseUrl = :baseUrl
  """
  )
  fun findAllByParentBaseUrl(baseUrl: String, page: Pageable): Page<Attachment>

  fun existsByBaseUrl(baseUrl: String): Boolean

  /** 根据id，查询 baseUrl符合条件的 baseUrl */
  fun findFirstByBaseUrl(baseUrl: String): Attachment?

  /** 根据 baseUrl 和 baseUri 查询当前的 base url */
  fun findFirstByBaseUrlAndBaseUri(baseUrl: String, baseUri: String): Attachment?

  /** 根据 baseUrl 查询其下的所有 附件 */
  fun findAllByBaseUrlIn(baseUrls: List<String>): List<Attachment>

  fun findAllByBaseUrlInAndBaseUriIn(
    baseUrls: List<String>,
    baseUris: List<String>
  ): List<Attachment>
}

@Repository
interface ILinkedAttachmentRepo : IRepo<LinkedAttachment> {
  /** ## 根据 baseUrl 查询其下的所有 附件 */
  @Query(
    """
    from LinkedAttachment a
    inner join LinkedAttachment b 
    on a.urlId = b.id
    where b.attType = net.yan100.compose.rds.core.typing.AttachmentTyping.BASE_URL
    and b.baseUrl = :baseUrl
  """
  )
  fun findAllByParentBaseUrl(baseUrl: String, page: Pageable): Page<LinkedAttachment>
}
