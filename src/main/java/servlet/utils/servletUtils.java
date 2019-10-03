package servlet.utils;

import databases.databaseUtils;
import json.JsonUtils;
import operations.Transaction;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class servletUtils {
    public static void setEncoding(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
    }

    public static Transaction getAndCheckTransactionParameters(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        BufferedReader reader = req.getReader();

        JSONObject inputParameters = JsonUtils.getInputParameters(reader);



        Transaction inputPurchase = null;
        try {
            if (checkParameters(resp, inputParameters)) return null;
            inputPurchase = new Transaction(inputParameters.getString("name"),
                    Integer.valueOf(inputParameters.getString("cost")),
                    Integer.valueOf(inputParameters.getString("quantity")),
                    inputParameters.getString("date"));
        } catch (JSONException | ParseException e) {
            responses.createBadInputParametersResponse(resp);
            e.printStackTrace();
        }
        return inputPurchase;
    }

    private static boolean checkParameters(HttpServletResponse resp, JSONObject inputParameters) throws IOException {
        if (!checkIsProductExist(inputParameters)) {
            responses.createProductDoesNotExistResponse(resp);
            return true;
        }
        if (checkCostAndQuantity(inputParameters)) {
            responses.createParametersBelowZeroResponse(resp);
            return true;
        }
        return false;
    }


    private static boolean checkIsProductExist(JSONObject inputParameters) throws IOException {
        return databaseUtils.isProductExist(inputParameters.getString("name"));
    }

    private static boolean checkCostAndQuantity(JSONObject inputParameters) {
        return Integer.parseInt(inputParameters.getString("cost")) <= 0 ||
                Integer.parseInt(inputParameters.getString("quantity")) <= 0;
    }
}
