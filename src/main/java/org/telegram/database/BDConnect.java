package org.telegram.database;

import bot.pac.QuestClass;
import bot.pac.UserClass;

import java.sql.SQLException;
import java.util.ArrayList;

public interface BDConnect {

    void init();

    void dispose();

    void clearDB();

    ArrayList<QuestClass> getQuestListWithTag(String tag);

    UserClass getUserFromBD(long user_id);

    void addOrUpdateBd(UserClass user);

    void connectionAutoCommit(boolean b);

    void executeCommit();
}
