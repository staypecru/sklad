package servlet;

import databases.databaseUtils;
import json.JsonUtils;
import org.json.JSONObject;
import servlet.utils.responses;
import servlet.utils.servletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class newProductServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        servletUtils.setEncoding(req, resp);
        String name;
        try {
            name = getProductName(req);
        } catch (Exception e) {
            responses.createBadInputParametersResponse(resp);
            return;
        }
        if (!databaseUtils.isProductExist(name)) {
            databaseUtils.createProduct(name);
            responses.createCreatedResponse(resp);
        } else {
            responses.createProductExistsResponse(resp);
        }
    }

    private String getProductName(HttpServletRequest req) throws IOException {

        BufferedReader reader = req.getReader();

        JSONObject jsonObject = JsonUtils.getInputParameters(reader);

        return jsonObject.getString("name");
    }


    /**
     * Temporary function to check every product in products table
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        servletUtils.setEncoding(req, resp);
        resp.setContentType("application/json; charset=UTF-8");
        ArrayList<String> products = databaseUtils.getProducts();
        PrintWriter writer = resp.getWriter();
        for (String product : products)
            writer.println(product);
    }
}
