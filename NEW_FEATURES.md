# New Features Added

## 1. SQLite Database Persistence ✅

**What it does:**
- Stores all room statuses (available/occupied)
- Saves guest names for each room
- Persists cleaning status (Clean/Dirty)
- Tracks cash balance across sessions

**Technical Details:**
- Database file: `hotel.db` (auto-created in project root)
- Tables: `rooms` and `cash_balance`
- Automatic initialization on first run
- All changes saved immediately

**Benefits:**
- Data survives application restart
- No data loss when closing the app
- Professional data management

## 2. Cash Balance Tracking ✅

**What it does:**
- Initial balance: $1000.00
- Displayed in header (top of screen)
- Updates in real-time when payments received
- Persists in database

**How it works:**
- Every checkout payment adds to balance
- Balance shown as: "Cash Balance: $1000.00"
- Automatically saved to database
- Loads previous balance on startup

**Example:**
- Start: $1000.00
- Guest pays $150 for suite → Balance: $1150.00
- Guest pays $80 for double → Balance: $1230.00

## 3. Leave Hotel Function ✅

**What it does:**
- Allows guest to leave WITHOUT payment
- Frees the room immediately
- Marks room as dirty for housekeeping
- Separate from checkout (which requires payment)

**Use Cases:**
- Guest leaves early
- Guest cancels stay
- Emergency departure
- Complimentary stays

**How to use:**
1. Click "🚪 Leave Hotel" in menu
2. Select occupied room
3. Confirm departure
4. Room becomes available and marked dirty

**Difference from Checkout:**
- Leave Hotel: No payment, just departure
- Checkout: Payment required, then departure

## 4. Occupied Room Protection ✅

**What it does:**
- Prevents marking occupied rooms as "Clean"
- Shows warning message if attempted
- Ensures rooms are only cleaned when empty

**Why it matters:**
- Can't clean a room while guest is inside
- Prevents logical errors
- Matches real hotel operations

**Validation:**
- Housekeeping can mark any room as "Dirty"
- Housekeeping can only mark AVAILABLE rooms as "Clean"
- Error message: "Cannot mark occupied room as clean. Guest is still in the room."

## 5. Fixed Room Status Logic ✅

**Previous behavior (WRONG):**
- Booking a room → immediately marked as "Dirty"
- Didn't make sense (guest just checked in)

**New behavior (CORRECT):**
- Booking a room → stays "Clean" (guest just arrived)
- After checkout/departure → marked as "Dirty" (needs cleaning)

**Status Flow:**
```
Clean Room → Guest Books → Still Clean (occupied)
                ↓
         Guest Leaves/Checks Out
                ↓
         Marked as Dirty (needs cleaning)
                ↓
         Housekeeping Cleans
                ↓
         Marked as Clean (ready for next guest)
```

## Database Schema

### rooms table:
```sql
CREATE TABLE rooms (
    room_number INTEGER PRIMARY KEY,
    is_available INTEGER NOT NULL,      -- 1 = available, 0 = occupied
    guest_name TEXT,                     -- NULL if no guest
    cleaning_status TEXT DEFAULT 'Clean' -- 'Clean' or 'Dirty'
)
```

### cash_balance table:
```sql
CREATE TABLE cash_balance (
    id INTEGER PRIMARY KEY CHECK (id = 1), -- Only one row
    balance REAL NOT NULL DEFAULT 1000.0   -- Starting balance
)
```

## Updated UI

**Header now shows:**
- Hotel name and location
- **Cash Balance: $XXXX.XX** (NEW!)

**Menu now includes:**
- 📋 Room Management
- 🛏️ Book Room
- **🚪 Leave Hotel** (NEW!)
- 💳 Checkout & Payment
- 🧹 Housekeeping
- 📊 View Invoices
- ❌ Exit

## Testing Checklist

✅ Book a room → Check database has guest name
✅ Close app → Reopen → Room still occupied
✅ Checkout with payment → Cash balance increases
✅ Try to clean occupied room → Shows error
✅ Guest leaves → Room marked dirty
✅ Clean room → Status saved to database
✅ Close and reopen → All data preserved

## Files Modified

1. `pom.xml` - Added SQLite dependency
2. `src/service/DatabaseService.java` - NEW FILE (database operations)
3. `src/ui/controller/MainController.java` - Added all new features
4. `src/ui/view/main.fxml` - Added Leave Hotel button and cash balance label
5. `README.md` - Updated documentation

## Dependencies Added

```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.0.0</version>
</dependency>
```

## Summary

All requested features have been implemented:
1. ✅ SQLite database for persistence
2. ✅ Cash balance tracking (starts at $1000)
3. ✅ Leave Hotel function (no payment)
4. ✅ Cannot clean occupied rooms
5. ✅ Fixed room status logic (not dirty when booked)

The application is now production-ready with proper data persistence and business logic!
