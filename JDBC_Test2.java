import java.sql.*; // All we need for JDBC
import java.text.*;
import java.io.*;
import java.util.*;

public class JDBC_Test2 {
    Connection db;        // A connection to the database
    Statement sql;       // Our statement to run queries with
    DatabaseMetaData dbmd;      // This is basically info the driver delivers
    // about the DB it just connected to. I use
    // it to get the DB version to confirm the
    // connection in this example.
    Integer sum = 0;
    String leaf_P = "";

    public JDBC_Test2(String argv[]) throws ClassNotFoundException, SQLException{
        String database = argv[0];
        String username = argv[1];
        String password = argv[2];
        Class.forName("org.postgresql.Driver"); //load the driver
        db = DriverManager.getConnection("jdbc:postgresql://cisdb/"+database,
                username,
                password); //connect to the db
        dbmd = db.getMetaData(); //get MetaData to confirm connection
        System.out.println("Connection to "+dbmd.getDatabaseProductName()+" "+
                dbmd.getDatabaseProductVersion()+" successful.\n");

        Scanner upper_P = new Scanner(System.in);
        System.out.println("Please input the UPPER_P:");
        String UPPER_P = upper_P.nextLine(); //get the user input value

        recursion(UPPER_P, 1);
        db.close();
    }

    public void recursion(String given_p, Integer qty)
            throws ClassNotFoundException, SQLException{
        Map<String, Integer> map = new HashMap<>();
        String upper_p = given_p;
        String lower_p = "";
        Integer rec_counter = 0;
        Integer rec_qty = qty;

        sql = db.createStatement(); //create a statement that we can use later

        String sqlText = "SELECT MINOR_P, qty " +
                "FROM PART_STRUCTURE "
                +"WHERE MAJOR_P = '"+ upper_p + "' "
                +"AND MINOR_P > '"+ lower_p + "' "
                +"ORDER BY MINOR_P";
        ResultSet rs = sql.executeQuery(sqlText);

        while (true){
            if(rs.next()){
                String this_p = rs.getString(1);
                Integer next_qty = rs.getInt(2);
                rec_counter++;
                recursion(this_p, next_qty*rec_qty);
            } else {
                if(rec_counter==0){
                    sum += rec_qty;
                    map.put(upper_p, sum);
                    System.out.println(map);
                }
                return;
            }
        }
    }

    public static  void  correctUsage(){
        System.out.println("\nIncorrect number of arguments.\nUsage:\n "+
                "java   \n");
        System.exit(-1);
    }

    public static void main (String args[]){
        if (args.length != 3) correctUsage();
        try {
            JDBC_Test2 demo = new JDBC_Test2(args);
        } catch (Exception ex){
            System.out.println("***Exception:\n"+ex);
            ex.printStackTrace();
        }
    }
}

