package com.example.model

import org.babyfish.jimmer.sql.EnumItem

enum class Gender {

  @EnumItem(name = "M")
  MALE,

  @EnumItem(name = "F")
  FEMALE
}
