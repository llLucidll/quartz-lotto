package com.example.myapplication;

import android.content.Intent;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.android.controller.ActivityController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 32) // Ensure this matches your targetSdkVersion

/*
Testing for correct methods in Attending Activity
TODO CURRENTLY THE TESTS FAIL IMPLYING THERE IS A BUG SOMEWHERE IN THE CODE. NEED TO FIX THIS AFTER HALFWAY
 */
public class AttendingActivityTest {

    private AttendingActivity activity;
    private RecyclerView recyclerViewWaitlist;
    private RecyclerView recyclerViewCancelled;
    private RecyclerView recyclerViewConfirmed;
    private AttendeeAdapter waitlistAdapter;
    private AttendeeAdapter cancelledAdapter;
    private AttendeeAdapter confirmedAdapter;
    private List<Attendee> waitlist;
    private List<Attendee> cancelledList;
    private List<Attendee> confirmedList;

    // Mocked Firebase Components
    private MockedStatic<FirebaseAuth> mockedAuth;
    private MockedStatic<FirebaseFirestore> mockedFirestore;
    private FirebaseAuth mockFirebaseAuth;
    private FirebaseFirestore mockFirestoreInstance;
    private CollectionReference mockEventsCollection;
    private CollectionReference mockUsersCollection;
    private DocumentReference mockEventDocument;
    private DocumentReference mockUserDocument;
    private Task<DocumentSnapshot> mockEventGetTask;
    private Task<DocumentSnapshot> mockUserGetTask;
    private Task<Void> mockUserUpdateTask;

    @Before
    public void setUp() {
        // Mock FirebaseAuth.getInstance() and getCurrentUser()
        mockedAuth = Mockito.mockStatic(FirebaseAuth.class);
        mockFirebaseAuth = Mockito.mock(FirebaseAuth.class);
        mockedAuth.when(FirebaseAuth::getInstance).thenReturn(mockFirebaseAuth);
        FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
        Mockito.when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockUser);
        Mockito.when(mockUser.getUid()).thenReturn("mockUserId");

        // Mock FirebaseFirestore.getInstance()
        mockedFirestore = Mockito.mockStatic(FirebaseFirestore.class);
        mockFirestoreInstance = Mockito.mock(FirebaseFirestore.class);
        mockedFirestore.when(FirebaseFirestore::getInstance).thenReturn(mockFirestoreInstance);

        // Mock CollectionReference for "Events" and "Users"
        mockEventsCollection = Mockito.mock(CollectionReference.class);
        mockUsersCollection = Mockito.mock(CollectionReference.class);
        Mockito.when(mockFirestoreInstance.collection("Events")).thenReturn(mockEventsCollection);
        Mockito.when(mockFirestoreInstance.collection("Users")).thenReturn(mockUsersCollection);

        // Mock DocumentReference for a specific event and user
        String testEventId = "testEventId123";
        String testUserId = "testUserId456";

        mockEventDocument = Mockito.mock(DocumentReference.class);
        Mockito.when(mockEventsCollection.document(testEventId)).thenReturn(mockEventDocument);

        mockUserDocument = Mockito.mock(DocumentReference.class);
        Mockito.when(mockUsersCollection.document(testUserId)).thenReturn(mockUserDocument);

        // Mock Tasks
        mockEventGetTask = Mockito.mock(Task.class);
        mockUserGetTask = Mockito.mock(Task.class);
        mockUserUpdateTask = Mockito.mock(Task.class);

        // Define behavior for eventRef.get()
        Mockito.when(mockEventDocument.get()).thenReturn(mockEventGetTask);
        Mockito.when(mockUserDocument.get()).thenReturn(mockUserGetTask);
        Mockito.when(mockUserDocument.update(ArgumentMatchers.eq("status"), ArgumentMatchers.anyString()))
                .thenReturn(mockUserUpdateTask);

        // Mocking Firestore responses for event.get()
        Mockito.when(mockEventGetTask.isSuccessful()).thenReturn(true);
        Mockito.when(mockEventGetTask.isComplete()).thenReturn(true);
        DocumentSnapshot mockEventSnapshot = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(mockEventGetTask.getResult()).thenReturn(mockEventSnapshot);

        // Mock "waitlist" field in event document
        List<Map<String, Object>> mockWaitlist = new ArrayList<>();
        Map<String, Object> attendee1 = new HashMap<>();
        attendee1.put("arrayField", List.of(testUserId, "waiting"));
        mockWaitlist.add(attendee1);
        Mockito.when(mockEventSnapshot.get("waitlist")).thenReturn(mockWaitlist);

        // Mocking Firestore responses for user.get()
        Mockito.when(mockUserGetTask.isSuccessful()).thenReturn(true);
        Mockito.when(mockUserGetTask.isComplete()).thenReturn(true);
        DocumentSnapshot mockUserSnapshot = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(mockUserGetTask.getResult()).thenReturn(mockUserSnapshot);
        Mockito.when(mockUserSnapshot.getString("name")).thenReturn("John Doe");

        // Mocking update task
        Mockito.when(mockUserUpdateTask.isSuccessful()).thenReturn(true);
        Mockito.when(mockUserUpdateTask.isComplete()).thenReturn(true);

        // Create a real Intent with "event_id"
        Intent intent = new Intent();
        intent.putExtra("event_id", testEventId);

