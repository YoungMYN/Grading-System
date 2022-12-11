package com.example.demo;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.Date;

public class DataBaseHandler extends Configs {
    private Connection dbConnection;

    public DataBaseHandler(){
        createDbConnection();
    }
    private void createDbConnection() {
        String jdbcURL = "jdbc:mysql://"+dbHost+":"+dbPort+"/"+dbName+"?useSSL=false&serverTimezone=UTC";
        try {
            dbConnection = DriverManager.getConnection(jdbcURL, dbUser, dbPass);
            System.out.println("success connection");
        }
        catch (SQLException e) {
            System.out.println("connection failed");
            System.out.println(e.getSQLState()+e.getMessage());
        }
    }
    private Connection getDbConnection(){
        return dbConnection;
    }
    public void addMark(String fullname,String group, Integer mark) { //ALTER TABLE myschema.users AUTO_INCREMENT=0;
        LocalDate now = LocalDate.now(ZoneId.of(Const.CURRENT_TIMEZONE));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        try {
            if (mark == null){
                throw new SQLException();
            }
            insertMark(fullname, group, mark, date);
        } catch (SQLException e) {
            Helper.HAVE_ERROR = 1;
            System.out.println("failed");
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
        } catch (SQLException e) {
            Helper.HAVE_ERROR = 1;
            e.printStackTrace();
        }
        return resultSet;
    }
    public ResultSet getAllStudentsWithMarksByCurrentSubject (String group){
        String select = "SELECT * " + "FROM " + Const.USER_TABLE+
                " WHERE `" + Const.USER_GROUP + "`= "+"'"+group+"'";
        StringBuilder in = new StringBuilder();
        try {
            ResultSet resultSet = getRSFromString(select);
            resultSet.next();
            in.append("'").append(resultSet.getInt(Const.USER_ID)).append("'");
            while (resultSet.next()){
                in.append(",'").append(resultSet.getInt(Const.USER_ID)).append("'");
            }
        } catch (SQLException e) {
            Helper.HAVE_ERROR = 1;
            e.printStackTrace();
        }

        select = "SELECT * " + "FROM " + Const.MARK_TABLE+
                " WHERE `" + Const.STUDENT_ID + "` IN ("+in+")"+
                " AND `"+Const.STUDENT_MARK_SUBJECT +"`='"+Helper.TEACHER_SUBJECT+"'";
        ResultSet resultSet = null;
        SortedSet<String> sortedSet = new TreeSet<>();
        try {
            resultSet = getRSFromString(select);
            while (resultSet.next()){
                sortedSet.add(resultSet.getString("date"));
            }
            String addNameColumn = "ALTER TABLE statistics ADD COLUMN `name` VARCHAR (20)";
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(addNameColumn);
            preparedStatement.executeUpdate();
            for(String i : sortedSet){
                String addColumn = "ALTER TABLE statistics ADD COLUMN `"+ i +"` INT(11)";
                preparedStatement = getDbConnection().prepareStatement(addColumn);
                preparedStatement.executeUpdate();
            }
            select = "SELECT * " + "FROM " + Const.USER_TABLE+
                    " WHERE `" + Const.USER_GROUP + "`= "+"'"+group+"'";
            resultSet = getRSFromString(select);
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
                    " AND `"+Const.STUDENT_MARK_SUBJECT +"`='"+Helper.TEACHER_SUBJECT+"'";
            while (allFromUserTable.next()){//заходим в пользователя
                ResultSet marksOfCurrentUser = getRSFromString(select+
                        " AND `" + Const.STUDENT_ID + "`= "
                        +allFromUserTable.getString("iduser"));
                while (marksOfCurrentUser.next()){//заходим в его оценки
                    String update = "UPDATE "+Const.STATISTICS_TABLE+
                            " SET `"+marksOfCurrentUser.getString("date")+"`="
                            + marksOfCurrentUser.getInt("mark") +" "+
                            "WHERE name='"+allFromUserTable.getString("fullname")+"'";
                    preparedStatement = getDbConnection().prepareStatement(update);
                    preparedStatement.executeUpdate();
                }
            }
            select = "SELECT * " + "FROM " + Const.STATISTICS_TABLE;
            resultSet = getRSFromString(select);
        } catch (SQLException e) {
            Helper.HAVE_ERROR = 1;
            e.printStackTrace();
        }
        String delete = "DELETE FROM "+Const.STATISTICS_TABLE;
        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(delete);
            preparedStatement.executeUpdate();
            preparedStatement = getDbConnection().prepareStatement("ALTER TABLE "+Const.STATISTICS_TABLE+" DROP COLUMN `name`");
            preparedStatement.executeUpdate();
            for(String i : sortedSet){
                preparedStatement = getDbConnection().prepareStatement("ALTER TABLE "+Const.STATISTICS_TABLE+" DROP COLUMN `"+i+"`");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            Helper.HAVE_ERROR = 1;
            e.printStackTrace();
        }
        return resultSet;
    }
    public ArrayList<String> getNamesInGroup(String group){
        ArrayList<String> namesInGroup = new ArrayList<>();
        String sqlNamesInGroup = "SELECT * FROM `"+Const.USER_TABLE
                +"` WHERE `"+Const.USER_GROUP+"` = '"+group+"'";
        try {
            ResultSet resultSet = getRSFromString(sqlNamesInGroup);
            while (resultSet.next()){
                namesInGroup.add(resultSet.getString(Const.USER_NAME));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return namesInGroup;
    }
    public HashSet<String> getGroupsList(){
        HashSet<String> groupsList = new HashSet<>();
        String select = "SELECT * FROM "+ Const.GROUPS_TABLE;
        try {
            ResultSet resultSet = getRSFromString(select);
            while (resultSet.next()){
                groupsList.add(resultSet.getString("groups"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupsList;
    }
    public boolean checkTeacher(String email,String password){
        String selectTeacher = "SELECT * FROM "+Const.TEACHERS_TABLE+" WHERE email = '"+email+"'";
        try {
            ResultSet teacher = getRSFromString(selectTeacher);
            if(teacher==null) return false;
            teacher.next();
            if(teacher.getString("password").equals(password)){
                return true;
            }
        } catch (SQLException e) {
            System.out.println("check fail");
            return false;
        }
        return false;
    }
    public String getTeacherName(String email){
        String sql = "SELECT * FROM "+Const.TEACHERS_TABLE+" WHERE email = '"+email+"'";
        String name = null;
        try {
            ResultSet resultSet = getRSFromString(sql);

            resultSet.next();
            name = resultSet.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }
    public String getStudentName(String email){
        String sql = "SELECT * FROM "+Const.USER_TABLE+" WHERE email = '"+email+"'";
        String name = null;
        try {
            ResultSet resultSet = getRSFromString(sql);
            resultSet.next();
            name = resultSet.getString("fullname");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }
    public Workbook getAllMarksInExcel(String fullname){
        Workbook workbook = new XSSFWorkbook();
        String sql = "SELECT * FROM "+Const.USER_TABLE+" WHERE fullname = '"+fullname+"'";
        try {
            ResultSet userSet = getRSFromString(sql);
            userSet.next();
            sql = "SELECT * FROM "+Const.MARK_TABLE
                    +" WHERE idstudent = '"+userSet.getString("iduser")+"'";
            PreparedStatement preparedStatement = getDbConnection()
                    .prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet marksSet = preparedStatement.executeQuery();

            SortedSet<String> dates = new TreeSet<>();
            SortedSet<String> subjects = getAllSubjects();
            while (marksSet.next()){
                dates.add(marksSet.getString("date"));
            }
            marksSet.beforeFirst();
            Sheet sheet = workbook.createSheet(Helper.STUDENT_NAME);
            Row nameRow = sheet.createRow(0);
            nameRow.createCell(0).setCellValue(Helper.STUDENT_NAME);
            Row infoRow = sheet.createRow(1);
            infoRow.createCell(0).setCellValue("subject");
            int j = 1;
            for(String i : dates){
                infoRow.createCell(j).setCellValue(i);
                j++;
            }
            int rowNum = 2;
            for(String i : subjects){
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(i);
                int count = 1;
                for (int k = 0; k < dates.size(); k++) {
                    while (marksSet.next()){
                        if(marksSet.getString("subject").equals(i)
                                & marksSet.getString("date").equals(infoRow.getCell(count).toString())){
                            row.createCell(count).setCellValue(marksSet.getInt("mark"));
                        }
                    }
                    marksSet.beforeFirst();
                    count++;
                }
                rowNum++;
            }
            return workbook;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Excel file creation failed");
        return null;
    }
    private TreeSet<String> getAllSubjects(){
        TreeSet<String> subjects= new TreeSet<>();
        String select = "SELECT * FROM "+Const.TEACHERS_TABLE;
        try {
            ResultSet allSubjects = getRSFromString(select);
            while (allSubjects.next()){
                subjects.add(allSubjects.getString("job"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }
    public String getTeacherSubject(String email){
        String sql = "SELECT * FROM "+Const.TEACHERS_TABLE+" WHERE email = '"+email+"'";
        String name = null;
        try {
            ResultSet resultSet = getRSFromString(sql);
            resultSet.next();
            name = resultSet.getString("job");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    private void insertMark (String fullname, String group, Integer mark, String date){
        try {
            String insert = "INSERT INTO " + Const.MARK_TABLE + " (" + "`" + Const.STUDENT_ID + "`" + "," + "`" + Const.STUDENT_MARK +
                    "`"+ "," + "`" + Const.STUDENT_MARK_DATE +
                    "`,`"+Const.STUDENT_MARK_SUBJECT+"`"+ ") VALUES (?,?,?,?)";
            ResultSet student = getStudent(fullname, group);
            student.next();
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
            preparedStatement.setInt(1, student.getInt(Const.USER_ID));
            preparedStatement.setInt(2, mark);
            preparedStatement.setDate(3, java.sql.Date.valueOf(date));
            preparedStatement.setString(4,Helper.TEACHER_SUBJECT);
            preparedStatement.executeUpdate();
        }
        catch (SQLException | NullPointerException e) {
            Helper.HAVE_ERROR = 1;
            e.printStackTrace();
        }
    }

    public boolean checkStudent(String email, String password) {
        String sql = "SELECT * FROM "+Const.USER_TABLE+" WHERE email = '"+email+"'";
        try {
            ResultSet resultSet = getRSFromString(sql);
            if(resultSet==null) return false;
            resultSet.next();
            if(resultSet.getString("password").equals(password)){
                return true;
            }
        } catch (SQLException e) {
            System.out.println("check failed");
            return false;
        }
        return false;
    }
    private ResultSet getRSFromString(String sql) throws SQLException{
        PreparedStatement preparedStatement = getDbConnection().prepareStatement(sql);
        return preparedStatement.executeQuery();
    }
    // метод гет юзер получаем юзера точнее его имя епта и группу. Если такой уже есть то в методе выше
    //просто хуярим скип. Но оставляем часть которую надо дописать. Добавление оценки.
    // Так же дописать хуятину в базу данных айди конкретного студента, не примари кей ибо он единственнен
    // а я сделаю дохуя столбцов мол айди оценка айди оценка и буду потом собирать стату бегая по id в marks
}