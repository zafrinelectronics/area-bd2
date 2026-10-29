package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Product
import com.example.data.model.Sale
import com.example.viewmodel.InventoryViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Apple Area BD", appName)
  }

  @Test
  fun `test sale profit calculation`() {
    val sale = Sale(
      productId = 1L,
      invoiceNumber = "AABD-12345",
      productDetails = "Apple iPhone 15 Pro Max",
      imei = "354891098234125",
      customerName = "Mr. Rahim",
      customerPhone = "01700000000",
      customerAddress = "Dhaka",
      costPrice = 138000.0,
      salePrice = 148000.0,
      paidAmount = 140000.0,
      dueAmount = 8000.0,
      paymentMethod = "Cash",
      warrantyInfo = "1 Year Service"
    )

    assertEquals(10000.0, sale.profit, 0.01)
    assertEquals(8000.0, sale.dueAmount, 0.01)
  }

  @Test
  fun `test bdt currency formatting`() {
    val formatted = InventoryViewModel.formatBdt(148000.0)
    assertTrue(formatted.isNotEmpty())
  }
}
