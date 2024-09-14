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
package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.core.intTyping
import net.yan100.compose.rds.core.typing.AttachmentTyping
import net.yan100.compose.rds.core.typing.AuditTyping
import net.yan100.compose.rds.core.typing.RuleTyping
import net.yan100.compose.rds.core.typing.cert.CertContentTyping
import net.yan100.compose.rds.core.typing.cert.CertPointTyping
import net.yan100.compose.rds.core.typing.cert.CertTyping
import net.yan100.compose.rds.core.typing.cert.DisTyping
import net.yan100.compose.rds.core.typing.relation.RelationTyping
import net.yan100.compose.rds.core.typing.shopping.GoodsChangeRecordTyping
import net.yan100.compose.rds.core.typing.userinfo.BloodTyping
import net.yan100.compose.rds.core.typing.userinfo.DegreeTyping
import net.yan100.compose.rds.core.typing.userinfo.GenderTyping
import org.springframework.stereotype.Component

@Component
@Converter
class AttachmentTypingConverter : AttributeConverter<AttachmentTyping?, Int?> by intTyping(AttachmentTyping::get)

@Component
@Converter
class AuditTypingConverter : AttributeConverter<AuditTyping?, Int?> by intTyping(AuditTyping::findVal)

@Component
@Converter
class BloodTypingConverter : AttributeConverter<BloodTyping?, Int?> by intTyping(BloodTyping::findVal)

@Component
@Converter
class CertContentTypingConverter : AttributeConverter<CertContentTyping?, Int?> by intTyping(CertContentTyping::findVal)

@Component
@Converter
class CertPointTypingConverter : AttributeConverter<CertPointTyping?, Int?> by intTyping(CertPointTyping::findVal)

@Component
@Converter
class CertTypingConverter : AttributeConverter<CertTyping?, Int?> by intTyping(CertTyping::findVal)

@Component
@Converter
class DegreeTypingConverter : AttributeConverter<DegreeTyping?, Int?> by intTyping(DegreeTyping::findVal)

@Converter
@Component
class DisTypingConverter : AttributeConverter<DisTyping?, Int?> by intTyping(DisTyping::findVal)

@Component
@Converter
class RuleTypingConverter : AttributeConverter<RuleTyping?, Int?> by intTyping(RuleTyping::findVal)

@Component
@Converter
class RelationTypingConverter : AttributeConverter<RelationTyping?, Int?> by intTyping(RelationTyping::findVal)

@Converter
@Component
class GenderTypingConverter : AttributeConverter<GenderTyping?, Int?> by intTyping(GenderTyping::findVal)

@Component
@Converter
class GoodsChangeRecordTypingConverter : AttributeConverter<GoodsChangeRecordTyping?, Int?> by intTyping(GoodsChangeRecordTyping::findVal)
