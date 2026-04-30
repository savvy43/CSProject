# Hotel Management System

A JavaFX-based hotel management application for managing rooms, bookings, payments, housekeeping, and cash balance tracking with SQLite database persistence.

## Features

### 1. Room Management
- View all rooms with their details (room number, style, amenities)
- See room availability status
- Track which guest is in which room
- All room statuses persist in SQLite database

### 2. Booking System
- Book available rooms for guests
- Enter guest name and select room
- Choose number of nights
- Automatic price calculation based on room type:
  - Single: $50/night
  - Double: $80/night
  - Suite: $150/night
- Automatic invoice generation
- Room status saved to database

### 3. Leave Hotel (Guest Departure)
- Process guest departure without payment
- Frees the room for new bookings
- Automatically marks room as dirty for housekeeping
- Useful for guests who leave early or cancel

### 4. Checkout & Payment
- Process checkout for occupied rooms with payment
- Multiple payment methods:
  - **Cash**: Enter cash amount, calculates change
  - **Credit Card**: Enter cardholder name and ZIP code
  - **Check**: Enter bank name and check number
- Payment amount added to hotel cash balance
- Automatic room availability update after checkout
- Invoice completion tracking
- Room marked as dirty after checkout

### 5. Cash Balance Tracking
- Initial balance: $1000.00
- Displayed in header (updates in real-time)
- Increases with each payment received
- Persists in database across sessions

### 6. Housekeeping Management
- View cleaning status of all rooms
- Mark rooms as Clean or Dirty
- **Cannot clean occupied rooms** (validation prevents this)
- Track room occupancy status
- Automatic status update to "Dirty" after checkout/departure
- Cleaning status saved to database

### 7. Invoice Tracking
- View all completed invoices
- Track total revenue

### 8. Database Persistence (SQLite)
- All room statuses saved automatically
- Guest names preserved across sessions
- Cleaning statuses persist
- Cash balance tracked in database
- Database file: `hotel.db` (created automatically)

## How to Run

### Using Maven:
```bash
mvn clean javafx:run
```

### Using IDE (IntelliJ IDEA):
1. Open the project in IntelliJ IDEA
2. Right-click on `src/ui/MainApp.java`
3. Select "Run 'MainApp.main()'"

## Requirements

- Java 21
- JavaFX 21
- Maven
- SQLite JDBC Driver (included in dependencies)

## Project Structure

```
src/
├── model/          # Domain models (Hotel, Room, Invoice, etc.)
├── payment/        # Payment processing (Cash, Credit Card, Check)
├── service/        # Business logic (BookingService, DatabaseService)
└── ui/
    ├── controller/ # UI controllers
    ├── view/       # FXML files
    └── style/      # CSS styling
hotel.db            # SQLite database (auto-created)
```

## Usage Guide

1. **View Rooms**: Click "Room Management" to see all rooms and their status
2. **Book a Room**: 
   - Click "Book Room"
   - Enter guest name
   - Select available room
   - Choose number of nights
   - Click "Confirm Booking"
3. **Guest Leaves** (no payment):
   - Click "Leave Hotel"
   - Select occupied room
   - Confirm departure
   - Room becomes available and marked dirty
4. **Checkout with Payment**: 
   - Click "Checkout & Payment"
   - Select occupied room
   - Choose payment method
   - Enter payment details
   - Click "Process Payment & Checkout"
   - Payment added to cash balance
5. **Housekeeping**: 
   - Click "Housekeeping"
   - Select a room
   - Mark as Clean (only for available rooms) or Dirty
   - Status saved to database
6. **View Revenue**: Click "View Invoices" to see completed transactions and total revenue

## Key Business Rules

- **Occupied rooms cannot be marked as clean** - prevents cleaning while guest is present
- **Rooms are marked dirty after checkout/departure** - ensures housekeeping knows to clean
- **All payments increase cash balance** - tracks hotel revenue
- **Database persistence** - all data survives application restart
- **Initial cash balance: $1000** - starting capital for the hotel

## Team Project Notes

- Model and Payment packages are maintained by team members
- UI and Service layers integrate with existing model code
- No authentication/security implemented (as per project requirements)
- SQLite database provides simple, file-based persistence
