public class RoomKey {
    private int keyNumber;
    private Room room;
    private boolean isActive;

    public RoomKey(int keyNumber, Room room) {
        this.keyNumber = keyNumber;
        this.room = room;
        this.isActive = true;
    }

    public void deactivateKey() {
        isActive = false;
    }

    public void activateKey() {
        isActive = true;
    }

    public boolean isActive() {
        return isActive;
    }

    public Room getRoom() {
        return room;
    }
}