package org.roulette.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

/**
 * Represent a player bet a value on a slot
 *
 * @author : Joao Costa (joaocarlosfilho@gmail.com) on 15/12/2016.
 */
@Data
public class Bet {
    @NonNull
    private Integer amount;
    @NonNull
    private Integer slot;
    @NonNull
    private LocalDateTime dateTime;
}
