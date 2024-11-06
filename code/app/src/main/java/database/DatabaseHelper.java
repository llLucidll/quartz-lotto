package database;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper {

    private final FirebaseFirestore db;

    public DatabaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    //to add a user to the waiting list
    public void addUserToWaitingList(String eventId, String userId, String userName, DatabaseCallback callback) {
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("userName", userName);
        user.put("timestamp", System.currentTimeMillis());

        db.collection("Events")
                .document(eventId)
                .collection("waitingList")
                .document(userId)
                .set(user)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    //maybe if we add another firestore method
}
