package io.github.truenine.composeserver

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * String utility class
 *
 * @author TrueNine
 * @since 2022-10-28
 */
interface IString {
  companion object {
    /**
     * Checks if a string is null or contains only whitespace characters
     *
     * @param text The string to check
     * @return true if the string is null, empty, or contains only whitespace characters, false otherwise
     */
    @JvmStatic
    @OptIn(ExperimentalContracts::class)
    fun nonText(text: String?): Boolean {
      contract { returns(true) implies (text == null) }
      return text.isNullOrBlank()
    }

    /**
     * Removes all line breaks, carriage returns, and tab characters from a string
     *
     * @param str The string to process
     * @return The processed single-line string
     */
    @JvmStatic
    fun inLine(str: String): String {
      return if (str.isNotBlank()) {
        str.replace(Regex("[\r\n\t]"), "")
      } else str
    }

    /**
     * Checks if a string contains non-whitespace characters
     *
     * @param text The string to check
     * @return true if the string is not null and contains at least one non-whitespace character, false otherwise
     */
    @JvmStatic
    @OptIn(ExperimentalContracts::class)
    fun hasText(text: String?): Boolean {
      contract { returns(false) implies (text != null) }
      return !text.isNullOrBlank()
    }

    /**
     * Checks if a character sequence contains non-whitespace characters
     *
     * @param str The character sequence to check
     * @return true if the character sequence contains at least one non-whitespace character, false otherwise
     */
    @JvmStatic
    fun containsText(str: CharSequence): Boolean {
      return str.any { !it.isWhitespace() }
    }

    /**
     * Truncates text if its length exceeds the specified maximum and adds ellipsis
     *
     * @param s The string to process
     * @param maxLen The maximum allowed length
     * @return The processed string, truncated and appended with "..." if the original string length exceeds the maximum
     */
    @JvmStatic
    inline fun omit(s: String, maxLen: Int): String {
      if (s.isBlank()) return s
      return if (s.length <= maxLen) s else "${s.substring(0, maxLen)}..."
    }

    const val EMPTY: String = ""
  }
}
