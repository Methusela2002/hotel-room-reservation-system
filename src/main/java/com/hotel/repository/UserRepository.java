package com.hotel.repository;

import com.hotel.model.User;
import com.hotel.util.FileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static final String FILE = "users.txt";

    // Makes "find the next id, then save" one atomic action
    private static final Object WRITE_LOCK = new Object();

    // ---------- READ ----------

    public List<User> findAll() throws IOException {
        List<User> users = new ArrayList<>();
        for (String line : FileHandler.readAllLines(FILE)) {
            users.add(User.fromFileString(line));
        }
        return users;
    }

    public User findById(String id) throws IOException {
        for (User user : findAll()) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public User findByEmail(String email) throws IOException {
        for (User user : findAll()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }

    // Case-insensitive search on id, name, email and phone
    public List<User> search(String keyword) throws IOException {
        List<User> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String key = keyword.trim().toLowerCase();
        for (User user : findAll()) {
            if (user.getId().toLowerCase().contains(key)
                    || user.getFullName().toLowerCase().contains(key)
                    || user.getEmail().toLowerCase().contains(key)
                    || user.getPhone().toLowerCase().contains(key)) {
                results.add(user);
            }
        }
        return results;
    }

    // ---------- CREATE ----------

    // Generates the id, saves the user and returns it
    public User save(User user) throws IOException {
        synchronized (WRITE_LOCK) {
            validate(user);
            if (findByEmail(user.getEmail()) != null) {
                throw new IllegalArgumentException("Email is already registered");
            }
            user.setId(nextId());
            FileHandler.appendLine(FILE, user.toFileString());
            return user;
        }
    }

    // ---------- UPDATE ----------

    // Replaces the record with the same id. Returns false if the id does not exist.
    public boolean update(User updated) throws IOException {
        synchronized (WRITE_LOCK) {
            validate(updated);
            List<User> users = findAll();

            for (User u : users) {
                if (!u.getId().equals(updated.getId())
                        && u.getEmail().equalsIgnoreCase(updated.getEmail())) {
                    throw new IllegalArgumentException("Email is already used by another user");
                }
            }

            boolean found = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId().equals(updated.getId())) {
                    users.set(i, updated);
                    found = true;
                    break;
                }
            }
            if (found) {
                writeUsers(users);
            }
            return found;
        }
    }

    // ---------- DELETE ----------

    // Returns false if the id does not exist
    public boolean delete(String id) throws IOException {
        synchronized (WRITE_LOCK) {
            List<User> users = findAll();
            boolean removed = users.removeIf(u -> u.getId().equals(id));
            if (removed) {
                writeUsers(users);
            }
            return removed;
        }
    }

    // ---------- helpers ----------

    private void writeUsers(List<User> users) throws IOException {
        List<String> lines = new ArrayList<>();
        for (User user : users) {
            lines.add(user.toFileString());
        }
        FileHandler.writeAllLines(FILE, lines);
    }

    // U001, U002, ... uses highest existing number + 1
    private String nextId() throws IOException {
        int max = 0;
        for (User user : findAll()) {
            try {
                int number = Integer.parseInt(user.getId().substring(1));
                if (number > max) {
                    max = number;
                }
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                // ignore ids that do not follow the Unnn pattern
            }
        }
        return String.format("U%03d", max + 1);
    }

    // Protects the file format: '|' separates fields and a newline ends a record
    private void validate(User user) {
        if (isBlank(user.getFullName()) || isBlank(user.getEmail()) || isBlank(user.getPassword())) {
            throw new IllegalArgumentException("Name, email and password are required");
        }
        if (!user.getEmail().matches("^[^@\\s|]+@[^@\\s|]+\\.[^@\\s|]+$")) {
            throw new IllegalArgumentException("Email format is invalid");
        }
        if (user.getPhone() == null) {
            user.setPhone("");
        }
        String[] fields = {user.getFullName(), user.getEmail(), user.getPassword(), user.getPhone()};
        for (String field : fields) {
            if (field.indexOf('|') >= 0 || field.indexOf('\n') >= 0 || field.indexOf('\r') >= 0) {
                throw new IllegalArgumentException("Fields cannot contain '|' or line breaks");
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}