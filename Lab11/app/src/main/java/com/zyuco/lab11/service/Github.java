package com.zyuco.lab11.service;

import com.zyuco.lab11.model.Repository;
import com.zyuco.lab11.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class Github {
    private static Github instance;
    private static final String BASE_URL = "https://api.github.com/";

    private Service service;

    private Github() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
        service = retrofit.create(Service.class);
    }

    public void getUser(String username, Callback<User> callback) {
        Call<User> call = service.getUser(username);
        call.enqueue(callback);
    }

    public void getRepos(String username, Callback<List<Repository>> callback) {
        Call<List<Repository>> call = service.getRepos(username);
        call.enqueue(callback);
    }

    public static Github getInstance() {
        if (instance == null) instance = new Github();
        return instance;
    }

    private interface Service {
        @GET("users/{user}")
        Call<User> getUser(@Path("user") String user);

        @GET("users/{user}/repos")
        Call<List<Repository>> getRepos(@Path("user") String user);
    }
}

