//package com.example.myapplication;
//
//import android.content.Context;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.ArgumentMatchers;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.annotation.Config;
//import org.robolectric.shadows.ShadowLooper;
//import org.robolectric.shadows.ShadowToast;
//import org.robolectric.android.controller.ActivityController;
//import org.robolectric.RuntimeEnvironment;
//
//import static org.junit.Assert.*;
//
//import java.util.Map;
//
//@RunWith(RobolectricTestRunner.class)
//@Config(sdk = 32) // Align with your targetSdkVersion
//public class AddFacilityActivityTest {
//
//    private AddFacilityActivity activity;
//    private EditText nameField;
//    private EditText locationField;
//    private Button saveButton;
//
//    // Mocked Firebase Components
//    private MockedStatic<FirebaseAuth> mockedAuth;
//    private MockedStatic<FirebaseFirestore> mockedFirestore;
//    private FirebaseAuth mockFirebaseAuth;
//    private FirebaseFirestore mockFirestoreInstance;
//    private CollectionReference mockCollectionReference;
//    private Task mockAddTask;
//    @Before
//    public void setUp() {
//        Context context = Robolectric.buildActivity(AddFacilityActivity.class).get().getApplicationContext();
//
//        // Initialize FirebaseApp if not already initialized
//        if (FirebaseApp.getApps(context).isEmpty()) {
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setApplicationId("1:1234567890:android:abcdef") // Dummy value
//                    .setApiKey("AIzaSyA...") // Dummy value
//                    .setProjectId("dummy-project-id") // Dummy value
//                    .setDatabaseUrl("https://dummy-database.firebaseio.com") // Dummy value
//                    .build();
//            FirebaseApp.initializeApp(context, options);
//        }
//
//        // Mock FirebaseAuth.getInstance() and getCurrentUser()
//        mockedAuth = Mockito.mockStatic(FirebaseAuth.class);
//        mockFirebaseAuth = Mockito.mock(FirebaseAuth.class);
//        FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
//        mockedAuth.when(FirebaseAuth::getInstance).thenReturn(mockFirebaseAuth);
//        Mockito.when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockUser);
//        Mockito.when(mockUser.getUid()).thenReturn("mockUserId");
//
//        // Mock FirebaseFirestore.getInstance()
//        mockedFirestore = Mockito.mockStatic(FirebaseFirestore.class);
//        mockFirestoreInstance = Mockito.mock(FirebaseFirestore.class);
//        mockedFirestore.when(FirebaseFirestore::getInstance).thenReturn(mockFirestoreInstance);
//
//        // Mock CollectionReference and Task<DocumentReference>
//        mockCollectionReference = Mockito.mock(CollectionReference.class);
//        mockAddTask = Mockito.mock(Task.class);
//
//        // Define behavior for firestore.collection("Facilities")
//        Mockito.when(mockFirestoreInstance.collection("Facilities")).thenReturn(mockCollectionReference);
//
//        // Define behavior for collectionReference.add(any())
//        Mockito.when(mockCollectionReference.add(ArgumentMatchers.any())).thenReturn(mockAddTask);
//
//        // Define behavior for mockAddTask (simulate successful addition)
//        Mockito.doAnswer(invocation -> {
//            OnSuccessListener<DocumentReference> successListener = invocation.getArgument(0);
//            successListener.onSuccess(Mockito.mock(DocumentReference.class));
//            return mockAddTask;
//        }).when(mockAddTask).addOnSuccessListener(ArgumentMatchers.any(OnSuccessListener.class));
//
//        Mockito.doAnswer(invocation -> {
//            OnFailureListener failureListener = invocation.getArgument(0);
//            // Uncomment the next line to simulate a failure
//            // failureListener.onFailure(new Exception("Mock failure"));
//            return mockAddTask;
//        }).when(mockAddTask).addOnFailureListener(ArgumentMatchers.any(OnFailureListener.class));
//
//        // Initialize the Activity after setting up mocks
//        ActivityController<AddFacilityActivity> controller = Robolectric.buildActivity(AddFacilityActivity.class)
//                .create()
//                .start()
//                .resume()
//                .visible();
//        activity = controller.get();
//
//        // Initialize UI components with correct IDs
//        nameField = activity.findViewById(R.id.facility_name);
//        locationField = activity.findViewById(R.id.facility_location);
//        saveButton = activity.findViewById(R.id.saveFacilityButton);
//
//        // Assertions to ensure views are found
//        assertNotNull("facility_name EditText not found", nameField);
//        assertNotNull("facility_location EditText not found", locationField);
//        assertNotNull("saveButton Button not found", saveButton);
//    }
//    @After
//    public void tearDown() {
//        // Close mocked static instances to prevent memory leaks and interference with other tests
//        mockedAuth.close();
//        mockedFirestore.close();
//    }
//
//    @Test
//    public void testSaveFacilityDetails_withEmptyName_shouldShowToast() {
//        // Arrange
//        nameField.setText(""); // Empty name
//        locationField.setText("Downtown"); // Valid location
//
//        // Act
//        saveButton.performClick();
//
//        // Assert
//        String latestToast = ShadowToast.getTextOfLatestToast();
//        assertEquals("Please enter all required fields", latestToast);
//    }
//
//    @Test
//    public void testSaveFacilityDetails_withEmptyLocation_shouldShowToast() {
//        // Arrange
//        nameField.setText("Gym"); // Valid name
//        locationField.setText(""); // Empty location
//
//        // Act
//        saveButton.performClick();
//
//        // Assert
//        String latestToast = ShadowToast.getTextOfLatestToast();
//        assertEquals("Please enter all required fields", latestToast);
//    }
//
//    @Test
//    public void testSaveFacilityDetails_withEmptyNameAndLocation_shouldShowToast() {
//        // Arrange
//        nameField.setText(""); // Empty name
//        locationField.setText(""); // Empty location
//
//        // Act
//        saveButton.performClick();
//
//        // Assert
//        String latestToast = ShadowToast.getTextOfLatestToast();
//        assertEquals("Please enter all required fields", latestToast);
//    }
//    @Test
//    public void testSaveFacilityDetails_withValidInputs_shouldProceed() {
//        // Arrange
//        nameField.setText("Library"); // Valid name
//        locationField.setText("Midtown"); // Valid location
//
//        // Act
//        saveButton.performClick();
//
//        // Process pending runnables to trigger listeners
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
//
//        // Assert
//        // Verify that collection("Facilities").add() was called with correct data
//        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
//        Mockito.verify(mockCollectionReference).add(captor.capture());
//
//        Map<String, Object> addedData = captor.getValue();
//        assertEquals("Library", addedData.get("name"));
//        assertEquals("Midtown", addedData.get("location"));
//
//        // Verify that a success Toast is shown
//        String latestToast = ShadowToast.getTextOfLatestToast();
//        assertEquals("Facility added successfully!", latestToast);
//    }
//}
