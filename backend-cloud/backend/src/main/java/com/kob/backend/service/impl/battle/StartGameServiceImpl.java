package com.kob.backend.service.impl.battle;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.service.battle.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId) {
        System.out.println("Start game: " + aId + " " + aBotId + " " + bId + " " + bBotId);
        WebSocketServer.startGame(aId, aBotId, bId, bBotId);
        return "Start game successfully";
    }
}
