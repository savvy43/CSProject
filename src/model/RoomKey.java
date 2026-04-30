package model;

public class RoomKey {

    private int keyNumber;
    private Room room;
    private boolean active;

    public RoomKey(int keyNumber, Room room) {
        this.keyNumber = keyNumber;
        this.room = room;
        this.active = true;
    }

    public void deactivate() {
        active = false;
    }

    public void activate() {
        active = true;
    }

    public boolean isActive() {
        return active;
    }

    public Room getRoom() {
        return room;
    }
}