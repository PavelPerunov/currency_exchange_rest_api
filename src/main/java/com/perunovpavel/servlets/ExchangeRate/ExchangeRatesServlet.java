package com.perunovpavel.servlets.ExchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perunovpavel.dao.CurrencyDao;
import com.perunovpavel.dao.ExchangeRateDao;
import com.perunovpavel.entity.Currency;
import com.perunovpavel.entity.ExchangeRate;
import com.perunovpavel.exception.CurrencyWithCodeAlreadyExistsException;
import com.perunovpavel.exception.DatabaseUnavailableException;
import com.perunovpavel.exception.OneOrBothCurrenciesFromPairNotExistException;
import com.perunovpavel.exception.RequiredFormFieldMissingException;
import com.perunovpavel.service.ExchangeRateService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {


    private final ObjectMapper mapper = new ObjectMapper();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<ExchangeRate> exchangeRates = exchangeRateDao.findAll();
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            mapper.writeValue(resp.getWriter(), exchangeRates);
        } catch (DatabaseUnavailableException exception) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String baseCurrency = req.getParameter("baseCurrencyId");
            String targetCurrency = req.getParameter("targetCurrencyId");
            String rateStr = (req.getParameter("rate"));
            if (baseCurrency.isEmpty() || targetCurrency.isEmpty() || rateStr.isEmpty()) {
                throw new RequiredFormFieldMissingException("Required form field is missing");
            }
            Currency baseCurrencyId = currencyDao.findByCode(baseCurrency);
            Currency targetCurrencyId = currencyDao.findByCode(targetCurrency);
            if (baseCurrencyId == null || targetCurrencyId == null) {
                throw new OneOrBothCurrenciesFromPairNotExistException("One or both currencies not found in the database");
            }
            ExchangeRate exchangeRate = new ExchangeRate(
                    baseCurrencyId,
                    targetCurrencyId,
                    Double.parseDouble(rateStr));
            if (ExchangeRateService.checkForPair(exchangeRate)) {
                throw new CurrencyWithCodeAlreadyExistsException("A currency pair with this code already exists");
            }
            exchangeRate.setId(exchangeRateDao.insertIntoExchangeRates(exchangeRate));
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            mapper.writeValue(resp.getWriter(), exchangeRate);
        } catch (RequiredFormFieldMissingException exception) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        } catch (CurrencyWithCodeAlreadyExistsException exception) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, exception.getMessage());
        } catch (OneOrBothCurrenciesFromPairNotExistException exception) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
        } catch (DatabaseUnavailableException exception) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

}

