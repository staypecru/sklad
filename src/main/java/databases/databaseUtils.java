package databases;

import operations.Transaction;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;


public class databaseUtils {
    public static final String DB_URL = "jdbc:h2:\\D:\\Study\\coding\\Java\\java aps\\sklad\\src\\main\\java\\databases\\"
            + "stockDatabase";
    private static final String INSERT_INTO = "INSERT INTO ";
    private static final String VALUES = " VALUES";
    private static final String SELECT_FROM = "SELECT * FROM ";
    private static final String WHERE = " WHERE ";
    private static final String AND = " AND ";

    public static void createProduct(String name) {
        name = name.toLowerCase();
        String insertRequest = INSERT_INTO + tables.PRODUCTS + " (name)" + VALUES +
                "('" + name + "')";
        createTransaction(insertRequest);
    }

    public static void createPurchase(Transaction purchase) {
        purchase.setName(purchase.getName().toLowerCase());
        java.sql.Date sqlDate = getSqlDateFromTransaction(purchase);
        String insertRequest = INSERT_INTO + tables.INCOME + " (name, cost, quantity, date, left)" + VALUES +
                "('" + purchase.getName() + "'" +
                ", " + purchase.getCost() +
                ", " + purchase.getQuantity() +
                ", '" + sqlDate + "'" +
                ", " + purchase.getQuantity() + ")";
        createTransaction(insertRequest);
    }

    public static void createDemand(Transaction demand) {
        demand.setName(demand.getName().toLowerCase());
        java.sql.Date sqlDate = getSqlDateFromTransaction(demand);
        String insertRequest = INSERT_INTO + tables.OUTCOME + " (name, cost, quantity, date)" + VALUES +
                "('" + demand.getName() + "'" +
                ", " + demand.getCost() +
                ", " + demand.getQuantity() +
                ", '" + sqlDate + "')";
        createTransaction(insertRequest);

    }

    private static java.sql.Date getSqlDateFromTransaction(Transaction transaction) {
        return getSqlDate(transaction.getDate());
    }

    private static java.sql.Date getSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }


    private static void createTransaction(String insertRequest) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            statement.execute(insertRequest);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isProductExist(String name) {
        name = name.toLowerCase();

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(SELECT_FROM + tables.PRODUCTS + WHERE + "name = '" + name + "'");

            if (isResultSetEmpty(resultSet)) {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static boolean isResultSetEmpty(ResultSet resultSet) throws SQLException {
        return !resultSet.next();
    }

    public static ArrayList<String> getProducts() {

        ArrayList<String> products = new ArrayList<>();
        String query = SELECT_FROM + tables.PRODUCTS;
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                products.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }


    public static ArrayList<Transaction> getPurchases(String name, Date date) {
        return getTransactions(name, date, tables.INCOME);
    }


    public static ArrayList<Transaction> getDemands(String name, Date date) {
        return getTransactions(name, date, tables.OUTCOME);
    }


    private static ArrayList<Transaction> getTransactions(String name, Date date, tables table) {

        String query = createGetTransactionsQuery(name, date, table);
        return getTransactionsFromDatabase(query);
    }


    private static String createGetTransactionsQuery(String name, Date date, tables table) {
        name = name.toLowerCase();
        java.sql.Date sqlDate = getSqlDate(date);
        return SELECT_FROM + table + WHERE + "name = '" + name + "'" + AND + "date <= DATE '" + sqlDate + "' ORDER BY date ASC";
//
    }

    private static ArrayList<Transaction> getTransactionsFromDatabase(String query) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                transactions.add(new Transaction(resultSet.getString("name"),
                        resultSet.getInt("cost"),
                        resultSet.getInt("quantity"),
                        resultSet.getDate("date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}
