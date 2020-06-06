package com.bi183.tedja.model;

import android.text.Html;

import com.google.gson.annotations.SerializedName;

public class Lagu {

    @SerializedName("id")
    private int id;
    @SerializedName("judul_lagu")
    private String judul_lagu;
    @SerializedName("album_lagu")
    private String album_lagu;
    @SerializedName("artis")
    private String artis;
    @SerializedName("tahun")
    private String tahun;
    @SerializedName("negara")
    private String negara;
    @SerializedName("publisher")
    private String publisher;
    @SerializedName("genre")
    private String genre;
    @SerializedName("cover")
    private String cover;

    public Lagu(int id, String judul_lagu, String album_lagu, String artis, String tahun, String negara, String publisher, String genre, String cover) {
        this.id = id;
        this.judul_lagu = judul_lagu;
        this.album_lagu = album_lagu;
        this.artis = artis;
        this.tahun = tahun;
        this.negara = negara;
        this.publisher = publisher;
        this.genre = genre;
        this.cover = cover;
    }

    public int getId() {
        return id;
    }

    public String getJudul_lagu() {
        return Html.fromHtml(judul_lagu).toString();
    }

    public String getAlbum_lagu() {
        return Html.fromHtml(album_lagu).toString();
    }

    public String getArtis() {
        return Html.fromHtml(artis).toString();
    }

    public String getTahun() {
        return tahun;
    }

    public String getNegara() {
        return Html.fromHtml(negara).toString();
    }

    public String getPublisher() {
        return Html.fromHtml(publisher).toString();
    }

    public String getGenre() {
        return Html.fromHtml(genre).toString();
    }

    public String getCover() {
        return cover;
    }
}
