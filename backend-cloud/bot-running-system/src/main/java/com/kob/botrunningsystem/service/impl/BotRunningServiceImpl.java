package com.kob.botrunningsystem.service.impl;

import com.kob.botrunningsystem.service.BotRunningService;
import com.kob.botrunningsystem.service.impl.utils.BotPool;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {

    private final static BotPool botPool = new BotPool();

    public static BotPool getBotPool() {
        return BotRunningServiceImpl.botPool;
    }

    @Override
    public String addBot(Integer userId, String botCode, String gameInfo) {
        System.out.println("Add bot: " + userId + " " + botCode + " " + gameInfo);
        botPool.addBot(userId, botCode, gameInfo);
        return "Add bot successfully.";
    }
}
