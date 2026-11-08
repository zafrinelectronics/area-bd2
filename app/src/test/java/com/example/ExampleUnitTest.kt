package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testSaleProfitCalculation() {
    val cost = 100000.0
    val sale = 115000.0
    assertEquals(15000.0, sale - cost, 0.001)
  }
}
