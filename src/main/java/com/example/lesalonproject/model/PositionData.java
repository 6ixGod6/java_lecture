package com.example.lesalonproject.model;



import javafx.beans.property.SimpleStringProperty;

public class PositionData {
    private String tradeID;
    private String currencyPair;
    private String quantity;
    private String direction;

    public PositionData(String tradeID, String currencyPair, String quantity, String direction) {
        this.tradeID = tradeID;
        this.currencyPair = currencyPair;
        this.quantity = quantity;
        this.direction = direction;
    }

    public String getTradeID() {
        return tradeID;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDirection() {
        return direction;
    }


    public SimpleStringProperty tradeIDProperty() {
        return new SimpleStringProperty(tradeID);
    }

    public SimpleStringProperty currencyPairProperty() {
        return new SimpleStringProperty(currencyPair);
    }

    public SimpleStringProperty quantityProperty() {
        return new SimpleStringProperty(quantity);
    }

    public SimpleStringProperty directionProperty() {
        return new SimpleStringProperty(direction);
    }
}
 
    

