package com.nomadev.direc.model;

import com.google.firebase.firestore.Exclude;

public class HasilPeriksaModel {

    public String getId_data() {
        return id_data;
    }

    public String setId_data(String id_data) {
        this.id_data = id_data;
        return id_data;
    }

    @Exclude
    private String id_data;

    private String hasil_periksa, keluhan, tanggal, terapi, id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
