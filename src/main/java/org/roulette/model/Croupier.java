package org.roulette.model;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstraction of a Croupier that controls the bets
 *
 * @author : Joao Costa (joaocarlosfilho@gmail.com) on 15/12/2016.
 */
@Slf4j
public class Croupier {

    public void execute(long waitMillis, AtomicBoolean betsEnable) {
        log.info("Starting Croupier");

        try {
            Thread.sleep(waitMillis);
        } catch (InterruptedException e) {
            log.error("Error to make thread sleep");
        }
        log.info("Croupier want to stop betting");
        betsEnable.set(false);
    }
}
