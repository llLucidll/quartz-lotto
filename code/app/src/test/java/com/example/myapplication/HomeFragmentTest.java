package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.android.controller.ActivityController;

import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
//
//@RunWith(RobolectricTestRunner.class)
//@Config(sdk = 32) // Adjust SDK version as needed
//public class HomeFragmentTest {
//
//    private HomeFragment homeFragment;
//    private ListView eventsListView;
//    private Button navigateButton;
//
//    // Mocked Firestore components
//    private MockedStatic<FirebaseFirestore> mockedFirestoreStatic;
//    private FirebaseFirestore mockFirestore;
//    private CollectionReference mockEventsCollection;
//    private Task<QuerySnapshot> mockGetTask;
//    private QuerySnapshot mockQuerySnapshot;
//
//    // To host the fragment within an activity
//    private FragmentActivity activity;
//
//    @Before
//    public void setUp() {
//        // Mock FirebaseFirestore.getInstance()
//        mockedFirestoreStatic = Mockito.mockStatic(FirebaseFirestore.class);
//        mockFirestore = Mockito.mock(FirebaseFirestore.class);
//        mockedFirestoreStatic.when(FirebaseFirestore::getInstance).thenReturn(mockFirestore);
//
//        // Mock CollectionReference for "Events"
//        mockEventsCollection = Mockito.mock(CollectionReference.class);
//        Mockito.when(mockFirestore.collection("Events")).thenReturn(mockEventsCollection);
//
//        // Mock Task<QuerySnapshot>
//        mockGetTask = Mockito.mock(Task.class);
//        Mockito.when(mockEventsCollection.get()).thenReturn(mockGetTask);
//
//        // Mock QuerySnapshot
//        mockQuerySnapshot = Mockito.mock(QuerySnapshot.class);
//        Mockito.when(mockGetTask.isSuccessful()).thenReturn(true);
//        Mockito.when(mockGetTask.isComplete()).thenReturn(true);
//        Mockito.when(mockGetTask.getResult()).thenReturn(mockQuerySnapshot);
//
//        // Initialize the fragment within an activity
//        ActivityController<FragmentActivity> controller = Robolectric.buildActivity(FragmentActivity.class)
//                .create()
//                .start()
//                .resume()
//                .visible();
//        activity = controller.get();
//
//        // **Programmatically add a FrameLayout with id fragment_container**
//        FrameLayout container = new FrameLayout(activity);
//        container.setId(R.id.fragment_container); // Ensure this ID exists in your resources
//        activity.setContentView(container);
//
//        // Initialize HomeFragment
//        homeFragment = new HomeFragment();
//        activity.getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.fragment_container, homeFragment)
//                .commit();
//
//        // Ensure fragment's onCreateView is called
//        Robolectric.flushForegroundThreadScheduler();
//
//        // Initialize UI components
//        eventsListView = homeFragment.getView().findViewById(R.id.events_list);
//        navigateButton = homeFragment.getView().findViewById(R.id.edit_or_create_button);
//    }
//
//    @After
//    public void tearDown() {
//        // Close the static mock to avoid interference with other tests
//        mockedFirestoreStatic.close();
//    }
//
//    /**
//     * Helper method to mock Firestore's get() response with given events.
//     */
//    private void mockFirestoreGet(List<Map<String, Object>> eventsData) {
//        List<QueryDocumentSnapshot> mockDocuments = new ArrayList<>();
//        for (Map<String, Object> eventData : eventsData) {
//            QueryDocumentSnapshot mockDoc = Mockito.mock(QueryDocumentSnapshot.class);
//            Mockito.when(mockDoc.getId()).thenReturn((String) eventData.get("eventId"));
//            Mockito.when(mockDoc.getString("eventName")).thenReturn((String) eventData.get("eventName"));
//            mockDocuments.add(mockDoc);
//        }
//        Mockito.when(mockQuerySnapshot.iterator()).thenReturn(mockDocuments.iterator());
//        Mockito.when(mockQuerySnapshot.size()).thenReturn(mockDocuments.size());
//
//        // Mock the addOnCompleteListener to trigger the callback
//        Mockito.doAnswer(invocation -> {
//            OnCompleteListener<QuerySnapshot> listener = invocation.getArgument(0);
//            listener.onComplete(mockGetTask);
//            return null;
//        }).when(mockGetTask).addOnCompleteListener(any(OnCompleteListener.class));
//    }
//
//    /**
//     * Test loading events with valid data should populate the ListView.
//     */
//    @Test
//    public void testLoadEvents_withValidData_shouldPopulateListView() {
//        // Arrange
//        List<Map<String, Object>> eventsData = new ArrayList<>();
//        Map<String, Object> event1 = new HashMap<>();
//        event1.put("eventName", "Event One");
//        event1.put("eventId", "E1");
//        eventsData.add(event1);
//
//        Map<String, Object> event2 = new HashMap<>();
//        event2.put("eventName", "Event Two");
//        event2.put("eventId", "E2");
//        eventsData.add(event2);
//
//        mockFirestoreGet(eventsData);
//
//        // Act
//        homeFragment.loadEvents();
//
//        // Process any pending tasks
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
//
//        // Assert
//        // Verify that the ListView is populated with event names
//        ArrayAdapter adapter = (ArrayAdapter) eventsListView.getAdapter();
//        assertNotNull("Adapter should not be null", adapter);
//        assertEquals("ListView should have 2 items", 2, adapter.getCount());
//        assertEquals("First item should be 'Event One'", "Event One", adapter.getItem(0));
//        assertEquals("Second item should be 'Event Two'", "Event Two", adapter.getItem(1));
//    }
//
//    /**
//     * Test loading events with no data should result in an empty ListView.
//     */
//    @Test
//    public void testLoadEvents_withNoData_shouldShowEmptyList() {
//        // Arrange
//        List<Map<String, Object>> eventsData = new ArrayList<>(); // Empty list
//        mockFirestoreGet(eventsData);
//
//        // Act
//        homeFragment.loadEvents();
//
//        // Process any pending tasks
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
//
//        // Assert
//        ArrayAdapter adapter = (ArrayAdapter) eventsListView.getAdapter();
//        assertNotNull("Adapter should not be null", adapter);
//        assertEquals("ListView should have 0 items", 0, adapter.getCount());
//    }
//
//    /**
//     * Test loading events with Firestore failure should show error Toast.
//     */
//    @Test
//    public void testLoadEvents_withFirestoreFailure_shouldShowErrorToast() {
//        // Arrange
//        Mockito.when(mockGetTask.isSuccessful()).thenReturn(false);
//        mockFirestoreGet(new ArrayList<>()); // Pass an empty list since it's a failure scenario
//
//        // Act
//        homeFragment.loadEvents();
//
//        // Process any pending tasks
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
//
//        // Assert
//        String expectedMessage = "Error loading events";
//        String actualMessage = ShadowToast.getTextOfLatestToast();
//        assertEquals("Should show error toast when Firestore get fails", expectedMessage, actualMessage);
//    }
//
//    /**
//     * Test clicking navigateButton should start CreateEventActivity.
//     */
//    @Test
//    public void testClickNavigateButton_shouldStartCreateEventActivity() {
//        // Arrange
//        Intent expectedIntent = new Intent(activity, CreateEventActivity.class);
//
//        // Act
//        navigateButton.performClick();
//
//        // Assert
//        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
//        Intent actualIntent = shadowActivity.getNextStartedActivity();
//        assertNotNull("An Intent should have been started", actualIntent);
//        assertEquals("Intent should start CreateEventActivity", expectedIntent.getComponent(), actualIntent.getComponent());
//    }
//
//    /**
//     * Test clicking on a list item should start WaitinglistActivity with correct event ID.
//     */
//    @Test
//    public void testListItemClick_shouldStartWaitinglistActivityWithCorrectEventId() {
//        // Arrange
//        List<Map<String, Object>> eventsData = new ArrayList<>();
//        Map<String, Object> event1 = new HashMap<>();
//        event1.put("eventName", "Event One");
//        event1.put("eventId", "E1");
//        eventsData.add(event1);
//
//        mockFirestoreGet(eventsData);
//
//        // Load events
//        homeFragment.loadEvents();
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
//
//        // Act
//        eventsListView.performItemClick(eventsListView, 0, 0);
//
//        // Assert
//        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
//        Intent actualIntent = shadowActivity.getNextStartedActivity();
//        assertNotNull("An Intent should have been started", actualIntent);
//        assertEquals("Intent should start WaitinglistActivity", WaitinglistActivity.class.getName(), actualIntent.getComponent().getClassName());
//        assertEquals("Intent should have the correct event_id extra", "E1", actualIntent.getStringExtra("event_id"));
//    }
//
//    /**
//     * Test onActivityResult with RESULT_OK should reload events.
//     */
//    @Test
//    public void testOnActivityResult_withResultOk_shouldReloadEvents() {
//        // Arrange
//        List<Map<String, Object>> initialEvents = new ArrayList<>();
//        Map<String, Object> event1 = new HashMap<>();
//        event1.put("eventName", "Event One");
//        event1.put("eventId", "E1");
//        initialEvents.add(event1);
//
//        mockFirestoreGet(initialEvents);
//
//        // Load initial events
//        homeFragment.loadEvents();
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
//
//        // Verify initial load
//        ArrayAdapter adapter = (ArrayAdapter) eventsListView.getAdapter();
//        assertNotNull("Adapter should not be null", adapter);
//        assertEquals("ListView should have 1 item", 1, adapter.getCount());
//        assertEquals("First item should be 'Event One'", "Event One", adapter.getItem(0));
//
//        // Mock adding a new event
//        List<Map<String, Object>> updatedEvents = new ArrayList<>();
//        Map<String, Object> event2 = new HashMap<>();
//        event2.put("eventName", "Event Two");
//        event2.put("eventId", "E2");
//        updatedEvents.add(event2);
//        mockFirestoreGet(updatedEvents);
//
//        // Act
//        homeFragment.onActivityResult(HomeFragment.CREATE_EVENT_REQUEST_CODE, Activity.RESULT_OK, null);
//
//        // Process any pending tasks
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
//
//        // Assert
//        // Verify that loadEvents was called and the ListView is updated
//        adapter = (ArrayAdapter) eventsListView.getAdapter();
//        assertNotNull("Adapter should not be null", adapter);
//        assertEquals("ListView should have 1 item after reload", 1, adapter.getCount());
//        assertEquals("First item should be 'Event Two'", "Event Two", adapter.getItem(0));
//    }
//}
