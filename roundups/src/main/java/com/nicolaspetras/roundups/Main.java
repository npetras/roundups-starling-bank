package com.nicolaspetras.roundups;

import com.nicolaspetras.roundups.service.RoundupsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    static Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * Runs the application
     * @param args
     */
    public static void main(String[] args) {
        RoundupsService appService = new RoundupsService();
        appService.runRoundupsApplication();
    }
}