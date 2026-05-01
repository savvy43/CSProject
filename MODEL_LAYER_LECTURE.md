# Complete Lecture: Understanding the Model Layer (YOUR CODE)

## Table of Contents
1. [Overview: What is the Model Layer?](#overview)
2. [Hotel.java - The Main Entity](#hotel-class)
3. [Room.java - Core Business Object](#room-class)
4. [HotelLocation.java - Value Object](#hotellocation-class)
5. [Invoice & InvoiceItem - Billing System](#invoice-classes)
6. [HouseKeeping.java - Service Management](#housekeeping-class)
7. [RoomKey.java - Access Control](#roomkey-class)
8. [How UI Integrates with Your Models](#ui-integration)
9. [Design Patterns Used](#design-patterns)
10. [Common Interview Questions & Answers](#interview-questions)

---

## Overview: What is the Model Layer? {#overview}

### What You Should Say:
"The model layer represents the **domain objects** or **business entities** of our hotel management system. These are pure Java classes that encapsulate the core business logic and data structures. They follow the **MVC (Model-View-Controller)** pattern where the Model is independent of the UI."

### Key Principles You Implemented:
1. **Encapsulation** - Private fields with public getters/setters
2. **Single Responsibility** - Each class has one clear purpose
3. **No UI Dependencies** - Models don't know about JavaFX or UI
4. **Business Logic** - Contains rules like room availability

---

## Hotel.java - The Main Entity {#hotel-class}

### Code Breakdown:
```java
public class Hotel {
    private String name;
    private HotelLocation location;
    private List<Room> rooms;
```

### What Each Part Does:

**1. Fields (Instance Variables):**
- `name` - The hotel's name (e.g., "Grand Hotel")
- `location` - A HotelLocation object (composition pattern)
- `rooms` - A List of Room objects (aggregation pattern)

**2. Constructor:**
```java
public Hotel(String name, HotelLocation location) {
    this.name = name;
    this.location = location;
    this.rooms = new ArrayList<>();
}
```

**Why initialize rooms as empty ArrayList?**
- Hotel starts with no rooms
- Rooms are added later using `addRoom()`
- Prevents NullPointerException

**3. Methods:**

```java
public void addRoom(Room room) {
    rooms.add(room);
}
```
- **Purpose**: Add a room to the hotel
- **Why not in constructor?** Flexible - can add rooms dynamically
- **Real-world analogy**: Hotel can expand by adding new rooms

```java
public List<Room> getAllRooms() {
    return rooms;
}
```
- **Purpose**: Get all rooms for display or processing
- **Used by**: UI to show room list, BookingService to find available rooms

### Questions You Might Get:

**Q: Why use a List instead of an array?**
**A:** "Lists are dynamic - we can add/remove rooms without knowing the size upfront. Arrays have fixed size. In a real hotel, rooms can be added or renovated, so List gives us flexibility."

**Q: Why is rooms initialized in the constructor?**
**A:** "To prevent NullPointerException. If we don't initialize it, calling addRoom() would crash. It's defensive programming."

**Q: Could you use a Set instead of List?**
**A:** "Yes, if we want to prevent duplicate rooms. But List is fine here because we control room addition and room numbers are unique identifiers."

---

## Room.java - Core Business Object {#room-class}

### Code Breakdown:
```java
public class Room {
    private int roomNumber;
    private String roomStyle;
    private boolean available;
    private List<String> amenities;
```

### Field Explanations:

**1. `roomNumber` (int)**
- Unique identifier for the room
- Used to distinguish rooms (101, 102, 201, etc.)
- **Why int not String?** Easier to sort, compare, and validate

**2. `roomStyle` (String)**
- Type of room: "Single", "Double", "Suite"
- Determines pricing in the system
- **Could use enum?** Yes, but String is simpler for this project

**3. `available` (boolean)**
- **true** = room is free for booking
- **false** = room is occupied
- **This is critical business logic!**

**4. `amenities` (List<String>)**
- Features like "WiFi", "TV", "Mini Bar"
- List because rooms can have multiple amenities
- Displayed to guests when choosing rooms

### Constructor:
```java
public Room(int roomNumber, String roomStyle, List<String> amenities) {
    this.roomNumber = roomNumber;
    this.roomStyle = roomStyle;
    this.amenities = amenities;
    this.available = true;  // ← Important!
}
```

**Why available = true by default?**
- New rooms start as available
- Makes sense: when you create a room, it's ready for guests
- Changed to false when booked

### Key Methods:

**1. Getters:**
```java
public int getRoomNumber() { return roomNumber; }
public String getRoomStyle() { return roomStyle; }
public boolean isAvailable() { return available; }
public List<String> getAmenities() { return amenities; }
```

**Why no setters for roomNumber and roomStyle?**
- These are **immutable** after creation
- Room number doesn't change
- Room style doesn't change (unless renovation, not in scope)

**2. Availability Control:**
```java
public void setAvailability(boolean available) {
    this.available = available;
}
```

**This is the MOST IMPORTANT method!**
- Called when booking: `room.setAvailability(false)`
- Called when checking out: `room.setAvailability(true)`
- Controls the entire booking flow

**3. toString() Method:**
```java
public String toString() {
    return "Room " + roomNumber + " (" + roomStyle + ")";
}
```

**Why override toString()?**
- Used by ComboBox in UI to display rooms
- Without this, would show memory address
- Makes debugging easier

### Questions You Might Get:

**Q: Why is available a boolean and not a String like "Available"/"Occupied"?**
**A:** "Boolean is more efficient and type-safe. We can use if(room.isAvailable()) directly. With String, we'd need string comparison which is error-prone and slower."

**Q: What if you want to add more room states like 'Under Maintenance'?**
**A:** "Good question! Then I'd use an enum like RoomStatus with values AVAILABLE, OCCUPIED, MAINTENANCE. But for this project scope, boolean is sufficient."

**Q: Why store amenities as List<String> instead of separate boolean fields?**
**A:** "Flexibility and scalability. With List, we can add any number of amenities without changing the class. With booleans (hasWifi, hasTV), we'd need to modify code for each new amenity."

---

## HotelLocation.java - Value Object {#hotellocation-class}

### Code Breakdown:
```java
public class HotelLocation {
    private String city;
    private String address;
```

### What is a Value Object?

**Definition:** A simple object that represents a descriptive aspect of the domain with no conceptual identity.

**In Plain English:** HotelLocation is just data - it describes WHERE the hotel is. Two locations with same city and address are considered equal.

### Why Separate Class?

**Option 1 (Bad):** Put city and address directly in Hotel
```java
public class Hotel {
    private String city;
    private String address;
    // ...
}
```

**Option 2 (Good - What You Did):** Separate HotelLocation class
```java
public class Hotel {
    private HotelLocation location;
    // ...
}
```

**Benefits:**
1. **Encapsulation** - Location logic is grouped together
2. **Reusability** - Could use HotelLocation for guest addresses too
3. **Single Responsibility** - Hotel manages rooms, HotelLocation manages location
4. **Easier to extend** - Can add zipCode, country, coordinates later

### Methods:

```java
public String toString() {
    return city + ", " + address;
}
```

**Why this toString()?**
- Used in UI to display hotel location
- Format: "Tashkent, Amir Temur Street 15"
- Clean, readable output

### Questions You Might Get:

**Q: Why create a separate class for just two fields?**
**A:** "It follows the Single Responsibility Principle. HotelLocation encapsulates location-related data and behavior. If we need to add validation (like checking if city exists) or formatting, it's all in one place. It also makes the Hotel class cleaner."

**Q: Is HotelLocation mutable or immutable?**
**A:** "Currently mutable because we have setters. But it could be made immutable by removing setters and only setting values in constructor. Immutable objects are safer in multi-threaded environments."

---

## Invoice & InvoiceItem - Billing System {#invoice-classes}

### InvoiceItem.java:

```java
public class InvoiceItem {
    private double amount;
    
    public InvoiceItem(double amount) {
        this.amount = amount;
    }
    
    public double getAmount() {
        return amount;
    }
}
```

**What is an InvoiceItem?**
- Represents ONE charge on a bill
- Could be: room charge, room service, minibar, etc.
- In our system: mainly room charges (price × nights)

**Why separate class for just amount?**
- **Extensibility**: Later could add description, date, tax
- **Composite Pattern**: Invoice contains multiple InvoiceItems
- **Real-world modeling**: Real invoices have line items

### Invoice.java:

```java
public class Invoice {
    private List<InvoiceItem> items = new ArrayList<>();
    
    public void addItem(InvoiceItem item) {
        items.add(item);
    }
    
    public double getTotalAmount() {
        double total = 0;
        for (InvoiceItem item : items) {
            total += item.getAmount();
        }
        return total;
    }
}
```

### Key Concepts:

**1. Composition Pattern:**
- Invoice HAS-A list of InvoiceItems
- One-to-many relationship
- Invoice manages its items

**2. getTotalAmount() Logic:**
```java
double total = 0;
for (InvoiceItem item : items) {
    total += item.getAmount();
}
return total;
```

**Step-by-step:**
1. Start with total = 0
2. Loop through each item
3. Add item's amount to total
4. Return final sum

**Example:**
- Item 1: $150 (3 nights × $50)
- Item 2: $20 (room service)
- Total: $170

**Modern Alternative (Java 8+):**
```java
return items.stream()
           .mapToDouble(InvoiceItem::getAmount)
           .sum();
```
But the loop is clearer for beginners.

### How It's Used in the System:

```java
// In MainController when booking:
Invoice invoice = new Invoice();
double roomPrice = getRoomPrice(selectedRoom);
invoice.addItem(new InvoiceItem(roomPrice * nights));
roomInvoices.put(selectedRoom, invoice);
```

**Flow:**
1. Guest books room for 3 nights at $50/night
2. Create new Invoice
3. Create InvoiceItem with amount = $150
4. Add item to invoice
5. Store invoice with room

### Questions You Might Get:

**Q: Why use List<InvoiceItem> instead of just storing total amount?**
**A:** "Flexibility and detail. With items, we can show itemized bills. Guest can see 'Room: $150, Breakfast: $30'. Just storing total loses this detail. Also easier to add/remove charges."

**Q: What if you want to add tax or discounts?**
**A:** "I could add a method like applyTax(double rate) or addDiscount(double percent) that modifies the total. Or create special InvoiceItem types like TaxItem or DiscountItem."

**Q: Why initialize items in the field declaration instead of constructor?**
**A:** "Both work, but field initialization is cleaner when the initial value is always the same. Every Invoice starts with an empty list, so we initialize it directly."

---

## HouseKeeping.java - Service Management {#housekeeping-class}

### Code Breakdown:
```java
public class HouseKeeping {
    private Room room;
    private String status;
```

### What is HouseKeeping?

**Purpose:** Tracks the cleaning status of rooms

**Real-world analogy:** In a real hotel, housekeeping department tracks which rooms need cleaning. This class models that.

### Why Link to Room?

```java
private Room room;
```

**This creates an association:**
- Each HouseKeeping object is FOR a specific room
- One-to-one relationship
- HouseKeeping "knows about" its Room

### Status Field:

```java
private String status;
```

**Possible values:**
- "Clean" - Room is clean and ready
- "Dirty" - Room needs cleaning

**Could use boolean?** Yes, but String is more descriptive and extensible (could add "In Progress" later)

### Methods:

**1. markClean():**
```java
public void markClean() {
    status = "Clean";
}
```
- Called when housekeeping finishes cleaning
- Simple setter with semantic meaning
- Better than `setStatus("Clean")` - more readable

**2. markDirty():**
```java
public void markDirty() {
    status = "Dirty";
}
```
- Called when guest checks out
- Signals room needs cleaning

**3. Getters:**
```java
public String getStatus() { return status; }
public Room getRoom() { return room; }
```
- Used by UI to display cleaning status
- Used to identify which room this HouseKeeping is for

### How It's Used:

```java
// In MainController initialization:
for (Room room : hotel.getAllRooms()) {
    houseKeepingList.add(new HouseKeeping(room, "Clean"));
}
```

**Creates HouseKeeping for each room:**
- Room 101 → HouseKeeping(room101, "Clean")
- Room 102 → HouseKeeping(room102, "Clean")
- etc.

**When guest checks out:**
```java
for (HouseKeeping hk : houseKeepingList) {
    if (hk.getRoom().equals(selectedRoom)) {
        hk.markDirty();
        break;
    }
}
```

**Logic:**
1. Loop through all HouseKeeping objects
2. Find the one for the checked-out room
3. Mark it as dirty
4. Break (found it, no need to continue)

### Questions You Might Get:

**Q: Why not just add a cleaningStatus field to Room class?**
**A:** "Separation of concerns. Room represents the physical room and its availability. HouseKeeping represents the service aspect. In a real system, housekeeping might have additional data like last cleaned date, assigned staff, etc. Keeping them separate makes the code more maintainable."

**Q: What's the relationship between Room and HouseKeeping?**
**A:** "It's a one-to-one association. Each Room has one HouseKeeping record, and each HouseKeeping is for one Room. HouseKeeping has a reference to Room, but Room doesn't know about HouseKeeping - this is unidirectional association."

**Q: Why use methods like markClean() instead of setStatus()?**
**A:** "It's called the 'Tell, Don't Ask' principle. markClean() is more expressive and hides the implementation. If we later change status to an enum or add logging, we only change markClean(), not every place that calls it."

---

## RoomKey.java - Access Control {#roomkey-class}

### Code Breakdown:
```java
public class RoomKey {
    private int keyNumber;
    private Room room;
    private boolean active;
```

### What is RoomKey?

**Purpose:** Represents a physical or digital key card for a room

**Real-world analogy:** Hotel key cards that guests use to access their rooms

### Fields Explained:

**1. keyNumber (int):**
- Unique identifier for the key
- Like a serial number
- Used to track which key is which

**2. room (Room):**
- Which room this key opens
- Association: RoomKey → Room
- One key is for one specific room

**3. active (boolean):**
- **true** = key works, can open door
- **false** = key is deactivated, won't work
- Security feature!

### Why Deactivate Keys?

**Security reasons:**
- Guest checks out → deactivate their key
- Key is lost → deactivate it
- Prevents unauthorized access

### Methods:

**1. Constructor:**
```java
public RoomKey(int keyNumber, Room room) {
    this.keyNumber = keyNumber;
    this.room = room;
    this.active = true;  // New keys are active
}
```

**Why active = true by default?**
- When you create a key, it should work
- Deactivated later when needed

**2. Control Methods:**
```java
public void deactivate() {
    active = false;
}

public void activate() {
    active = true;
}
```

**Use cases:**
- deactivate(): Guest checks out
- activate(): Reissue same key to new guest

**3. Getters:**
```java
public boolean isActive() { return active; }
public Room getRoom() { return room; }
```

### How It Could Be Used (Not in Current UI):

```java
// When guest checks in:
RoomKey key = new RoomKey(1001, room101);
giveKeyToGuest(key);

// When guest checks out:
key.deactivate();

// If guest loses key:
key.deactivate();
RoomKey newKey = new RoomKey(1002, room101);
```

### Questions You Might Get:

**Q: Why isn't RoomKey used in the current UI?**
**A:** "It's part of the domain model for completeness, but the current UI focuses on booking and payment flow. In a full system, we'd have a check-in screen that issues keys. The model is designed to be extensible."

**Q: Could multiple keys exist for one room?**
**A:** "Yes! In real hotels, you can have multiple key cards for one room (for couples, families). We'd need a List<RoomKey> in Room or a separate KeyManager class to track all keys."

**Q: Why have both activate() and deactivate() instead of setActive(boolean)?**
**A:** "Semantic clarity and intent. key.deactivate() is clearer than key.setActive(false). It's self-documenting code. Also, we could add logging or validation in these methods later."

---

## How UI Integrates with Your Models {#ui-integration}

### The Flow: Model → Controller → View

```
[Model Layer]  ←→  [Controller]  ←→  [View (FXML)]
Your Code          MainController      UI Elements
```

### Example 1: Displaying Rooms

**1. Model (Your Code):**
```java
public class Room {
    public int getRoomNumber() { return roomNumber; }
    public String getRoomStyle() { return roomStyle; }
    public boolean isAvailable() { return available; }
}
```

**2. Controller Uses Model:**
```java
// In MainController
for (Room room : service.getHotel().getAllRooms()) {
    if (room.isAvailable()) {
        roomCombo.getItems().add(room);
    }
}
```

**What happens:**
- Gets all rooms from Hotel
- Checks each room's availability using `isAvailable()`
- Adds available rooms to ComboBox

**3. View Displays:**
```xml
<ComboBox fx:id="roomCombo" />
```

**Result:** User sees "Room 101 (Single)" in dropdown

### Example 2: Booking a Room

**User Action:** Clicks "Confirm Booking"

**Controller Code:**
```java
Room selectedRoom = roomCombo.getValue();  // Get selected room
if (service.book(selectedRoom)) {          // Try to book
    // Success!
}
```

**BookingService Code:**
```java
public boolean book(Room room) {
    if (room == null || !room.isAvailable()) return false;
    room.setAvailability(false);  // ← Uses YOUR model!
    return true;
}
```

**What happens:**
1. Controller gets Room object from UI
2. Passes to BookingService
3. Service calls YOUR `setAvailability(false)` method
4. Room is now occupied

### Example 3: Housekeeping Display

**Controller Creates Table:**
```java
TableColumn<HouseKeeping, Integer> roomCol = new TableColumn<>("Room #");
roomCol.setCellValueFactory(data -> 
    new SimpleObjectProperty<>(
        data.getValue().getRoom().getRoomNumber()  // ← Uses YOUR models!
    )
);
```

**What happens:**
1. Table needs room number for each HouseKeeping
2. Calls `getRoom()` on HouseKeeping (your code)
3. Calls `getRoomNumber()` on Room (your code)
4. Displays in table

### Key Integration Points:

**Your Model Methods Used by UI:**
- `Room.getRoomNumber()` - Display room number
- `Room.getRoomStyle()` - Display room type
- `Room.isAvailable()` - Check if bookable
- `Room.setAvailability()` - Book/checkout room
- `Room.getAmenities()` - Show room features
- `Hotel.getAllRooms()` - Get room list
- `Hotel.getName()` - Display hotel name
- `HotelLocation.toString()` - Display location
- `HouseKeeping.getStatus()` - Show cleaning status
- `Invoice.getTotalAmount()` - Calculate bill

### Why This Design Works:

**1. Separation of Concerns:**
- Models: Define data and business rules
- Controller: Handles user actions
- View: Displays information

**2. Models Don't Know About UI:**
- Room class has no JavaFX imports
- Can be tested without UI
- Could be used in web app, mobile app, etc.

**3. Controller is the Bridge:**
- Translates user actions to model operations
- Translates model data to UI display
- Handles exceptions and validation

---

## Design Patterns Used {#design-patterns}

### 1. Encapsulation Pattern

**What:** Private fields with public getters/setters

**Where:** All your model classes

**Example:**
```java
public class Room {
    private int roomNumber;  // Private!
    
    public int getRoomNumber() {  // Public getter
        return roomNumber;
    }
}
```

**Why:**
- Protects data from invalid changes
- Can add validation in setters
- Can change internal implementation without breaking code

### 2. Composition Pattern

**What:** Object contains other objects

**Where:** 
- Hotel HAS-A HotelLocation
- Hotel HAS-MANY Rooms
- Invoice HAS-MANY InvoiceItems

**Example:**
```java
public class Hotel {
    private HotelLocation location;  // Composition
    private List<Room> rooms;        // Composition
}
```

**Why:**
- Models real-world relationships
- "Has-a" relationship
- Promotes reusability

### 3. Value Object Pattern

**What:** Object that represents a descriptive aspect

**Where:** HotelLocation, InvoiceItem

**Characteristics:**
- No unique identity
- Defined by its values
- Usually immutable (or should be)

**Example:**
```java
HotelLocation loc1 = new HotelLocation("Tashkent", "Street 1");
HotelLocation loc2 = new HotelLocation("Tashkent", "Street 1");
// loc1 and loc2 are "equal" even though different objects
```

### 4. Domain Model Pattern

**What:** Objects that model the business domain

**Where:** All your model classes

**Characteristics:**
- Represents real-world concepts
- Contains business logic
- Independent of technical concerns

**Your Domain:**
- Hotel, Room, HouseKeeping = Real hotel concepts
- Not technical objects like Database, Controller

---

## Common Interview Questions & Answers {#interview-questions}

### Q1: "Explain the relationship between Hotel and Room"

**Answer:**
"Hotel and Room have a one-to-many composition relationship. One Hotel contains many Rooms. I implemented this using a List<Room> in the Hotel class. The Hotel is responsible for managing its rooms through the addRoom() and getAllRooms() methods. This models the real-world relationship where a hotel owns and manages multiple rooms."

### Q2: "Why did you make Room.available a boolean instead of a String?"

**Answer:**
"I chose boolean for type safety and efficiency. With boolean, we can only have two states: true or false, which matches our business requirement. It's also more efficient in memory and allows direct use in if statements. If we needed more states like 'Under Maintenance', I would refactor to use an enum, but for this scope, boolean is the right choice."

### Q3: "What would you change if this were a real production system?"

**Answer:**
"Several things:
1. Add validation - check room numbers are positive, names aren't empty
2. Make some classes immutable - like HotelLocation shouldn't change
3. Add equals() and hashCode() methods for proper object comparison
4. Use enums for room styles instead of Strings
5. Add timestamps - when was room booked, when was it cleaned
6. Add more business logic - pricing rules, booking dates, guest information
7. Consider using Builder pattern for complex objects like Room"

### Q4: "How does your model support the booking functionality?"

**Answer:**
"The Room class has an 'available' boolean field and a setAvailability() method. When a guest books a room, the BookingService calls setAvailability(false), marking it as occupied. When they check out, it calls setAvailability(true), making it available again. The isAvailable() method allows the UI to filter and display only bookable rooms. This encapsulates the booking state within the Room object itself."

### Q5: "Why separate Invoice and InvoiceItem?"

**Answer:**
"This follows the Composite pattern and models real-world invoices. An invoice can have multiple line items - room charges, services, taxes, etc. By separating them, we can:
1. Add multiple charges to one invoice
2. Show itemized bills to guests
3. Calculate totals by summing items
4. Extend InvoiceItem later with descriptions, dates, etc.
It's more flexible than just storing a single total amount."

### Q6: "What's the purpose of the toString() methods?"

**Answer:**
"The toString() methods provide human-readable string representations of objects. For example, Room's toString() returns 'Room 101 (Single)' which is used by JavaFX ComboBox to display rooms. Without it, we'd see memory addresses like 'Room@1a2b3c'. It's also helpful for debugging - I can print objects directly to see their state."

### Q7: "How would you add room pricing to your model?"

**Answer:**
"I'd add a price field to the Room class or create a separate Pricing class. Option 1: Add 'private double pricePerNight' to Room. Option 2: Create a Pricing class that maps room styles to prices. Currently, pricing is in the controller, but ideally it should be in the model layer as it's business logic. I'd probably use a Map<String, Double> in Hotel class mapping room styles to prices."

### Q8: "Explain the HouseKeeping class design"

**Answer:**
"HouseKeeping represents the service aspect of room management, separate from the Room entity itself. It has a reference to a Room and tracks its cleaning status. I used methods like markClean() and markDirty() instead of a simple setter because they're more expressive and follow the 'Tell, Don't Ask' principle. This separation allows us to add housekeeping-specific features later like assigned staff, cleaning schedules, or last cleaned timestamp without cluttering the Room class."

### Q9: "What if two guests try to book the same room simultaneously?"

**Answer:**
"In the current implementation, this could cause a race condition. In a production system, I'd add synchronization or use database transactions. We could:
1. Use synchronized methods on the booking operation
2. Implement optimistic locking with version numbers
3. Use database-level locking
4. Add a 'reserved' state between available and occupied
For this project scope, we assume single-user access, but I'm aware of the concurrency issue."

### Q10: "Why doesn't Room have a reference back to Hotel?"

**Answer:**
"This is a unidirectional relationship - Hotel knows about Rooms, but Rooms don't know about Hotel. This is intentional because:
1. A Room doesn't need to know which hotel it belongs to for its functionality
2. It reduces coupling - Room is more independent
3. It prevents circular references which can cause memory issues
4. It's simpler and follows the principle of least knowledge
If we needed it, we could add it, but it's not necessary for our use cases."

---

## Study Tips for Presentation

### 1. Practice Explaining Each Class
- Pick any class and explain its purpose in 30 seconds
- Explain each field and why it's needed
- Explain each method and when it's called

### 2. Trace a Booking Flow
Be able to explain step-by-step:
1. User selects room from dropdown
2. Controller gets Room object
3. Calls service.book(room)
4. Service calls room.setAvailability(false)
5. Room is now occupied

### 3. Know Your Design Decisions
For each class, know:
- Why you chose that data type
- Why you made it a separate class
- What alternatives you considered

### 4. Understand the Relationships
Draw this diagram:
```
Hotel ──(has)──> HotelLocation
  │
  └──(contains)──> Room
                    │
                    └──(tracked by)──> HouseKeeping
                    │
                    └──(accessed by)──> RoomKey

Invoice ──(contains)──> InvoiceItem
```

### 5. Be Ready for "What If" Questions
- What if we need to add room pricing?
- What if we need to track booking dates?
- What if we need to support multiple hotels?
- What if we need to add room photos?

**Answer format:** "I would [solution] because [reason]. For example, [example]."

---

## Final Confidence Boosters

### You Can Say:
✅ "I designed the model layer to represent the core business entities"
✅ "I used encapsulation to protect data integrity"
✅ "I separated concerns - each class has a single responsibility"
✅ "I used composition to model real-world relationships"
✅ "My models are independent of the UI, making them reusable"
✅ "I followed object-oriented principles like encapsulation and abstraction"

### Don't Say:
❌ "I just made some classes"
❌ "I don't know why I did it that way"
❌ "The AI told me to do it"

### Remember:
- You understand the WHAT (what each class does)
- You understand the WHY (why it's designed that way)
- You understand the HOW (how it integrates with the system)

**You've got this! 🎓**
