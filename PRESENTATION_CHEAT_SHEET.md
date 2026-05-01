# Presentation Day Cheat Sheet - Quick Reference

## Your Elevator Pitch (30 seconds)
"I designed the model layer which represents the core business entities of our hotel management system. This includes Hotel, Room, HotelLocation, Invoice, HouseKeeping, and RoomKey classes. These classes encapsulate the business logic and data structures following object-oriented principles like encapsulation, composition, and single responsibility. They're independent of the UI, making them reusable and testable."

---

## Quick Class Summaries

### Hotel.java
**Purpose:** Main entity representing the hotel
**Key Fields:** name, location, List<Room>
**Key Methods:** addRoom(), getAllRooms()
**One-liner:** "Manages hotel information and contains all rooms"

### Room.java  
**Purpose:** Represents a bookable room
**Key Fields:** roomNumber, roomStyle, available, amenities
**Key Methods:** isAvailable(), setAvailability(), getters
**One-liner:** "Core business object that tracks room state and availability"

### HotelLocation.java
**Purpose:** Value object for location data
**Key Fields:** city, address
**Key Methods:** toString()
**One-liner:** "Encapsulates location information as a reusable component"

### Invoice.java
**Purpose:** Represents a guest's bill
**Key Fields:** List<InvoiceItem>
**Key Methods:** addItem(), getTotalAmount()
**One-liner:** "Manages billing with itemized charges"

### InvoiceItem.java
**Purpose:** Single charge on an invoice
**Key Fields:** amount
**Key Methods:** getAmount()
**One-liner:** "Represents one line item on a bill"

### HouseKeeping.java
**Purpose:** Tracks room cleaning status
**Key Fields:** room, status
**Key Methods:** markClean(), markDirty()
**One-liner:** "Manages cleaning status separate from room availability"

### RoomKey.java
**Purpose:** Represents access control
**Key Fields:** keyNumber, room, active
**Key Methods:** activate(), deactivate()
**One-liner:** "Models key cards with activation control for security"

---

## Key Relationships (Memorize This!)

```
Hotel ──(1:1)──> HotelLocation    "Hotel has one location"
Hotel ──(1:N)──> Room              "Hotel has many rooms"
Room  ──(1:1)──> HouseKeeping      "Each room has cleaning status"
Room  ──(1:N)──> RoomKey           "Room can have multiple keys"
Invoice ──(1:N)──> InvoiceItem     "Invoice has multiple charges"
```

---

## Design Patterns You Used

1. **Encapsulation** - Private fields, public methods
2. **Composition** - Objects contain other objects (Hotel has Rooms)
3. **Value Object** - HotelLocation, InvoiceItem
4. **Domain Model** - Classes represent real-world concepts

---

## Most Likely Questions & Quick Answers

### "Walk me through the Room class"
"Room represents a bookable room with a room number, style like Single or Suite, an availability boolean, and a list of amenities. The key method is setAvailability() which is called when booking or checking out. I used boolean for availability because it's type-safe and efficient - only two states needed."

### "Why separate Hotel and HotelLocation?"
"Separation of concerns and reusability. HotelLocation encapsulates location logic. If we need to add validation or formatting, it's in one place. Also follows Single Responsibility - Hotel manages rooms, HotelLocation manages location data."

### "Explain Invoice and InvoiceItem relationship"
"Composite pattern. Invoice contains multiple InvoiceItems, modeling real invoices with line items. This allows itemized billing - room charges, services, etc. More flexible than just storing a total."

### "How does UI use your models?"
"The controller acts as a bridge. It calls my model methods like room.isAvailable() to check status, room.setAvailability(false) to book, and hotel.getAllRooms() to display rooms. Models don't know about UI - they're pure business logic."

### "What would you improve?"
"Add validation, use enums for room styles, make some classes immutable, add equals/hashCode, add timestamps, implement Builder pattern for complex objects, and add more business logic like pricing rules."

---

## Technical Terms to Use (Sound Smart!)

- **Encapsulation** - Hiding internal data
- **Composition** - Has-a relationship
- **Aggregation** - Weaker form of composition
- **Domain Model** - Business logic objects
- **Value Object** - Object defined by its values
- **Separation of Concerns** - Each class has one job
- **Single Responsibility Principle** - One reason to change
- **Type Safety** - Compiler catches errors
- **Immutability** - Object can't be changed after creation

