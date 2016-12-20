package org.roulette.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.time.LocalDateTime;

/**
 * Wheel abstraction that creates a new random between 0 and 36 each spin
 *
 * @author : Joao Costa (joaocarlosfilho@gmail.com) on 15/12/2016.
 */
@Slf4j
public class Wheel {

    public static final String WHEEL_STARTED_TO_SPIN = "Wheel started to spin";
    public static final String WHEEL_STOP_ON_SLOT = "Wheel stop on slot '{}'";
    private int slot;
    private RandomDataGenerator randomDataGenerator;
    private LocalDateTime finishedSpin;
    private boolean spinning;

    public Wheel() {
        slot = -1;
        randomDataGenerator = new RandomDataGenerator();
        finishedSpin = null;
        spinning = false;
    }

    public int getSlot() {
        return slot;
    }

    public LocalDateTime getFinishedSpin() {
        return finishedSpin;
    }

    public boolean isSpinning() {
        return spinning;
    }

    public void startSpin() {
        slot = -1;
        finishedSpin = null;
        spinning = true;
        log.info(WHEEL_STARTED_TO_SPIN);
    }

    public void stopSpin() {
        finishedSpin = LocalDateTime.now();
        spinning = false;
        slot = randomDataGenerator.nextInt(0, 36);
        log.info(WHEEL_STOP_ON_SLOT, slot);
    }
}
