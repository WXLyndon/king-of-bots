package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread {
    private final static ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Queue<Bot> bots = new LinkedList<>();

    public void addBot(Integer userId, String botCode, String gameInfo){
        lock.lock();

        try{
            bots.add(new Bot(userId, botCode, gameInfo));
        }
        finally {
            lock.unlock();
        }
    }

    private void consume(Bot bot) {

    }

    @Override
    public void run() {

        while (true) {
            lock.lock();

            if (bots.isEmpty()) {
                try {
                    condition.await(); // block the thread
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            } else {
                Bot bot = bots.remove();
                lock.unlock();
                consume(bot); // Time consuming might be long, so executes this after lock released.
            }
        }

    }
}
