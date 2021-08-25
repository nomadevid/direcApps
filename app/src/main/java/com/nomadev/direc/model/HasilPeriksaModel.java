package com.nomadev.direc.model;

public class HasilPeriksaModel {

    private String hasil_periksa, keluhan, tanggal, terapi;

    public HasilPeriksaModel(){

    }

    public HasilPeriksaModel(String hasil_periksa, String keluhan, String tanggal, String terapi) {
        this.hasil_periksa = hasil_periksa;
        this.keluhan = keluhan;
        this.tanggal = tanggal;
        this.terapi = terapi;
    }

    public String getHasil_periksa() {
        return hasil_periksa;
    }

    public void setHasil_periksa(String hasil_periksa) {
        this.hasil_periksa = hasil_periksa;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getTerapi() {
        return terapi;
    }

    public void setTerapi(String terapi) {
        this.terapi = terapi;
    }
}
