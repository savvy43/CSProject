# Exception Handling Documentation

## Overview
Comprehensive exception handling has been added to all user input areas and critical operations in the Hotel Management System.

## Exception Handling by Feature

### 1. Application Startup (MainApp.java)
- **FXML Loading**: Catches errors when loading the main UI layout
- **CSS Loading**: Gracefully handles missing CSS files with warning
- **Stage Initialization**: Catches any startup errors and displays error dialog
- **Main Method**: Top-level exception handler for fatal errors

### 2. Room Management
- **Room List Loading**: Handles errors when fetching room data
- **Table Refresh**: Catches exceptions during data refresh
- **Null Checks**: Validates room objects before display

### 3. Booking System
**Input Validation:**
- Guest name cannot be empty
- Guest name must be at least 2 characters
- Guest name must contain only letters and spaces
- Room selection is required
- Number of nights must be between 1 and 30
- Validates spinner input for invalid numbers

**Exception Handling:**
- `NullPointerException`: Missing required fields
- `NumberFormatException`: Invalid numeric input
- General exceptions with descriptive error messages
- Room availability verification before booking

### 4. Checkout & Payment System
**Input Validation:**

**Cash Payment:**
- Cash amount field cannot be empty
- Cash amount cannot be negative
- Cash must be sufficient to cover the bill
- Validates numeric input format

**Credit Card Payment:**
- Cardholder name required (min 2 characters)
- ZIP code required (must be exactly 5 digits)
- Validates ZIP code format (digits only)

**Check Payment:**
- Bank name required
- Check number required
- Check number must contain only digits

**Exception Handling:**
- `NumberFormatException`: Invalid cash amount
- `IllegalArgumentException`: Invalid payment details
- `NullPointerException`: Missing payment information
- Invoice validation (checks for null and zero amounts)
- Payment method selection validation
- Transaction failure handling with clear error messages

### 5. Housekeeping Management
- **Table Selection**: Validates room selection before marking status
- **Status Update**: Catches errors during status changes
- **Data Loading**: Handles errors when loading housekeeping data
- **Null Checks**: Validates selected items before operations

### 6. Invoice Management
- **Invoice Loading**: Handles errors when loading completed invoices
- **Revenue Calculation**: Protects against null invoices
- **Display Formatting**: Catches formatting errors with fallback values
- **Empty State**: Gracefully handles no invoices scenario

### 7. Initialization
- **Controller Init**: Catches errors during controller initialization
- **Hotel Info Display**: Validates hotel data before display
- **Default View Loading**: Handles errors when loading initial view

### 8. Exit Handler
- **Confirmation Dialog**: Catches errors in dialog display
- **Fallback**: Ensures application can exit even if dialog fails

## Error Message Types

### Validation Errors
- Clear, user-friendly messages
- Specific guidance on what needs to be corrected
- Examples: "Guest name must be at least 2 characters"

### System Errors
- Descriptive error messages with context
- Includes exception message when helpful
- Examples: "Failed to load rooms: [error details]"

### Payment Errors
- Specific to payment method
- Includes validation rules
- Examples: "ZIP code must be 5 digits"

## User Experience Features

1. **No Crashes**: All exceptions are caught and handled gracefully
2. **Clear Feedback**: Users always know what went wrong
3. **Data Integrity**: Validates all inputs before processing
4. **Graceful Degradation**: Application continues running even if one feature fails
5. **Helpful Messages**: Error messages guide users to correct their input

## Testing Recommendations

Test these scenarios to verify exception handling:

1. **Empty Fields**: Try submitting forms with empty required fields
2. **Invalid Numbers**: Enter text in numeric fields
3. **Negative Values**: Try negative numbers for cash/nights
4. **Invalid Formats**: Test wrong ZIP code formats, check numbers with letters
5. **Insufficient Cash**: Try paying with less cash than required
6. **No Selection**: Click buttons without selecting items from tables/combos
7. **Edge Cases**: Test boundary values (0, 31 nights, etc.)

## Code Quality

- All user inputs are validated before processing
- Exceptions are caught at appropriate levels
- Error messages are user-friendly and actionable
- No silent failures - all errors are reported
- Defensive programming throughout (null checks, range validation)
