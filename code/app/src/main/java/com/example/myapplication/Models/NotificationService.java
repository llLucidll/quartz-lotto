package com.example.myapplication.Models;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.NotificationUtils;
import com.example.myapplication.R;

import java.util.Map;

public class NotificationService {

    private String notificationID;
    private String description;
    private String title;
    private User user;
    private String channelId;

    public static void sendNotification(User user) {
        if (user.getNotificationsPerm()) {

        }
    }
}
