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
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class salesReportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        servletUtils.setEncoding(req, resp);

        String name = getAndCheckNameFromRequest(req, resp);
        Date date = getAndCheckDateFromRequest(req, resp);
        if (!databaseUtils.isProductExist(name)) {
            responses.createProductDoesNotExistResponse(resp);
            return;
        }

        ArrayList<Transaction> purchases;
        ArrayList<Transaction> demands;
        try {
            purchases = databaseUtils.getPurchases(name, date);
            demands = databaseUtils.getDemands(name, date);
        } catch (Exception e) {
            responses.createBadInputParametersResponse(resp);
            return;
        }



        int sum = calculateProfit(purchases, demands);
        System.out.println("Прибыль: " + sum);

        checkSumAndCreateResponse(resp, sum);

    }


    private String getAndCheckNameFromRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            return req.getParameter("name");
        } catch (Exception e) {
            responses.createBadInputParametersResponse(resp);
            e.printStackTrace();
        }
        return null;
    }

    private Date getAndCheckDateFromRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        try {
            return dateFormat.parse(req.getParameter("date"));
        } catch (ParseException e) {
            responses.createBadInputParametersResponse(resp);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Рассчет себестоимости по системе FIFO
     *
     * @return -1 если количество проданных товаров больше, чем закупленных ( учитывает даты поступлений).
     * Иначе возвращает прибыль от проданных товаров за вычетом закупленных.
     */
    private int calculateProfit(ArrayList<Transaction> purchases, ArrayList<Transaction> demands) {
        int sum = 0;
        for (Transaction demand : demands) {
            for (int i = 0; i < purchases.size(); i++) {
                Transaction purchase = purchases.get(i);
                if (isPurchaseWasBeforeDemand(demand, purchase)) {
                    int temporaryQuantity = demand.getQuantity() - purchase.getQuantity();
                    int singleProfit = demand.getCost() - purchase.getCost();

                    if (temporaryQuantity < 0) {
                        sum += getTemporaryProfit(demand, singleProfit);
                        demand.setQuantity(0);
                        purchase.setQuantity(Math.abs(temporaryQuantity));
                        break;
                    } else if (temporaryQuantity > 0) {
                        sum += getTemporaryProfit(purchase, singleProfit);
                        purchases.remove(purchase);
                        i--;
                        demand.setQuantity(temporaryQuantity);
                    } else if (temporaryQuantity == 0) {
                        sum += getTemporaryProfit(demand, singleProfit);
                        demand.setQuantity(0);
                        purchases.remove(purchase);
                        break;
                    }
                }
            }
            if (!isDemandQuantityEmpty(demand)) {
                return -1;
            }
        }
        return sum;
    }


    private int getTemporaryProfit(Transaction demand, int singleProfit) {
        return demand.getQuantity() * singleProfit;
    }

    private boolean isDemandQuantityEmpty(Transaction demand) {
        return demand.getQuantity() <= 0;
    }

    private boolean isPurchaseWasBeforeDemand(Transaction demand, Transaction purchase) {
        return demand.getDate().compareTo(purchase.getDate()) >= 0;
    }


    private void checkSumAndCreateResponse(HttpServletResponse resp, int sum) throws IOException {
        if (sum == -1) {
            responses.createQuantityErrorResponse(resp);
        } else {
            createResponse(resp, sum);
        }
    }

    private void createResponse(HttpServletResponse resp, int sum) throws IOException {
        PrintWriter writer = resp.getWriter();
        writer.println("Прибыль: " + sum);
    }


}
