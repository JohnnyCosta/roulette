package org.roulette;

import org.roulette.game.Roulette;

/**
 * Main application bootstrap
 *
 * @author : Joao Costa (joaocarlosfilho@gmail.com) on 15/12/2016.
 */
public class Application {

    public static void main(String[] args) {

        Roulette roulette = new Roulette();
        roulette.run();
    }
}