        // Initialize the Activity with the real Intent after setting up mocks
        ActivityController<AttendingActivity> controller = Robolectric.buildActivity(AttendingActivity.class, intent)
                .create()
                .start()
                .resume()
                .visible();
        activity = controller.get();

        // Process pending tasks to ensure Firestore callbacks are executed
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Initialize UI components with correct IDs
        recyclerViewWaitlist = activity.findViewById(R.id.recyclerViewWaitlist);
        recyclerViewCancelled = activity.findViewById(R.id.recyclerViewCancelled);
        recyclerViewConfirmed = activity.findViewById(R.id.recyclerViewConfirmed);

        recyclerViewWaitlist.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewCancelled.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewConfirmed.setLayoutManager(new LinearLayoutManager(activity));

        waitlist = new ArrayList<>();
        cancelledList = new ArrayList<>();
        confirmedList = new ArrayList<>();

        // Use spies to allow verification of adapter methods
        waitlistAdapter = Mockito.spy(new AttendeeAdapter(waitlist, true, false));
        cancelledAdapter = Mockito.spy(new AttendeeAdapter(cancelledList, false, false));
        confirmedAdapter = Mockito.spy(new AttendeeAdapter(confirmedList, false, false));

        recyclerViewWaitlist.setAdapter(waitlistAdapter);
        recyclerViewCancelled.setAdapter(cancelledAdapter);
        recyclerViewConfirmed.setAdapter(confirmedAdapter);

        // Assertions to ensure views are found
        assertNotNull("recyclerViewWaitlist not found", recyclerViewWaitlist);
        assertNotNull("recyclerViewCancelled not found", recyclerViewCancelled);
        assertNotNull("recyclerViewConfirmed not found", recyclerViewConfirmed);
    }

    @After
    public void tearDown() {
        // Close mocked static instances to prevent memory leaks and interference with other tests
        mockedAuth.close();
        mockedFirestore.close();
    }

    @Test
    public void testLoadAttendingList_withValidData_shouldPopulateRecyclerViews() {
        // Act
        // The initial Firestore load has already been processed in setUp()
        // No additional actions required here

        // Assert
        // Verify waitlist is populated
        assertEquals(1, waitlist.size());
        Attendee attendee = waitlist.get(0);
        assertEquals("testUserId456", attendee.getUserID());
        assertEquals("John Doe", attendee.getName());
        assertEquals("waiting", attendee.getStatus());

        // Verify adapters are notified
        Mockito.verify(waitlistAdapter, Mockito.times(1)).notifyDataSetChanged();
        Mockito.verify(cancelledAdapter, Mockito.never()).notifyDataSetChanged();
        Mockito.verify(confirmedAdapter, Mockito.never()).notifyDataSetChanged();
    }

    @Test
    public void testMoveToCancelled_shouldUpdateFirestoreAndRecyclerViews() {
        // Arrange
        // At this point, the initial Firestore load has been processed in setUp()
        // Verify initial load
        assertEquals(1, waitlist.size());
        Attendee initialAttendee = waitlist.get(0);
        assertEquals("testUserId456", initialAttendee.getUserID());
        assertEquals("John Doe", initialAttendee.getName());
        assertEquals("waiting", initialAttendee.getStatus());

        // Act
        activity.moveToCancelled(0);

        // Assert
        // Verify Firestore update
        Mockito.verify(mockUserDocument).update("status", "cancelled");

        // Verify lists are updated
        assertEquals(0, waitlist.size());
        assertEquals(1, cancelledList.size());
        Attendee cancelledAttendee = cancelledList.get(0);
        assertEquals("testUserId456", cancelledAttendee.getUserID());
        assertEquals("John Doe", cancelledAttendee.getName());
        assertEquals("cancelled", cancelledAttendee.getStatus());

        // Verify adapters are notified
        Mockito.verify(waitlistAdapter, Mockito.times(1)).notifyDataSetChanged();
        Mockito.verify(cancelledAdapter, Mockito.times(1)).notifyDataSetChanged();
    }

    @Test
    public void testMoveToConfirmed_shouldUpdateFirestoreAndRecyclerViews() {
        // Arrange
        // At this point, the initial Firestore load has been processed in setUp()
        // Verify initial load
        assertEquals(1, waitlist.size());
        Attendee initialAttendee = waitlist.get(0);
        assertEquals("testUserId456", initialAttendee.getUserID());
        assertEquals("John Doe", initialAttendee.getName());
        assertEquals("waiting", initialAttendee.getStatus());

        // Act
        activity.moveToConfirmed(0);

        // Assert
        // Verify Firestore update
        Mockito.verify(mockUserDocument).update("status", "confirmed");

        // Verify lists are updated
        assertEquals(0, waitlist.size());
        assertEquals(1, confirmedList.size());
        Attendee confirmedAttendee = confirmedList.get(0);
        assertEquals("testUserId456", confirmedAttendee.getUserID());
        assertEquals("John Doe", confirmedAttendee.getName());
        assertEquals("confirmed", confirmedAttendee.getStatus());

        // Verify adapters are notified
        Mockito.verify(waitlistAdapter, Mockito.times(1)).notifyDataSetChanged();
        Mockito.verify(confirmedAdapter, Mockito.times(1)).notifyDataSetChanged();
    }

    // Additional test methods can be added here to cover more scenarios
}
