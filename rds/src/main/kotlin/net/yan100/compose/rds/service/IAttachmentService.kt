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
package net.yan100.compose.rds.service

import net.yan100.compose.core.alias.Pq
import net.yan100.compose.core.alias.Pr
import net.yan100.compose.rds.core.util.JpaPagedWrapper
import net.yan100.compose.rds.entities.attachment.Attachment
import net.yan100.compose.rds.entities.attachment.LinkedAttachment
import net.yan100.compose.rds.service.base.IService

interface IAttachmentService : IService<Attachment> {
  fun existsByBaseUrl(baseUrl: String): Boolean

  fun fetchOrCreateAttachmentLocationByBaseUrlAndBaseUri(baseUrl: String, baseUri: String): Attachment

  fun findByBaseUrl(baseUrl: String): Attachment?

  fun findByBaseUrlAndBaseUri(baseUrl: String, baseUri: String): Attachment?

  fun findAllByBaseUrlIn(baseUrls: List<String>): List<Attachment>

  fun findAllByBaseUrlInAndBaseUriIn(baseUrls: List<String>, baseUris: List<String>): List<Attachment>

  fun findFullUrlById(id: String): String?

  fun findAllFullUrlByMetaNameStartingWith(metaName: String, page: Pq = JpaPagedWrapper.DEFAULT_MAX): Pr<String>

  fun findMetaNameById(id: String): String?

  fun findSaveNameById(id: String): String?

  /** ## 根据 baseurl 查询其下的所有文件地址 */
  fun findAllLinkedAttachmentByParentBaseUrl(baseUrl: String, page: Pq = Pq.DEFAULT_MAX): Pr<LinkedAttachment>

  /** ## 根据 baseurl 查询其下的所有文件 */
  fun findAllByParentBaseUrl(baseUrl: String, page: Pq = Pq.DEFAULT_MAX): Pr<Attachment>

  /** ## 根据 id 查新附件的url */
  fun findLinkedById(id: String): LinkedAttachment?

  fun findAllLinkedById(ids: List<String>): List<LinkedAttachment>
}
