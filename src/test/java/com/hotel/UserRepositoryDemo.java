package com.hotel;

import com.hotel.model.Customer;
import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import com.hotel.util.DataConfig;
import com.hotel.util.FileHandler;

import java.util.ArrayList;

public class UserRepositoryDemo {

    public static void main(String[] args) throws Exception {
        DataConfig.setDataDir(System.getProperty("java.io.tmpdir") + "/hotel-demo");
        FileHandler.writeAllLines("users.txt", new ArrayList<String>());

        UserRepository repo = new UserRepository();

        System.out.println("--- CREATE ---");
        repo.save(new Customer(null, "Kasun Perera", "kasun@gmail.com", "pass1", "0771111111"));
        repo.save(new Customer(null, "Nimali Silva", "nimali@gmail.com", "pass2", "0772222222"));
        printAll(repo);

        System.out.println("--- DUPLICATE EMAIL ---");
        try {
            repo.save(new Customer(null, "Someone Else", "KASUN@gmail.com", "x", ""));
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }

        System.out.println("--- SEARCH 'nim' ---");
        for (User u : repo.search("nim")) {
            System.out.println(u.getId() + " " + u.getFullName());
        }

        System.out.println("--- UPDATE U001 ---");
        User u1 = repo.findById("U001");
        u1.setFullName("Kasun P. Perera");
        System.out.println("Updated: " + repo.update(u1));
        printAll(repo);

        System.out.println("--- DELETE U002 ---");
        System.out.println("Deleted: " + repo.delete("U002"));
        printAll(repo);
    }

    private static void printAll(UserRepository repo) throws Exception {
        for (User u : repo.findAll()) {
            System.out.println(u.toFileString());
        }
    }
}