---

## Code Snippets to Memorize

### Room Availability Check
```java
if (room.isAvailable()) {
    room.setAvailability(false);
    // Room is now booked
}
```

### Adding Room to Hotel
```java
Hotel hotel = new Hotel("Grand Hotel", location);
Room room = new Room(101, "Single", amenities);
hotel.addRoom(room);
```

### Invoice Calculation
```java
Invoice invoice = new Invoice();
invoice.addItem(new InvoiceItem(150.0));
double total = invoice.getTotalAmount(); // 150.0
```

### Housekeeping Flow
```java
HouseKeeping hk = new HouseKeeping(room, "Clean");
// Guest checks out
hk.markDirty();
// After cleaning
hk.markClean();
```

---

## If You Get Stuck

### Stalling Phrases (Buy Time to Think):
- "That's a great question. Let me think about that..."
- "To answer that properly, let me explain the context first..."
- "There are actually a few ways to approach that..."
- "That relates to an important design decision I made..."

### Redirect Strategy:
If you don't know something, redirect to what you DO know:
- "I'm not sure about that specific detail, but what I can tell you is..."
- "That's outside the current scope, but related to that, I implemented..."
- "That would be an interesting extension. Currently, I focused on..."

---

## Body Language Tips

✅ **DO:**
- Stand up straight
- Make eye contact
- Speak clearly and confidently
- Use hand gestures to explain relationships
- Smile when appropriate
- Pause before answering (shows you're thinking)

❌ **DON'T:**
- Say "um" or "like" too much
- Look at the floor
- Rush through explanations
- Apologize for your code
- Say "I don't know" without trying

---

## Opening Statement (Memorize This!)

"Good morning/afternoon. I was responsible for designing the model layer of our hotel management system. The model layer consists of seven classes that represent the core business entities: Hotel, Room, HotelLocation, Invoice, InvoiceItem, HouseKeeping, and RoomKey. 

These classes follow object-oriented principles and are designed to be independent of the user interface, making them reusable and testable. The key relationships include Hotel containing multiple Rooms, Invoice containing multiple InvoiceItems, and HouseKeeping tracking the cleaning status of each Room.

I'd be happy to walk through any of these classes in detail or explain how they integrate with the rest of the system."

---

## Closing Statement

"In summary, my model layer provides a solid foundation for the hotel management system. The classes are well-encapsulated, follow design patterns, and accurately model the business domain. While there's always room for improvement, I'm confident this design meets the project requirements and demonstrates good object-oriented programming practices. Thank you."

---

## Emergency Backup Answers

### If asked something you really don't know:
"That's an interesting question that goes beyond the current implementation. In a production system, I would research best practices for [topic] and consult with the team. For this project, I focused on [what you did do], which addresses the core requirements."

### If asked about something your teammate did:
"That's actually [teammate's name]'s area - they handled the [payment/service/UI] layer. I can speak to how my model layer interfaces with their work through [specific example], but they would be better suited to explain the details of their implementation."

### If asked about a bug:
"That's a good catch. The current implementation handles [what it does handle]. To address that issue, I would [proposed solution]. It's a trade-off between [consideration A] and [consideration B], and for this project scope, we prioritized [what you prioritized]."

---

## Last-Minute Checklist

Before presentation:
- [ ] Review all 7 class purposes
- [ ] Know the relationships diagram
- [ ] Practice explaining one booking flow
- [ ] Prepare one "improvement" answer
- [ ] Review design patterns used
- [ ] Practice opening statement
- [ ] Get good sleep!

---

## Confidence Mantras

- "I understand my code"
- "I made thoughtful design decisions"
- "I can explain the why, not just the what"
- "Questions are opportunities to show knowledge"
- "I've got this!"

---

## Remember

**The professor wants to see:**
1. You understand OOP concepts ✓
2. You can explain your design decisions ✓
3. You can think critically about improvements ✓
4. You can communicate technical concepts ✓

**You have all of these! Just be confident and clear.**

Good luck! 🍀🎓
