package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer botId; // -1: play in person
    private String botCode;
    private Integer sx;
    private Integer sy;
    private List<Integer> steps;

    // Check if the tails of the snakes are increasing in the current turn.
    private boolean checkTailIncreasing(int step) {
        if (step <= 10) {
            return true;
        }

        // After the 10th turn, the tail will be increasing for each 3 turns.
        return step % 3 == 1;
    }

    public List<Cell> getCells() {
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};

        int x = sx;
        int y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for (int d : steps) {
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            // Delete the snake's tail when tail is not increasing.
            if (!checkTailIncreasing(++step)) {
                res.remove(0);
            }
        }
        return res;
    }

    public String getStepsString(){
        StringBuilder res = new StringBuilder();

        for (int d: steps){
            res.append(d);
        }
        return res.toString();
    }
}
