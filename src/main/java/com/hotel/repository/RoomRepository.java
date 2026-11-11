package com.hotel.repository;

import com.hotel.model.Room;
import com.hotel.util.FileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomRepository {

    private static final String FILE = "rooms.txt";

    private static final Object WRITE_LOCK = new Object();

    // ---------- READ ----------

    public List<Room> findAll() throws IOException {
        List<Room> rooms = new ArrayList<>();
        for (String line : FileHandler.readAllLines(FILE)) {
            rooms.add(Room.fromFileString(line));
        }
        return rooms;
    }

    public Room findById(String id) throws IOException {
        for (Room room : findAll()) {
            if (room.getId().equals(id)) {
                return room;
            }
        }
        return null;
    }

    public Room findByRoomNumber(String roomNumber) throws IOException {
        for (Room room : findAll()) {
            if (room.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                return room;
            }
        }
        return null;
    }

    // Keyword matches room number, type or description.
    // type is STANDARD / DELUXE / SUITE, or empty for all types.
    public List<Room> search(String keyword, String type) throws IOException {
        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        String wantedType = type == null ? "" : type.trim().toUpperCase();

        List<Room> results = new ArrayList<>();
        for (Room room : findAll()) {
            if (!wantedType.isEmpty() && !room.getType().equals(wantedType)) {
                continue;
            }
            if (key.isEmpty()
                    || room.getRoomNumber().toLowerCase().contains(key)
                    || room.getType().toLowerCase().contains(key)
                    || room.getDescription().toLowerCase().contains(key)) {
                results.add(room);
            }
        }
        return results;
    }

    // Rooms that are in service, of the wanted type (empty = any) and large enough.
    // Step 11 will also exclude rooms that already have a booking for the dates.
    public List<Room> findAvailable(String type, int guests) throws IOException {
        String wantedType = type == null ? "" : type.trim().toUpperCase();

        List<Room> results = new ArrayList<>();
        for (Room room : findAll()) {
            if (!room.isInService()) {
                continue;
            }
            if (!wantedType.isEmpty() && !room.getType().equals(wantedType)) {
                continue;
            }
            if (room.getCapacity() < guests) {
                continue;
            }
            results.add(room);
        }
        return results;
    }

    // ---------- CREATE ----------

    public Room save(Room room) throws IOException {
        synchronized (WRITE_LOCK) {
            validate(room);
            if (findByRoomNumber(room.getRoomNumber()) != null) {
                throw new IllegalArgumentException("Room number " + room.getRoomNumber() + " already exists");
            }
            room.setId(nextId());
            FileHandler.appendLine(FILE, room.toFileString());
            return room;
        }
    }

    // ---------- UPDATE ----------

    // Replaces the record with the same id. Returns false if the id does not exist.
    public boolean update(Room updated) throws IOException {
        synchronized (WRITE_LOCK) {
            validate(updated);
            List<Room> rooms = findAll();

            for (Room r : rooms) {
                if (!r.getId().equals(updated.getId())
                        && r.getRoomNumber().equalsIgnoreCase(updated.getRoomNumber())) {
                    throw new IllegalArgumentException("Room number " + updated.getRoomNumber() + " already exists");
                }
            }

            boolean found = false;
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).getId().equals(updated.getId())) {
                    rooms.set(i, updated);
                    found = true;
                    break;
                }
            }
            if (found) {
                writeRooms(rooms);
            }
            return found;
        }
    }

    // ---------- DELETE ----------

    public boolean delete(String id) throws IOException {
        synchronized (WRITE_LOCK) {
            List<Room> rooms = findAll();
            boolean removed = rooms.removeIf(r -> r.getId().equals(id));
            if (removed) {
                writeRooms(rooms);
            }
            return removed;
        }
    }

    // ---------- helpers ----------

    private void writeRooms(List<Room> rooms) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Room room : rooms) {
            lines.add(room.toFileString());
        }
        FileHandler.writeAllLines(FILE, lines);
    }

    // R001, R002, ... uses highest existing number + 1
    private String nextId() throws IOException {
        int max = 0;
        for (Room room : findAll()) {
            try {
                int number = Integer.parseInt(room.getId().substring(1));
                if (number > max) {
                    max = number;
                }
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                // ignore ids that do not follow the Rnnn pattern
            }
        }
        return String.format("R%03d", max + 1);
    }

    private void validate(Room room) {
        if (room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Room number is required");
        }
        room.setRoomNumber(room.getRoomNumber().trim());

        if (room.getPricePerNight() <= 0) {
            throw new IllegalArgumentException("Price per night must be greater than 0");
        }
        if (room.getCapacity() < 1 || room.getCapacity() > 10) {
            throw new IllegalArgumentException("Capacity must be between 1 and 10");
        }
        if (room.getDescription() == null) {
            room.setDescription("");
        }
        room.setDescription(room.getDescription().trim());

        String[] fields = {room.getRoomNumber(), room.getDescription()};
        for (String field : fields) {
            if (field.indexOf('|') >= 0 || field.indexOf('\n') >= 0 || field.indexOf('\r') >= 0) {
                throw new IllegalArgumentException("Fields cannot contain '|' or line breaks");
            }
        }
    }
}