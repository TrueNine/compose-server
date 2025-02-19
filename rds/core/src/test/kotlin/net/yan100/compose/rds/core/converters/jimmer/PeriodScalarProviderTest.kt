package net.yan100.compose.rds.core.converters.jimmer

import java.time.Period
import java.time.format.DateTimeParseException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PeriodScalarProviderTest {

  private val provider = PeriodScalarProvider()

  // region toScalar Tests

  @Test
  fun `toScalar should parse valid period string`() {
    // Arrange
    val validInput = "P1Y2M3D"
    val expectedPeriod = Period.of(1, 2, 3)

    // Act
    val result = provider.toScalar(validInput)

    // Assert
    assertEquals(expectedPeriod, result)
  }

  @Test
  fun `toScalar should throw exception for invalid string`() {
    // Arrange
    val invalidInput = "invalid"

    // Act & Assert
    assertFailsWith<DateTimeParseException> {
      provider.toScalar(invalidInput)
    }
  }

  @Test
  fun `toScalar should handle zero period`() {
    // Arrange
    val zeroPeriodInput = "P0D"

    // Act
    val result = provider.toScalar(zeroPeriodInput)

    // Assert
    assertEquals(Period.ZERO, result)
  }

  @Test
  fun `toScalar should throw exception on empty string`() {
    // Act & Assert
    assertFailsWith<DateTimeParseException> {
      provider.toScalar("")
    }
  }

  // endregion

  // region toSql Tests

  @Test
  fun `toSql should convert period to ISO string`() {
    // Arrange
    val period = Period.of(1, 2, 3)
    val expectedString = "P1Y2M3D"

    // Act
    val result = provider.toSql(period)

    // Assert
    assertEquals(expectedString, result)
  }

  @Test
  fun `toSql should handle zero period`() {
    // Arrange
    val zeroPeriod = Period.ZERO

    // Act
    val result = provider.toSql(zeroPeriod)

    // Assert
    assertEquals("P0D", result)
  }

  // endregion
}
