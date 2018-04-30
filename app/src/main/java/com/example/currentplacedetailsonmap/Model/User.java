package com.example.currentplacedetailsonmap.Model;

import java.io.Serializable;
import java.util.Map;

public class User implements Serializable
{

    public String userId;
    public String achternaam;
    public String voornaam;
    public String geslacht;
    public String email;
    public String wachtwoord;
    public Address adress;
    public String online;

    public Map<String, String> timestamp;

    public User(String userId,String achternaam, String voornaam, String geslacht, String email, String wachtwoord, Address adress,String online)
    {
        this.userId = userId;
        this.achternaam = achternaam;
        this.voornaam = voornaam;
        this.geslacht = geslacht;
        this.email = email;
        this.wachtwoord = wachtwoord;
        this.adress = adress;
        this.online = online;
        this.timestamp = timestamp;
    }

    public User()
    {

    }

    public Map<String, String> getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Map<String, String> timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getStatus()
    {
        return online;
    }

    public void setStatus(String online)
    {
        this.online = online;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }

    public String getGeslacht() {
        return geslacht;
    }

    public void setGeslacht(String geslacht) {
        this.geslacht = geslacht;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAdress() {
        return adress;
    }

    public void setAdress(Address adress) {
        this.adress = adress;
    }

    @Override
    public String toString()
    {
        return email + " ";
        /*
        return "User{" +
                "userId='" + userId + '\'' +
                ", achternaam='" + achternaam + '\'' +
                ", voornaam='" + voornaam + '\'' +
                ", geslacht='" + geslacht + '\'' +
                ", email='" + email + '\'' +
                ", wachtwoord='" + wachtwoord + '\'' +
                ", adress=" + adress +
                '}';*/
    }
}
