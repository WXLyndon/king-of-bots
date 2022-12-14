package com.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{token}")  // Do not end with '/'
public class WebSocketServer {

    private final static int rows = 13;
    private final static int cols = 14;
    private final static int innerWallsCount = 20;
    private final static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    private User user;
    private Session session = null;

    private static UserMapper userMapper;
    private static RecordMapper recordMapper;
    private static BotMapper botMapper;

    private static RestTemplate restTemplate;

    private Game game = null;
    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";


    public static RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public Game getGame() {
        return game;
    }

    public static UserMapper getUserMapper() {
        return WebSocketServer.userMapper;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    public static RecordMapper getRecordMapper() {
        return recordMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    public void setBotMapper(BotMapper botMapper) {
        WebSocketServer.botMapper = botMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        WebSocketServer.restTemplate = restTemplate;
    }

    public static ConcurrentHashMap<Integer, WebSocketServer> getUsers() {
        return users;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // Construct connection
        this.session = session;
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);

        if (this.user != null) {
            users.put(userId, this);
        } else {
            this.session.close();
        }

        System.out.println(users);
        System.out.println("Connected!");
    }

    @OnClose
    public void onClose() {
        // Close connection
        System.out.println("Disconnected!");
        if (this.user != null) {
            users.remove(this.user.getId());
        }
    }

    public static void startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId){
        User a = userMapper.selectById(aId);
        User b = userMapper.selectById(bId);

        Bot botA = botMapper.selectById(aBotId);
        Bot botB = botMapper.selectById(bBotId);

        Game game = new Game(rows,
                cols,
                innerWallsCount,
                a.getId(),
                botA,
                b.getId(),
                botB);

        game.createMap();

        // To avoid the error if either user's client is closed.
        if (users.get(a.getId()) != null) {
            users.get(a.getId()).game = game;
        }

        if (users.get(b.getId()) != null) {
            users.get(b.getId()).game = game;
        }

        game.start(); // Start a new game thread

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());

        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getMap());


        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", respGame);

        // To avoid the error if either user's client is closed.
        if (users.get(a.getId()) != null) {
            users.get(a.getId()).sendMessage(respA.toJSONString());
        }

        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame);

        // To avoid the error if either user's client is closed.
        if (users.get(b.getId()) != null) {
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }


    }

    private void startMatching(Integer botId) {
        System.out.println("Start Matching!");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        data.add("bot_id", botId.toString());
        restTemplate.postForObject(addPlayerUrl, data, String.class);

    }

    private void stopMatching() {
        System.out.println("Stop Matching!");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }

    private void move(int direction) {
        if (game.getPlayerA().getId().equals(user.getId())){
            if (game.getPlayerA().getBotId().equals(-1)) { // Only accepts frontend input when player plays in person
                game.setNextStepA(direction);
            }
        } else if (game.getPlayerB().getId().equals(user.getId())) {
            if (game.getPlayerB().getBotId().equals(-1)) { // Only accepts frontend input when player plays in person
                game.setNextStepB(direction);
            }
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) { // Just like router
        // Get message from clients
        System.out.println("Message received!");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");

        if ("start-matching".equals(event)) {
            startMatching(data.getInteger("bot_id"));
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}