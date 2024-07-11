package com.perunovpavel.service;

public class CurrencyCodeService {
    public static String getCurrencyCode(String pathInfo){
        return pathInfo.substring(1).toUpperCase();
    }
}
