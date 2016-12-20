package org.roulette.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Player betting logic
 *
 * @author : Joao Costa (joaocarlosfilho@gmail.com) on 15/12/2016.
 */
@Slf4j
public class Player {

    private String name;
    private RandomDataGenerator randomDataGenerator;

    public Player(String name) {
        this.name = name;
        randomDataGenerator = new RandomDataGenerator();
    }

    public String getName() {
        return name;
    }

    public void startBetting(AtomicBoolean betsEnable, long waitMillis, ConcurrentMap<String, List<Bet>> bets) {

        boolean willJoin = randomDataGenerator.nextInt(0, 1) == 0 ? true : false;
        int numberBets = randomDataGenerator.nextInt(1, 10);

        List<Bet> newBets = new ArrayList<>();
        if (willJoin) {
            log.info("'{}' will join with a number of bets '{}'", name, numberBets);
            for (int i = 0; i < numberBets; i++) {
                if (betsEnable.get()) {
                    int amount = randomDataGenerator.nextInt(1, 100);
                    int slot = randomDataGenerator.nextInt(0, 36);
                    log.info("'{}' adding amount '{}' slot '{}'", name, amount, slot);
                    newBets.add(new Bet(amount, slot, LocalDateTime.now()));
                    try {
                        Thread.sleep(waitMillis);
                    } catch (InterruptedException e) {
                        log.error("Error to make thread sleep");
                    }
                } else {
                    log.info("'{}' cannot place more bets", name);
                    break;
                }
            }
            bets.put(name, newBets);
            log.info("'{}' placed all bets", name);
        } else {
            log.info("'{}' will not join ", name);
        }
    }
}
