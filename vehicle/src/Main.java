import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Calendar;

public class Main {

    public static void main(String[] args) {
        try {
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost/datamining";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "");

            // create a sql date object so we can use it in our INSERT statement
            Calendar calendar = Calendar.getInstance();
            java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

            // the mysql insert statement
            String query = " insert into vehicle (price, mileage, make, model, trim,type,cylinders,liters,doors,cruise,sound,leather)"
                    + " values (?, ?, ?, ?, ?,?,?,?,?,?,?,?)";


            Charset charset = Charset.forName("US-ASCII");
            Path file = Paths.get("/Users/lginnali/masters/data-mining/vehicle/vehicle2.txt");
            PrintWriter writer = new PrintWriter("/Users/lginnali/masters/data-mining/vehicle/vehicle2.csv", "UTF-8");
            try {
                BufferedReader reader = Files.newBufferedReader(file, charset);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    String[] split = line.split("\t");

                    PreparedStatement preparedStmt = conn.prepareStatement(query);
                    preparedStmt.setBigDecimal(1, new BigDecimal(split[0]));
                    preparedStmt.setInt(2, Integer.parseInt(split[1]));
                    preparedStmt.setString(3, split[2]);
                    preparedStmt.setString(4, split[3]);
                    preparedStmt.setString(5, split[4]);
                    preparedStmt.setString(6, split[5]);
                    preparedStmt.setInt(7, Integer.parseInt(split[6]));
                    preparedStmt.setBigDecimal(8, new BigDecimal(split[7]));
                    preparedStmt.setInt(9, Integer.parseInt(split[8]));
                    preparedStmt.setInt(10, Integer.parseInt(split[9]));
                    preparedStmt.setInt(11, Integer.parseInt(split[10]));
                    preparedStmt.setInt(12, Integer.parseInt(split[11]));
                    // execute the preparedstatement
                    for (int i = 0; i < 12; i++) {
                        writer.print(split[i]);
                        writer.print(",");
                    }
                    writer.println();
                    preparedStmt.execute();
                    System.out.println(line);
                }
            } catch (IOException x) {
                x.printStackTrace();
                System.err.format("IOException: %s%n", x);
            }
            writer.close();
            conn.close();
        } catch (Exception e) {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }

    }
}
