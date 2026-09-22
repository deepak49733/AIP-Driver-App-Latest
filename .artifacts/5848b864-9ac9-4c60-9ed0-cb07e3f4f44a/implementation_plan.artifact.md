# Fix NumberFormatException in Ride ID Parsing

The application crashes with `NumberFormatException` when attempting to parse offline ride IDs (e.g., `"OFF_1915_1787335971858"`) as integers. This happens because the system recently introduced offline ride IDs as strings, but several parts of the codebase still use `Integer.parseInt()` or `.toInt()` on the ride ID.

## Proposed Changes

### [SharedprefrenceManager](file:///D:/Nicole/FinalCode11_08_2026/app/src/main/java/com/sc/aipdriver/activities/otherclasses/SharedprefrenceManager.java)
- Add `getRideIdInt()` helper to safely return `0` for offline or invalid ride IDs.
- Add `isOfflineRide()` helper to check if the current ride ID is an offline ID.

### [ImprovedFarmList.kt](file:///D:/Nicole/FinalCode11_08_2026/app/src/main/java/com/sc/aipdriver/activities/ui/ImprovedFarmList.kt)
- Update multiple locations where `rideId.toInt()` is called to handle offline IDs.
- Use `setRideIdString()` when setting the ride ID in shared preferences if it might be an offline ID.
- Update `sendToServer` to correctly set `rideId` and `offlineRideId` in the `StartDataSend` object.

### [FarmListRoute.java](file:///D:/Nicole/FinalCode11_08_2026/app/src/main/java/com/sc/aipdriver/activities/ui/FarmListRoute.java)
- Update `addList` and `OnNoteListChanged` (or similar) to safely handle offline ride IDs when populating `StartDataSend`.

### [FarmDetailActivity.kt](file:///D:/Nicole/FinalCode11_08_2026/app/src/main/java/com/sc/aipdriver/activities/ui/FarmDetailActivity.kt)
- Update ride ID parsing logic to be safe for offline IDs.

## Verification Plan

### Automated Tests
- I will check if the project compiles after the changes.
- Since I cannot run the app, I will rely on code analysis to ensure all problematic `parseInt`/`toInt` calls are addressed.

### Manual Verification
- The user should verify that starting and completing rides in offline mode no longer causes a crash.
- Verify that ride data is correctly synced when coming back online.
