package com.yolla.orderingapi.common.valueObject;

public enum Currency {
    TRY("TRY", "Türk Lirası", "₺"),
    USD("USD", "ABD Doları", "$"),
    EUR("EUR", "Euro", "€"),
    GBP("GBP", "İngiliz Sterlini", "£"),
    CNY("CNY", "Çin Yuanı", "¥"),
    ARS("ARS", "Arjantin Pesosu", "$"),
    BRL("BRL", "Brezilya Reali", "R$"),
    AED("AED", "Birleşik Arap Emirlikleri Dirhemi", "د.إ"),
    IQD("IQD", "Irak Dinarı", "ع.د");

    private String value;
    private String description;
    private String symbol;

    Currency(String value, String description, String symbol) {
        this.value = value;
        this.description = description;
        this.symbol = symbol;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public String getSymbol() {
        return symbol;
    }
}
