package com.hotel;

import com.hotel.model.DeluxeRoom;
import com.hotel.model.Room;
import com.hotel.model.StandardRoom;
import com.hotel.model.SuiteRoom;
import com.hotel.repository.RoomRepository;
import com.hotel.util.DataConfig;
import com.hotel.util.FileHandler;

import java.util.ArrayList;

public class RoomRepositoryDemo {

    public static void main(String[] args) throws Exception {
        DataConfig.setDataDir(System.getProperty("java.io.tmpdir") + "/hotel-demo");
        FileHandler.writeAllLines("rooms.txt", new ArrayList<String>());

        RoomRepository repo = new RoomRepository();

        System.out.println("--- CREATE ---");
        repo.save(new StandardRoom(null, "101", 10000, 2, true, "Standard room with garden view"));
        repo.save(new DeluxeRoom(null, "201", 10000, 3, true, "Deluxe room with balcony"));
        repo.save(new SuiteRoom(null, "301", 10000, 4, true, "Suite with living area"));
        printAll(repo);

        System.out.println("--- POLYMORPHISM: total price for 3 nights ---");
        for (Room room : repo.findAll()) {
            System.out.println(room.getType() + " -> " + room.calculateTotalPrice(3));
        }

        System.out.println("--- DUPLICATE ROOM NUMBER ---");
        try {
            repo.save(new StandardRoom(null, "101", 9000, 2, true, ""));
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }

        System.out.println("--- INVALID PRICE ---");
        try {
            repo.save(new StandardRoom(null, "102", -5, 2, true, ""));
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }

        System.out.println("--- SEARCH type=DELUXE ---");
        for (Room r : repo.search("", "DELUXE")) {
            System.out.println(r.getId() + " " + r.getRoomNumber());
        }

        System.out.println("--- UPDATE R001 (price 12000, maintenance) ---");
        Room r1 = repo.findById("R001");
        r1.setPricePerNight(12000);
        r1.setInService(false);
        System.out.println("Updated: " + repo.update(r1));
        printAll(repo);

        System.out.println("--- DELETE R002 ---");
        System.out.println("Deleted: " + repo.delete("R002"));
        printAll(repo);
    }

    private static void printAll(RoomRepository repo) throws Exception {
        for (Room r : repo.findAll()) {
            System.out.println(r.toFileString());
        }
    }
}