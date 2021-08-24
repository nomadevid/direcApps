package com.nomadev.direc.model;

public class PasienModel {
    private String nama, kelamin, telepon, alamat, tanggal_lahir;

    public PasienModel() {

    }

    public PasienModel(String nama, String kelamin, String telepon, String alamat, String tanggal_lahir) {
        this.nama = nama;
        this.kelamin = kelamin;
        this.telepon = telepon;
        this.alamat = alamat;
        this.tanggal_lahir = tanggal_lahir;
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
