package com.example.entities

import net.yan100.compose.rds.jimmer.generators.JimmerSnowflakeLongIdGenerator
import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.jackson.LongToStringConverter
import org.babyfish.jimmer.sql.*
import java.math.BigDecimal

@Entity
interface Book {
  @Id
  @GeneratedValue(generatorRef = JimmerSnowflakeLongIdGenerator.JIMMER_SNOWFLAKE_LONG_ID_GENERATOR_NAME)
  @JsonConverter(LongToStringConverter::class)
  val id: Long

  @Key
  val name: String

  @Key
  val edition: Int

  val price: BigDecimal

  @ManyToOne
  val store: BookStore?

  @ManyToMany
  @JoinTable(
    name = "BOOK_AUTHOR_MAPPING",
    joinColumnName = "BOOK_ID",
    inverseJoinColumnName = "AUTHOR_id"
  )
  val authors: List<Author>
}
