package net.yan100.compose.rds.crud.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.intTyping
import net.yan100.compose.rds.typing.RelationItemTyping
import org.springframework.stereotype.Component

@Component
@Converter
class RelationItemTypingConverter :
  AttributeConverter<RelationItemTyping?, Int?> by intTyping(
    RelationItemTyping::findVal
  )
