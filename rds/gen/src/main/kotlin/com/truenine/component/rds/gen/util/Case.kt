package com.truenine.component.rds.gen.util

import com.truenine.component.core.lang.Str

object Case {
  @JvmStatic
  fun firstUpper(dbName: String): String {
    return if (Str.hasText(dbName)) {
      dbName.split("_").map {
        val b = it.toCharArray()
        b[0] = b[0].uppercaseChar()
        String(b)
      }.reduce { a, b ->
        a + b
      }
    } else {
      dbName
    }
  }

  @JvmStatic
  fun firstLover(dbName: String): String {
    val a = firstUpper(dbName).toCharArray()
    val b = a[0].lowercaseChar()
    a[0] = b
    return String(a)
  }
}
