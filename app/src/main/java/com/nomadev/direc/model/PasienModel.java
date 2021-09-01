package com.nomadev.direc.model;

import com.google.firebase.firestore.Exclude;

public class PasienModel {

    public String getId() {
        return id;
    }

    public String setId(String id) {
        this.id = id;
        return id;
    }

    @Exclude
    private String id;

    private String nama, kelamin, telepon, alamat, tanggal_lahir;
    public boolean isSection;

    public PasienModel() {

    }

    public PasienModel(String nama, String kelamin, String telepon, String alamat, String tanggal_lahir, boolean isSection) {
        this.nama = nama;
        this.kelamin = kelamin;
        this.telepon = telepon;
        this.alamat = alamat;
        this.tanggal_lahir = tanggal_lahir;
        this.isSection = isSection;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKelamin() {
        return kelamin;
    }

    public void setKelamin(String kelamin) {
        this.kelamin = kelamin;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTanggalLahir() {
        return tanggal_lahir;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggal_lahir = tanggalLahir;
    }
}
