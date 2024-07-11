package com.perunovpavel.service;

import com.perunovpavel.dao.ExchangeRateDao;
import com.perunovpavel.entity.ExchangeRate;

public class ExchangeRateService {

    private static final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();

    public static String getBaseCurrency(String pathInfo) {
        return pathInfo.substring(1).toUpperCase().substring(0, 3);
    }

    public static String getTargetCurrency(String pathInfo) {
        return pathInfo.substring(1).toUpperCase().substring(3);
    }

    public ExchangeRate findExchangeRate(String from, String to) {
        ExchangeRate directRate = exchangeRateDao.findByPair(from, to);
        if (directRate.getId() != 0) {
            return directRate;
        }

        ExchangeRate reverseRate = exchangeRateDao.findByPair(to, from);
        if (reverseRate.getId() != 0) {
            return new ExchangeRate(reverseRate.getTargetCurrencyId(), reverseRate.getBaseCurrencyId(), 1 / reverseRate.getRate());
        }

        ExchangeRate fromToUsd = exchangeRateDao.findByPair("USD", from);
        ExchangeRate toToUsd = exchangeRateDao.findByPair("USD", to);
        if (fromToUsd.getId() != 0 && toToUsd.getId() != 0) {
            double crossRate = toToUsd.getRate() / fromToUsd.getRate();
            return new ExchangeRate(fromToUsd.getTargetCurrencyId(), toToUsd.getTargetCurrencyId(), crossRate);
        }
        throw new RuntimeException("Exchange rate not found");
    }

    public static boolean checkForPair(ExchangeRate exchangeRate) {
        ExchangeRate pair = exchangeRateDao.findByPair(exchangeRate.getBaseCurrencyId().getCode(), exchangeRate.getTargetCurrencyId().getCode());
        return pair == null;
    }
}
