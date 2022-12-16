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

import static com.example.demo.Const.*;
import static com.example.demo.Helper.*;

//class that provides a connection to the database and interacts with it
public class DataBaseHandler{
    private Connection dbConnection;

    public DataBaseHandler(){
        createDbConnection();
    }
    private void createDbConnection() {
        Configs configs = new Configs();
        String jdbcURL = "jdbc:mysql://"
                +configs.dbHost+":"+configs.dbPort+"/"+configs.dbName+"?useSSL=false&serverTimezone=UTC";
        try {
            setDbConnection(DriverManager.getConnection(jdbcURL, configs.dbUser, configs.dbPass));
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
    private void setDbConnection(Connection connection){
        dbConnection = connection;
    }

    //adding a mark to a student by his name and group for today's date
    public void addMark(String fullname,String group, Integer mark) {
        LocalDate now = LocalDate.now(ZoneId.of(CURRENT_TIMEZONE));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",new Locale("ru"));
        String date = formatter.format(Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        try {
            if (mark == null){
                throw new SQLException();
            }
            insertMark(fullname, group, mark, date);
        } catch (SQLException e) {
            HAVE_ERROR = 1;
            System.out.println("failed");
            e.printStackTrace();
        }
    }
    //auxiliary function that returns the result from the database with the desired student
    private ResultSet getStudent (String fullname, String group){
        String select = "SELECT * " + "FROM " + USER_TABLE + " WHERE `" + USER_NAME + "`=? "
                + "AND `" + USER_GROUP + "`=?";
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(select);
            preparedStatement.setString(1, fullname);
            preparedStatement.setString(2, group);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            HAVE_ERROR = 1;
            e.printStackTrace();
        }
        return resultSet;
    }
    //function that returns the ResultSet of a table with all grades of students in the selected group
    public ResultSet getAllStudentsWithMarksByCurrentSubject (String group){
        //define a list of all student IDs in the group
        String select = "SELECT * " + "FROM " + USER_TABLE+
                " WHERE `" + USER_GROUP + "`= "+"'"+group+"'";
        StringBuilder in = new StringBuilder();
        try {
            ResultSet resultSet = getRSFromString(select);
            if(resultSet!=null) {
                resultSet.next();
                in.append("'").append(resultSet.getInt(USER_ID)).append("'");
                while (resultSet.next()) {
                    in.append(",'").append(resultSet.getInt(USER_ID)).append("'");
                }
            }
            try { if (resultSet != null) resultSet.close(); } catch (Exception ignored) {}
        } catch (SQLException e) {
            HAVE_ERROR = 1;
            e.printStackTrace();
        }
        //select all grades of students with these IDs by current subject
        select = "SELECT * " + "FROM " + MARK_TABLE+
                " WHERE `" + STUDENT_ID + "` IN ("+in+")"+
                " AND `"+ STUDENT_MARK_SUBJECT +"`='"+ TEACHER_SUBJECT+"'";
        ResultSet resultSet = null;
        SortedSet<String> sortedSet = new TreeSet<>();
        try {
            resultSet = getRSFromString(select);

            // make a separate table with all grades and names of students by date
            while (resultSet.next()){
                sortedSet.add(resultSet.getString("date"));
            }
            String addNameColumn = "ALTER TABLE "+STATISTICS_TABLE+" ADD COLUMN `name` VARCHAR (20)";
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(addNameColumn);
            preparedStatement.executeUpdate();
            for(String i : sortedSet){
                String addColumn = "ALTER TABLE "+STATISTICS_TABLE+" ADD COLUMN `"+ i +"` INT(11)";
                preparedStatement = getDbConnection().prepareStatement(addColumn);
                preparedStatement.executeUpdate();
            }
            select = "SELECT * " + "FROM " + USER_TABLE+
                    " WHERE `" + USER_GROUP + "`= "+"'"+group+"'";
            resultSet = getRSFromString(select);
            while (resultSet.next()){
                String insert = "INSERT INTO " + STATISTICS_TABLE + " (" + "`" + STATISTICS_NAME + "`)"+
                        "VALUES (?)";
                preparedStatement = getDbConnection().prepareStatement(insert);
                preparedStatement.setString(1, resultSet.getString("fullname"));
                preparedStatement.executeUpdate();
            }
            preparedStatement = getDbConnection().prepareStatement(select);
            ResultSet allFromUserTable =  preparedStatement.executeQuery();
            select = "SELECT * " + "FROM " + MARK_TABLE+
                    " WHERE `" + STUDENT_ID + "` IN ("+in+")"+
                    " AND `"+ STUDENT_MARK_SUBJECT +"`='"+ TEACHER_SUBJECT+"'";
            while (allFromUserTable.next()){//зgo to user
                ResultSet marksOfCurrentUser = getRSFromString(select+
                        " AND `" + STUDENT_ID + "`= "
                        +allFromUserTable.getString("iduser"));
                while (marksOfCurrentUser.next()){//go to user's marks
                    //adding a mark in the table with statistics
                    String update = "UPDATE "+ STATISTICS_TABLE+
                            " SET `"+marksOfCurrentUser.getString("date")+"`="
                            + marksOfCurrentUser.getInt("mark") +" "+
                            "WHERE name='"+allFromUserTable.getString("fullname")+"'";
                    preparedStatement = getDbConnection().prepareStatement(update);
                    preparedStatement.executeUpdate();
                }
                try {  marksOfCurrentUser.close(); } catch (Exception ignored) {}
            }
            try {
                preparedStatement.close();
                allFromUserTable.close();} catch (Exception ignored) {}
            //saving a table result set and returning it
            select = "SELECT * " + "FROM " + STATISTICS_TABLE;
            resultSet = getRSFromString(select);
        } catch (SQLException e) {
            HAVE_ERROR = 1;
            e.printStackTrace();
        }
        //deleting data from sql table after saving result in resultSet for next iteration
        String delete = "DELETE FROM "+ STATISTICS_TABLE;
        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(delete);
            preparedStatement.executeUpdate();
            preparedStatement = getDbConnection().prepareStatement("ALTER TABLE "+ STATISTICS_TABLE+" DROP COLUMN `name`");
            preparedStatement.executeUpdate();
            for(String i : sortedSet){
                preparedStatement = getDbConnection().prepareStatement("ALTER TABLE "+ STATISTICS_TABLE+" DROP COLUMN `"+i+"`");
                preparedStatement.executeUpdate();
            }
            try { preparedStatement.close(); } catch (Exception ignored) {}
        } catch (SQLException e) {
            HAVE_ERROR = 1;
            e.printStackTrace();
        }
        return resultSet;
    }

    //returns list of names in selected group
    public ArrayList<String> getNamesInGroup(String group){
        ArrayList<String> namesInGroup = new ArrayList<>();
        String sqlNamesInGroup = "SELECT * FROM `"+ USER_TABLE
                +"` WHERE `"+ USER_GROUP+"` = '"+group+"'";
        try {
            ResultSet resultSet = getRSFromString(sqlNamesInGroup);
            while (resultSet.next()){
                namesInGroup.add(resultSet.getString(USER_NAME));
            }
            try { resultSet.close(); } catch (Exception ignored) {}
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return namesInGroup;
    }

    //returns set of strings (a list of all existing study groups)
    public HashSet<String> getGroupsList(){
        HashSet<String> groupsList = new HashSet<>();
        String select = "SELECT * FROM "+ GROUPS_TABLE;
        try {
            ResultSet resultSet = getRSFromString(select);
            while (resultSet.next()){
                groupsList.add(resultSet.getString("groups"));
            }
            try { resultSet.close(); } catch (Exception ignored) {}
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupsList;
    }

    //boolean authorization function for any user
    public boolean checkUser(String email, String password) {
        String sql = null;
        if(ARE_TEACHER==1) {
            sql = "SELECT * FROM " + TEACHERS_TABLE + " WHERE email = '" + email + "'";
        }
        else if(ARE_TEACHER ==0) {
            sql = "SELECT * FROM " + USER_TABLE + " WHERE email = '" + email + "'";
        }
        try {
            ResultSet resultSet = getRSFromString(sql);
            if(resultSet==null) return false;
            else resultSet.next();
            if(resultSet.getString("password").equals(md5Custom(password))){
                return true;
            }
            try {resultSet.close(); } catch (Exception ignored) {}
        } catch (SQLException e) {
            System.out.println("check failed");
            return false;
        }
        return false;
    }

    public String getTeacherName(String email){
        String sql = "SELECT * FROM "+ TEACHERS_TABLE+" WHERE email = '"+email+"'";
        String name = null;
        try {
            ResultSet resultSet = getRSFromString(sql);
            if(resultSet!=null){
                resultSet.next();
                name = resultSet.getString("name");
            }
            try {if(resultSet!=null) resultSet.close(); } catch (Exception ignored) {}
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }
    public String getStudentName(String email){
        String sql = "SELECT * FROM "+ USER_TABLE+" WHERE email = '"+email+"'";
        String name = null;
        try {
            ResultSet resultSet = getRSFromString(sql);
            if(resultSet!=null) {
                resultSet.next();
                name = resultSet.getString(USER_NAME);
            }
            try {if(resultSet!=null) resultSet.close(); } catch (Exception ignored) {}
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    //returns a workbook with the student's grades in all subjects
    public Workbook getAllStudentMarksInExcel(String email){
        Workbook workbook = new XSSFWorkbook();
        //getting student
        String sql = "SELECT * FROM "+ USER_TABLE+" WHERE "+USER_MAIL+" = '"+email+"'";
        try {
            ResultSet userSet = getRSFromString(sql);
            if(userSet!=null) {
                userSet.next();
                //getting student's marks
                sql = "SELECT * FROM " + MARK_TABLE
                        + " WHERE "+STUDENT_ID+" = '" + userSet.getString(USER_ID) + "'";
            }
            PreparedStatement preparedStatement = getDbConnection()
                    .prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet marksSet = preparedStatement.executeQuery();
            //gets all the dates on which the student was graded and sort them
            SortedSet<String> dates = new TreeSet<>();
            SortedSet<String> subjects = getAllSubjects();
            while (marksSet.next()){
                dates.add(marksSet.getString("date"));
            }
            marksSet.beforeFirst();
            Sheet sheet = workbook.createSheet(STUDENT_NAME);
            Row nameRow = sheet.createRow(0);
            nameRow.createCell(0).setCellValue(STUDENT_NAME);
            Row infoRow = sheet.createRow(1);
            infoRow.createCell(0).setCellValue("subject");
            //writing sorted dates to the row
            int j = 1;
            for(String i : dates){
                infoRow.createCell(j).setCellValue(i);
                j++;
            }
            int rowNum = 2;
            for(String i : subjects){
                //creating a row for each subject
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(i);
                int count = 1;
                for (int k = 0; k < dates.size(); k++) {
                    //for each date trying to find suitable existing mark and write it to the row
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
            try {
                if(userSet!=null) userSet.close();
                marksSet.close();
                preparedStatement.close();
            } catch (Exception ignored) {}

            return workbook;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Excel file creation failed");
        return null;
    }
    //returns Excel workbook from existing sql table with statistic
    public Workbook getAllGroupMarksInExcel(String group){
        Workbook statisticsWorkbook = new XSSFWorkbook();
        Sheet sheet = statisticsWorkbook.createSheet(group);
        //ResultSet with all from statistics table
        ResultSet statByCurrentSubject =
                getAllStudentsWithMarksByCurrentSubject(group);
        ArrayList<String> columnNames = new ArrayList<>();
        //moving data from table to workbook
        try {
            Row subjectRow = sheet.createRow(0);
            subjectRow.createCell(0).setCellValue(TEACHER_SUBJECT);
            Row datesRow = sheet.createRow(1);
            ResultSetMetaData metaData = statByCurrentSubject.getMetaData();
            for (int i = 2; i <= metaData.getColumnCount(); i++) {
                String nameOfColumn = metaData.getColumnName(i);
                columnNames.add(nameOfColumn);
                datesRow.createCell(i - 2).setCellValue(nameOfColumn);
            }
            int i = 1;
            while (statByCurrentSubject.next()) {
                i++;
                Row studentMarksRow = sheet.createRow(i);
                for (int j = 0; j < columnNames.size(); j++) {
                    try {
                        if (statByCurrentSubject.getString(columnNames.get(j)) == null) {
                            studentMarksRow.createCell(j)
                                    .setCellValue("");
                        } else {
                            studentMarksRow.createCell(j)
                                    .setCellValue(statByCurrentSubject.getInt(columnNames.get(j)));
                        }
                    } catch (SQLDataException | NumberFormatException e) {
                        studentMarksRow.createCell(j)
                                .setCellValue(statByCurrentSubject.getString(columnNames.get(j)));
                    }
                }
            }
        } catch (SQLException e) {
            Helper.HAVE_ERROR = 1;
            e.printStackTrace();
        }
        return statisticsWorkbook;
    }
    //all existing subjects taught by teachers
    private TreeSet<String> getAllSubjects(){
        TreeSet<String> subjects= new TreeSet<>();
        String select = "SELECT * FROM "+ TEACHERS_TABLE;
        try {
            ResultSet allSubjects = getRSFromString(select);
            while (allSubjects.next()){
                subjects.add(allSubjects.getString("job"));
            }
            try {allSubjects.close(); } catch (Exception ignored) {}
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }
    //returns subject of the teacher(identification by email)
    public String getTeacherSubject(String email){
        String sql = "SELECT * FROM "+ TEACHERS_TABLE+" WHERE email = '"+email+"'";
        String name = null;
        try {
            ResultSet resultSet = getRSFromString(sql);
            if(resultSet!=null) {
                resultSet.next();
                name = resultSet.getString("job");
            }
            try {if (resultSet != null) resultSet.close();} catch (Exception ignored) {}
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    //add a new mark for student to database
    private void insertMark (String fullname, String group, Integer mark, String date){
        try {
            String insert = "INSERT INTO " + MARK_TABLE + " (" + "`" + STUDENT_ID + "`" + "," + "`" + STUDENT_MARK +
                    "`"+ "," + "`" + STUDENT_MARK_DATE +
                    "`,`"+ STUDENT_MARK_SUBJECT+"`"+ ") VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
            ResultSet student = getStudent(fullname, group);
            if(student!=null) {
                student.next();
                preparedStatement.setInt(1, student.getInt(USER_ID));
            }
            preparedStatement.setInt(2, mark);
            preparedStatement.setDate(3, java.sql.Date.valueOf(date));
            preparedStatement.setString(4, TEACHER_SUBJECT);
            preparedStatement.executeUpdate();
            try {if(student!=null)student.close(); } catch (Exception ignored) {}
        }
        catch (SQLException | NullPointerException e) {
            HAVE_ERROR = 1;
            e.printStackTrace();
        }
    }
    //a helper function that queries the database for the given SQL query and returns a ResultSet
    private ResultSet getRSFromString(String sql) throws SQLException{
        PreparedStatement preparedStatement = getDbConnection().prepareStatement(sql);
        return preparedStatement.executeQuery();
    }
}