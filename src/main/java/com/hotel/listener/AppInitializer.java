package com.hotel.listener;

import com.hotel.util.DataConfig;
import com.hotel.util.FileHandler;

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
        } catch (IOException e) {
            System.out.println("[Hotel] Could not prepare data files: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}