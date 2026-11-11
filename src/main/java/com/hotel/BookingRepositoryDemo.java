package com.hotel;

import com.hotel.model.Booking;
import com.hotel.repository.BookingRepository;
import com.hotel.util.DataConfig;
import com.hotel.util.FileHandler;

import java.time.LocalDate;
import java.util.ArrayList;

public class BookingRepositoryDemo {

    public static void main(String[] args) throws Exception {
        DataConfig.setDataDir(System.getProperty("java.io.tmpdir") + "/hotel-demo-bookings");
        FileHandler.writeAllLines("bookings.txt", new ArrayList<String>());

        BookingRepository repo = new BookingRepository();
        LocalDate d = LocalDate.of(2030, 1, 10);

        System.out.println("--- CREATE: R001, 10 Jan to 13 Jan ---");
        Booking first = repo.save(new Booking(null, "U002", "R001", d, d.plusDays(3), 2, 25500, null));
        System.out.println(first.toFileString());

        System.out.println("--- OVERLAP TESTS on R001 ---");
        attempt(repo, "inside   11-12 Jan", d.plusDays(1), d.plusDays(2));
        attempt(repo, "starts before 9-11 Jan", d.minusDays(1), d.plusDays(1));
        attempt(repo, "ends after 12-15 Jan", d.plusDays(2), d.plusDays(5));
        attempt(repo, "back-to-back 13-15 Jan", d.plusDays(3), d.plusDays(5));
        attempt(repo, "ends on check-in 8-10 Jan", d.minusDays(2), d);

        System.out.println("--- BOOKED ROOM IDS for 11-12 Jan ---");
        System.out.println(repo.findBookedRoomIds(d.plusDays(1), d.plusDays(2)));

        System.out.println("--- ALL ---");
        for (Booking b : repo.findAll()) {
            System.out.println(b.toFileString());
        }
    }

    private static void attempt(BookingRepository repo, String label, LocalDate in, LocalDate out) throws Exception {
        try {
            Booking b = repo.save(new Booking(null, "U003", "R001", in, out, 2, 1000, null));
            System.out.println(label + " -> ACCEPTED as " + b.getId());
        } catch (IllegalArgumentException e) {
            System.out.println(label + " -> REJECTED: " + e.getMessage());
        }
    }
}
