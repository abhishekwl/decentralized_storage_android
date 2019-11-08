package com.netlify.abhishekwl.dcloud;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignInActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private MaterialButton signInButton;
    private TextView createAnAccountTextView;
    private ProgressBar progressBar;

    private OkHttpClient okHttpClient;
    private String appServerUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeComponents();
        initializeViews();
    }

    private void initializeComponents() {
        okHttpClient = new OkHttpClient();
        appServerUrl = getString(R.string.app_server_url);
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.signInEmailEditText);
        passwordEditText = findViewById(R.id.signInPasswordEditText);
        signInButton = findViewById(R.id.signInButton);
        createAnAccountTextView = findViewById(R.id.signInCreateAnAccountTextView);
        progressBar = findViewById(R.id.signInProgressBar);

        signInButton.setOnClickListener(v -> {
            String email = Objects.requireNonNull(emailEditText.getText()).toString();
            String password = Objects.requireNonNull(passwordEditText.getText()).toString();
            signIn(email, password);
        });
    }

    private void signIn(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        String usersEndpoint = appServerUrl+"/users/auth";
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("email", email);
            userJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, userJson.toString());
        Request request = new Request.Builder().url(usersEndpoint).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                notifyMessage(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (response.body() == null) notifyMessage("User account with the given email address does not exist");
                    else {
                        String responseBody = Objects.requireNonNull(response.body()).string();
                        JSONObject userJson = new JSONObject(responseBody);
                        Intent mainActivityIntent = new Intent(SignInActivity.this, MainActivity.class);
                        mainActivityIntent.putExtra("user_id", userJson.getString("_id"));
                        mainActivityIntent.putExtra("user_name", userJson.getString("email"));
                        startActivity(mainActivityIntent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    notifyMessage(e.getMessage());
                }
            }
        });
    }

    private void notifyMessage(String message) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            Snackbar.make(signInButton, message, Snackbar.LENGTH_LONG).show();
        });
    }

}
