package service;

import model.Hotel;
import model.Room;

public class BookingService {

    private Hotel hotel;

    public BookingService(Hotel hotel) {
        this.hotel = hotel;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public Room getAvailableRoom() {
        for (Room room : hotel.getAllRooms()) {
            if (room.isAvailable()) {
                return room;
            }
        }
        return null;
    }

    public boolean book(Room room) {
        if (room == null || !room.isAvailable()) return false;
        room.setAvailability(false);
        return true;
    }
}