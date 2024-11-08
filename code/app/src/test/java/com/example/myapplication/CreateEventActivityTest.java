package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.test.core.app.ActivityScenario;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

/*
 * Corrected tests for CreateEventActivity
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 32)
public class CreateEventActivityTest {

    private CreateEventActivity activity;
    private EditText eventNameEditText;
    private EditText dateEditText;
    private EditText timeEditText;
    private EditText descriptionEditText;
    private EditText maxAttendeesEditText;
    private EditText maxWaitlistEditText;
    private CheckBox geolocationCheckBox;
    private ImageView qrCodeImageView;
    private Button saveButton;
    private Button generateQRButton;

    // Mocked Firestore components
    private MockedStatic<FirebaseFirestore> mockedFirestoreStatic;
    private FirebaseFirestore mockFirestore;
    private CollectionReference mockEventsCollection;
    private DocumentReference mockEventDocument;
    private Task<Void> mockSetTask;

    @Before
    public void setUp() {
        // Mock FirebaseFirestore.getInstance()
        mockedFirestoreStatic = Mockito.mockStatic(FirebaseFirestore.class);
        mockFirestore = Mockito.mock(FirebaseFirestore.class);
        mockedFirestoreStatic.when(FirebaseFirestore::getInstance).thenReturn(mockFirestore);

        // Mock CollectionReference for "Events"
        mockEventsCollection = Mockito.mock(CollectionReference.class);
        Mockito.when(mockFirestore.collection("Events")).thenReturn(mockEventsCollection);

        // Mock DocumentReference for "Events"
        mockEventDocument = Mockito.mock(DocumentReference.class);
        Mockito.when(mockEventsCollection.document()).thenReturn(mockEventDocument);
        Mockito.when(mockEventDocument.getId()).thenReturn("MockEventId");

        // Mock set operation for Events
        mockSetTask = Mockito.mock(Task.class);
        Mockito.when(mockEventDocument.set(any(Map.class))).thenReturn(mockSetTask);
        Mockito.when(mockSetTask.isSuccessful()).thenReturn(true);
        Mockito.when(mockSetTask.isComplete()).thenReturn(true);

        // Initialize the activity
        Intent intent = new Intent();
        ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(CreateEventActivity.class);
        scenario.onActivity(activity -> {
            this.activity = activity;

            // Initialize UI components
            eventNameEditText = activity.findViewById(R.id.eventNameEditText);
            dateEditText = activity.findViewById(R.id.dateEditText);
            timeEditText = activity.findViewById(R.id.timeEditText);
            descriptionEditText = activity.findViewById(R.id.descriptionEditText);
            maxAttendeesEditText = activity.findViewById(R.id.maxAttendeesEditText);
            maxWaitlistEditText = activity.findViewById(R.id.maxWaitlistEditText);
            geolocationCheckBox = activity.findViewById(R.id.geolocationCheckBox);
            saveButton = activity.findViewById(R.id.saveButton);
            generateQRButton = activity.findViewById(R.id.generateQRButton);
            qrCodeImageView = activity.findViewById(R.id.qrCodeImageView);
        });
    }

    @After
    public void tearDown() {
        // Close the static mock to avoid interference with other tests
        mockedFirestoreStatic.close();
    }

    @Test
    public void testSaveEvent_withValidData_shouldSaveToFirestore() {
        // Arrange
        String eventName = "Sample Event";
        String date = "2023-12-25";
        String time = "18:00";
        String description = "Christmas Gathering";
        String maxAttendees = "100";
        String maxWaitlist = "50";
        boolean geolocationEnabled = true;

        eventNameEditText.setText(eventName);
        dateEditText.setText(date);
        timeEditText.setText(time);
        descriptionEditText.setText(description);
        maxAttendeesEditText.setText(maxAttendees);
        maxWaitlistEditText.setText(maxWaitlist);
        geolocationCheckBox.setChecked(geolocationEnabled);

        // Act
        saveButton.performClick();

        // Process any pending tasks
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Assert
        // Capture the data passed to Firestore for Events
        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(mockEventDocument).set(eventCaptor.capture());

        Map<String, Object> capturedEvent = eventCaptor.getValue();
        assertEquals("Sample Event", capturedEvent.get("eventName"));
        assertEquals("Christmas Gathering", capturedEvent.get("description"));
        assertEquals("2023-12-25", capturedEvent.get("drawDate"));
        assertEquals("18:00", capturedEvent.get("eventDateTime"));
        assertEquals(Integer.valueOf(100), capturedEvent.get("maxAttendees"));
        assertEquals(Integer.valueOf(50), capturedEvent.get("maxOnWaitList"));
        assertEquals(true, capturedEvent.get("geolocationEnabled"));

        String expectedQrCodeLink = "eventapp://event/" + mockEventDocument.getId();
        assertEquals(expectedQrCodeLink, capturedEvent.get("qrCodeLink"));
    }

    @Test
    public void testSaveEvent_withEmptyMaxWaitlist_shouldHandleNull() {
        // Arrange
        String eventName = "Sample Event";
        String date = "2023-12-25";
        String time = "18:00";
        String description = "Christmas Gathering";
        String maxAttendees = "100";
        boolean geolocationEnabled = false;

        eventNameEditText.setText(eventName);
        dateEditText.setText(date);
        timeEditText.setText(time);
        descriptionEditText.setText(description);
        maxAttendeesEditText.setText(maxAttendees);
        maxWaitlistEditText.setText(""); // Empty maxWaitlist
        geolocationCheckBox.setChecked(geolocationEnabled);

        // Act
        saveButton.performClick();

        // Process any pending tasks
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Assert
        // Capture the data passed to Firestore for Events
        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(mockEventDocument).set(eventCaptor.capture());

        Map<String, Object> capturedEvent = eventCaptor.getValue();
        assertEquals("Sample Event", capturedEvent.get("eventName"));
        assertEquals("Christmas Gathering", capturedEvent.get("description"));
        assertEquals("2023-12-25", capturedEvent.get("drawDate"));
        assertEquals("18:00", capturedEvent.get("eventDateTime"));
        assertEquals(Integer.valueOf(100), capturedEvent.get("maxAttendees"));
        assertNull("maxOnWaitList should be null when input is empty", capturedEvent.get("maxOnWaitList"));
        assertEquals(false, capturedEvent.get("geolocationEnabled"));

        String expectedQrCodeLink = "eventapp://event/" + mockEventDocument.getId();
        assertEquals(expectedQrCodeLink, capturedEvent.get("qrCodeLink"));
    }

    @Test
    public void testSaveButton_click_shouldCallSaveEvent() {
        // Arrange
        String eventName = "Sample Event";
        String date = "2023-12-25";
        String time = "18:00";
        String description = "Christmas Gathering";
        String maxAttendees = "100";
        String maxWaitlist = "50";
        boolean geolocationEnabled = true;

        eventNameEditText.setText(eventName);
        dateEditText.setText(date);
        timeEditText.setText(time);
        descriptionEditText.setText(description);
        maxAttendeesEditText.setText(maxAttendees);
        maxWaitlistEditText.setText(maxWaitlist);
        geolocationCheckBox.setChecked(geolocationEnabled);

        // Act
        saveButton.performClick();

        // Process any pending tasks
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Assert
        // Verify Firestore save for Events
        Mockito.verify(mockEventDocument).set(any(Map.class));

        // Since the activity doesn't call finish(), we don't check for activity finishing.
    }
}
