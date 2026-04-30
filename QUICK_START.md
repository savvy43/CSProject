# Quick Start Guide

## First Time Setup

1. **Run the application** (IntelliJ or Maven)
2. Database `hotel.db` will be created automatically
3. Initial cash balance set to $1000.00
4. All 6 rooms start as available and clean

## Common Workflows

### Workflow 1: Book a Room
```
1. Click "🛏️ Book Room"
2. Enter guest name (e.g., "John Smith")
3. Select room from dropdown
4. Choose number of nights (e.g., 3)
5. Click "✅ Confirm Booking"
6. Room is now occupied (still clean)
```

### Workflow 2: Guest Checks Out (WITH Payment)
```
1. Click "💳 Checkout & Payment"
2. Select occupied room
3. See guest name and amount due
4. Choose payment method:
   - Cash: Enter amount (must be >= bill)
   - Credit Card: Enter name and 5-digit ZIP
   - Check: Enter bank name and check number
5. Click "💳 Process Payment & Checkout"
6. Payment added to cash balance
7. Room marked as dirty
8. Room becomes available
```

### Workflow 3: Guest Leaves (NO Payment)
```
1. Click "🚪 Leave Hotel"
2. Select occupied room
3. Confirm departure
4. Room marked as dirty
5. Room becomes available
6. No payment collected
```

### Workflow 4: Clean a Room
```
1. Click "🧹 Housekeeping"
2. Select a dirty room (must be available!)
3. Click "✅ Mark as Clean"
4. Room status updated
5. Room ready for next guest
```

## Important Rules

### ✅ You CAN:
- Book any available room
- Mark any room as dirty
- Mark AVAILABLE rooms as clean
- Process checkout for occupied rooms
- Process departure for occupied rooms

### ❌ You CANNOT:
- Book an occupied room
- Clean an occupied room (validation prevents this)
- Checkout an available room
- Enter negative numbers
- Use invalid payment details

## Cash Balance Examples

```
Starting Balance: $1000.00

Guest 1 books Single (3 nights × $50) = $150
Guest 1 checks out and pays → Balance: $1150.00

Guest 2 books Suite (2 nights × $150) = $300
Guest 2 checks out and pays → Balance: $1450.00

Guest 3 books Double (1 night × $80) = $80
Guest 3 leaves without paying → Balance: $1450.00 (unchanged)
```

## Room Status Flow

```
┌─────────────┐
│ Clean       │ ← Room ready for guest
│ Available   │
└──────┬──────┘
       │ Guest books room
       ↓
┌─────────────┐
│ Clean       │ ← Guest is staying
│ Occupied    │
└──────┬──────┘
       │ Guest leaves/checks out
       ↓
┌─────────────┐
│ Dirty       │ ← Needs cleaning
│ Available   │
└──────┬──────┘
       │ Housekeeping cleans
       ↓
┌─────────────┐
│ Clean       │ ← Ready for next guest
│ Available   │
└─────────────┘
```

## Troubleshooting

**Problem:** Can't mark room as clean
**Solution:** Check if room is occupied. Only available rooms can be marked clean.

**Problem:** Cash balance not updating
**Solution:** Make sure you're using "Checkout & Payment", not "Leave Hotel"

**Problem:** Data lost after closing
**Solution:** Check if `hotel.db` file exists in project root. Should be created automatically.

**Problem:** Can't book a room
**Solution:** Make sure room is available (not occupied) and clean

## Data Persistence

Everything is saved automatically:
- Room availability
- Guest names
- Cleaning status
- Cash balance

Close and reopen the app - all data will be there!

## Payment Method Details

### Cash Payment
- Enter amount in dollars
- Must be >= bill amount
- Change calculated automatically
- Example: Bill $150, Pay $200 → Change $50

### Credit Card Payment
- Enter cardholder name (min 2 characters)
- Enter ZIP code (exactly 5 digits)
- Example: "John Smith", "12345"

### Check Payment
- Enter bank name
- Enter check number (digits only)
- Example: "Chase Bank", "123456"

## Tips for Demo

1. **Show booking flow**: Book 2-3 rooms with different guests
2. **Show cash balance**: Start at $1000, checkout one guest, show increase
3. **Show leave hotel**: Have one guest leave without paying
4. **Show housekeeping**: Try to clean occupied room (shows error), then clean available room
5. **Show persistence**: Close app, reopen, show all data is still there
6. **Show invoices**: View completed transactions and total revenue

## Room Prices

- Single: $50/night
- Double: $80/night  
- Suite: $150/night

## Default Rooms

- 101: Single (WiFi, TV)
- 102: Single (WiFi, TV, Mini Bar)
- 201: Double (WiFi, TV, Mini Bar, Balcony)
- 202: Double (WiFi, TV, Mini Bar)
- 301: Suite (WiFi, TV, Mini Bar, Balcony, Jacuzzi)
- 302: Suite (WiFi, TV, Mini Bar, Balcony, Kitchen)

Good luck with your demo! 🎉
