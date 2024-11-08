package com.example.myapplication;

import android.widget.EditText;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 30)  // Use an appropriate Android SDK version
public class OrganizerProfileActivityTest {

    private OrganizerProfileActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(OrganizerProfileActivity.class).create().get();
    }

    private <T> T getPrivateField(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = OrganizerProfileActivity.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(activity);
    }

    private Object invokePrivateMethod(String methodName, Class<?>[] parameterTypes, Object... args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = OrganizerProfileActivity.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(activity, args);
    }

    @Test
    public void testNameField() {
        try {
            // Access nameField using reflection
            EditText nameField = getPrivateField("nameField");
            nameField.setText("Test Name");

            // Verify nameField text is set correctly
            assertEquals("Test Name", nameField.getText().toString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Reflection failed for testNameField: " + e.getMessage());
        }
    }

    @Test
    public void testEmailFieldValidation() {
        try {
            EditText emailField = getPrivateField("emailField");
            emailField.setText("invalid-email");

            boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailField.getText()).matches();
            assertFalse("Email should be invalid", isValid);

            emailField.setText("test@example.com");
            isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailField.getText()).matches();
            assertTrue("Email should be valid", isValid);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Reflection failed for testEmailFieldValidation: " + e.getMessage());
        }
    }

    @Test
    public void testDOBValidation() {
        try {
            EditText dobField = getPrivateField("dobField");
            dobField.setText("01/01/2000");

            boolean isValid = (boolean) invokePrivateMethod("isDOBValid", new Class[]{String.class}, dobField.getText().toString());
            assertTrue("DOB should be valid", isValid);

            dobField.setText("invalid-date");
            isValid = (boolean) invokePrivateMethod("isDOBValid", new Class[]{String.class}, dobField.getText().toString());
            assertFalse("DOB should be invalid", isValid);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            fail("Reflection failed for testDOBValidation: " + e.getMessage());
        }
    }
}