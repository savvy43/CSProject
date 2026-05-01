# Practice Q&A Session - Rehearse These!

## Round 1: Basic Understanding

### Q: What classes did you create?
**A:** "I created seven model classes: Hotel, Room, HotelLocation, Invoice, InvoiceItem, HouseKeeping, and RoomKey. These represent the core business entities of the hotel management system."

### Q: What does the Room class do?
**A:** "The Room class represents a bookable room in the hotel. It stores the room number, room style like Single or Suite, availability status, and a list of amenities. The key functionality is tracking whether the room is available for booking through the isAvailable() method and setAvailability() method."

### Q: Why did you create a separate HotelLocation class?
**A:** "I created HotelLocation as a separate class to follow the Single Responsibility Principle. It encapsulates all location-related data and behavior. This makes the Hotel class cleaner and makes HotelLocation reusable - we could use it for guest addresses or branch locations. It also makes it easier to add location features like validation or geocoding later."

### Q: Explain the relationship between Hotel and Room.
**A:** "Hotel and Room have a one-to-many composition relationship. One Hotel contains many Rooms, represented by a List<Room> in the Hotel class. The Hotel manages its rooms through addRoom() and getAllRooms() methods. This models the real-world relationship where a hotel owns and manages multiple rooms."

### Q: What's the purpose of the Invoice class?
**A:** "Invoice represents a guest's bill. It contains a list of InvoiceItems, where each item is a charge like room fees or services. The getTotalAmount() method calculates the total by summing all items. This follows the Composite pattern and allows for itemized billing."

---

## Round 2: Design Decisions

### Q: Why use boolean for Room.available instead of String?
**A:** "I chose boolean for three reasons: First, type safety - we can only have true or false, preventing invalid states. Second, efficiency - booleans use less memory than Strings. Third, usability - we can use it directly in if statements without string comparison. If we needed more states like 'Under Maintenance', I would refactor to use an enum."

### Q: Why initialize the rooms list in the Hotel constructor?
**A:** "To prevent NullPointerException. If we don't initialize it, calling addRoom() would crash because we'd be calling add() on null. It's defensive programming - we ensure the list always exists, even if it's empty. This is a common best practice in Java."

### Q: Could you use an array instead of List for rooms?
**A:** "I could, but List is better here. Arrays have fixed size - we'd need to know the number of rooms upfront. Lists are dynamic - we can add rooms as needed. In a real hotel, rooms can be added during renovations or expansions, so List provides the flexibility we need."

### Q: Why doesn't Room have a setRoomNumber() method?
**A:** "Because room numbers shouldn't change after creation - they're immutable identifiers. Once a room is created as Room 101, it stays Room 101. Only providing a getter prevents accidental modification. This is called making the field effectively immutable, which prevents bugs and makes the code more predictable."

### Q: Why separate Invoice and InvoiceItem?
**A:** "This follows the Composite pattern and models real-world invoices. Benefits include: First, we can have multiple charges on one bill. Second, we can show itemized details to guests. Third, it's extensible - we can add descriptions, dates, or tax calculations to InvoiceItem later. Fourth, it's more flexible than just storing a single total amount."

---

## Round 3: Integration & Usage

### Q: How does the UI use your Room class?
**A:** "The controller calls my Room methods to manage bookings. For example, when displaying available rooms, it calls isAvailable() to filter. When a guest books, it calls setAvailability(false) to mark it occupied. When checking out, it calls setAvailability(true) to free it. The UI also uses getRoomNumber(), getRoomStyle(), and getAmenities() to display room information to users."

### Q: Walk me through what happens when a guest books a room.
**A:** "First, the user selects a room from the UI dropdown. The controller gets that Room object and passes it to the BookingService. The service checks if the room is available using isAvailable(). If true, it calls setAvailability(false) to mark it occupied. The controller then creates an Invoice with InvoiceItems for the charges and stores it. The room is now booked and won't appear in the available rooms list."

### Q: How does HouseKeeping relate to Room?
**A:** "HouseKeeping has a one-to-one association with Room. Each HouseKeeping object tracks the cleaning status of one specific room through a Room reference. When a guest checks out, the system finds the HouseKeeping object for that room and calls markDirty(). When housekeeping staff cleans it, they call markClean(). This separates the service aspect from the room entity itself."

### Q: Why doesn't Room know about HouseKeeping?
**A:** "This is a unidirectional association - HouseKeeping knows about Room, but not vice versa. Room doesn't need to know about its cleaning status for its core functionality. This reduces coupling and follows the principle of least knowledge. It makes Room more independent and reusable. If we needed bidirectional access, we could add it, but it's not necessary for our use cases."

### Q: How would you add a guest to a room?
**A:** "Currently, guest information is stored in the controller's guestNames map. Ideally, I would add a Guest class to the model layer with fields like name, phone, email. Then Room could have a 'private Guest currentGuest' field. When booking, we'd call room.setCurrentGuest(guest). When checking out, we'd call room.setCurrentGuest(null). This would be a cleaner design that keeps guest data in the model layer where it belongs."

