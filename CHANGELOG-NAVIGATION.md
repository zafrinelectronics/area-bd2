# Apple Area Shop — Navigation update

## Included in this source update
- Added a `More` destination to the existing app tab state.
- Replaced the previous six-item bottom bar with the requested five positions: Dashboard, Transactions, central Add (+), Reports, and More.
- Added a More menu with shortcuts to existing Sales, Stock/Product, Expenses/Transactions, Reports, Employees, and Settings screens, plus Logout.
- Purchase, Customers, Suppliers, and HRM shortcuts currently open the closest existing screens because dedicated workflows/data screens are not implemented yet.
- No Room entities, database version, or existing stored records were changed in this navigation step.

## Verification limits
- Source-level checks performed: both project copies were updated; Kotlin braces/parentheses balance; new tab references and screen file are present.
- A full Android build was not run because this project copy has no Gradle wrapper script/JAR and the environment has no configured Android SDK.
- This ZIP is source code, not an installable APK.
