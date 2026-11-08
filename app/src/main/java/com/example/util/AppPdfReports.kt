package com.example.util

import android.content.Context
import com.example.data.model.Employee
import com.example.data.model.Expense
import com.example.data.model.Product
import com.example.data.model.ProductStatus
import com.example.data.model.SalaryPayment
import com.example.data.model.Sale
import com.example.data.model.ShopSettings
import com.example.viewmodel.AppTab
import com.example.viewmodel.DashboardMetrics
import com.example.viewmodel.MonthlyReport
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppPdfReports {
    private fun money(value: Double): String = "৳" + String.format(Locale.US, "%,.2f", value)
    private fun date(value: Long): String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(value))

    fun export(
        context: Context, tab: AppTab, settings: ShopSettings,
        products: List<Product>, sales: List<Sale>, expenses: List<Expense>,
        employees: List<Employee>, salaries: List<SalaryPayment>,
        metrics: DashboardMetrics, report: MonthlyReport
    ) {
        val summary = mutableListOf<Pair<String, String>>()
        val rows = mutableListOf<String>()
        val title = when (tab) {
            AppTab.DASHBOARD -> "Dashboard Summary"
            AppTab.STOCK -> "Stock Report"
            AppTab.SALES -> "Sales Report"
            AppTab.PURCHASE -> "Purchase Records"
            AppTab.CUSTOMERS -> "Customer Report"
            AppTab.SUPPLIERS -> "Supplier Report"
            AppTab.TRANSACTIONS -> "Transactions Report"
            AppTab.EXPENSES -> "Cashbook and Expenses"
            AppTab.EMPLOYEES -> "Employee and Salary Report"
            AppTab.REPORTS -> "Business Report"
            AppTab.SETTINGS -> "Shop Settings"
            AppTab.MORE -> "App Information"
        }
        when (tab) {
            AppTab.DASHBOARD -> {
                summary.addAll(listOf(
                    "স্টকে পণ্য" to metrics.inStockCount.toString(),
                    "মোট স্টক ক্রয়মূল্য" to money(metrics.totalStockValue),
                    "স্টকের সম্ভাব্য বিক্রয়মূল্য" to money(metrics.totalTargetValue),
                    "মোট বিক্রয় সংখ্যা" to metrics.totalSalesCount.toString(),
                    "মোট বিক্রয়" to money(metrics.totalRevenue),
                    "মোট পণ্য লাভ (অন্যান্য খরচের আগে)" to money(metrics.totalProfit),
                    "মোট বকেয়া" to money(metrics.totalDue),
                    "আজকের নেট ব্যালেন্স" to money(metrics.todayNetBalance),
                    "কর্মচারী সংখ্যা" to metrics.totalStaffCount.toString()
                ))
                rows.addAll(products.filter { it.status == ProductStatus.IN_STOCK.name }.map { "স্টক: ${it.company} ${it.model} ${it.variant} | IMEI ${it.imei} | ক্রয় ${money(it.purchasePrice)} | বিক্রয় ${money(it.targetSalePrice)}" })
                rows.addAll(sales.map { "বিক্রয়: ${it.invoiceNumber} | ${it.productDetails} | ${it.customerName} | ${money(it.salePrice)} | ${date(it.soldAt)}" })
            }
            AppTab.STOCK -> {
                val stock = products.filter { it.status == ProductStatus.IN_STOCK.name }
                summary.add("মোট স্টক পণ্য" to stock.size.toString())
                summary.add("স্টক ক্রয়মূল্য" to money(stock.sumOf { it.purchasePrice }))
                summary.add("সম্ভাব্য বিক্রয়মূল্য" to money(stock.sumOf { it.targetSalePrice }))
                rows.addAll(stock.map { "${it.company} ${it.model} ${it.variant} | ${it.color} | IMEI ${it.imei} | সাপ্লায়ার ${it.supplier} | ক্রয় ${money(it.purchasePrice)} | বিক্রয় ${money(it.targetSalePrice)}" })
            }
            AppTab.SALES -> {
                summary.add("মোট ইনভয়েস" to sales.size.toString())
                summary.add("মোট বিক্রয়" to money(sales.sumOf { it.salePrice }))
                summary.add("মোট আদায়" to money(sales.sumOf { it.paidAmount }))
                summary.add("মোট বকেয়া" to money(sales.sumOf { it.dueAmount }))
                summary.add("মোট পণ্য লাভ" to money(sales.sumOf { it.profit }))
                rows.addAll(sales.sortedByDescending { it.soldAt }.map { "${it.invoiceNumber} | ${date(it.soldAt)} | ${it.productDetails} | ${it.customerName} (${it.customerPhone}) | বিক্রয় ${money(it.salePrice)} | আদায় ${money(it.paidAmount)} | বকেয়া ${money(it.dueAmount)}" })
            }
            AppTab.PURCHASE -> {
                summary.add("রেকর্ডকৃত পণ্য" to products.size.toString())
                summary.add("রেকর্ডকৃত পণ্যের ক্রয়মূল্য" to money(products.sumOf { it.purchasePrice }))
                rows.addAll(products.sortedByDescending { it.createdAt }.map { "${date(it.createdAt)} | ${it.company} ${it.model} ${it.variant} | ${it.supplier} | ক্রয় ${money(it.purchasePrice)} | অবস্থা ${it.status}" })
            }
            AppTab.CUSTOMERS -> {
                val groups = sales.groupBy { it.customerPhone.ifBlank { it.customerName } }
                summary.add("মোট ক্রেতা (ফোন/নাম অনুযায়ী)" to groups.size.toString())
                rows.addAll(groups.values.map { list -> val first = list.first(); "${first.customerName} | ${first.customerPhone} | ইনভয়েস ${list.size} | মোট ক্রয় ${money(list.sumOf { it.salePrice })} | বকেয়া ${money(list.sumOf { it.dueAmount })}" })
            }
            AppTab.SUPPLIERS -> {
                val groups = products.groupBy { it.supplier.ifBlank { "নাম নেই" } }
                summary.add("মোট সাপ্লায়ার" to groups.size.toString())
                rows.addAll(groups.map { (name, list) -> "$name | পণ্য ${list.size} | রেকর্ডকৃত ক্রয়মূল্য ${money(list.sumOf { it.purchasePrice })}" })
            }
            AppTab.TRANSACTIONS -> {
                summary.add("বিক্রয় এন্ট্রি" to sales.size.toString())
                summary.add("আয়/খরচ এন্ট্রি" to expenses.size.toString())
                rows.addAll(sales.map { "বিক্রয় | ${date(it.soldAt)} | ${it.invoiceNumber} | ${it.customerName} | ${money(it.salePrice)}" })
                rows.addAll(expenses.map { "${it.type} | ${date(it.dateMillis)} | ${it.category} | ${it.note} | ${money(it.amount)} | ${it.paymentMethod}" })
            }
            AppTab.EXPENSES -> {
                val income = expenses.filter { it.type == "INCOME" }.sumOf { it.amount }
                val expense = expenses.filter { it.type == "EXPENSE" }.sumOf { it.amount }
                summary.add("মোট আয়" to money(income))
                summary.add("মোট খরচ" to money(expense))
                summary.add("নেট ব্যালেন্স" to money(income - expense))
                rows.addAll(expenses.sortedByDescending { it.dateMillis }.map { "${date(it.dateMillis)} | ${it.type} | ${it.category} | ${it.note} | ${money(it.amount)} | ${it.paymentMethod}" })
            }
            AppTab.EMPLOYEES -> {
                summary.add("মোট কর্মচারী" to employees.size.toString())
                summary.add("সক্রিয় কর্মচারী" to employees.count { it.isActive }.toString())
                summary.add("রেকর্ডকৃত বেতন পরিশোধ" to money(salaries.sumOf { it.amountPaid }))
                rows.addAll(employees.map { "কর্মচারী: ${it.name} | ${it.designation} | ${it.phone} | মাসিক বেতন ${money(it.monthlySalary)} | ${if (it.isActive) "সক্রিয়" else "নিষ্ক্রিয়"}" })
                rows.addAll(salaries.map { "বেতন: ${it.employeeName} | ${it.monthYear} | ${money(it.amountPaid)} | ${date(it.paymentDateMillis)} | ${it.paymentMethod}" })
            }
            AppTab.REPORTS -> {
                summary.addAll(listOf(
                    "রিপোর্ট সময়কাল" to report.periodName,
                    "বিক্রিত ইউনিট" to report.unitsSold.toString(),
                    "মোট বিক্রয়" to money(report.totalRevenue),
                    "পণ্যের ক্রয়মূল্য" to money(report.totalCost),
                    "পণ্য লাভ" to money(report.totalProfit),
                    "লাভের হার" to String.format(Locale.US, "%.1f%%", report.profitMarginPercent),
                    "আদায়" to money(report.totalPaid),
                    "বকেয়া" to money(report.totalDue),
                    "বর্তমান স্টক ক্রয়মূল্য" to money(metrics.totalStockValue)
                ))
                rows.addAll(report.topSellingModels.map { "${it.first} | বিক্রিত ইউনিট ${it.second}" })
            }
            AppTab.SETTINGS -> {
                summary.addAll(listOf("দোকানের নাম" to settings.shopName, "ট্যাগলাইন" to settings.shopTagline, "ফোন" to settings.shopPhone, "ঠিকানা" to settings.shopAddress, "অ্যাপের ভাষা" to settings.appLanguage))
                rows.addAll(listOf("রিপ্লেসমেন্ট নীতি: ${settings.warrantyReplacement}", "সার্ভিস নীতি: ${settings.warrantyService}", "এক্সচেঞ্জ নীতি: ${settings.warrantyExchangeDeduction}", "মেমো ফুটার: ${settings.memoFooterNote}"))
            }
            AppTab.MORE -> summary.add("দোকান" to settings.shopName)
        }
        PdfReportGenerator.export(context, settings.shopName, title, summary, rows)
    }
}