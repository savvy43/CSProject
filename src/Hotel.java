import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private String name;
    private HotelLocation location;
    private List<Room> rooms;

    public Hotel(String name, HotelLocation location) {
        this.name = name;
        this.location = location;
        this.rooms = new ArrayList<>();
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public List<Room> getAllRooms() {
        return rooms;
    }

    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isAvailable()) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    public String getName() {
        return name;
    }

    public HotelLocation getLocation() {
        return location;
    }
}