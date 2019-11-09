package com.netlify.abhishekwl.dcloud;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostsFragment extends Fragment {

    private ArrayList<Post> postArrayList = new ArrayList<>();

    private RecyclerView postRecyclerView;
    private View rootView;
    private OkHttpClient okHttpClient;
    private PostRecyclerViewAdapter postRecyclerViewAdapter;
    private String appServerUrl;


    public PostsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_posts, container, false);
        initializeComponents();
        initializeViews();
        fetchData();
        return rootView;
    }

    private void fetchData() {
        Request request = new Request.Builder().url(appServerUrl).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                notifyMessage(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String responseBody = Objects.requireNonNull(response.body()).string();
                    JSONArray responseJsonArray = new JSONArray(responseBody);
                    postArrayList.clear();
                    for (int i=0; i<responseJsonArray.length(); i++) {
                        JSONObject postJson = responseJsonArray.getJSONObject(i);
                        String authorName = postJson.getString("author_name");
                        String authorId = postJson.getString("author_id");
                        String fileName = postJson.getString("name");
                        String postDescription = postJson.getString("description");
                        String postHash = postJson.getString("hash");
                        long fileSize = postJson.getLong("size");
                        boolean isPrivate = postJson.getBoolean("private");
                        if (!isPrivate) {
                            Post post = new Post(authorId, authorName, "", fileName, postDescription, postHash, fileSize);
                            postArrayList.add(post);
                        }
                    }
                    Collections.reverse(postArrayList);
                    postRecyclerViewAdapter = new PostRecyclerViewAdapter(rootView.getContext(), postArrayList);
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> postRecyclerView.setAdapter(postRecyclerViewAdapter));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initializeComponents() {
        okHttpClient = new OkHttpClient();
        appServerUrl = getString(R.string.app_server_url)+"/posts/public";
    }

    private void initializeViews() {
        postRecyclerView = rootView.findViewById(R.id.publicRecyclerView);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        postRecyclerView.setHasFixedSize(true);
    }

    private void notifyMessage(String message) {
        Toast.makeText(rootView.getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
