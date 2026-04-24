import java.util.List;

public class Room {
    private int roomNumber;
    private String roomStyle;   // keep simple (can later change to enum)
    private boolean isAvailable;
    private List<String> amenities;

    public Room(int roomNumber, String roomStyle, List<String> amenities) {
        this.roomNumber = roomNumber;
        this.roomStyle = roomStyle;
        this.amenities = amenities;
        this.isAvailable = true;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getRoomStyle() {
        return roomStyle;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailability(boolean available) {
        this.isAvailable = available;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + roomStyle + ")";
    }
}