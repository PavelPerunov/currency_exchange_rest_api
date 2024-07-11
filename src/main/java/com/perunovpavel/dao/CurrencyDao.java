package com.perunovpavel.dao;

import com.perunovpavel.entity.Currency;
import com.perunovpavel.exception.*;
import com.perunovpavel.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {

    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private CurrencyDao() {
    }


    public static final String FIND_ALL = """
            SELECT *
            FROM Currencies
            """;

    public static final String FIND_BY_CODE = """
            SELECT id,code,fullName,sign
            FROM Currencies
            WHERE code = ?
            """;
    public static final String FIND_ID_BY_CODE = """
            SELECT id
            from Currencies
            where code = ?
            """;
    public static final String INSERT_INTO_CURRENCY = """
            INSERT INTO Currencies (code, fullName, sign) VALUES(?,?,?);
            """;

    public static final String FIND_BY_ID = """
            SELECT id,code,fullName,sign
            FROM Currencies
            WHERE id = ?
            """;
    public static final String CHECK_CURRENCY = """
            SELECT COUNT(*) FROM Currencies WHERE code = ?
            """;

    public void insertIntoCurrency(Currency currency) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement checkStatement = connection.prepareStatement(CHECK_CURRENCY);
             PreparedStatement insertStatement = connection.prepareStatement(INSERT_INTO_CURRENCY)) {

            String code = currency.getCode();
            String fullName = currency.getFullName();
            String sign = currency.getSign();

            if (code.isEmpty() || fullName.isEmpty() || sign.isEmpty()) {
                throw new RequiredFormFieldMissingException("Required form field is missing");
            }

            checkStatement.setString(1, code);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                throw new CurrencyWithCodeAlreadyExistsException("Currency with this code already exists");
            }

            insertStatement.setString(1, code);
            insertStatement.setString(2, fullName);
            insertStatement.setString(3, sign);
            insertStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseUnavailableException("Database unavailable");
        }
    }

    public List<Currency> findAll() {
        try (Connection connection = ConnectionManager.open(); PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL)) {
            List<Currency> currencies = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            return currencies;
        } catch (SQLException exception) {
            throw new DatabaseUnavailableException("Database unavailable");
        }
    }

    public Currency findByCode(String code) {
        if (code == null || code.isEmpty()) {
            throw new CurrencyCodeMissingFromAddressException("Currency code is missing from the address");
        }
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODE)) {

            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                throw new CurrencyNotFoundException("Currency not found");
            }
            return buildCurrency(resultSet);
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Database unavailable");
        }
    }

    public Currency findById(int id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {
            preparedStatement.setInt(1, id);
            return buildCurrency(preparedStatement.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int findIdByCode(String code) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ID_BY_CODE)) {
            preparedStatement.setString(1, code);
            return preparedStatement.executeQuery().getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("fullName"),
                resultSet.getString("sign")
        );
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

}
