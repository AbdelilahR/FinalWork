package com.example.currentplacedetailsonmap.Model;

import android.widget.TextView;

import java.io.Serializable;

public class Statistiek implements Serializable
{
    private long time;
    private int burnedCalories;
    private float distanceInMeters;
    private String date;
    private int id;
    private String name;

    public Statistiek()
    {
    }

    public Statistiek(String name, int id, long time, int burnedCalories, float distanceInMeters, String date)
    {
        this.time = time;
        this.burnedCalories = burnedCalories;
        this.distanceInMeters = distanceInMeters;
        this.date = date;
        this.id = id;
        this.name = name;
    }



    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public int getBurnedCalories()
    {
        return burnedCalories;
    }

    public void setBurnedCalories(int burnedCalories)
    {
        this.burnedCalories = burnedCalories;
    }

    public float getDistanceInMeters()
    {
        return distanceInMeters;
    }

    public void setDistanceInMeters(float distanceInMeters)
    {
        this.distanceInMeters = distanceInMeters;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }
}
