package com.example.entities

import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.jackson.LongToStringConverter
import org.babyfish.jimmer.sql.*

@Entity
interface BookStore {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonConverter(LongToStringConverter::class)
  val id: Long

  @Key
  val name: String

  val website: String?

  @OneToMany(mappedBy = "store")
  val books: List<Book>
}
