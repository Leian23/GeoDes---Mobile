package com.example.geodes_mobile.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.geodes_mobile.R;

public class FeedbackFragment extends Fragment {

    private static final int REQUEST_CODE_ATTACHMENT = 123;

    private EditText editTextFeedback;
    private Uri attachmentUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragments_feedback, container, false);

        ImageButton menuButton = rootView.findViewById(R.id.menu_button);
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        // Initialize UI components
        editTextFeedback = rootView.findViewById(R.id.editTextFeedback);
        Button buttonSubmitFeedback = rootView.findViewById(R.id.buttonSubmitFeedback);
        Button buttonAttachFile = rootView.findViewById(R.id.buttonAttachFile);

        // Set click listener for the submit button
        buttonSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the feedback text
                String feedbackText = editTextFeedback.getText().toString();

                // Check if feedback is not empty
                if (!feedbackText.trim().isEmpty()) {
                    // Send feedback via email
                    sendFeedbackEmail(feedbackText);
                } else {
                    // Show a toast message if feedback is empty
                    Toast.makeText(getActivity(), "Please enter your feedback", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for the attach file button
        buttonAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the file picker to choose an attachment
                openFilePicker();
            }
        });

        return rootView;
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_ATTACHMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ATTACHMENT && resultCode == getActivity().RESULT_OK) {
            if (data != null && data.getData() != null) {
                attachmentUri = data.getData();
                Toast.makeText(getActivity(), "Attachment selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendFeedbackEmail(String feedbackText) {
        // Recipient email address
        String recipient = "feedback@example.com";

        // Email subject
        String subject = "Feedback for Your App";

        // Email body
        String body = "Dear Developer,\n\nI would like to provide the following feedback:\n" + feedbackText;

        // Create the email intent
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        // Check if an attachment URI is provided
        if (attachmentUri != null) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, attachmentUri);
        }

        // Check if there is an activity (email app) to handle the intent
        if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        } else {
            // Handle the case where no email client is available
            Toast.makeText(getActivity(), "No email app installed", Toast.LENGTH_SHORT).show();
        }
    }
}
