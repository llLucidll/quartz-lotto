package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.text.TextUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EditProfileActivityTest {

    private EditProfileActivity activity;

    @Mock
    private Uri mockImageUri;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = spy(new EditProfileActivity());
    }

    @Test
    public void testGenerateDefaultAvatar() {
        String name = "John";
        activity.generateDefaultAvatar(name);

        String expectedFirstLetter = "J"; // First letter of "John"
        assertNotNull(activity.profileImageView); // Ensure the avatar was set
        assertEquals(expectedFirstLetter, name.substring(0, 1).toUpperCase());
    }

    @Test
    public void testValidateInputs_allFieldsValid() {
        when(activity.nameField.getText().toString().trim()).thenReturn("John Doe");
        when(activity.emailField.getText().toString().trim()).thenReturn("john@example.com");
        when(activity.dobField.getText().toString().trim()).thenReturn("01/01/2000");
        when(activity.countrySpinner.getSelectedItem().toString()).thenReturn("Canada");

        boolean isValid = activity.validateInputs();
        assertTrue(isValid);
    }

    @Test
    public void testValidateInputs_missingName() {
        when(activity.nameField.getText().toString().trim()).thenReturn("");
        when(activity.emailField.getText().toString().trim()).thenReturn("john@example.com");
        when(activity.dobField.getText().toString().trim()).thenReturn("01/01/2000");
        when(activity.countrySpinner.getSelectedItem().toString()).thenReturn("Canada");

        boolean isValid = activity.validateInputs();
        assertFalse(isValid);
    }

    @Test
    public void testValidateInputs_invalidEmail() {
        when(activity.nameField.getText().toString().trim()).thenReturn("John Doe");
        when(activity.emailField.getText().toString().trim()).thenReturn("notAnEmail");
        when(activity.dobField.getText().toString().trim()).thenReturn("01/01/2000");
        when(activity.countrySpinner.getSelectedItem().toString()).thenReturn("Canada");

        boolean isValid = activity.validateInputs();
        assertFalse(isValid);
    }

    @Test
    public void testDeleteProfileImage_resetsToDefault() {
        activity.imageUri = mockImageUri;
        activity.deleteProfileImage();

        assertNull(activity.imageUri); // Ensure the image URI is cleared
        verify(activity.profileImageView).setImageResource(R.drawable.ic_profile); // Verify default image is set
    }
}
