package com.example.lesalonproject.model;




import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PriceData {
    private final StringProperty date;
    private final StringProperty bid;
    private final StringProperty ask;

    public PriceData(String date, String bid, String ask) {
        this.date = new SimpleStringProperty(date);
        this.bid = new SimpleStringProperty(bid);
        this.ask = new SimpleStringProperty(ask);
    }

    public StringProperty dateProperty() {
        return date;
    }

    public StringProperty bidProperty() {
        return bid;
    }

    public StringProperty askProperty() {
        return ask;
    }
}