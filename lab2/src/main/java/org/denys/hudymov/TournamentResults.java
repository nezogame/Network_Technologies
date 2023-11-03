package org.denys.hudymov;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TournamentResults {
    private int player1Wins = 0;
    private int player2Wins = 0;

    public void increasePlayer1Wins(){
        player1Wins++;
    }
    public void increasePlayer2Wins(){
        player2Wins++;
    }
}
