
package model;
import java.util.List;

public class Room {

    private int roomNumber;
    private String roomStyle;
    private boolean available;
    private List<String> amenities;

    public Room(int roomNumber, String roomStyle, List<String> amenities) {
        this.roomNumber = roomNumber;
        this.roomStyle = roomStyle;
        this.amenities = amenities;
        this.available = true;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getRoomStyle() {
        return roomStyle;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailability(boolean available) {
        this.available = available;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public String toString() {
        return "Room " + roomNumber + " (" + roomStyle + ")";
    }
}