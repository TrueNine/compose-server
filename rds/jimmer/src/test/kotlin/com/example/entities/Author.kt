package com.example.entities

import com.example.enums.EGender
import net.yan100.compose.rds.jimmer.generators.JimmerSnowflakeLongIdGenerator
import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.jackson.LongToStringConverter
import org.babyfish.jimmer.sql.*

@Entity
interface Author {
  @Id
  @GeneratedValue(generatorRef = JimmerSnowflakeLongIdGenerator.JIMMER_SNOWFLAKE_LONG_ID_GENERATOR_NAME)
  @JsonConverter(LongToStringConverter::class)
  val id: Long

  @Key
  val firstName: String

  @Key
  val lastName: String

  /*
   * 这里，Gender是一个枚举，，代码稍后给出
   */
  val gender: EGender

  @ManyToMany(mappedBy = "authors")
  val books: List<Book>
}
