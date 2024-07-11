package com.perunovpavel.service;

import com.perunovpavel.entity.Currency;

import java.io.PrintWriter;
import java.util.List;

public class PrintCurrencies {

    public static void printCurrencies(PrintWriter printWriter, List<Currency> currencies) {
        for (Currency entity:currencies) {
            printWriter.println(entity);
        }
    }
}
