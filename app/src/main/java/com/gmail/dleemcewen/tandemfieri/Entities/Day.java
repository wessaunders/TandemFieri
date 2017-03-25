package com.gmail.dleemcewen.tandemfieri.Entities;

import com.gmail.dleemcewen.tandemfieri.Formatters.DateFormatter;
import com.google.firebase.database.Exclude;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ruth on 2/24/2017.
 */

public class Day {
    String name;
    int hourOpen;   //military time
    int hourClosed; //military time
    boolean isOpen;

    public Day() {
    }

    public Day(String name, boolean isOpen) {
        this.name = name;
        this.isOpen = isOpen;
    }

    public Day(String name, int hourOpen, int hourClosed, boolean isOpen) {
        this.name = name;
        this.hourOpen = hourOpen;
        this.hourClosed = hourClosed;
        this.isOpen = isOpen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHourOpen() {
        return hourOpen;
    }

    public void setHourOpen(int hourOpen) {
        this.hourOpen = hourOpen;
    }

    public int getHourClosed() {
        return hourClosed;
    }

    public void setHourClosed(int hourClosed) {
        this.hourClosed = hourClosed;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    /*return the hours of delivery similar to military format
    for example if delivery duration is 4hr 30min this will return 430
    12 hrs 0 min will return 1200
    * */

    @Exclude
    public int calculateDeliveryHoursDuration (){
        int m1 = hourOpen % 100;
        int m2 = hourClosed % 100;
        int h1 = (hourOpen - m1)/100;
        int h2 = (hourClosed - m2)/100;
        int dh = h2 - h1;
        int dm = m2 - m1;
        if (dm < 0){
            dm += 60;
            dh -= 1;
        }
        if(dh < 0){
            dh += 24;
        }
        return dh * 100 + dm;
    }

    @Override
    public String toString() {
        return "Day{" +
                "name='" + name + '\'' +
                ", hourOpen='" + hourOpen + '\'' +
                ", hourClosed='" + hourClosed + '\'' +
                ", isOpen=" + isOpen +
                '}';
    }

    /**
     * compareOpenTimeWithCurrentTime compares the defined open time with the current time
     * to determine if the restaurant is currently open
     * @param hourOpen indicates the time the restaurant opens in military format
     * @return true or false
     */
    @Exclude
    public boolean compareOpenTimeWithCurrentTime(int hourOpen, Date currentDate) {
        int currentMilitaryTime = DateFormatter.convertStandardTimeToMilitaryTime(currentDate);
        return currentMilitaryTime > hourOpen;
    }

    /**
     * compareClosedTimeWithCurrentTime compares the defined closed time with the current date/time
     * to determine if the restaurant is currently closed
     * @param hourClosed indicates the time the restaurant closes in military format
     * @return true or false
     */
    @Exclude
    public boolean compareClosedTimeWithCurrentTime(int hourClosed, Date currentDate) {
        Date closedTime = DateFormatter.convertMilitaryTimeToStandardDate(hourClosed);

        if ((hourClosed / 100) < 12) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(closedTime);
            int day = calendar.get(Calendar.DAY_OF_YEAR);
            calendar.set(Calendar.DAY_OF_YEAR, ++day);
            closedTime = calendar.getTime();
        }
        return closedTime.after(currentDate);
    }
}
