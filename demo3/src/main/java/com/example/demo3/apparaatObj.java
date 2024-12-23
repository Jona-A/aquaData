package com.example.demo3;

public class apparaatObj {

    public String installD, locatie, beschrijving;
    public int adID, gpsId;
    public double stofWaarde;
    public boolean gps;

    public apparaatObj(int adID, String installD, String locatie, String beschrijving, double stofWaarde, boolean gps, int gpsId) {
        this.adID = adID;
        this.installD = installD;
        this.locatie = locatie;
        this.beschrijving = beschrijving;
        this.stofWaarde = stofWaarde;
        this.gps = gps;
        this.gpsId = gpsId;
    }

    public int getAdID() {
        return adID;
    }

    public void setAdID(int adID) {
        this.adID = adID;
    }

    public String getInstallD() {
        return installD;
    }

    public void setInstallD(String installD) {
        this.installD = installD;
    }

    public String getLocatie() {
        return locatie;
    }

    public void setLocatie(String locatie) {
        this.locatie = locatie;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public boolean getGps() {
        return gps;
    }

    public void setGps(boolean gps) {
        this.gps = gps;
    }

    public double getStofWaarde() {
        return stofWaarde;
    }

    public void setStofWaarde(double stofWaarde) {
        this.stofWaarde = stofWaarde;
    }

    public int getGpsId() {
        return gpsId;
    }

    public void setGpsId(int gpsId) {
        this.gpsId = gpsId;
    }
}
