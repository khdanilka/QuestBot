package bot.pac;

import javax.jws.Oneway;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by android on 8/17/17.
 */
public class UserClass {

    private long user_id;
    private String name;
    private String lastname;
    private String nickname;
    private int condition;
    private String currentCategory;
    private long message_id;
    ArrayList<QuestClass> quests; //<hashcode(QuestClass),Questclass>
    private int index;
    private String lastActiveTime;
    //final Lock lock = new ReentrantLock();

    public UserClass(long user_id, String name, int condition, String currentCategory, long message_id, int index) {
        this.user_id = user_id;
        this.name = name;
        this.condition = condition;
        this.currentCategory = currentCategory;
        this.message_id = message_id;
        this.index = index;
    }

    public UserClass(long user_id, String name, String lastname, String nickname) {
        this.user_id = user_id;
        this.name = name;
        this.lastname = lastname;
        this.nickname = nickname;
    }

    public UserClass() {

    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String toString() {
        return "UserClass{" +
                "user_id=" + user_id +
                ", name='" + name + '\'' +
                ", condition=" + condition +
                ", currentCategory='" + currentCategory + '\'' +
                ", message_id=" + message_id +
                ", lastActiveTime=" + lastActiveTime +
                '}';
    }

    public void setQuestsFromArrayList(ArrayList<QuestClass> arr){

//        quests = new LinkedHashMap<>();
//        for(QuestClass q: arr){
//            quests.put(q.hashCode(),q);
//        }
        this.quests = new ArrayList<>();
        this.quests = arr;

    }

    public long getMessage_id() {
        return message_id;
    }

    public void setMessage_id(long message_id) {
        this.message_id = message_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public void setCurrentCategory(String currentCategory) {
        this.currentCategory = currentCategory;
    }


    public void setIndex(int index) {
        this.index = index;
    }

    public void setLastActiveTime(long lastActiveTime) {
        this.lastActiveTime = Long.toString(lastActiveTime);
    }


    public long getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public int getCondition() {
        return condition;
    }

    public String getCurrentCategory() {
        return currentCategory;
    }

//    public HashMap<Integer, QuestClass> getQuests() {
//        return quests;
//    }

    public int getIndex() {
        return index;
    }

    public Long getLastActiveTime() {
        return Long.valueOf(lastActiveTime);
    }


}
