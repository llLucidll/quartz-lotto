//package com.example.myapplication;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.mock;
//
//import android.os.Build;
//import android.os.Looper;
//import android.widget.ImageButton;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapplication.WaitinglistActivity;
//import com.example.myapplication.Attendee;
//import com.example.myapplication.AttendeeAdapter;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.Shadows;
//import org.robolectric.annotation.Config;
//import org.robolectric.junit.jupiter.RobolectricExtension;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@ExtendWith(RobolectricExtension.class)  // Enables JUnit 5 + Robolectric integration
//@Config(sdk = Build.VERSION_CODES.P)     // Specifies the SDK version to emulate
//public class WaitinglistActivityTest {
//
//    private WaitinglistActivity activity;
//    private RecyclerView recyclerView;
//    private AttendeeAdapter adapter;
//    private List<Attendee> attendeeList;
//
//    @Mock
//    private LinearLayoutManager layoutManager;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // Initialize the activity
//        activity = Robolectric.buildActivity(WaitinglistActivity.class)
//                .create()
//                .start()
//                .resume()
//                .visible()
//                .get();
//
//        // Initialize test attendees list
//        attendeeList = new ArrayList<>();
//        attendeeList.add(new Attendee("1", "John Doe", "not chosen"));
//
//        // Set up RecyclerView and Adapter
//        recyclerView = activity.findViewById(R.id.recyclerViewAttendees);
//        adapter = new AttendeeAdapter(attendeeList, false, true);
//
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(adapter);
//
//        // Run any pending UI tasks to complete initialization
//        Shadows.shadowOf(Looper.getMainLooper()).idle();
//    }
//
//    @Test
//    public void testRecyclerViewSetup() {
//        // Verify RecyclerView and its components are set up correctly
//        assertNotNull(recyclerView, "RecyclerView should be initialized");
//        assertNotNull(recyclerView.getLayoutManager(), "RecyclerView should have a LayoutManager");
//        assertNotNull(recyclerView.getAdapter(), "RecyclerView should have an Adapter");
//
//        // Verify that the adapter has the correct item count
//        assertEquals(1, adapter.getItemCount(), "Adapter should contain 1 item");
//    }
//}
