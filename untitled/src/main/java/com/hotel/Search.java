package com.hotel;
import java.time.Duration;
import java.time.LocalDate;


public interface Search {
    public void searchRoom(RoomStyle style, LocalDate startDate, Duration duration);

}
