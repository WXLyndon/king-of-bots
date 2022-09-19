package com.kob.backend.consumer.utils;

import java.util.Random;

public class Game {
    private final Integer rows;
    private final Integer cols;
    private final Integer innerWallsCount;

    private final int[][] map;

    private static final int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};


    // Using flood fill method to check_connectivity
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

    public Game(Integer rows, Integer cols, Integer innerWallsCount) {
        this.rows = rows;
        this.cols = cols;
        this.innerWallsCount = innerWallsCount;
        this.map = new int[rows][cols];
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
}
