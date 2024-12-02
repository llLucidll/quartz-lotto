package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;

/**
 * US 01.04.01 As an entrant I want to receive notification when chosen from the waiting list (when I "win" the lottery)
 * US 01.04.02 As an entrant I want to receive notification of not chosen on the app (when I "lose" the lottery)
 * US 01.04.03 As an entrant I want to opt out of receiving notifications from organizers and admin
 * US 02.05.01 As an organizer I want to send a notification to chosen entrants to sign up for events.
 * US 02.06.04 As an organizer I want to cancel entrants that did not sign up for the event
 * US 02.07.01 As an organizer I want to send notifications to all entrants on the waiting list
 * US 02.07.02 As an organizer I want to send notifications to all selected entrants
 * US 02.07.03 As an organizer I want to send a notification to all cancelled entrants
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 32) // Set SDK to TIRAMISU (API 32)
public class NotificationServiceTest {

    private Context context;
    private MockedStatic<NotificationUtils> mockNotificationUtils;
    private MockedStatic<FirebaseFirestore> mockFirebaseFirestore;
    private MockedStatic<NotificationManagerCompat> mockNotificationManagerCompat;
    private MockedStatic<ContextCompat> mockContextCompat;
    private MockedStatic<ActivityCompat> mockActivityCompat;
    private MockedStatic<Toast> mockToast;

    private FirebaseFirestore mockFirestore;
    private CollectionReference mockCollectionReference;
    private Task<DocumentReference> mockAddTask;
    private DocumentReference mockDocumentReference;

    @Before
    public void setUp() {
        // Initialize context using Robolectric
        context = Robolectric.buildActivity(Activity.class).create().get();

        // Mock NotificationUtils.getChannelId()
        mockNotificationUtils = Mockito.mockStatic(NotificationUtils.class);
        mockNotificationUtils.when(NotificationUtils::getChannelId).thenReturn("test_channel_id");

        // Mock FirebaseFirestore
        mockFirebaseFirestore = Mockito.mockStatic(FirebaseFirestore.class);
        mockFirestore = Mockito.mock(FirebaseFirestore.class);
        mockCollectionReference = Mockito.mock(CollectionReference.class);
        mockAddTask = Mockito.mock(Task.class);
        mockDocumentReference = Mockito.mock(DocumentReference.class);

        Mockito.when(FirebaseFirestore.getInstance()).thenReturn(mockFirestore);
        Mockito.when(mockFirestore.collection("notifications")).thenReturn(mockCollectionReference);
        Mockito.when(mockCollectionReference.add(any(Map.class))).thenReturn(mockAddTask);

        // Mock NotificationManagerCompat
        mockNotificationManagerCompat = Mockito.mockStatic(NotificationManagerCompat.class);
        NotificationManagerCompat mockNotificationManager = Mockito.mock(NotificationManagerCompat.class);
        Mockito.when(NotificationManagerCompat.from(context)).thenReturn(mockNotificationManager);

        // Mock ContextCompat.checkSelfPermission()
        mockContextCompat = Mockito.mockStatic(ContextCompat.class);
        Mockito.when(ContextCompat.checkSelfPermission(eq(context), eq(Manifest.permission.POST_NOTIFICATIONS)))
                .thenReturn(PackageManager.PERMISSION_GRANTED); // Default to granted

        // Mock ActivityCompat.requestPermissions()
        mockActivityCompat = Mockito.mockStatic(ActivityCompat.class);
        // No need to stub requestPermissions as we will verify if it's called

        // Mock Toast.makeText()
        mockToast = Mockito.mockStatic(Toast.class);
        Toast mockToastInstance = Mockito.mock(Toast.class);
        Mockito.when(Toast.makeText(any(Context.class), any(CharSequence.class), anyInt()))
                .thenReturn(mockToastInstance);
    }

    @After
    public void tearDown() {
        // Close all static mocks
        mockNotificationUtils.close();
        mockFirebaseFirestore.close();
        mockNotificationManagerCompat.close();
        mockContextCompat.close();
        mockActivityCompat.close();
        mockToast.close();
    }

    /**
     * Helper method to create a user map.
     */
    private Map<String, Object> createUserMap(String userId, String name, boolean notificationsEnabled) {
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("name", name);
        user.put("notificationsEnabled", notificationsEnabled);
        return user;
    }

    /**
     * Test sending notification with user null.
     * Expectation: No notification sent, no Firestore save.
     */
    @Test
    public void testSendNotification_UserNull_ShouldNotSendNotification() {
        // Arrange
        Map<String, Object> user = null;
        String title = "Test Title";
        String description = "Test Description";

        // Act
        NotificationService.sendNotification(user, context, title, description);

        // Assert
        // Verify that NotificationManagerCompat.notify() was never called
        mockNotificationManagerCompat.verifyNoInteractions();

        // Verify that Firestore add was never called
        Mockito.verify(mockCollectionReference, Mockito.never()).add(any(Map.class));

        // Verify that Toast was never shown
        mockToast.verify(() -> Toast.makeText(any(Context.class), any(CharSequence.class), anyInt()), Mockito.never());
    }
}