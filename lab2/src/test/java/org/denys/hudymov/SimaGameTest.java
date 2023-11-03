package org.denys.hudymov;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimaGameTest {
    SimaGame simaGame = new SimaGame();
    @Test
    public void testAllCorner(){
        simaGame.initializeMiddle();
        simaGame.run(3, 2);
        simaGame.run(3, 1);
        simaGame.run(3, 0);
        simaGame.run(2, 0);
        simaGame.run(1, 0);
        simaGame.run(0, 0);
        simaGame.run(0, 1);
        simaGame.run(0, 2);
        simaGame.run(0, 3);
        simaGame.run(0, 4);
        simaGame.run(0, 5);
        simaGame.run(0, 6);
        simaGame.run(0, 6);
        simaGame.run(1, 6);
        simaGame.run(2, 6);
        simaGame.run(3, 6);
        simaGame.run(4, 6);
        simaGame.run(5, 6);
        simaGame.run(6, 6);
        simaGame.run(6, 5);
        simaGame.run(6, 4);
        simaGame.run(6, 3);
        simaGame.run(6, 2);
        simaGame.run(6, 1);
        simaGame.run(6, 0);
        simaGame.run(5, 0);
        simaGame.run(4, 0);
        System.out.println(simaGame.printBoard());
    }

    @Test
    public void testWinCorner(){
        simaGame.initializeMiddle();
        simaGame.run(3, 2);
        simaGame.run(3, 1);
        simaGame.run(3, 0);
        simaGame.run(2, 0);
        simaGame.run(1, 0);
        simaGame.run(0, 0);
        simaGame.run(0, 1);
        simaGame.run(0, 2);
        simaGame.run(0, 3);
        simaGame.run(1, 3);
        simaGame.run(2, 3);
        simaGame.run(2, 2);
        simaGame.run(1, 1);
        var s = simaGame.run(1, 2);
        System.out.println(s);
        System.out.println(simaGame.printBoard());
    }

    @Test
    public void testWin(){
        simaGame.initializeMiddle();
        simaGame.run(2, 2);
        simaGame.run(1, 1);
        simaGame.run(0, 0);
        simaGame.run(0, 1);
        simaGame.run(1, 2);
        simaGame.run(2, 3);
        simaGame.run(3, 4);
        simaGame.run(4, 5);
        simaGame.run(5, 4);
        simaGame.run(6, 5);
        simaGame.run(5, 6);
        var s = simaGame.run(6, 6);
        simaGame.run(1, 1);
        simaGame.run(1, 2);
        System.out.println(s);
        System.out.println(simaGame.printBoard());
    }
}