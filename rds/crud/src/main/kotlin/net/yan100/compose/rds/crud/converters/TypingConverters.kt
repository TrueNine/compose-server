package net.yan100.compose.rds.crud.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.intTyping
import net.yan100.compose.rds.typing.*
import org.springframework.stereotype.Component

@Component
@Converter
class AttachmentTypingConverter :
  AttributeConverter<AttachmentTyping?, Int?> by intTyping(
    AttachmentTyping::get
  )

@Component
@Converter
class AuditTypingConverter :
  AttributeConverter<AuditTyping?, Int?> by intTyping(AuditTyping::findVal)

@Component
@Converter
class BloodTypingConverter :
  AttributeConverter<BloodTyping?, Int?> by intTyping(BloodTyping::findVal)

@Component
@Converter
class CertContentTypingConverter :
  AttributeConverter<CertContentTyping?, Int?> by intTyping(
    CertContentTyping::findVal
  )

@Component
@Converter
class CertPointTypingConverter :
  AttributeConverter<CertPointTyping?, Int?> by intTyping(
    CertPointTyping::findVal
  )

@Component
@Converter
class CertTypingConverter :
  AttributeConverter<CertTyping?, Int?> by intTyping(CertTyping::findVal)

@Component
@Converter
class DegreeTypingConverter :
  AttributeConverter<DegreeTyping?, Int?> by intTyping(DegreeTyping::findVal)

@Converter
@Component
class DisTypingConverter :
  AttributeConverter<DisTyping?, Int?> by intTyping(DisTyping::findVal)

@Component
@Converter
class RuleTypingConverter :
  AttributeConverter<RuleTyping?, Int?> by intTyping(RuleTyping::findVal)

@Component
@Converter
class RelationTypingConverter :
  AttributeConverter<RelationTyping?, Int?> by intTyping(
    RelationTyping::findVal
  )

@Converter
@Component
class GenderTypingConverter :
  AttributeConverter<GenderTyping?, Int?> by intTyping(GenderTyping::findVal)

@Component
@Converter
class GoodsChangeRecordTypingConverter :
  AttributeConverter<GoodsChangeRecordTyping?, Int?> by intTyping(
    GoodsChangeRecordTyping::findVal
  )
