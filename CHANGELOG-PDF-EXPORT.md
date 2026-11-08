# PDF export update

- Added a reusable, paginated PDF report generator using Android's native PdfDocument API.
- Added PDF downloads for Dashboard summary, Stock, Sales, Purchase overview, Customer directory, Supplier directory, Cashbook (income/expense), Employee directory, Salary history, Transactions, and Business Reports.
- Exports are generated from the records currently supplied to each screen; the report includes summary totals and row details where available.
- Android 10+ saves into the public Downloads folder using MediaStore. Older Android versions save into the app-specific Downloads folder.
- Existing individual sales invoice PDF export remains available.
- Removed the Delivery Agency menu entry, navigation destination, and local delivery implementation as requested.

Limitations retained from the existing app: Purchase is currently derived from product purchase-price fields, Customers are derived from sales invoices, and Suppliers are derived from product supplier fields. These exports do not create a separate purchase ledger, customer account ledger, or supplier account ledger.

Build and device verification: not run in this environment because Gradle/Android SDK and the Gradle wrapper are unavailable. A GitHub Actions build is still required before installing this update.
