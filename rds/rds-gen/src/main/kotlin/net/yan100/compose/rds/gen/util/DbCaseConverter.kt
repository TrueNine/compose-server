package net.yan100.compose.rds.gen.util

import net.yan100.compose.core.lang.hasText

object DbCaseConverter {

    @JvmStatic
    fun firstUpper(dbName: String): String {
        return if (dbName.hasText()) {
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
