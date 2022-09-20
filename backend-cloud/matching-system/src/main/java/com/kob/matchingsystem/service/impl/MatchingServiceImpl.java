package com.kob.matchingsystem.service.impl;

import com.kob.matchingsystem.service.MatchingService;
import com.kob.matchingsystem.service.impl.utils.MatchingPool;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    private final static MatchingPool matchingPool = new MatchingPool();

    public static MatchingPool getMatchingPool() {
        return matchingPool;
    }

    @Override
    public String addPlayer(Integer userId, Integer rating, Integer botId) {
        System.out.println("add player: " + userId + " " + rating + " Bot: " + botId);
        matchingPool.addPlayer(userId, rating, botId);
        return "add player successfully";
    }

    @Override
    public String removePlayer(Integer userId) {
        System.out.println("remove player: " + userId);
        matchingPool.removePlayer(userId);
        return "remove player successfully";
    }
}
