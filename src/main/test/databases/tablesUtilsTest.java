package databases;

import org.junit.Assert;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class tablesUtilsTest {
    public static final String DB_URL = "jdbc:h2:\\D:\\Study\\coding\\Java\\java aps\\sklad\\src\\main\\java\\databases\\"
            + "stockDatabase";

    @Test
    public void isTablesCreated() throws SQLException {

        tablesUtils.createTables();

        try (Connection connection = DriverManager.getConnection(DB_URL);) {
            ArrayList<String> tables = new ArrayList<>();
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
            Assert.assertEquals(true, tables.contains(databases.tables.COUNT.toString()));
            Assert.assertEquals(true, tables.contains(databases.tables.INCOME.toString()));
            Assert.assertEquals(true, tables.contains(databases.tables.OUTCOME.toString()));
            Assert.assertEquals(true, tables.contains(databases.tables.PRODUCTS.toString()));
        }
    }
}