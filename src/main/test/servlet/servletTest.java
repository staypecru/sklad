package servlet;

import databases.databaseUtils;
import databases.tables;
import operations.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import servlet.utils.responses;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class servletTest extends Mockito {

    private final String newProductTrueQuery = "{name:'testproduct'}";
    private final String newProductFalseQuery = "{nam:'testproduct'}";

    private final String purchaseTrueQuery = "{name: 'testproduct',cost:'500',quantity:'5',date:'10.02.19'}";
    private final String purchaseProductDoesNotExistQuery = "{name: 'testproductERROR',cost:'500',quantity:'5',date:'10.02.19'}";
    private final String purchaseWrongParametersQuery = "{name: 'testproduct',costERROR:'500',quantity:'5',date:'10.02.19'}";
    private final String purchaseCostBelowZeroQuery = "{name: 'testproduct',cost:'-500',quantity:'5',date:'10.02.19'}";
    private final String purchaseQuantityBelowZeroQuery = "{name: 'testproduct',cost:'500',quantity:'-5',date:'10.02.19'}";

    private final String demandTrueQuery = "{name: 'testproduct',cost:'2000',quantity:'3',date:'10.03.19'}";
    private final String demandProductDoesNotExistQuery = "{name: 'testproductERROR',cost:'2000',quantity:'3',date:'10.03.19'}";
    private final String demandWrongParametersQuery = "{name: 'testproduct',costERROR:'2000',quantity:'3',date:'10.03.19'}";
    private final String demandCostBelowZeroQuery = "{name: 'testproduct',cost:'-2000',quantity:'3',date:'10.03.19'}";
    private final String demandQuantityBelowZeroQuery = "{name: 'testproduct',cost:'2000',quantity:'-3',date:'10.03.19'}";


    private HttpServletRequest req;
    private HttpServletResponse resp;
    private StringWriter stringWriter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Before
    public void initialize() throws IOException, ServletException {
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);

        stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(resp.getWriter()).thenReturn(writer);
    }

    @Test
    public void isTablesExist() throws SQLException, ServletException, IOException {
        new initializeServlet().doGet(req, resp);

        try (Connection connection = DriverManager.getConnection(databaseUtils.DB_URL)) {
            ArrayList<String> tables = new ArrayList<>();
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
            Assert.assertTrue(tables.contains(databases.tables.COUNT.toString()));
            Assert.assertTrue(tables.contains(databases.tables.INCOME.toString()));
            Assert.assertTrue(tables.contains(databases.tables.OUTCOME.toString()));
            Assert.assertTrue(tables.contains(databases.tables.PRODUCTS.toString()));
        }
    }

    @Test
    public void newProductTrueQuery() throws IOException, ServletException {

        setGetReaderFunction(newProductTrueQuery);

        new newProductServlet().doPost(req, resp);

        ArrayList<String> products = databaseUtils.getProducts();
        Assert.assertEquals("Product was created", "testproduct", products.get(products.size() - 1));

    }

    private void setGetReaderFunction(String query) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(query));
        when(req.getReader()).thenReturn(bufferedReader);
    }


    @Test
    public void newProductFalseQuery() throws IOException, ServletException {

        setGetReaderFunction(newProductFalseQuery);

        new newProductServlet().doPost(req, resp);

        Assert.assertTrue("Bad input parameters check: ", stringWriter.toString().contains(responses.BAD_INPUT_PARAMETERS_RESPONSE));

    }

    @Test
    public void purchaseTrueQuery() throws IOException, ServletException, ParseException {

        setGetReaderFunction(purchaseTrueQuery);

        new purchaseServlet().doPost(req, resp);

        ArrayList<Transaction> purchases = databaseUtils.getPurchases("testproduct", dateFormat.parse("2019-02-10"));

        Transaction purchase = parseTransactionFromDatabase(purchases);
        Transaction testPurchase = new Transaction("testproduct", 500, 5, dateFormat.parse("2019-02-10"));

        Assert.assertEquals("Purchase was created", testPurchase, purchase);
    }

    private Transaction parseTransactionFromDatabase(ArrayList<Transaction> purchases) throws ParseException {
        Transaction purchaseFromDatabase = purchases.get(purchases.size() - 1);
        changeDateFormat(purchaseFromDatabase);
        return purchaseFromDatabase;
    }

    private void changeDateFormat(Transaction transaction) throws ParseException {
        String st = transaction.getDate().toString();
        Date date = dateFormat.parse(st);
        transaction.setDate(date);
    }

    @Test
    public void purchaseProductDoesNotExistQuery() throws IOException, ServletException, ParseException {

        setGetReaderFunction(purchaseProductDoesNotExistQuery);

        new purchaseServlet().doPost(req, resp);

        Assert.assertTrue("Product does not exist check: ", stringWriter.toString().contains(responses.PRODUCT_DOES_NOT_EXIST_RESPONSE));
    }

    @Test
    public void purchaseWrongParametersQuery() throws IOException, ServletException, ParseException {

        setGetReaderFunction(purchaseWrongParametersQuery);

        new purchaseServlet().doPost(req, resp);

        Assert.assertTrue("Bad input parameters check: ", stringWriter.toString().contains(responses.BAD_INPUT_PARAMETERS_RESPONSE));
    }

    @Test
    public void purchaseCostBelowZeroQuery() throws IOException, ServletException, ParseException {

        setGetReaderFunction(purchaseCostBelowZeroQuery);

        new purchaseServlet().doPost(req, resp);

        Assert.assertTrue("Cost below zero check: ", stringWriter.toString().contains(responses.PARAMETERS_BELOW_ZERO_RESPONSE));
    }

    @Test
    public void purchaseQuantityBelowZeroQuery() throws IOException, ServletException, ParseException {

        setGetReaderFunction(purchaseQuantityBelowZeroQuery);

        new purchaseServlet().doPost(req, resp);

        Assert.assertTrue("Quantity below zero check: ", stringWriter.toString().contains(responses.PARAMETERS_BELOW_ZERO_RESPONSE));
    }


    @Test
    public void demandTrueQuery() throws IOException, ServletException, ParseException {

        setGetReaderFunction(demandTrueQuery);

        new demandServlet().doPost(req, resp);

        ArrayList<Transaction> demands = databaseUtils.getDemands("testproduct", dateFormat.parse("2019-03-10"));

        Transaction demand = parseTransactionFromDatabase(demands);
        Transaction testDemand = new Transaction("testproduct", 2000, 3, dateFormat.parse("2019-03-10"));

        Assert.assertEquals("Demand was created", testDemand, demand);
    }


    @Test
    public void demandProductDoesNotExistQuery() throws IOException, ServletException, ParseException {

        setGetReaderFunction(demandProductDoesNotExistQuery);

        new purchaseServlet().doPost(req, resp);

        Assert.assertTrue("Product does not exist demand check: ", stringWriter.toString().contains(responses.PRODUCT_DOES_NOT_EXIST_RESPONSE));
    }

    @Test
    public void demandWrongParametersQuery() throws IOException, ServletException, ParseException {

        setGetReaderFunction(demandWrongParametersQuery);

        new purchaseServlet().doPost(req, resp);

        Assert.assertTrue("Bad input parameters demand check: ", stringWriter.toString().contains(responses.BAD_INPUT_PARAMETERS_RESPONSE));
    }

    @Test
    public void demandCostBelowZeroQuery() throws IOException, ServletException, ParseException {

        setGetReaderFunction(demandCostBelowZeroQuery);

        new purchaseServlet().doPost(req, resp);

        Assert.assertTrue("Cost below zero demand check: ", stringWriter.toString().contains(responses.PARAMETERS_BELOW_ZERO_RESPONSE));
    }

    @Test
    public void demandQuantityBelowZeroQuery() throws IOException, ServletException, ParseException {

        setGetReaderFunction(demandQuantityBelowZeroQuery);

        new purchaseServlet().doPost(req, resp);

        Assert.assertTrue("Quantity below zero demand check: ", stringWriter.toString().contains(responses.PARAMETERS_BELOW_ZERO_RESPONSE));
    }


    public void deleteTestProductAndTransactions() throws ParseException {
        String testName = "testproduct";

        deleteProduct(testName);
        deletePurchase(testName);
        deleteDemand(testName);
    }


    private void deleteProduct(String testName) {
        String query = "DELETE FROM " + tables.PRODUCTS + " WHERE name = '" + testName + "'";
        deleteFromDatabase(query);
    }

    private void deletePurchase(String testName) {
        String query = "DELETE FROM " + tables.INCOME + " WHERE name = '" + testName + "'";
        deleteFromDatabase(query);
    }

    private void deleteDemand(String testName) {
        String query = "DELETE FROM " + tables.OUTCOME + " WHERE name = '" + testName + "'";
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
    public void salesReportTrueQuery() throws IOException, ServletException, ParseException {

        deleteTestProductAndTransactions();

        newProductTrueQuery();

        purchaseTrueQuery();

        demandTrueQuery();

        when(req.getParameter("name")).thenReturn("testproduct");
        when(req.getParameter("date")).thenReturn("10.04.19");

        showPurchasesAndDemands();

        new salesReportServlet().doGet(req, resp);

        Assert.assertTrue("SalesReport true query:", stringWriter.toString().contains("4500"));

    }

    @Test
    public void salesReportWrongQuery() throws IOException, ServletException, ParseException {

        deleteTestProductAndTransactions();

        newProductTrueQuery();

        purchaseTrueQuery();

        demandTrueQuery();

        when(req.getParameter("name")).thenReturn("testproduct");
        when(req.getParameter("date")).thenReturn("ERROR");


        new salesReportServlet().doGet(req, resp);

        Assert.assertTrue("SalesReport wrong query:", stringWriter.toString().contains(responses.BAD_INPUT_PARAMETERS_RESPONSE));

    }

    private void showPurchasesAndDemands() {
        //      In case you want to check purchases and demands from database
        ArrayList<Transaction> purchases = databaseUtils.getPurchases("testproduct", new Date());
        ArrayList<Transaction> demands = databaseUtils.getDemands("testproduct", new Date());

        System.out.println("Purchases");
        for (Transaction transaction : purchases)
            System.out.println(transaction);

        System.out.println("//////");
        System.out.println("Demands");

        for (Transaction transaction : demands)
            System.out.println(transaction);

        System.out.println();
    }


    @Test
    public void salesReportQuantityErrorQuery() throws IOException, ServletException, ParseException {

        deleteTestProductAndTransactions();

        newProductTrueQuery();

        purchaseTrueQuery();

        demandTrueQuery();
        demandTrueQuery();

        when(req.getParameter("name")).thenReturn("testproduct");
        when(req.getParameter("date")).thenReturn("10.04.19");

        new salesReportServlet().doGet(req, resp);

        Assert.assertTrue("SalesReport quantity error query:", stringWriter.toString().contains(responses.QUANTITY_ERROR_RESPONSE));
    }


    @Test
    public void salesReportProductDoesNotExistQuery() throws IOException, ServletException, ParseException {

        deleteTestProductAndTransactions();


        when(req.getParameter("name")).thenReturn("testproduct");
        when(req.getParameter("date")).thenReturn("10.04.19");

        new salesReportServlet().doGet(req, resp);

        Assert.assertTrue("SalesReport quantity error query:", stringWriter.toString().contains(responses.PRODUCT_DOES_NOT_EXIST_RESPONSE));
    }

}