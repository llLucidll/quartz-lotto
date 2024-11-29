// // Tried to make this into MVC, giving up for now - may come back to it later, otherwise will delete
//package com.example.myapplication.Repositories;
//
//import android.util.Log;
//
//import com.example.myapplication.Models.Attendee;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AttendeesRepository {
//
//    private static final String TAG = "AttendeesRepository";
//    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//
//    public interface AttendeeCallback {
//        void onCallback(List<Attendee> attendees);
//    }
//
//    public void getAttendees(String eventId, AttendeeCallback callback) {
//        db.collection("Events").document(eventId)
//                .collection("Waitlist")
//                .get()
//                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        List<Attendee> attendees = new ArrayList<>();
//                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
//                            Attendee attendee = doc.toObject(Attendee.class);
//                            if (attendee != null) {
//                                String status = attendee.getStatus();
//                                attendee.setUserId(doc.getId());
//                                attendee.setStatus(status);
//                                attendees.add(attendee);
//                            }
//                        }
//                        callback.onCallback(attendees);
//                    }
//                })
//                .addOnFailureListener(e -> Log.e(TAG, "Error fetching attendees: ", e));
//    }
//}
