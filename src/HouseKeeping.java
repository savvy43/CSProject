public class HouseKeeping {
    private Room room;
    private String status; // e.g., "Clean", "Dirty", "In Progress"

    public HouseKeeping(Room room, String status) {
        this.room = room;
        this.status = status;
    }

    public void markClean() {
        status = "Clean";
    }

    public void markDirty() {
        status = "Dirty";
    }

    public String getStatus() {
        return status;
    }

    public Room getRoom() {
        return room;
    }
}