---

## Round 4: Advanced Concepts

### Q: What design patterns did you use?
**A:** "I used several patterns: First, Encapsulation - all fields are private with public getters/setters. Second, Composition - Hotel contains HotelLocation and Rooms, Invoice contains InvoiceItems. Third, Value Object pattern for HotelLocation and InvoiceItem. Fourth, Domain Model pattern - all classes represent real business concepts. These patterns make the code more maintainable and follow object-oriented best practices."

### Q: What is a Value Object and where did you use it?
**A:** "A Value Object is an object defined by its values rather than identity. Two value objects with the same values are considered equal. I used this for HotelLocation - two locations with the same city and address are equivalent. Also InvoiceItem - it's just an amount, no unique identity needed. Value Objects are typically immutable and represent descriptive aspects of the domain."

### Q: How would you make HotelLocation immutable?
**A:** "I would remove the setters and only set values in the constructor. Like this: make city and address final, remove setCity() and setAddress(), and only allow setting them via the constructor. This prevents modification after creation. Immutable objects are thread-safe and prevent bugs from unexpected changes. The toString() method would still work since it only reads values."

### Q: What's the difference between composition and aggregation?
**A:** "Composition is a strong 'has-a' relationship where the contained object can't exist without the container. If Hotel is deleted, its HotelLocation is deleted too. Aggregation is weaker - the contained object can exist independently. For example, if we had a Guest class, Room would aggregate Guest - the guest exists independently of the room. In my design, Hotel-Room and Invoice-InvoiceItem are composition."

### Q: How would you handle concurrent bookings?
**A:** "Great question. Currently, if two users try to book the same room simultaneously, we could have a race condition. Solutions include: First, use synchronized methods on the booking operation. Second, implement optimistic locking with version numbers. Third, use database transactions with isolation levels. Fourth, add a 'reserved' state between available and occupied with a timeout. For this project, we assume single-user access, but I'm aware of the concurrency challenge."

---

## Round 5: Improvements & Extensions

### Q: What would you improve in your model layer?
**A:** "Several things: First, add validation - check room numbers are positive, names aren't empty. Second, use enums for room styles instead of Strings for type safety. Third, add equals() and hashCode() methods for proper object comparison. Fourth, add timestamps for booking dates and cleaning times. Fifth, implement Builder pattern for complex objects like Room. Sixth, add more business logic like pricing rules directly in the model. Seventh, make appropriate classes immutable like HotelLocation."

### Q: How would you add room pricing?
**A:** "I'd add a pricing mechanism to the model layer since it's business logic. Option 1: Add a 'private double pricePerNight' field to Room. Option 2: Create a Pricing class with a Map<String, Double> mapping room styles to prices. Option 3: Create a PricingStrategy interface for flexible pricing rules. I'd probably go with Option 2 - a Pricing class in the Hotel that maps styles to prices, making it easy to update prices without modifying Room objects."

### Q: How would you support booking dates?
**A:** "I'd create a Booking class with fields: Room room, Guest guest, LocalDate checkIn, LocalDate checkOut, BookingStatus status. This would replace the simple boolean availability in Room. Room would have a List<Booking> to track all bookings. To check availability, we'd check if any booking overlaps with the requested dates. This allows future bookings, booking history, and more complex scenarios like partial availability."

### Q: What if you needed to support multiple hotels?
**A:** "I'd create a HotelChain or HotelManagement class that contains a List<Hotel>. Each Hotel would need a unique ID. The UI would first ask which hotel, then show rooms for that hotel. The database would need a hotel_id foreign key in the rooms table. The model layer is already structured well for this - Hotel is independent, so we just need a container class to manage multiple hotels."

### Q: How would you add room photos?
**A:** "I'd add a 'private List<String> photoUrls' field to Room, storing file paths or URLs. For better organization, I could create a RoomPhoto class with fields like url, caption, isPrimary. Then Room would have 'private List<RoomPhoto> photos'. The UI would display these in a gallery. This keeps the model clean while supporting multiple photos per room."

---

## Round 6: Tricky Questions

### Q: Why is RoomKey not used in the current system?
**A:** "RoomKey is part of the complete domain model but not implemented in the current UI. It's there for extensibility - in a full system, we'd have a check-in screen that issues keys. The model is designed to be more complete than the current UI requirements. This is actually good design - the model represents the full business domain, and we implement UI features incrementally."

### Q: What's wrong with your current design?
**A:** "I wouldn't say 'wrong', but there are trade-offs. First, using String for room styles isn't type-safe - an enum would be better. Second, Room's availability is too simple - doesn't support future bookings. Third, no validation in setters - could set negative room numbers. Fourth, missing equals/hashCode - can't properly compare rooms. Fifth, no timestamps - can't track when things happened. These are acceptable for a class project but would need addressing in production."

