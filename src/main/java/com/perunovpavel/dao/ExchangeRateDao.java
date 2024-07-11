package com.perunovpavel.dao;

import com.perunovpavel.entity.ExchangeRate;
import com.perunovpavel.exception.CurrencyCodeOfPairMissingInAddressException;
import com.perunovpavel.exception.DatabaseUnavailableException;
import com.perunovpavel.exception.ExchangeRatePairNotFoundException;
import com.perunovpavel.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao {

    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private ExchangeRateDao() {
    }

    public static final String FIND_ALL = """
            SELECT id,baseCurrencyId,targetCurrencyId,rate
            FROM ExchangeRates
            """;

    public static final String UPDATE_RATE = """
            UPDATE ExchangeRates
            SET rate = ?
            where baseCurrencyId = ? and targetCurrencyId = ?
            """;

    public static final String FIND_BY_PAIR = """
            SELECT id,baseCurrencyId,targetCurrencyId,rate
            from ExchangeRates
            where baseCurrencyId = ? and targetCurrencyId = ?
            """;

    public static final String INSERT_INTO_EXCHANGE_RATES = """
            insert into ExchangeRates(baseCurrencyId, targetCurrencyId, rate) VALUES (?,?,?)
            """;

    public List<ExchangeRate> findAll() {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL)) {
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("DataBase unavailable");
        }
    }

    public ExchangeRate findByPair(String baseCurrencyId, String targetCurrencyId) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_PAIR)) {
            if (baseCurrencyId.isEmpty() || targetCurrencyId.isEmpty()) {
                throw new CurrencyCodeOfPairMissingInAddressException("Currency codes of the pair are missing in the address");
            }

            preparedStatement.setInt(1, currencyDao.findIdByCode(baseCurrencyId));
            preparedStatement.setInt(2, currencyDao.findIdByCode(targetCurrencyId));
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new ExchangeRatePairNotFoundException("Exchange rate for the pair was not found ");
            }
            return buildExchangeRate(resultSet);
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("DataBase unavailable");
        }
    }

    public int insertIntoExchangeRates(ExchangeRate exchangeRate) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_EXCHANGE_RATES, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, exchangeRate.getBaseCurrencyId().getId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrencyId().getId());
            preparedStatement.setDouble(3, exchangeRate.getRate());
            preparedStatement.executeUpdate();
            return preparedStatement.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("DataBase unavailable");
        }
    }

    public ExchangeRate update(ExchangeRate exchangeRate) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_RATE, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setDouble(1, exchangeRate.getRate());
            preparedStatement.setInt(2, exchangeRate.getBaseCurrencyId().getId());
            preparedStatement.setInt(3, exchangeRate.getTargetCurrencyId().getId());
            preparedStatement.executeUpdate();
            return findByPair(exchangeRate.getBaseCurrencyId().getCode(), exchangeRate.getTargetCurrencyId().getCode());
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("DataBase unavailable");
        }
    }


    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(resultSet.getInt("id"),
                currencyDao.findById(resultSet.getInt("baseCurrencyId")),
                currencyDao.findById(resultSet.getInt("targetCurrencyId")),
                resultSet.getDouble("rate"));
    }

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }
}
