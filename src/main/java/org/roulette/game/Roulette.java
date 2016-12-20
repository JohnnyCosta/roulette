package org.roulette.game;

import lombok.extern.slf4j.Slf4j;
import org.roulette.model.Bet;
import org.roulette.model.Croupier;
import org.roulette.model.Player;
import org.roulette.model.Wheel;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Roulette game logic
 *
 * @author : Joao Costa (joaocarlosfilho@gmail.com) on 15/12/2016.
 */
@Slf4j
public class Roulette {

    public static final String BYE = "Bye";
    public static final String DO_YOU_WANT_TO_PLAY_ANOTHER_ROUND_Y_N = "Do you want to play another round? (y/n)";
    public static final String WELCOME_TO_THE_ROULETTE_GAME = "Welcome to the roulette game";
    public static final String HOW_MANY_PLAYERS_WILL_HAVE_THIS_GAME = "How many players will have this game?";
    public static final String INVALID_NUMBER_OF_PLAYERS_ONLY_A_MAXIMUM_OF_5_PLAYERS_CAN_JOIN_THE_GAME = "Invalid number of players. Only a maximum of 5 players can join the game.";
    public static final String PLAYER = "Player ";
    public static final String STARTING_A_NEW_SPIN_ROUND = "Starting a new spin round";
    public static final String NO_WINNERS_ON_THIS_GAME = "No winners on this game!!";
    public static final String BET_IS_IGNORED = "Bet is ignored: {}";
    public static final String WON_A_BET_OF = "'{}' won a bet of '{}'";
    public static final String Y = "y";
    public static final String N = "n";
    public static final String INVALID_ANSWER = "Invalid answer.";
    public static final int CROUPIER_MILLIS = 20000;
    public static final int WAIT_BETS_MILLIS = 5000;
    public static final String INVALID_INPUT_USING_DEFAULT_VALUE = "Invalid input '{}', using default value '{}'";
    public static final String BET = "'{}' placed bets: '{}'";
    public static final String ERROR_TO_WAIT_THREAD_FINISH = "Error to wait thread finish";

    private Scanner input;

    public Roulette() {
        input = new Scanner(System.in);
    }

    /**
     * Main game loop
     */
    public void run() {
        log.info(WELCOME_TO_THE_ROULETTE_GAME);

        log.info(HOW_MANY_PLAYERS_WILL_HAVE_THIS_GAME);
        Integer numPlayers = getIntFromInput(-1);
        while (numPlayers < 1 || numPlayers > 10) {
            log.error(INVALID_NUMBER_OF_PLAYERS_ONLY_A_MAXIMUM_OF_5_PLAYERS_CAN_JOIN_THE_GAME);
            log.info(HOW_MANY_PLAYERS_WILL_HAVE_THIS_GAME);
            numPlayers = getIntFromInput(-1);
        }

        final List<Player> players = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player(PLAYER + (i + 1)));
        }

        final Croupier croupier = new Croupier();

        final ConcurrentMap<String, List<Bet>> bets = new ConcurrentHashMap<>();

        final AtomicBoolean betsEnable = new AtomicBoolean(true);

        final Wheel wheel = new Wheel();

        // GAME MAIN LOOP
        do {
            log.info(STARTING_A_NEW_SPIN_ROUND);

            bets.clear();
            betsEnable.set(true);

            wheel.startSpin();

            List<Thread> playerThreads = new ArrayList<Thread>();
            for (Player player : players) {
                Thread thread = new Thread(() -> {
                    player.startBetting(betsEnable, WAIT_BETS_MILLIS, bets);
                });
                thread.start();
                playerThreads.add(thread);
            }

            Thread croupierThread = new Thread(() -> {
                croupier.execute(CROUPIER_MILLIS, betsEnable);
            });
            croupierThread.start();
            try {
                croupierThread.join();
            } catch (InterruptedException e) {
                log.error(ERROR_TO_WAIT_THREAD_FINISH);
            }

            playerThreads
                    .forEach(thread -> {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            log.error(ERROR_TO_WAIT_THREAD_FINISH);
                        }
                    });

            wheel.stopSpin();
            checkWins(bets, wheel);
        }
        while (checkYesNoQuestion(DO_YOU_WANT_TO_PLAY_ANOTHER_ROUND_Y_N));
        log.info(BYE);
    }

    private void checkWins(ConcurrentMap<String, List<Bet>> bets, Wheel wheel) {
        final AtomicBoolean hasWinner = new AtomicBoolean(false);

        bets.entrySet()
                .forEach(entry -> {
                    log.info(BET, entry.getKey(), entry.getValue());
                    for (Bet bet : entry.getValue()) {
                        if (bet.getSlot() == wheel.getSlot()) {
                            if (bet.getDateTime().isBefore(wheel.getFinishedSpin())) {
                                log.info(WON_A_BET_OF, entry.getKey(), bet.getAmount());
                                hasWinner.set(true);
                            } else {
                                log.error(BET_IS_IGNORED, bet);
                            }
                        }
                    }
                });

        if (!hasWinner.get()) {
            log.info(NO_WINNERS_ON_THIS_GAME);
        }
    }

    private int getIntFromInput(int def) {
        try {
            return input.nextInt();
        } catch (InputMismatchException e) {
            log.error(INVALID_INPUT_USING_DEFAULT_VALUE, input.next(), def);
            return def;
        }
    }

    private String getStringFromInput() {
        return input.next();
    }

    private boolean checkYesNoQuestion(String msg) {
        log.info(msg);
        String answer = getStringFromInput();
        while (!Y.equals(answer) && !N.equals(answer)) {
            log.error(INVALID_ANSWER);
            log.info(msg);
            answer = getStringFromInput();
        }
        if (Y.equals(answer)) {
            return true;
        } else if (N.equals(answer)) {
            return false;

        } else {
            log.error(INVALID_ANSWER);
            return true;
        }
    }
}
