# Delivery management update

- Added local delivery-agency add, edit, and delete controls.
- Added delivery-order records with invoice, customer, phone, agency, and tracking number.
- Added a status-cycle action: pending, collected, in transit, delivered, returned.
- Records are persisted on this device using SharedPreferences JSON. They are not Room database records and are not yet connected to sales invoices automatically or synced to cloud backups.
- Deleting an agency also deletes delivery orders assigned to that agency.
- ZIP integrity was checked. Android compilation and device testing were not performed because the Android build environment is unavailable.