### Q: How do you prevent someone from creating a Room with invalid data?
**A:** "Currently, there's no validation, which is a weakness. I would add validation in the constructor and setters. For example, check roomNumber > 0, roomStyle is not null or empty, amenities list is not null. I could throw IllegalArgumentException for invalid data. Even better, I could use the Builder pattern with validation, or create a RoomFactory that ensures only valid rooms are created."

### Q: What if Room needs to know which Hotel it belongs to?
**A:** "I'd add a 'private Hotel hotel' field to Room and set it in the constructor or when adding to Hotel. This creates a bidirectional association. However, this increases coupling - Room now depends on Hotel. I'd only add it if there's a clear use case, like 'room.getHotel().getName()'. For our current needs, the unidirectional relationship (Hotel knows Room) is sufficient and simpler."

### Q: How would you test your model classes?
**A:** "I'd write unit tests for each class. For Room, test: creating a room, checking initial availability is true, setAvailability changes state, getters return correct values. For Hotel, test: adding rooms, getAllRooms returns all added rooms, hotel with no rooms returns empty list. For Invoice, test: adding items, getTotalAmount sums correctly, empty invoice returns 0. I'd use JUnit and test edge cases like null values, empty lists, and boundary conditions."

---

## Round 7: Comparison Questions

### Q: Why use a class instead of just a HashMap for rooms?
**A:** "Classes provide structure, type safety, and behavior. With a HashMap, we'd have to remember which keys exist and what types the values are. With a Room class, the compiler enforces the structure. We also get methods like isAvailable() that encapsulate logic. Classes are self-documenting - you can see what a Room is by looking at the class. HashMaps are just data bags without meaning."

### Q: Could you use a database instead of these classes?
**A:** "These classes ARE the model layer - they represent the business logic. The database is for persistence. We need both. The classes define what a Room IS and what it can DO. The database stores the current state. We use DatabaseService to save/load Room objects to/from the database. Separating model from persistence follows the Separation of Concerns principle and makes the code more maintainable."

### Q: What's the difference between your model and the payment classes?
**A:** "Both are model layer, but different domains. My classes model the hotel domain - rooms, bookings, housekeeping. The payment classes model the payment domain - transactions, payment methods. This is domain-driven design - we separate different business concerns. My classes focus on 'what is a hotel', payment classes focus on 'how do we process payments'. They integrate at the service layer."

---

## Round 8: Real-World Scenarios

### Q: A guest wants to extend their stay. How would you handle that?
**A:** "Currently, we'd need to modify the Invoice to add more InvoiceItems for the additional nights. Ideally, I'd add a Booking class with checkIn and checkOut dates. To extend, we'd call booking.setCheckOutDate(newDate) and add new InvoiceItems for the extra nights. We'd also need to check if the room is available for those dates - no other booking conflicts."

### Q: How would you handle room upgrades?
**A:** "I'd add an upgrade method to the booking process. When upgrading, we'd: 1) Set the old room's availability to true, 2) Set the new room's availability to false, 3) Update the guest mapping to the new room, 4) Adjust the invoice - add a credit for unused nights in old room, add charges for new room. This requires careful state management to ensure consistency."

### Q: What if a room is damaged and needs to be taken offline?
**A:** "This reveals a limitation - we only have 'available' boolean. I'd extend this to a RoomStatus enum with values: AVAILABLE, OCCUPIED, MAINTENANCE, OUT_OF_SERVICE. Then we'd have room.setStatus(RoomStatus.MAINTENANCE). The booking system would only show AVAILABLE rooms. This is more flexible than boolean and supports real hotel operations."

---

## Practice Delivery Tips

### When explaining code:
1. Start with the purpose
2. Explain the fields
3. Explain the methods
4. Give a usage example
5. Mention design decisions

### Example:
"Let me explain the Room class. Its purpose is to represent a bookable room in the hotel. It has four fields: roomNumber for identification, roomStyle for the type like Single or Suite, available to track booking status, and amenities for features like WiFi. The key methods are isAvailable() to check status and setAvailability() to change it. For example, when booking, we call room.setAvailability(false). I chose boolean for availability because it's type-safe and efficient for our two-state requirement."

### If you blank out:
1. Take a breath
2. Repeat the question
3. Start with what you DO know
4. Build from there

### Remember:
- Speak slowly and clearly
- Use technical terms correctly
- Give concrete examples
- Show you understand the "why"
- Be confident!

---

## Final Prep Checklist

Practice saying out loud:
- [ ] Explain each class in 30 seconds
- [ ] Explain one relationship diagram
- [ ] Walk through one booking flow
- [ ] Answer "why separate classes?"
- [ ] Answer "what would you improve?"
- [ ] Answer "how does UI use your code?"

You're ready! 🎯
