package com.OPSC.OPSC7312;

public class FavoriteClass
{
    String uid;
    double latitude;
    double longitude;
    double rating;
    String Address;
    String FavID;

    public FavoriteClass(String uid, double latitude, double longitude, double rating, String address, String favID) {
        this.uid = uid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.Address = address;
        this.FavID = favID;
        this.rating = rating;
    }

    public FavoriteClass(){}
    public String getUid()
    {
        return uid;
    }
    public String getFavID() {
        return FavID;
    }

    public void setFavID(String favID) {
        FavID = favID;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

}