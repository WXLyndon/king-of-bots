package com.kob.botrunningsystem.service.impl;

import com.kob.botrunningsystem.service.BotRunningService;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {
    @Override
    public String addBot(Integer userId, String botCode, String gameInfo) {
        System.out.println("Add bot: " + userId + " " + botCode + " " + gameInfo);
        return "Add bot successfully.";
    }
}
