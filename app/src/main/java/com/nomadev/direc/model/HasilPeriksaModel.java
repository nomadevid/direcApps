package com.nomadev.direc.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

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
    private int tagihan;
    private String hasil_periksa, keluhan, tanggal, terapi, id, penyakit, pemeriksa, skema1, skema2;
    private ArrayList<String> urlString;
    private Timestamp timestamp;
    public boolean isSection;

    public HasilPeriksaModel() {

    }

    public HasilPeriksaModel(String pemeriksa, String penyakit, String hasil_periksa, String keluhan, String tanggal, String terapi, int tagihan, String skema1, String skema2, Timestamp timestamp, boolean isSection) {
        this.hasil_periksa = hasil_periksa;
        this.keluhan = keluhan;
        this.tanggal = tanggal;
        this.terapi = terapi;
        this.timestamp = timestamp;
        this.isSection = isSection;
        this.penyakit = penyakit;
        this.tagihan = tagihan;
        this.pemeriksa = pemeriksa;
        this.skema1  = skema1;
        this.skema2 = skema2;
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

    public String getPemeriksa() {
        return pemeriksa;
    }

    public void setPemeriksa(String pemeriksa) {
        this.pemeriksa = pemeriksa;
    }

    public String getPenyakit() {
        return penyakit;
    }

    public void setPenyakit(String penyakit) {
        this.penyakit = penyakit;
    }

    public int getTagihan() {
        return tagihan;
    }

    public void setTagihan(int tagihan) {
        this.tagihan = tagihan;
    }

    public String getSkema1() {
        return skema1;
    }

    public void setSkema1(String skema1) {
        this.skema1 = skema1;
    }

    public String getSkema2() {
        return skema2;
    }

    public void setSkema2(String skema2) {
        this.skema2 = skema2;
    }
}
