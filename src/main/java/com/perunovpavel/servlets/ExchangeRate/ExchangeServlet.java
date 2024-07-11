package com.perunovpavel.servlets.ExchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perunovpavel.dao.CurrencyDao;
import com.perunovpavel.dao.ExchangeRateDao;
import com.perunovpavel.entity.ExchangeRate;
import com.perunovpavel.service.ExchangeRateService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrency = req.getParameter("from");
        String targetCurrency = req.getParameter("to");
        double amount = Double.parseDouble(req.getParameter("amount"));
        ExchangeRateService rateService = new ExchangeRateService();
        ExchangeRate exchangeRate = rateService.findExchangeRate(baseCurrency, targetCurrency);
        mapper.writeValue(resp.getWriter(), createResponseJson(exchangeRate, amount));
    }

    private Map<String, Object> createResponseJson(ExchangeRate exchangeRate, double amount) {
        Map<String, Object> responseJson = new LinkedHashMap<>();
        responseJson.put("baseCurrency", exchangeRate.getBaseCurrencyId());
        responseJson.put("targetCurrency", exchangeRate.getTargetCurrencyId());
        responseJson.put("rate", exchangeRate.getRate());
        responseJson.put("amount", amount);
        responseJson.put("convertedAmount", amount * exchangeRate.getRate());
        return responseJson;
    }
}
