package populatedb;

import datetime.MyDateTime;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/*
SET IP AS AUTHORISED IN GC BEFORE USING
 */
public class WriteToGcMySql {

    private static final String URL_AND_PATH = "jdbc:mysql://35.246.59.212/attendance";
    private static Connection connection = null;
    private static Statement statement = null;
    private static ResultSet resultSet = null;
    private static PreparedStatement preparedStatement = null;

    public static void main(String[] args) {
//        System.out.println("populateRandomChallengeTable(): " + populateRandomChallengeTable());
//
//        System.out.println("populateStudentTable(): " + populateStudentTable());
//
//        System.out.println("populateScheduledLectureTable(): " + populateScheduledLectureTable(new MyDateTime().convertStringDateToDate("08/04/2019")));
//
//        System.out.println("populateStudentScheduledLectureTable(): " + populateStudentScheduledLectureTable(new MyDateTime().convertStringDateToDate("08/04/2019")));

        System.out.println(insertOneScheduledLecture());
//        System.out.println(insertOneStudentScheduledLecture());
    }

    public static boolean populateStudentTable() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL_AND_PATH, System.getenv("USERNAME"),System.getenv("PASSWORD"));

//            String sqlString = "INSERT INTO student(student_id, first_name, last_name, imei, password_hash, auth_token, firebase_device_id) VALUES('c15248569'," +
//                    "'John', 'Samsung', '355589062194587', '$2a$09$C2zj.u/Q9iixuNJNXbLUguXGWkviM2HOiA1tXovEy6Lisb0OacPmu', '3agjbea2qpntb7ji4td2lbk2k6'," +
//                    "'eQgsmhJf8do:APA91bHKyBEChRRT9-SHzkRXDQTGGrZo3_eyPljgN5wwoqVqB4pZRBe-tGbUR7Og2O_J9blWjt-79T4SsjTAMBy43b6R_Lz63IYRXnewmTHzGOX0HE6HbzwdYlSQE54E2u8a5c2sFYcL')";
//            preparedStatement = connection.prepareStatement(sqlString);
//            preparedStatement.executeUpdate();
//            sqlString = "INSERT INTO student(student_id, first_name, last_name, imei, password_hash, auth_token, firebase_device_id) VALUES('d16123425', 'Adam', 'Sony'," +
//                    "'358354082252783', '$2a$09$C2zj.u/Q9iixuNJNXbLUguXGWkviM2HOiA1tXovEy6Lisb0OacPmu', '8aolsi0h5f5pjd8fq9168mni0o'," +
//                    "'e3cf27xwnNU:APA91bGXNuFMwqxTy0VND_5kDODA_hjVmrWppFxKDWjO2eCXvTt4JxjeKitYC9jnUF7Rmo5sQuSImypVopybRZEqaklt70chlWWcf888V3Yxxu6CADNRQGfjX2Vqksjn9005jI8ZmT30')";
//            preparedStatement = connection.prepareStatement(sqlString);
//            preparedStatement.executeUpdate();
            String sqlString = "INSERT INTO student(student_id, first_name, last_name, imei, password_hash) VALUES('c15248569'," +
                    "'John', 'Samsung', '355589062194587', '$2a$09$C2zj.u/Q9iixuNJNXbLUguXGWkviM2HOiA1tXovEy6Lisb0OacPmu')";
            preparedStatement = connection.prepareStatement(sqlString);
            preparedStatement.executeUpdate();
            sqlString = "INSERT INTO student(student_id, first_name, last_name, imei, password_hash) VALUES('d16123425', 'Adam', 'Sony'," +
                    "'358354082252783', '$2a$09$C2zj.u/Q9iixuNJNXbLUguXGWkviM2HOiA1tXovEy6Lisb0OacPmu')";
            preparedStatement = connection.prepareStatement(sqlString);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(preparedStatement != null)preparedStatement.close();
                if(connection != null)connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean populateRandomChallengeTable() {
        MyDateTime myDateTime = new MyDateTime();
        Date startDate = myDateTime.convertStringDateToDate("01/09/2018");
        Date endDate = myDateTime.convertStringDateToDate("31/05/2019");
        ArrayList<String> classStartTimes = myDateTime.getClassStartTimes("9", "23");
        // LinkedHashMap<String, ArrayList<String>> weekdayDatesAndCollegeHours = myDateTime.getWeekdayDatesAndCollegeHours(startDate, endDate, classStartTimes); // for Mon to Fri
        LinkedHashMap<String, ArrayList<String>> weekDatesAndCollegeHours = myDateTime.getWeekDatesAndCollegeHours(startDate, endDate, classStartTimes); // for Mon to Sun

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL_AND_PATH, System.getenv("USERNAME"),System.getenv("PASSWORD"));
            for(Map.Entry<String, ArrayList<String>> entry : weekDatesAndCollegeHours.entrySet()) {
                for(String classStartTime : entry.getValue()) {
                    try {
                        String sqlString = "insert into challenge(date, hour, random_number) " +
                                "values('" + entry.getKey() + "', '" + classStartTime + "', " + ThreadLocalRandom.current().nextInt(1000000000, 2000000000) + ")";
                        preparedStatement = connection.prepareStatement(sqlString);
                        preparedStatement.executeUpdate();
                    } catch(SQLIntegrityConstraintViolationException u) {
                        u.printStackTrace();
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(preparedStatement != null)preparedStatement.close();
                if(connection != null)connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean populateScheduledLectureTable(Date startDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(startDate);
        endCal.add(Calendar.DAY_OF_MONTH, 7);

        do {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL_AND_PATH, System.getenv("USERNAME"),System.getenv("PASSWORD"));
                // Class 09:00
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String occurringOn = simpleDateFormat.format(startCal.getTime());
                String sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                        "challenge_random_number) VALUES('1001', '" + occurringOn + "', '09:00:00', '10:00:00', 'dt228/4', 'cmpu4001'," +
                        "(SELECT random_number FROM challenge WHERE date = '" + occurringOn + "' AND hour = '09:00:00'))";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();

                // Class 10:00
                sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                        "challenge_random_number) VALUES('1001', '" + occurringOn + "', '10:00:00', '11:00:00', 'dt228/4', 'cmpu4001'," +
                        "(SELECT random_number FROM challenge WHERE date = '" + occurringOn + "' AND hour = '10:00:00'))";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();

                // Class 11:00
                sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                        "challenge_random_number) VALUES('1001', '" + occurringOn + "', '11:00:00', '12:00:00', 'dt228/4', 'cmpu4002'," +
                        "(SELECT random_number FROM challenge WHERE date = '" + occurringOn + "' AND hour = '11:00:00'))";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();

                // Class 12:00
                sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                        "challenge_random_number) VALUES('1001', '" + occurringOn + "', '12:00:00', '13:00:00', 'dt228/4', 'cmpu4003'," +
                        "(SELECT random_number FROM challenge WHERE date = '" + occurringOn + "' AND hour = '12:00:00'))";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();

                // Class 14:00
                sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                        "challenge_random_number) VALUES('1001', '" + occurringOn + "', '14:00:00', '15:00:00', 'dt228/4', 'cmpu4004'," +
                        "(SELECT random_number FROM challenge WHERE date = '" + occurringOn + "' AND hour = '14:00:00'))";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();

                // Class 15:00
                sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                        "challenge_random_number) VALUES('1001', '" + occurringOn + "', '15:00:00', '16:00:00', 'dt228/4', 'cmpu4004'," +
                        "(SELECT random_number FROM challenge WHERE date = '" + occurringOn + "' AND hour = '15:00:00'))";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();

                // Class 16:00
                sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                        "challenge_random_number) VALUES('1001', '" + occurringOn + "', '16:00:00', '17:00:00', 'dt228/4', 'cmpu4005'," +
                        "(SELECT random_number FROM challenge WHERE date = '" + occurringOn + "' AND hour = '16:00:00'))";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();

                // Class 17:00
                sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                        "challenge_random_number) VALUES('1001', '" + occurringOn + "', '17:00:00', '18:00:00', 'dt228/4', 'cmpu4006'," +
                        "(SELECT random_number FROM challenge WHERE date = '" + occurringOn + "' AND hour = '17:00:00'))";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();

                // Class 18:00
                sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                        "challenge_random_number) VALUES('1001', '" + occurringOn + "', '18:00:00', '19:00:00', 'dt228/4', 'cmpu4007'," +
                        "(SELECT random_number FROM challenge WHERE date = '" + occurringOn + "' AND hour = '18:00:00'))";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();

                // Class 19:00
                sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                        "challenge_random_number) VALUES('1001', '" + occurringOn + "', '19:00:00', '20:00:00', 'dt228/4', 'cmpu4008'," +
                        "(SELECT random_number FROM challenge WHERE date = '" + occurringOn + "' AND hour = '19:00:00'))";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();

                // Class 20:00
                sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                        "challenge_random_number) VALUES('1001', '" + occurringOn + "', '20:00:00', '21:00:00', 'dt228/4', 'cmpu4009'," +
                        "(SELECT random_number FROM challenge WHERE date = '" + occurringOn + "' AND hour = '20:00:00'))";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();

                // Class 21:00
                sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                        "challenge_random_number) VALUES('1001', '" + occurringOn + "', '21:00:00', '22:00:00', 'dt228/4', 'cmpu4010'," +
                        "(SELECT random_number FROM challenge WHERE date = '" + occurringOn + "' AND hour = '21:00:00'))";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();

                // Class 22:00
                sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                        "challenge_random_number) VALUES('1001', '" + occurringOn + "', '22:00:00', '23:00:00', 'dt228/4', 'cmpu4011'," +
                        "(SELECT random_number FROM challenge WHERE date = '" + occurringOn + "' AND hour = '22:00:00'))";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();

                // Class 23:00
                sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                        "challenge_random_number) VALUES('1001', '" + occurringOn + "', '23:00:00', '00:00:00', 'dt228/4', 'cmpu4012'," +
                        "(SELECT random_number FROM challenge WHERE date = '" + occurringOn + "' AND hour = '23:00:00'))";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if(preparedStatement != null)preparedStatement.close();
                    if(connection != null)connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            startCal.add(Calendar.DAY_OF_MONTH, 1);
        } while(startCal.getTimeInMillis() < endCal.getTimeInMillis());
        return true;
    }

    public static boolean populateStudentScheduledLectureTable(Date startDate) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL_AND_PATH, System.getenv("USERNAME"),System.getenv("PASSWORD"));

            for(int i = 298; i <= 395; i++) {
//                String sqlString = "INSERT INTO student_scheduled_lecture(student_id, scheduled_lecture_id)" +
//                        "VALUES('c15248569', " + i + ")";
//                preparedStatement = connection.prepareStatement(sqlString);
//                preparedStatement.executeUpdate();
//                sqlString = "INSERT INTO student_scheduled_lecture(student_id, scheduled_lecture_id)" +
//                        "VALUES('d16123425', " + i + ")";
//                preparedStatement = connection.prepareStatement(sqlString);
//                preparedStatement.executeUpdate();
//                String sqlString = "INSERT INTO student_scheduled_lecture(student_id, scheduled_lecture_id)" +
//                        "VALUES('c15248569', " + i + ")";
//                preparedStatement = connection.prepareStatement(sqlString);
//                preparedStatement.executeUpdate();
                String sqlString = "INSERT INTO student_scheduled_lecture(student_id, scheduled_lecture_id)" +
                        "VALUES('c15248569', " + i + ")";
                preparedStatement = connection.prepareStatement(sqlString);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(preparedStatement != null)preparedStatement.close();
                if(connection != null)connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }





    public static boolean insertOneScheduledLecture() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL_AND_PATH, System.getenv("USERNAME"),System.getenv("PASSWORD"));

            String sqlString = "INSERT INTO scheduled_lecture(room_id, occurring_on, starts_at, ends_at, course_code, module_code," +
                    "challenge_random_number) VALUES('1002', '2019-04-11', '15:00:00', '15:59:59', 'dt228/4', 'cmpu4005'," +
                    "(SELECT random_number FROM challenge WHERE date = '2019-04-11' AND hour = '15:00:00'))";
            preparedStatement = connection.prepareStatement(sqlString);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(preparedStatement != null)preparedStatement.close();
                if(connection != null)connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean insertOneStudentScheduledLecture() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL_AND_PATH, System.getenv("USERNAME"),System.getenv("PASSWORD"));

            String sqlString = "INSERT INTO student_scheduled_lecture(student_id, scheduled_lecture_id)" +
                    "VALUES('c15248569', 396)";
            preparedStatement = connection.prepareStatement(sqlString);
            preparedStatement.executeUpdate();
//            sqlString = "INSERT INTO student_scheduled_lecture(student_id, scheduled_lecture_id)" +
//                    "VALUES('d16123425', 46)";
//            preparedStatement = connection.prepareStatement(sqlString);
//            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(preparedStatement != null)preparedStatement.close();
                if(connection != null)connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }



}
