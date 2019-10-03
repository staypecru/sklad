package databases;

import operations.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class databaseUtilsTest {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String testName = "testproduct";

    @Test
    public void createProduct() {

        databaseUtils.createProduct(testName);

        ArrayList<String> products = getProducts();

        Assert.assertEquals("Product was created", testName, products.get(products.size() - 1));

        deleteProduct(testName);

        products = getProducts();

        Assert.assertEquals("Product was deleted", false, products.contains(testName));

    }


    private ArrayList<String> getProducts() {
        ArrayList<String> products = new ArrayList<>();
        String query = "SELECT * FROM " + tables.PRODUCTS;
        try (Connection connection = DriverManager.getConnection(databaseUtils.DB_URL);
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

    private void deleteProduct(String testName) {
        String query = "DELETE FROM " + tables.PRODUCTS + " WHERE name = '" + testName + "'";
        deleteFromDatabase(query);
    }

    private void deleteFromDatabase(String query) {
        try (Connection connection = DriverManager.getConnection(databaseUtils.DB_URL);
             Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void createPurchase() {
        Transaction purchase = new Transaction("testname", 1000, 10, new Date());

        databaseUtils.createPurchase(purchase);

        ArrayList<Transaction> purchases = databaseUtils.getPurchases(purchase.getName(), purchase.getDate());

        Transaction purchaseFromDatabase = parseTransactionFromDatabase(purchases);

        Assert.assertEquals("Purchase was created", purchase, purchaseFromDatabase);

        deletePurchase(purchase);

        purchases = databaseUtils.getPurchases(purchase.getName(), purchase.getDate());

        Assert.assertFalse("Purchase was deleted", purchases.contains(purchase));

    }

    private Transaction parseTransactionFromDatabase(ArrayList<Transaction> purchases) {
        Transaction purchaseFromDatabase = purchases.get(purchases.size() - 1);

        changeDateFormat(purchaseFromDatabase);
        return purchaseFromDatabase;
    }

    private void changeDateFormat(Transaction transaction) {
        try {
            String st = transaction.getDate().toString();
            Date date = dateFormat.parse(st);
            transaction.setDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void deletePurchase(Transaction purchase) {
        String query = "DELETE FROM " + tables.INCOME + " WHERE name = '" + purchase.getName() + "'";
        deleteFromDatabase(query);
    }


    @Test
    public void createDemand() {

        Transaction demand = new Transaction("testname", 1000, 10, new Date());

        databaseUtils.createDemand(demand);

        ArrayList<Transaction> demands = databaseUtils.getDemands(demand.getName(), demand.getDate());

        Transaction testDemand = parseTransactionFromDatabase(demands);

        Assert.assertEquals("Demand was created", demand, testDemand);

        deleteDemand(demand);

        demands = databaseUtils.getDemands(demand.getName(), demand.getDate());

        Assert.assertFalse("Demand was deleted", demands.contains(demand));

    }

    private void deleteDemand(Transaction demand) {
        String query = "DELETE FROM " + tables.OUTCOME + " WHERE name = '" + demand.getName() + "'";
        deleteFromDatabase(query);
    }

    @Test
    public void isProductExist() {

        Assert.assertFalse("check before creation", databaseUtils.isProductExist(testName));

        databaseUtils.createProduct(testName);

        Assert.assertTrue("check after creation", databaseUtils.isProductExist(testName));

        deleteProduct(testName);

        Assert.assertFalse("Product was deleted", databaseUtils.isProductExist(testName));
    }
}