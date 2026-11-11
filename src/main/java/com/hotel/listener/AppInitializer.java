package com.hotel.listener;

import com.hotel.model.Admin;
import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import com.hotel.util.DataConfig;
import com.hotel.util.FileHandler;
import com.hotel.util.PasswordUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;

@WebListener
public class AppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        String dir = event.getServletContext().getInitParameter("dataDir");
        if (dir != null && !dir.trim().isEmpty()) {
            DataConfig.setDataDir(dir.trim());
        }
        try {
            FileHandler.ensureFileExists("users.txt");
            System.out.println("[Hotel] Data directory: " + DataConfig.getDataDir());
            createDefaultAdmin();
        } catch (IOException e) {
            System.out.println("[Hotel] Could not prepare data files: " + e.getMessage());
        }
    }

    // Creates one admin account the first time the app starts
    private void createDefaultAdmin() throws IOException {
        UserRepository repo = new UserRepository();
        for (User user : repo.findAll()) {
            if ("ADMIN".equals(user.getRole())) {
                return;
            }
        }
        repo.save(new Admin(null, "System Admin", "admin@hotel.com",
                PasswordUtil.hash("Admin@123"), "0770000001"));
        System.out.println("[Hotel] Default admin created: admin@hotel.com / Admin@123");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}