import java.lang.invoke.LambdaMetafactory;
import java.sql.*;
import java.util.Set;

public class SQLUtils {

    //tries to connect to the database using the default settings
    public static Connection connect() throws java.sql.SQLException{
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo","user", "password");
        if (con != null) {
            System.out.println("Connected to the database");
        }
        return con;
    }

    //tries to connect to the database using the provided settings
    public static Connection connect(String host, int port,String db, String user, String pw) throws java.sql.SQLException{
        Connection con = DriverManager.getConnection("jdbc:mysql:"+host+":"+port+"/"+db,user, pw);
        if (con != null) {
            System.out.println("Connected to the database");
        }
        return con;
    }

    public static ResultSet runStmt(Connection con, String sql) throws java.sql.SQLException{
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        return rs;
    }

    //uses the command select all (sql) in the provided connection and table of said database
    public static ResultSet selectAll(Connection con, String table) throws java.sql.SQLException{
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
        return rs;
    }

    //same as above but with a condition
    public static ResultSet selectAll(Connection con, String table, String column, String condition) throws java.sql.SQLException{
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE "+column+" = "+condition);
        return rs;
    }

    //same as above but with multiple conditions
    public static ResultSet selectAll(Connection con, String table, String[] columns, String[] conditions, boolean simple) throws java.sql.SQLException{
        boolean first = false;
        String sql = "SELECT * FROM "+table+" WHERE ";
        if(simple) {
            for (int i = 0; i < conditions.length; i++) {
                if (conditions[i] == null || conditions[i].equals("") || conditions[i].equals("''")) continue;
                if (!first) {
                    first = true;
                    sql += columns[i] + " = " + conditions[i];
                    continue;
                }
                sql += " AND " + columns[i] + " = " + conditions[i];
            }
        } else {
            for (int i = 0; i < conditions.length; i++) {
                if (conditions[i] == null || conditions[i].equals("") || conditions[i].equals("''")) continue;
                if (!first) {
                    first = true;
                    sql += "(" + columns[i] +" "+conditions[i]+")";
                    continue;
                }
                sql += " AND " + "(" + columns[i] +" "+ conditions[i]+")";
            }
        }
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        return rs;
    }

    public static int getSize(ResultSet rs) throws java.sql.SQLException{
        int size =0;
        while(rs.next()) size++;
        return size;
    }

    //same as printResults, but gets them in a String, this assumes the table has a PK id
    public static String[][] getResults(ResultSet rs, String[] columns, int size, int amount) throws java.sql.SQLException{
        if(amount>size) throw new SQLException("There aren't enough samples for that");
        if(amount<=0) throw new SQLException("That is not a valid number!");
        size=amount;
        String[][] dataArray = new String[size+1][columns.length+1];
        int j = 1;
        dataArray[0]=columns;
        while(rs.next()) {
            String[] output = new String[columns.length];
            for(int i = 0; i<columns.length; i++) {
                output[i] = rs.getString(columns[i]);
            }
            dataArray[j]=output;
            if(j==amount) break;
            j++;
        }
        return dataArray;
    }

    //same as above but with no amount defined (aka infinite possibilities)
    public static String[][] getResults(ResultSet rs, String[] columns, int size) throws java.sql.SQLException{
        String[][] dataArray = new String[size+1][columns.length+1];
        int j = 1;
        dataArray[0]=columns;
        while(rs.next()) {
            String[] output = new String[columns.length];
            for(int i = 0; i<columns.length; i++) {
                if(i==0) {
                    output[i]=rs.getString("id");
                    continue;
                }
                output[i] = rs.getString(columns[(i-1)]);
            }
            dataArray[j]=output;
            j++;
        }
        return dataArray;
    }

    //prints to console all the data in a selected column (in selected table)
    public static void printResults(ResultSet rs, String column) throws java.sql.SQLException{
        while(rs.next()) {
            System.out.println(rs.getString(column));
        }
    }

    //prints to console all the data in the selected columns (in selected table)
    public static void printResults(ResultSet rs, String[] columns) throws java.sql.SQLException{
        while(rs.next()) {
            System.out.print("Column id, Value: "+rs.getString("id")+", ");
            for(int i = 0; i<columns.length; i++) {
                System.out.print("Column "+columns[i]+", Value: "+rs.getString(columns[i])+", ");
            }
            System.out.println();
        }
    }

    //inserts values onto database
    public static void insert(Connection con, String table, String[] columns, String[] data) throws java.sql.SQLException{
        Statement stmt = con.createStatement();
        String insert = "insert into " + table + " "
                    + " ("+convertStringArray(columns,false)+")"
                    +" values ("+convertStringArray(data,true)+")";
        stmt.executeUpdate(insert);
        System.out.println("Inserted the data");
    }

    //updates values in database
    public static void update(Connection con, String table, String column, String data, String condition) throws java.sql.SQLException{
        Statement stmt = con.createStatement();
        String update = "update " + table + " "
                + " set "+column+"='"+data+"' "
                + " where "+condition;
        stmt.executeUpdate(update);
        System.out.println("Updated data");

    }

    //same as above, but for multiple columns and one condition
    public static void update(Connection con, String table, String[] column, String data[], String condition) throws java.sql.SQLException{
        Statement stmt = con.createStatement();
        String set = "";
        boolean first = true;
        if(column.length!=data.length) throw new java.sql.SQLException("Columns and Data don't match");
        for(int i=0; i<column.length;i++) {
            if(data[i]!=(null)&&!data[i].equals("")&&!data[i].equals("''")){
                if (first) {
                    first = false;
                    set+=column[i]+" = '"+data[i]+"'";
                    continue;
                }
                set+=", "+column[i]+" = '"+data[i]+"'";
            }
        }
        String update = "update " + table + " "
                + " set " + set
                + " where " + condition;
        stmt.executeUpdate(update);
        System.out.println(update);
        System.out.println("Updated data");

    }

    //deletes values from database
    public static void delete(Connection con, String table, String condition) throws java.sql.SQLException{
        int rows = 0;
        Statement stmt = con.createStatement();
        String delete = "delete from " + table + " "
                + " where "+condition;
        rows=stmt.executeUpdate(delete);
        System.out.println("Deleted selected data ("+rows+" rows affected)");
    }

    //this is useful for converting Arrays of Strings for easier use
    public static String convertStringArray(String[] array, Boolean encase) {
        String out = "";
        if(encase) {
            for(int i = 0; i<array.length; i++) {
                if(i==array.length-1) {
                    if(!array[i].equals("")) {
                        out += "'" + array[i] + "'";
                    } else {
                        out += "NULL";
                    }
                    return out;
                }
                if(!array[i].equals("")) {
                    out += "'"+array[i]+"', ";
                } else {
                    out += "NULL, ";
                }
            }
        }
        else {
            for(int i = 0; i<array.length; i++) {
                if(i==array.length-1) {
                    if(!array[i].equals("")) {
                        out += array[i];
                    } else {
                        out += "NULL";
                    }
                    return out;
                }
                if(!array[i].equals("")) {
                    out += array[i]+", ";
                } else {
                    out += "NULL, ";
                }
            }
        }
        return out;  //this should not actually be executed
    }

}
