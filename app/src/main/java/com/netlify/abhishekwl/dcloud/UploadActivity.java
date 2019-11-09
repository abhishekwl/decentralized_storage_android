package com.netlify.abhishekwl.dcloud;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadActivity extends AppCompatActivity {

    private static final int PICKFILE_REQUEST_CODE = 971;

    private RadioGroup typeRadioGroup, privacyTypeRadioGroup;
    private EditText descriptionEditText;
    private FloatingActionButton addFileFab;
    private MaterialButton uploadButton;
    private TextView selectedFileNameTextView;
    private ProgressBar uploadProgressBar;

    private OkHttpClient okHttpClient;
    private String postType;
    private Uri selectedFileUri;
    private ContentResolver contentResolver;
    private String ipfsPushUrl;
    private String filePath;
    private String fileName;
    private long fileSize;
    private String fileHash;
    private String userId;
    private String userName;
    private String description;
    private String fileMimeType;
    private File selectedFile;
    private MimeTypeMap mimeTypeMap;
    private boolean filePrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initializeComponents();
        initializeViews();
    }

    private void initializeComponents() {
        okHttpClient = new OkHttpClient();
        contentResolver = getContentResolver();
        mimeTypeMap = MimeTypeMap.getSingleton();
        ipfsPushUrl = getString(R.string.ipfs_push_url);
        userId = getIntent().getStringExtra("user_id");
        userName = getIntent().getStringExtra("user_name");
    }

    private void initializeViews() {
        typeRadioGroup = findViewById(R.id.uploadTypeRadioGroup);
        descriptionEditText = findViewById(R.id.uploadDescriptionEditText);
        addFileFab = findViewById(R.id.uploadAddFileFab);
        uploadButton = findViewById(R.id.uploadButton);
        selectedFileNameTextView = findViewById(R.id.selectedFileNameTextView);
        uploadProgressBar = findViewById(R.id.uploadProgressBar);
        privacyTypeRadioGroup = findViewById(R.id.publicOrPrivateTypeRadioGroup);

        privacyTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.uploadPrivateTypeRadioButton:
                    filePrivate = true;
                    break;
                default:
                    filePrivate = false;
            }
        });

        typeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.uploadPhotosRadioButton:
                    postType = "IMAGE";
                    break;
                case R.id.uploadVideosRadioButton:
                    postType = "VIDEO";
                    break;
                default:
                    postType = "POST";
                    break;
            }
        });

        addFileFab.setOnClickListener(v -> {
            Intent pickFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pickFileIntent.setType("*/*");
            startActivityForResult(pickFileIntent, PICKFILE_REQUEST_CODE);
        });

        addFileFab.setColorFilter(Color.WHITE);

        uploadButton.setOnClickListener(v -> uploadFileToIpfs());
    }

    private void uploadFileToIpfs() {
        uploadProgressBar.setVisibility(View.VISIBLE);

        String mimeType = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(selectedFileUri));
        if (Objects.requireNonNull(mimeType).equalsIgnoreCase("jpg") || mimeType.equalsIgnoreCase("png") || mimeType.equalsIgnoreCase("jpeg")) mimeType = "image/"+mimeType;
        else if (mimeType.equalsIgnoreCase("mp4") || mimeType.equalsIgnoreCase("3gp") || mimeType.equalsIgnoreCase("off") || mimeType.equalsIgnoreCase("wmv") || mimeType.equalsIgnoreCase("webm") || mimeType.equalsIgnoreCase("flv") || mimeType.equalsIgnoreCase("avi")) mimeType = "video/"+mimeType;
        else if (mimeType.equalsIgnoreCase("txt")) mimeType = "text/"+mimeType;
        else mimeType = "application/"+mimeType;

        String requestUrl = ipfsPushUrl+"/api/v0/add";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("path", fileName, RequestBody.create(selectedFile, MediaType.get(Objects.requireNonNull(mimeType))))
                .build();

        Request request = new Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                notifyMessage(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String responseBody = Objects.requireNonNull(response.body()).string();
                    JSONObject responseJson = new JSONObject(responseBody);
                    fileHash = responseJson.getString("Hash");
                    uploadFileMetaData(fileName, fileHash);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void uploadFileMetaData(String name, String hash) {
        try {
            String appServerUrl = getString(R.string.app_server_url)+"/posts";
            JSONObject postJson = new JSONObject();
            postJson.put("author_id", userId);
            postJson.put("author_name", userName);
            postJson.put("description", descriptionEditText.getText().toString());
            postJson.put("name", name);
            postJson.put("hash", hash);
            postJson.put("category", postType);
            postJson.put("size", fileSize);
            postJson.put("private", filePrivate);
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(mediaType, postJson.toString());
            Request request = new Request.Builder().url(appServerUrl).post(requestBody).build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    notifyMessage(e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String _id = jsonObject.getString("_id");

                        fileHash = null;
                        fileMimeType = null;
                        fileName = null;
                        filePath = null;
                        filePrivate = false;
                        fileSize = 0;
                        selectedFileUri = null;
                        selectedFile = null;

                        runOnUiThread(() -> {
                            notifyMessage("Post added with id "+_id);
                            selectedFileNameTextView.setText("Not selected");

                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICKFILE_REQUEST_CODE && resultCode==RESULT_OK && data!=null && data.getData()!=null) {
            selectedFileUri = data.getData();
            analyzeUri(selectedFileUri);
        }
    }

    private void analyzeUri(Uri selectedFileUri) {
        Cursor returnCursor = contentResolver.query(selectedFileUri, null, null, null, null);
        if (returnCursor!=null && returnCursor.moveToFirst()) {
            int nameIndex = Objects.requireNonNull(returnCursor).getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            fileName = returnCursor.getString(nameIndex);
            selectedFileNameTextView.setText(fileName);
            fileSize = returnCursor.getLong(sizeIndex);
            returnCursor.close();
            getFileFromUri(selectedFileUri);
        }
    }

    private void notifyMessage(String message) {
        uploadProgressBar.setVisibility(View.GONE);
        Snackbar.make(descriptionEditText, message, Snackbar.LENGTH_LONG).show();
    }

    public void getFileFromUri(Uri contentUri) {
        try {
            InputStream inputStream = contentResolver.openInputStream(contentUri);
            String filePath = getFilesDir().getPath() + fileName;
            selectedFile = new File(filePath);
            OutputStream out = new FileOutputStream(selectedFile);
            byte[] buf = new byte[1024];
            int len;
            while((len= Objects.requireNonNull(inputStream).read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            notifyMessage(e.getMessage());
        }
    }
}
