package com.example.enums

import org.babyfish.jimmer.sql.EnumItem

enum class EGender {
  @EnumItem(name = "M")
  MALE,

  @EnumItem(name = "F")
  FEMALE
}
