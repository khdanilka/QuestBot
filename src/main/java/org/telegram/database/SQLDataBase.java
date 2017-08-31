package org.telegram.database;


import bot.pac.QuestClass;
import bot.pac.UserClass;

import java.sql.*;
import java.util.ArrayList;

public class SQLDataBase implements BDConnect {

    private Connection connection;
    private Statement statement;


    @Override
    public void addOrUpdateBd(UserClass user) {

        try {

            int i = updateDataBase(user);
            System.out.println("добавили в базу обновления = " + i);
            if (i == 0) addToDataBase(user);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ArrayList<QuestClass> getQuestListWithTag(String tag) {

        ArrayList<QuestClass> arrQ = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement("SELECT * FROM Quests where " + tag + "= ?");
            //System.out.println("SELECT * FROM Quests where " + tag + "= ?");
            ps.setString(1, "true");
            rs = ps.executeQuery();

            while (rs.next()) {
                arrQ.add(new QuestClass(rs.getInt(1), // id
                                        rs.getString(2), // name
                                        rs.getString(3), // description
                                        rs.getString(4), // photo
                                        rs.getString(5), // contacts
                                        rs.getString(6))); // location
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return arrQ;

    }

    @Override
    public void init() {
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:quest_db.db");
            statement = connection.createStatement();
        }catch (ClassNotFoundException | SQLException e){
            throw new RuntimeException(e);
        }

    }

//    @Override
//    public void createDB() {
//        try {
//            statement.execute("CREATE TABLE if not exists 'persons' " +
//                    "('id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                    "'DepCode' STRING(20), " +
//                    "'DepJob' STRING(100), " +
//                    "'Description' STRING(255));");
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("Таблица создана или уже существует.");
//    }


    @Override
    public void clearDB() {

        try {
            statement.execute("DELETE FROM 'quests' ");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void dispose()  {
        try{
            connection.close();
        } catch (SQLException e){
            throw  new RuntimeException(e);
        }

    }

    public void connectionAutoCommit(boolean b){
        try {
            connection.setAutoCommit(b);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void executeCommit(){
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserClass getUserFromBD(long user_id) {

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement("SELECT * FROM Users where user_id= ?");
            ps.setString(1, Long.toString(user_id));
            rs = ps.executeQuery();

            if (rs.next()) {
                long bdUserId = Long.valueOf(rs.getString(2));
                String bdName = rs.getString(3);
                int bdCondition = rs.getInt(5);
                String bdCategory = rs.getString(6);
                long bdMessageId = Long.valueOf(rs.getString(7));
                int bdIndex = rs.getInt(9);
                return new UserClass(bdUserId, bdName,bdCondition,bdCategory, bdMessageId, bdIndex);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int updateDataBase(UserClass user) throws SQLException {
        PreparedStatement ps = null;
        ps = connection.prepareStatement
                ("UPDATE Users SET condition = ?, category = ?, message_id = ?, index_q = ?  WHERE user_id = ?");
        ps.setInt(1, user.getCondition());
        ps.setString(2, user.getCurrentCategory());
        ps.setString(3, Long.toString(user.getMessage_id()));
        ps.setInt(4, user.getIndex());
        ps.setString(5, Long.toString(user.getUser_id()));
        return ps.executeUpdate();
    }
//
//    public void deleteFromDataBase(int primaryKey) throws SQLException{
//        PreparedStatement ps = null;
//        ps = connection.prepareStatement
//                ("DELETE FROM Persons WHERE id = ?");
//        ps.setInt(1, primaryKey);
//        ps.executeUpdate();
//    }
//
    public void addToDataBase(UserClass user) throws SQLException{
        PreparedStatement ps = null;
        ps = connection.prepareStatement
                ("INSERT INTO Users (user_id, name, lastname,condition,category,message_id,nickname, index_q) " +
                        "VALUES(?, ?, ?,?,?,?,?,?);");
        ps.setString(1, Long.toString(user.getUser_id()));
        ps.setString(2, user.getName());
        ps.setString(3, user.getLastname());
        ps.setInt(4, user.getCondition());
        ps.setString(5, user.getCurrentCategory());
        ps.setString(6, Long.toString(user.getMessage_id()));
        ps.setString(7, user.getNickname());
        ps.setInt(8, user.getIndex());
        ps.execute();
    }
//
//
//    public void addDataToDB(String title) throws SQLException {
//
//        connection.setAutoCommit(false);
//        PreparedStatement ps = connection.prepareStatement
//                ("INSERT INTO persons (DepCode, DepJob, Description) VALUES(?, ?, ?);");
//        for (int i = 1; i < 10001; i ++){
//                ps.setString(1,"12" + i);
//                ps.setString(2, title + i);
//                ps.setString(3,"всем одинаковое описание");
//                //ps.setInt(4, i);
//                ps.addBatch();
//        }
//        ps.executeBatch();
//        connection.commit();
//        connection.setAutoCommit(true);
//
//    }
//
//    public ResultSet getDataBase() throws SQLException{
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        ps = connection.prepareStatement ("SELECT * FROM Persons");
//        rs = ps.executeQuery();
//        return rs;
//    }

}
