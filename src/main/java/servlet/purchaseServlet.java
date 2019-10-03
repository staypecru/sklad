package servlet;

import databases.databaseUtils;
import operations.Transaction;
import servlet.utils.responses;
import servlet.utils.servletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class purchaseServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        servletUtils.setEncoding(req, resp);

        Transaction inputPurchase = servletUtils.getAndCheckTransactionParameters(req, resp);
        if (inputPurchase != null) {
            databaseUtils.createPurchase(inputPurchase);
            responses.createCreatedResponse(resp);
        }

    }


}

