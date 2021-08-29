package com.nomadev.direc.model;

public class HistoryModel {

    private String idPasien, nama, addDate, addTime, tanggalLahir;

    public HistoryModel() {
    }

    public HistoryModel(String idPasien, String nama, String addDate, String addTime, String tanggalLahir) {
        this.idPasien = idPasien;
        this.nama = nama;
        this.addDate = addDate;
        this.addTime = addTime;
        this.tanggalLahir = tanggalLahir;
    }

    public String getIdPasien() {
        return idPasien;
    }

    public void setIdPasien(String idPasien) {
        this.idPasien = idPasien;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }
}
