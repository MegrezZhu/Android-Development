package com.zyuco.lab11.service;

import com.zyuco.lab11.model.Repository;
import com.zyuco.lab11.model.User;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
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
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
        service = retrofit.create(Service.class);
    }

    public void getUser(String username, Observer<User> observer) {
        service
            .getUser(username)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer);
    }

    public void getRepos(String username, Observer<List<Repository>> observer) {
        service
            .getRepos(username)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer);
    }

    public static Github getInstance() {
        if (instance == null) instance = new Github();
        return instance;
    }

    private interface Service {
        @GET("users/{user}")
        Observable<User> getUser(@Path("user") String user);

        @GET("users/{user}/repos")
        Observable<List<Repository>> getRepos(@Path("user") String user);
    }
}
