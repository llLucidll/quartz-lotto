import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.os.Bundle;
import android.os.Looper;

import com.example.myapplication.WaitinglistActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test class for {@link WaitinglistActivity}.
 * This test focuses on testing the logic of loading attendees without actually accessing Firebase.
 */
public class WaitinglistActivityTest {

    // Mocks for Firestore and Firestore-related classes
    @Mock
    FirebaseFirestore db;

    @Mock
    CollectionReference usersRef;

    @Mock
    Query query;

    @Mock
    QuerySnapshot querySnapshot;

    @Mock
    QueryDocumentSnapshot documentSnapshot;

    @Mock
    private Looper looper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Mock the looper method
        when(Looper.myLooper()).thenReturn(looper);
    }

    // The activity under test
    private WaitinglistActivity waitinglistActivity;


    /**
     * Test method for {@link WaitinglistActivity#loadNotChosenAttendees()}.
     * This test verifies that the attendees list is populated correctly without accessing Firestore.
     * The Firestore operations are mocked to simulate the database response.
     */

    @Test
    public void testLoadNotChosenAttendees() throws Exception {
        // Using reflection to call the protected method onCreate
        Method onCreateMethod = WaitinglistActivity.class.getDeclaredMethod("onCreate", Bundle.class);
        onCreateMethod.setAccessible(true); // Make the method accessible

        // Call the protected onCreate method with null (Bundle)
        onCreateMethod.invoke(waitinglistActivity, (Bundle) null);

        // Now you can test your logic after calling onCreate
        assertNotNull(waitinglistActivity.getAttendees()); // Verify attendees list is not null
        assertEquals(1, waitinglistActivity.getAttendees().size()); // Assuming one attendee is added
    }


}
