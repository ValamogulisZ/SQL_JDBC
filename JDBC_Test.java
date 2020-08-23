import java.sql.*; // All we need for JDBC
import java.text.*;
import java.io.*;
import java.util.Scanner;

public class JDBC_Test {
    Connection db;        // A connection to the database
    Statement sql;       // Our statement to run queries with
    DatabaseMetaData dbmd;      // This is basically info the driver delivers
                                // about the DB it just connected to. I use
                                // it to get the DB version to confirm the
                                // connection in this example.

    public JDBC_Test(String argv[]) throws ClassNotFoundException, SQLException{
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

        recursion(UPPER_P);
        db.close();
    }

    public void recursion(String given_p)
            throws ClassNotFoundException, SQLException{
        String upper_p = given_p;
        String lower_p = "";

        sql = db.createStatement(); //create a statement that we can use later

        String sqlText = "SELECT MINOR_P " +
                "FROM PART_STRUCTURE "
                +"WHERE MAJOR_P = '"+ upper_p + "' "
                +"AND MINOR_P > '"+ lower_p + "' "
                +"ORDER BY MINOR_P";
        // System.out.println("Executing this command: "+sqlText+"\n");
        ResultSet rs = sql.executeQuery(sqlText);

        while (true){
            if(rs.next()){
                String usr = rs.getString(1);
                System.out.println(usr + "");
                recursion(usr);
            } else {
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
            JDBC_Test demo = new JDBC_Test(args);
        } catch (Exception ex){
            System.out.println("***Exception:\n"+ex);
            ex.printStackTrace();
        }
    }
}
