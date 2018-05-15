package com.example.currentplacedetailsonmap.Model;

import java.io.Serializable;

/**
 * Created by AkshayeJH on 13/07/17.
 */

public class Friends implements Serializable
{

    public String request;
    public Address adress;
    public User user;

    public Friends(String request,User user)
    {
        this.request = request;

        this.user = user;
    }

    public String getRequest()
    {
        return request;
    }

    public void setRequest(String request)
    {
        this.request = request;
    }

    public Address getAdress()
    {
        return adress;
    }

    public void setAdress(Address adress)
    {
        this.adress = adress;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}
