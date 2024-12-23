package com.example.demo3;

public class ppmObj {
    public String tijdStip;
    public float ppmWaarde;

    public ppmObj(String tijdStip, float ppmWaarde) {
        this.tijdStip = tijdStip;
        this.ppmWaarde = ppmWaarde;
    }

    public void setTijdStip(String tijdStip) {
        this.tijdStip = tijdStip;
    }
    public String getTijdStip() {
        return tijdStip;
    }
    public void setPpmWaarde(float ppmWaarde) {
        this.ppmWaarde = ppmWaarde;
    }
    public float getPpmWaarde() {
        return ppmWaarde;
    }
}
