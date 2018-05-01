package com.example.currentplacedetailsonmap.Model;

import java.io.Serializable;

import de.hdodenhof.circleimageview.CircleImageView;

public class User implements Serializable
{

    public String userId;
    public String achternaam;
    public String voornaam;
    public String geslacht;
    public String email;
    public String wachtwoord;
    public Address adress;
    public String status;
    public String avatar;


    public User()
    {

    }
    public User(String userId, String achternaam, String voornaam, String geslacht, String email, String wachtwoord, Address adress, String online, String avatar)
    {
        this.userId = userId;
        this.achternaam = achternaam;
        this.voornaam = voornaam;
        this.geslacht = geslacht;
        this.email = email;
        this.wachtwoord = wachtwoord;
        this.adress = adress;
        this.status = online;
        this.avatar = avatar;

    }


    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String online)
    {
        this.status = online;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getAchternaam()
    {
        return achternaam;
    }

    public void setAchternaam(String achternaam)
    {
        this.achternaam = achternaam;
    }

    public String getVoornaam()
    {
        return voornaam;
    }

    public void setVoornaam(String voornaam)
    {
        this.voornaam = voornaam;
    }

    public String getGeslacht()
    {
        return geslacht;
    }

    public void setGeslacht(String geslacht)
    {
        this.geslacht = geslacht;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Address getAdress()
    {
        return adress;
    }

    public void setAdress(Address adress)
    {
        this.adress = adress;
    }

    public String getWachtwoord()
    {
        return wachtwoord;
    }

    public void setWachtwoord(String wachtwoord)
    {
        this.wachtwoord = wachtwoord;
    }


}
