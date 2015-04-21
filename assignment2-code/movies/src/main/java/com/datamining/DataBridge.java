package com.datamining;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class DataBridge {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, ClassNotFoundException, SQLException {
	// write your code here

        String myDriver = "org.gjt.mm.mysql.Driver";
        String myUrl = "jdbc:mysql://localhost/test";
        Class.forName(myDriver);
        Connection conn = DriverManager.getConnection(myUrl, "root", "");
        String[] dataList = {"/Users/lginnali/masters/data-mining/Data-mining/movies/ml-1m/movies.txt",
                "/Users/lginnali/masters/data-mining/Data-mining/movies/ml-1m/users.txt",
                "/Users/lginnali/masters/data-mining/Data-mining/movies/ml-1m/ratings.txt"};
        // create a sql date object so we can use it in our INSERT statement
        Calendar calendar = Calendar.getInstance();
        java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

        // the mysql insert statement
        storeMovies(conn);
//        storeUsers(conn );
//        storeRatings(conn);

//        validateData(dataList);

    }

    /**
     * create table movies(
     movieId decimal(8,2),
     name varchar(100),
     year varchar(20),
     genre varchar(50)
     );
     * @param conn
     * @throws SQLException
     */
    private static void storeMovies(Connection conn) throws SQLException {
        String query = " insert into movies (movieId, name, year, genre)"
                + " values (?, ?, ?, ?)";
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/Users/lginnali/masters/data-mining/Data-mining/movies/ml-1m/movies.txt"));
            String line = null;

            while ((line = reader.readLine()) != null) {
                String[] split = line.split("::");
                for(String genre:split[2].split("\\|")){
                    PreparedStatement preparedStmt = conn.prepareStatement(query);
                    preparedStmt.setBigDecimal(1, new BigDecimal(split[0]));
                    preparedStmt.setString(2, split[1].split("\\(")[0]);
                    preparedStmt.setString(3, split[1].substring(split[1].length() - 5, split[1].length() - 1));
                    preparedStmt.setString(4, genre);
                    preparedStmt.execute();
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
            System.err.format("IOException: %s%n", x);
        }
    }


    private static void validateData(String[] dataFiles) throws SQLException {
        for (final String file : dataFiles) {
            (new Thread() {
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String line = null;

                        while ((line = reader.readLine()) != null) {
                            String[] split = line.split("::");
                            List<String> strings = Arrays.asList(split);
                            if (strings.contains("")) {
                                System.out.println(line);
                            }
                        }
                    } catch (IOException x) {
                        x.printStackTrace();
                        System.err.format("IOException: %s%n", x);
                    }
                }
            }).start();
        }
    }

    /**
     * create table ratings(
     userId decimal(8,2),
     movieId decimal(8,2),
     rating  decimal (8,2),
     time timestamp
     )
     * @param conn
     * @throws SQLException
     */
    private static void storeRatings(Connection conn) throws SQLException {
        String query = " insert into ratings (userId, movieId, rating, time)"
                + " values (?, ?, ?, ?)";
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/Users/lginnali/masters/data-mining/Data-mining/movies/ml-1m/ratings.txt"));
            String line = null;

            while ((line = reader.readLine()) != null) {
                String[] split = line.split("::");
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setBigDecimal(1, new BigDecimal(split[0]));
                preparedStmt.setString(2, split[1]);
                preparedStmt.setBigDecimal(3, new BigDecimal(split[2]));
                preparedStmt.setTimestamp(4, new Timestamp(new Long(split[3])));
                preparedStmt.execute();
            }
        } catch (IOException x) {
            x.printStackTrace();
            System.err.format("IOException: %s%n", x);
        }
    }

    /**
     * create table users(
     userId decimal (8,2),
     gender varchar(1),
     age decimal(8,2),
     occupation varchar(20),
     zipCode decimal(8,2)
     );
     * @param conn
     * @throws SQLException
     */
    private static void storeUsers(Connection conn) throws SQLException {
        String query = " insert into users (userId, gender, age, occupation,zipCode)"
                + " values (?, ?, ?, ?,?)";
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/Users/lginnali/masters/data-mining/Data-mining/movies/ml-1m/users.txt"));
            String line = null;

            while ((line = reader.readLine()) != null) {
                String[] split = line.split("::");
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setBigDecimal(1, new BigDecimal(split[0]));
                preparedStmt.setString(2, split[1]);
                preparedStmt.setBigDecimal(3, new BigDecimal(split[2]));
                preparedStmt.setString(4, split[3]);
                preparedStmt.setString(5, split[4]);
                preparedStmt.execute();
            }
        } catch (IOException x) {
            x.printStackTrace();
            System.err.format("IOException: %s%n", x);
        }
    }
}
