package com.example.currentplacedetailsonmap.Class;

import com.google.android.gms.maps.model.LatLng;

public class User
{

    public String userId;
    public String achternaam;
    public String voornaam;
    public String geslacht;
    public String email;
    public String wachtwoord;
    public Address adress;

    public User()
    {
    }

    public User(String userId,String achternaam, String voornaam, String geslacht, String email, String wachtwoord, Address adress)
    {
        this.userId = userId;
        this.achternaam = achternaam;
        this.voornaam = voornaam;
        this.geslacht = geslacht;
        this.email = email;
        this.wachtwoord = wachtwoord;
        this.adress = adress;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "userId='" + userId + '\'' +
                ", achternaam='" + achternaam + '\'' +
                ", voornaam='" + voornaam + '\'' +
                ", geslacht='" + geslacht + '\'' +
                ", email='" + email + '\'' +
                ", wachtwoord='" + wachtwoord + '\'' +
                ", adress=" + adress +
                '}';
    }
}
