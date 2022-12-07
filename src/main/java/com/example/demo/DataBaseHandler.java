package com.example.demo;

import javafx.beans.binding.SetBinding;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

public class DataBaseHandler extends Configs{
    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName+"?autoReconnect=true&useSSL=false";

        Class.forName("com.mysql.jdbc.Driver");

        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);
        return dbConnection;
    }
    public void addMark(String fullname,String group, Integer mark,String date) { //ALTER TABLE myschema.users AUTO_INCREMENT=0;
        try {
            if (mark == null){
                throw new SQLException();
            }
            insertMark(fullname, group, mark, date);
        } catch (SQLException e) {
            Const.HAVE_ERROR = 1;
            e.printStackTrace();
        }


    }
    private ResultSet getStudent (String fullname, String group){
            String select = "SELECT * " + "FROM " + Const.USER_TABLE + " WHERE `" + Const.USER_NAME + "`=? "
                    + "AND `" + Const.USER_GROUP + "`=?";
            ResultSet resultSet = null;
            try {
                PreparedStatement preparedStatement = getDbConnection().prepareStatement(select);
                preparedStatement.setString(1, fullname);
                preparedStatement.setString(2, group);
                resultSet = preparedStatement.executeQuery();
            } catch (SQLException | ClassNotFoundException e) {
                Const.HAVE_ERROR = 1;
                e.printStackTrace();
            }
            return resultSet;
    }
    public ResultSet getAllStudentsWithMarksByCurrentSubject (String group){
        String select = "SELECT * " + "FROM " + Const.USER_TABLE+
                " WHERE `" + Const.USER_GROUP + "`= "+"'"+group+"'";
        PreparedStatement preparedStatement = null;
        String in = "";
        try {
            preparedStatement = getDbConnection().prepareStatement(select);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            in=in+"'"+resultSet.getInt(Const.USER_ID)+"'";
            while (resultSet.next()){
                in=in+",'"+resultSet.getInt(Const.USER_ID)+"'";
            }
        } catch (SQLException | ClassNotFoundException e) {
            Const.HAVE_ERROR = 1;
            e.printStackTrace();
        }

        select = "SELECT * " + "FROM " + Const.MARK_TABLE+
                " WHERE `" + Const.STUDENT_ID + "` IN ("+in+")"+
                " AND `"+Const.STUDENT_MARK_SUBJECT +"`='"+Const.TEACHER_SUBJECT+"'";
        ResultSet resultSet = null;
        SortedSet<String> sortedSet = null;
        try {
            preparedStatement = getDbConnection().prepareStatement(select);
            resultSet = preparedStatement.executeQuery();
            sortedSet = new TreeSet<>();
            while (resultSet.next()){
                sortedSet.add(resultSet.getString("date"));
            }

            String addNameColoumn = "ALTER TABLE statistics ADD COLUMN `name` VARCHAR (20)";
            preparedStatement = getDbConnection().prepareStatement(addNameColoumn);
            preparedStatement.executeUpdate();

            for(String i : sortedSet){
                String addColoumn = "ALTER TABLE statistics ADD COLUMN `"+ i +"` INT(11)";
                preparedStatement = getDbConnection().prepareStatement(addColoumn);
                preparedStatement.executeUpdate();
            }



            select = "SELECT * " + "FROM " + Const.USER_TABLE+
                    " WHERE `" + Const.USER_GROUP + "`= "+"'"+group+"'";
            preparedStatement = getDbConnection().prepareStatement(select);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String insert = "INSERT INTO " + Const.STATISTICS_TABLE + " (" + "`" + Const.STATISTICS_NAME + "`)"+
                        "VALUES (?)";
                preparedStatement = getDbConnection().prepareStatement(insert);
                preparedStatement.setString(1, resultSet.getString("fullname"));
                preparedStatement.executeUpdate();
            }

            //селект = все из юзеров, где группа равна выбранной
            preparedStatement = getDbConnection().prepareStatement(select);
            ResultSet allFromUserTable =  preparedStatement.executeQuery();
            select = "SELECT * " + "FROM " + Const.MARK_TABLE+
                    " WHERE `" + Const.STUDENT_ID + "` IN ("+in+")"+
                    " AND `"+Const.STUDENT_MARK_SUBJECT +"`='"+Const.TEACHER_SUBJECT+"'";
            while (allFromUserTable.next()){//заходим в пользователя
                ResultSet marksOfCurrentUser = getDbConnection().prepareStatement(select+
                        " AND `" + Const.STUDENT_ID + "`= "
                        +allFromUserTable.getString("iduser")).executeQuery();
                while (marksOfCurrentUser.next()){//заходим в его оценки
                    System.out.println(marksOfCurrentUser.getInt("mark"));
                    String update = "UPDATE "+Const.STATISTICS_TABLE+
                            " SET `"+marksOfCurrentUser.getString("date")+"`="
                            + marksOfCurrentUser.getInt("mark") +" "+
                            "WHERE name='"+allFromUserTable.getString("fullname")+"'";
                    System.out.println(update);
                    preparedStatement = getDbConnection().prepareStatement(update);
                    preparedStatement.executeUpdate();
                }
                System.out.println("///");

            }
            select = "SELECT * " + "FROM " + Const.STATISTICS_TABLE;
            preparedStatement = getDbConnection().prepareStatement(select);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            Const.HAVE_ERROR = 1;
            e.printStackTrace();
        }
        String delete = "DELETE FROM "+Const.STATISTICS_TABLE;
        try {
            preparedStatement = getDbConnection().prepareStatement(delete);
            preparedStatement.executeUpdate();
            preparedStatement = getDbConnection().prepareStatement("ALTER TABLE "+Const.STATISTICS_TABLE+" DROP COLUMN `name`");
            preparedStatement.executeUpdate();
            for(String i : sortedSet){
                preparedStatement = getDbConnection().prepareStatement("ALTER TABLE "+Const.STATISTICS_TABLE+" DROP COLUMN `"+i+"`");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | ClassNotFoundException e) {
            Const.HAVE_ERROR = 1;
            e.printStackTrace();
        }
        return resultSet;
    }
    public ArrayList<String> getNamesInGroup(String group){
        ArrayList<String> namesInGroup = new ArrayList<>();
        String sqlNamesInGroup = "SELECT * FROM `"+Const.USER_TABLE
                +"` WHERE `"+Const.USER_GROUP+"` = '"+group+"'";
        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(sqlNamesInGroup);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                namesInGroup.add(resultSet.getString(Const.USER_NAME));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return namesInGroup;
    }
    public HashSet<String> getGroupsList(){
        HashSet<String> groupsList = new HashSet<>();
        String select = "SELECT * FROM "+ Const.GROUPS_TABLE;
        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(select);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                groupsList.add(resultSet.getString("groups"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return groupsList;
    }
    public boolean checkTeacher(String email,String password){
        String sql = "SELECT * FROM teachers WHERE email = '"+email+"'";
        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet==null) return false;
            resultSet.next();
            if(resultSet.getString("password").equals(password)){
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("fuck");
            return false;
        }
        return false;
    }
    public String getTeacherName(String email){
        String sql = "SELECT * FROM teachers WHERE email = '"+email+"'";
        PreparedStatement preparedStatement = null;
        String name = null;
        try {
            preparedStatement = getDbConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            name = resultSet.getString("name");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }
    public String getStudentName(String email){
        String sql = "SELECT * FROM "+Const.USER_TABLE+" WHERE email = '"+email+"'";
        PreparedStatement preparedStatement = null;
        String name = null;
        try {
            preparedStatement = getDbConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            name = resultSet.getString("fullname");
        } catch (SQLException | ClassNotFoundException e) {

            e.printStackTrace();
        }
        return name;
    }
    public String getTeacherSubject(String email){
        String sql = "SELECT * FROM teachers WHERE email = '"+email+"'";
        PreparedStatement preparedStatement = null;
        String name = null;
        try {
            preparedStatement = getDbConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            name = resultSet.getString("job");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }
    private void insertUser (String fullname, String group){
        try {
            if(fullname.equals("")){
                throw new SQLException();
            }
            String insert = "INSERT INTO " + Const.USER_TABLE + " (" + "`" + Const.USER_NAME + "`"
                    + "," + "`" + Const.USER_GROUP + "`" + ") VALUES (?,?)";
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
            preparedStatement.setString(1, fullname);
            preparedStatement.setString(2, group);
            preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            Const.HAVE_ERROR = 1;
            e.printStackTrace();
        }
    }
    private void insertMark (String fullname, String group, Integer mark, String date){
        try {
            String insert = "INSERT INTO " + Const.MARK_TABLE + " (" + "`" + Const.STUDENT_ID + "`" + "," + "`" + Const.STUDENT_MARK +
                    "`"+ "," + "`" + Const.STUDENT_MARK_DATE +
                    "`,`"+Const.STUDENT_MARK_SUBJECT+"`"+ ") VALUES (?,?,?,?)";
            ResultSet rs = getStudent(fullname, group);
            rs.next();
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
            preparedStatement.setInt(1, rs.getInt(Const.USER_ID));
            preparedStatement.setInt(2, mark);
            preparedStatement.setDate(3, java.sql.Date.valueOf(date));
            preparedStatement.setString(4,Const.TEACHER_SUBJECT);
            preparedStatement.executeUpdate();
        }
        catch (SQLException | ClassNotFoundException| NullPointerException e) {
                Const.HAVE_ERROR = 1;
                e.printStackTrace();
        }
    }

    public boolean checkStudent(String email, String password) {
        String sql = "SELECT * FROM "+Const.USER_TABLE+" WHERE email = '"+email+"'";
        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet==null) return false;
            resultSet.next();
            if(resultSet.getString("password").equals(password)){
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("fuck");
            return false;
        }
        return false;
    }
    // метод гет юзер получаем юзера точнее его имя епта и группу. Если такой уже есть то в методе выше
    //просто хуярим скип. Но оставляем часть которую надо дописать. Добавление оценки.
    // Так же дописать хуятину в базу данных айди конкретного студента, не примари кей ибо он единственнен
    // а я сделаю дохуя столбцов мол айди оценка айди оценка и буду потом собирать стату бегая по id в marks
}