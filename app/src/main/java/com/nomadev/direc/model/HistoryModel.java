package com.nomadev.direc.model;

import java.util.Date;

public class HistoryModel {

    private String idPasien, nama, addDate, addTime, tanggalLahir, idHistory;
    private Date timeStamp;

    public HistoryModel() {
    }

    public HistoryModel(String idPasien, String nama, String addDate, String addTime, String tanggalLahir, String idHistory, Date timeStamp) {
        this.idPasien = idPasien;
        this.nama = nama;
        this.addDate = addDate;
        this.addTime = addTime;
        this.tanggalLahir = tanggalLahir;
        this.idHistory = idHistory;
        this.timeStamp = timeStamp;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getIdHistory() {
        return idHistory;
    }

    public void setIdHistory(String idHistory) {
        this.idHistory = idHistory;
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
