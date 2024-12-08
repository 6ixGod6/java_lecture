package com.example.lesalonproject.model;



public class MarketPosition {
    private String instrument;
    private int quantity;
    private double unrealizedPL;

    //Pour bien tracker la position du marché

    public MarketPosition(String instrument, int quantity, double unrealizedPL) {
        this.instrument = instrument;
        this.quantity = quantity;
        this.unrealizedPL = unrealizedPL;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnrealizedPL() {
        return unrealizedPL;
    }

    public void setUnrealizedPL(double unrealizedPL) {
        this.unrealizedPL = unrealizedPL;
    }
}
