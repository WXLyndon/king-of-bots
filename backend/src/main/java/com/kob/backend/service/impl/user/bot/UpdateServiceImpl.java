package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {

    @Autowired
    BotMapper botMapper;

    @Override
    public Map<String, String> update(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int botId = Integer.parseInt(data.get("bot_id"));

        String nickname = data.get("nickname");
        String description = data.get("description");
        String code = data.get("code");

        Map<String, String> map = new HashMap<>();

        if (description == null || description.length() == 0) {
            description = "No description.";
        }

        if (nickname == null || nickname.length() == 0) {
            map.put("error_message", "Nickname cannot be empty.");
            return map;
        }

        if (nickname.length() > 100) {
            map.put("error_message", "Nickname cannot be longer than 100 characters.");
            return map;
        }

        if (description.length() > 300) {
            map.put("error_message", "Description cannot be longer than 300 characters.");
            return map;
        }

        if (code == null || code.length() == 0) {
            map.put("error_message", "Code cannot be empty.");
            return map;
        }

        if (code.length() > 10000) {
            map.put("error_message", "Code cannot be longer than 10000 characters.");
            return map;
        }

        Bot bot = botMapper.selectById(botId);

        if (bot == null) {
            map.put("error_message", "Bot does not exist.");
            return map;
        }

        if (!bot.getUserId().equals(user.getId())) {
            map.put("error_message", "This bot does not belong to you, so you do not have right to update it.");
            return map;
        }

        Bot newBot = new Bot(bot.getId(),
                user.getId(),
                nickname,
                description,
                code,
                bot.getRating(),
                bot.getCreateTime(),
                new Date());

        botMapper.updateById(newBot);

        map.put("error_message", "success");

        return map;
    }
}
