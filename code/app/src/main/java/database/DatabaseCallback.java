package database;


//helps the databasehelper class to communicate whether successful or fail
//to mainactivity
public interface DatabaseCallback {
    void onSuccess();
    void onFailure(Exception e);
}

