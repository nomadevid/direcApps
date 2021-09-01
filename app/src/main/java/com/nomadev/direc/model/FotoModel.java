package com.nomadev.direc.model;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class FotoModel {

    private Uri foto;

    public Uri getFoto() {
        return foto;
    }

    public void setFoto(Uri foto) {
        this.foto = foto;
    }

//    public static List<FotoModel> getImageList(ArrayList photoList){
//
//        List<FotoModel> fotoModelArrayList = new ArrayList<>();
//        ArrayList<Uri> photo = photoList;
//
//        for (int i = 0; i < photo.size(); i++){
//            FotoModel foto = new FotoModel();
//            foto.setFoto(photo.get(i));
//            fotoModelArrayList.add(foto);
//        }
//        return fotoModelArrayList;
//    }


}
