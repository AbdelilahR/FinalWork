package com.example.currentplacedetailsonmap.Model;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class Utility
{
    public Utility()
    {
    }

    /**
     * source: https://stackoverflow.com/questions/8911356/whats-the-best-practice-to-round-a-float-to-2-decimals?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    public static float round(float d, int decimalPlace)
    {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    /**
     * Source
     * https://stackoverflow.com/questions/20438627/getlastknownlocation-returns-null?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
     */
    public static Location getLastKnownLocation(Context context)
    {
        LocationManager mLocationManager = (LocationManager) context.getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers)
        {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null)
            {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())
            {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    /**
     * https://www.viralandroid.com/2015/11/get-current-time-in-android-programmatically.html
     * @return
     */
    public static String getTime()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdformat.format(calendar.getTime());
        return date;
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
