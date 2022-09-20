package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread {
    private static List<Player> players = new ArrayList<>();
    private static Set<Integer> playersId = new HashSet<>(); // To avoid player matches to self
    private final ReentrantLock lock = new ReentrantLock();

    private static RestTemplate restTemplate;
    private static final String startGameUrl = "http://127.0.0.1:3000/battle/start/game/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating) {
        lock.lock();

        try {
            if (!playersId.contains(userId)){
                players.add(new Player(userId, rating, 0));
                playersId.add(userId);
            }
        } finally{
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();

        try {
            List<Player> newPlayers = new ArrayList<>();

            for (Player player: players){
                if (!player.getUserId().equals(userId)){
                    newPlayers.add(player);
                }
            }
            players = newPlayers;
            playersId.remove(userId);
        } finally{
            lock.unlock();
        }
    }

    // All the players' waiting time increases 1 in matching pool.
    private void increaseWaitingTime() {
        for (Player player: players) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    // Check if two players could be matched.
    private boolean checkMatched(Player a, Player b) {
        int ratingDifference = Math.abs(a.getRating() - b.getRating());
        int waitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime()); // Both two players should accepts this diff.
        return ratingDifference <= waitingTime * 10;

    }

    // Send matching result
    private void sendResult(Player a, Player b) {
        System.out.println("Send result: " + a + " " + b);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id", a.getUserId().toString());
        data.add("b_id", b.getUserId().toString());
        restTemplate.postForObject(startGameUrl, data, String.class);
    }

    // Try to match all the player in matching pool.
    private void matchPlayers() {
        System.out.println("matching players: " + players.toString());
        boolean[] used = new boolean[players.size()];
        for (int i = 0; i < players.size(); i ++ ){
            if (used[i]){
                continue;
            }
            for (int j = i + 1; j < players.size(); j ++){
                if (used[j]) {
                    continue;
                }
                Player a = players.get(i);
                Player b = players.get(j);

                if (checkMatched(a, b)){
                    used[i] = used[j] = true;
                    sendResult(a, b);
                    playersId.remove(a.getUserId());
                    playersId.remove(b.getUserId());
                    break;
                }
            }
        }

        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i ++){
            if (!used[i]) {
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;

    }

    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(1000);

                lock.lock();

                try {
                    increaseWaitingTime();
                    matchPlayers();
                }
                finally{
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
