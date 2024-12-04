package com.example.entities

import net.yan100.compose.rds.jimmer.generators.JimmerSnowflakeLongIdGenerator
import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.jackson.LongToStringConverter
import org.babyfish.jimmer.sql.*

@Entity
interface BookStore {
  @Id
  @GeneratedValue(generatorRef = JimmerSnowflakeLongIdGenerator.JIMMER_SNOWFLAKE_LONG_ID_GENERATOR_NAME)
  @JsonConverter(LongToStringConverter::class)
  val id: Long

  @Key
  val name: String

  val website: String?

  @OneToMany(mappedBy = "store")
  val books: List<Book>
}
