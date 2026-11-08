# Apple Area Shop — safety patch

## Change
- Removed Room's `fallbackToDestructiveMigration()` from `AppDatabase`.
- If the database schema version changes without a registered migration, Room will now fail rather than silently delete the existing database.

## Important limitation
- This is a conservative data-protection change, not a completed migration strategy.
- A future database schema upgrade must include explicit Room `Migration` objects and must be tested against a copy of the existing version-4 database before release.
- No new menu screens or database tables are included in this patch.
- An Android APK has not been built or tested.
