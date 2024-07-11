package com.perunovpavel.servlets.Currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perunovpavel.dao.CurrencyDao;
import com.perunovpavel.entity.Currency;
import com.perunovpavel.exception.CurrencyWithCodeAlreadyExistsException;
import com.perunovpavel.exception.DatabaseUnavailableException;
import com.perunovpavel.exception.RequiredFormFieldMissingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;


@WebServlet("/currencies")
public class CurrencyServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Currency> currencies = currencyDao.findAll();
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            mapper.writeValue(resp.getWriter(), currencies);
        } catch (DatabaseUnavailableException exception) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String code = req.getParameter("code");
            String fullName = req.getParameter("fullName");
            String sign = req.getParameter("sign");
            currencyDao.insertIntoCurrency(new Currency(code, fullName, sign));
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            mapper.writeValue(resp.getWriter(), currencyDao.findByCode(code));
        } catch (RequiredFormFieldMissingException exception) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        } catch (CurrencyWithCodeAlreadyExistsException exception) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, exception.getMessage());
        } catch (DatabaseUnavailableException exception) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
}
