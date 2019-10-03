package databases;

import java.sql.*;
import java.util.ArrayList;

public class tablesUtils {
    private static final String START_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    private static final String FINISH_CREATE_TABLE_COUNT = "\nCOUNT(\n" +
            "  id integer NOT NULL AUTO_INCREMENT PRIMARY KEY ,\n" +
            "  name text NOT NULL,\n" +
            "  profit integer NOT NULL,\n" +
            "  date date NOT NULL,\n" +
            ");";

    private static final String FINISH_CREATE_TABLE_INCOME = "\nINCOME(\n" +
            "  id integer NOT NULL AUTO_INCREMENT PRIMARY KEY, \n" +
            "  name text NOT NULL,\n" +
            "  cost integer NOT NULL,\n" +
            "  quantity integer NOT NULL,\n" +
            "  date date NOT NULL,\n" +
            "  left integer NOT NULL,\n" +
            ");";

    private static final String FINISH_CREATE_TABLE_OUTCOME = "\nOUTCOME(\n" +
            "  id integer NOT NULL AUTO_INCREMENT PRIMARY KEY ,\n" +
            "  name text NOT NULL,\n" +
            "  cost integer NOT NULL,\n" +
            "  quantity integer NOT NULL,\n" +
            "  date date NOT NULL,\n" +
            ");";

    private static final String FINISH_CREATE_TABLE_PRODUCTS = "\nPRODUCTS(\n" +
            "  id integer NOT NULL AUTO_INCREMENT PRIMARY KEY ,\n" +
            "  name text NOT NULL\n" +
            ");";


    public static ArrayList<String> createTables() {
        ArrayList<String> tablesName = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUtils.DB_URL);
             Statement statement = connection.createStatement()) {

            createTable(statement, tables.COUNT);
            createTable(statement, tables.INCOME);
            createTable(statement, tables.OUTCOME);
            createTable(statement, tables.PRODUCTS);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tablesName;
    }


    static private void createTable(Statement statement, tables table) throws SQLException {
        String tableFinish = "";
        switch (table) {
            case COUNT:
                tableFinish = FINISH_CREATE_TABLE_COUNT;
                break;
            case INCOME:
                tableFinish = FINISH_CREATE_TABLE_INCOME;
                break;
            case OUTCOME:
                tableFinish = FINISH_CREATE_TABLE_OUTCOME;
                break;
            case PRODUCTS:
                tableFinish = FINISH_CREATE_TABLE_PRODUCTS;
                break;
        }
        statement.execute(START_CREATE_TABLE + tableFinish);
    }
}
