package servlet.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class responses {


    public static final String BAD_INPUT_PARAMETERS_RESPONSE = "Ошибка: Вы ввели некорректные данные.";
    public static final String PRODUCT_DOES_NOT_EXIST_RESPONSE = "Ошибка: Продукта с таким названием нет в базе данных";
    public static final String PRODUCT_EXISTS_RESPONSE = "Ошибка: Переданный продукт уже существует в базе данных";
    public static final String QUANTITY_ERROR_RESPONSE = "Ошибка: Товаров продано больше чем поступило";
    public static final String PARAMETERS_BELOW_ZERO_RESPONSE = "Ошибка: Переданны отрицательные значения параметров";

    public static void createProductExistsResponse(HttpServletResponse resp) throws IOException {
        PrintWriter writer = setAndGetResponseWriter(resp);
        writer.println(PRODUCT_EXISTS_RESPONSE);
    }

    public static void createBadInputParametersResponse(HttpServletResponse resp) throws IOException {
        PrintWriter writer = setAndGetResponseWriter(resp);
        writer.println(BAD_INPUT_PARAMETERS_RESPONSE);
    }

    public static void createParametersBelowZeroResponse(HttpServletResponse resp) throws IOException {
        PrintWriter writer = setAndGetResponseWriter(resp);
        writer.println(PARAMETERS_BELOW_ZERO_RESPONSE);
    }

    public static void createProductDoesNotExistResponse(HttpServletResponse resp) throws IOException {
        PrintWriter writer = setAndGetResponseWriter(resp);
        writer.println(PRODUCT_DOES_NOT_EXIST_RESPONSE);
    }

    public static void createQuantityErrorResponse(HttpServletResponse resp) throws IOException {
        PrintWriter writer = setAndGetResponseWriter(resp);
        writer.println(QUANTITY_ERROR_RESPONSE);
    }


    public static PrintWriter setAndGetResponseWriter(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_CONFLICT);
        return resp.getWriter();
    }


    public static void createCreatedResponse(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }
}
