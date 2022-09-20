package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> add(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        String nickname = data.get("nickname");
        String description = data.get("description");
        String code = data.get("code");

        Map<String, String> map = new HashMap<>();

        if (description == null || description.length() == 0){
            description = "No description.";
        }

        if (nickname == null || nickname.length() == 0){
            map.put("error_message", "Nickname cannot be empty.");
            return map;
        }

        if (nickname.length() > 100){
            map.put("error_message", "Nickname cannot be longer than 100 characters.");
            return map;
        }

        if (description.length() > 300){
            map.put("error_message", "Description cannot be longer than 300 characters.");
            return map;
        }

        if (code == null || code.length() == 0){
            map.put("error_message", "Code cannot be empty.");
            return map;
        }

        if (code.length() > 10000){
            map.put("error_message", "Code cannot be longer than 10000 characters.");
            return map;
        }

        Date now = new Date();
        Bot bot = new Bot(null, user.getId(), nickname, description, code, now, now);

        botMapper.insert(bot);

        map.put("error_message", "success");
        return map;
    }
}
