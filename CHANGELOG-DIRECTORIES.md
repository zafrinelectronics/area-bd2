# Apple Area Shop – directory and purchase screens

- Purchase screen displays existing product records and their stored supplier, purchase price, and stock status. The add action opens the existing product form; it does not create a separate purchase ledger.
- Customers screen derives a deduplicated customer directory from existing sales invoices (phone number when present, otherwise name).
- Suppliers screen groups existing products by supplier and totals their stored purchase prices.
- No Room entities, columns, or database version were changed.

Build verification: not run; this environment has no Android SDK installation and no Gradle executable/wrapper JAR.
