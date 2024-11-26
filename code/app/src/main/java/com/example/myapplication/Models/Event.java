package com.example.myapplication.Models;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.example.myapplication.NotificationService;
import com.example.myapplication.UserProfile;
import com.example.myapplication.WaitingList;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;



public class Event {
    private String eventName;
    private String eventId;
    private String date;
    private String time;
    private String description;
    private int maxAttendees;
    private Integer maxWaitlist; // Nullable
    private boolean geolocationPerm;
    private String qrCodeLink;
    private String posterUrl;
    private EntrantList waitingList;
    private boolean geolocationEnabled;

    public Event(String eventId, String eventName, String date, String time, String description, int maxAttendees, Integer maxWaitlist, boolean geolocationEnabled, String qrCodeLink) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.date = date;
        this.time = time;
        this.description = description;
        this.maxAttendees = maxAttendees;
        this.maxWaitlist = maxWaitlist;
        this.geolocationPerm = geolocationEnabled;
        this.qrCodeLink = qrCodeLink;
        this.waitingList = new EntrantList(this.maxWaitlist, this.maxAttendees);
    }
    // Simplified constructor
    public Event(String eventId, String eventName) {
        this.eventId = eventId;
        this.eventName = eventName;
    }

    public String getEventId() {
        return eventId;
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }


    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }


    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }


    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    public int getMaxAttendees() {
        return maxAttendees;
    }
    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public Integer getMaxWaitlist() {
        return maxWaitlist;
    }
    public void setMaxWaitlist(Integer maxWaitlist) {
        this.maxWaitlist = maxWaitlist;
    }

    public boolean isGeolocationEnabled() {
        return geolocationEnabled;
    }
    public void setGeolocationEnabled(boolean geolocationEnabled) {
        this.geolocationEnabled = geolocationEnabled;
    }

    public String getQrCodeLink() {
        return qrCodeLink;
    }
    public void generateQrCodeLink(String qrCodeLink) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrCodeLink, BarcodeFormat.QR_CODE, 300, 300);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    private String getPosterUrl() {
        return posterUrl;
    }
    private void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

//    /*
//    Samples a specified number of users from the waiting list
//     */
//    public List<UserProfile> sampleAttendees(int selectedCapacity) {
//        return waitingList.sampleAttendees(selectedCapacity);
//    }

//    public void notifySampledAttendees(List<Map<String, Object>> sampledAttendees, Context context, NotificationService notificationService) {
//        for (Map<String, Object> user: sampledAttendees) {
//            String title = "Congratulations!";
//            String description = "You have been selected from the waiting list";
//            NotificationService.sendNotification(user, context, title, description);
//
//        }
//    }
}

