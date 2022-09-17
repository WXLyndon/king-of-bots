package com.kob.backend.controller.battle;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BotInfoController {

    @RequestMapping("/battle/getbotinfo/")
    public Map<String, String> getBotInfo(){
        Map<String, String> bot = new HashMap<>();
        bot.put("name", "apple");
        bot.put("rating", "1500");
        return bot;
    }
}
