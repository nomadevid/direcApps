package com.nomadev.direc.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.sql.Time;
import java.util.ArrayList;

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
    private ArrayList<String> urlString;
    private Timestamp timestamp;
    public boolean isSection;

    public HasilPeriksaModel(){

    }

    public HasilPeriksaModel(String hasil_periksa, String keluhan, String tanggal, String terapi, Timestamp timestamp, boolean isSection) {
        this.hasil_periksa = hasil_periksa;
        this.keluhan = keluhan;
        this.tanggal = tanggal;
        this.terapi = terapi;
        this.timestamp = timestamp;
        this.isSection = isSection;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
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

    public ArrayList<String> getUrlString() {
        return urlString;
    }

    public void setUrlString(ArrayList<String> urlString) {
        this.urlString = urlString;
    }

}
