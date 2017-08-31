package bot.pac;

import org.telegram.database.BDConnect;
import org.telegram.database.SQLDataBase;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Iterator;
import java.util.Map;


public class MainClass {

    static long l = System.currentTimeMillis();

    public static void main(String[] args) {
        // Initialize Api Context
        ApiContextInitializer.init();

        // Instantiate Telegram Bots API
        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Register our bot
        try {
            botsApi.registerBot(new QuestBot(new SQLDataBase()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        BDConnect bd = new SQLDataBase();



        Thread clearUsers = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("обновляется... ");

                    bd.init();
                    bd.connectionAutoCommit(false);

                    Iterator<Map.Entry<Long, UserClass>> itr1 = QuestBot.users.entrySet().iterator();
                    while (itr1.hasNext()) {
                        Map.Entry<Long, UserClass> entry = itr1.next();
                       // System.out.println(entry.getKey() + " = " + entry.getValue());
                        UserClass q = entry.getValue();
                        if (System.currentTimeMillis() - q.getLastActiveTime() > 10000) {
                            bd.addOrUpdateBd(q);
                            itr1.remove();
                            System.out.println("удалили юзера из кеша");
                        }
                    }
                    bd.executeCommit();
                    bd.connectionAutoCommit(true);
                    bd.dispose();
                }

            }
        });

        clearUsers.start();



    }
}
