package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {
    private final Integer rows;
    private final Integer cols;
    private final Integer innerWallsCount;

    private final int[][] map;

    private static final int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    private final Player playerA, playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing"; // playing -> finished
    private String loser = ""; // "all": tie; "A": A loses; "B": B loses.

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    public Game(Integer rows, Integer cols, Integer innerWallsCount, Integer idA, Integer idB) {
        this.rows = rows;
        this.cols = cols;
        this.innerWallsCount = innerWallsCount;
        this.map = new int[rows][cols];
        playerA = new Player(idA, rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, 1, cols - 2, new ArrayList<>());
    }


    // Using flood fill method to check connectivity.
    private boolean checkConnectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) {
            return true;
        }

        map[sx][sy] = 1;

        for (int i = 0; i < 4; i++) {
            int x = sx + dx[i];
            int y = sy + dy[i];

            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && map[x][y] == 0) {
                if (checkConnectivity(x, y, tx, ty)) {
                    map[sx][sy] = 0;
                    return true;
                }
            }

        }
        map[sx][sy] = 0;
        return false;
    }

    public int[][] getMap() {
        return map;
    }

    private boolean drawMap() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                map[i][j] = 0;
            }
        }

        // Add walls to the edges of the map
        for (int r = 0; r < this.rows; r++) {
            map[r][0] = map[r][this.cols - 1] = 1;
        }
        for (int c = 0; c < this.cols; c++) {
            map[0][c] = map[this.rows - 1][c] = 1;
        }

        // Create inner walls randomly.
        Random random = new Random();
        for (int i = 0; i < this.innerWallsCount / 2; i++) {
            for (int j = 0; j < 1000; j++) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if (map[r][c] == 1 || map[this.rows - 1 - r][this.cols - 1 - c] == 1) {
                    continue;
                }

                // Initial locations for both snakes.
                if ((r == this.rows - 2 && c == 1) || (c == this.cols - 2 && r == 1)) {
                    continue;
                }

                map[r][c] = map[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return checkConnectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        for (int i = 0; i < 1000; i++) {
            if (drawMap()) {
                break;
            }
        }
    }

    // Waiting for two users' next operations
    private boolean nextStep() {
        /* In frontend, snakes move 5 blocks per second, so the backend needs to wait at least 0.2 second
         to record the next step.  */
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Waiting for at most 5 seconds to get the players' next operations.
        for (int i = 0; i < 25; i++) {
            try {
                Thread.sleep(200);

                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean checkValid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);

        if (map[cell.x][cell.y] == 1) {
            return false;
        }

        for (int i = 0; i < n - 1; i++) {
            if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y) {
                return false;
            }
        }

        for (int i = 0; i <= n - 1; i++) {
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y) {
                return false;
            }
        }

        return true;
    }

    // Check if the next operations of two players are valid.
    private void judge() {
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = checkValid(cellsA, cellsB);
        boolean validB = checkValid(cellsB, cellsA);

        if (!validA || !validB){
            status = "finished";

            if (!validA && !validB){
                loser = "all";
            } else if (!validA){
                loser = "A";
            } else {
                loser = "B";
            }

        }

    }

    // Send message to two clients.
    public void sendAllMessage(String message) {
        WebSocketServer.getUsers().get(playerA.getId()).sendMessage(message);
        WebSocketServer.getUsers().get(playerB.getId()).sendMessage(message);
    }

    // Send the next moves of both snakes to two clients.
    private void sendMove() {
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }
    }

    private String getMapString(){
        StringBuilder res = new StringBuilder();

        for (int i = 0; i < rows; i ++){
            for (int j = 0; j < cols; j ++){
                res.append(map[i][j]);
            }
        }

        return res.toString();
    }

    private void saveToDatabase(){
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );

        WebSocketServer.getRecordMapper().insert(record);
    }


    // Send result to two clients
    private void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run() {

        // 13 rows, 14 cols, 13 * 14 < 1000 (May coule use while (true) instead)
        for (int i = 0; i < 1000; i++) {
            if (nextStep()) {
                judge();

                if (status.equals("playing")) {
                    sendMove();
                } else {
                    sendResult();
                    break;
                }

            } else {
                status = "finished";

                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else if (nextStepA == null) {
                        loser = "A";
                    } else if (nextStepB == null) {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
