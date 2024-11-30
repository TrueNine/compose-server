package com.example.entities

import com.example.enums.EGender
import org.babyfish.jimmer.sql.*

@Entity
interface Author {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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
