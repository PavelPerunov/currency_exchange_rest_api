package com.perunovpavel.servlets.ExchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perunovpavel.dao.CurrencyDao;
import com.perunovpavel.dao.ExchangeRateDao;
import com.perunovpavel.entity.ExchangeRate;
import com.perunovpavel.exception.*;
import com.perunovpavel.service.ExchangeRateService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateByPairServlet extends HttpServlet {
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            String baseCurrency = ExchangeRateService.getBaseCurrency(pathInfo);
            String targetCurrency = ExchangeRateService.getTargetCurrency(pathInfo);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            mapper.writeValue(resp.getWriter(), exchangeRateDao.findByPair(baseCurrency, targetCurrency));
        } catch (CurrencyCodeOfPairMissingInAddressException exception) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        } catch (ExchangeRatePairNotFoundException exception) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
        } catch (DatabaseUnavailableException exception) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            String baseCurrency = ExchangeRateService.getBaseCurrency(pathInfo);
            String targetCurrency = ExchangeRateService.getTargetCurrency(pathInfo);
            String parameter = req.getReader().readLine();
            String rateFromParameter = parameter.replace("rate=", "");

            if (baseCurrency.isEmpty() || targetCurrency.isEmpty() || rateFromParameter.isEmpty()) {
                throw new RequiredFormFieldMissingException("Required form field is missing");
            }

            double rateStr = Double.parseDouble(rateFromParameter);
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setRate(rateStr);
            exchangeRate.setBaseCurrencyId(currencyDao.findByCode(baseCurrency));
            exchangeRate.setTargetCurrencyId(currencyDao.findByCode(targetCurrency));
            if (!ExchangeRateService.checkForPair(exchangeRate)) {
                throw new CurrencyPairIsNotInDataBaseException("Currency pair is missing in the database");
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            mapper.writeValue(resp.getWriter(), exchangeRateDao.update(exchangeRate));
        } catch (RequiredFormFieldMissingException exception) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        } catch (CurrencyPairIsNotInDataBaseException exception) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
        } catch (DatabaseUnavailableException exception) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }
}
