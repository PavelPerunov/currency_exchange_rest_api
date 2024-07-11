package com.perunovpavel.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExchangeRate {
    private Integer id;
    private Currency baseCurrencyId;
    private Currency targetCurrencyId;
    private double rate;

    public ExchangeRate(Currency baseCurrencyId, Currency targetCurrencyId, double rate) {
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }
}
