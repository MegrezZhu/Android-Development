package com.zyuco.lab11;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.zyuco.lab11.model.Repository;
import com.zyuco.lab11.model.User;
import com.zyuco.lab11.service.Github;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Zyuco.main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testNetWork();
    }

    private void testNetWork() {
//        Github.getInstance().getUser("megrezzhu", new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                User user = response.body();
//                Log.i(TAG, String.format("name %s, blog %s, id %d", user.name, user.blog, user.id));
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//
//            }
//        });
        Github.getInstance().getRepos("megrezzhu", new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                List<Repository> repos = response.body();
                for (Repository repo : repos) {
                    Log.i(TAG, String.format("get repo: %s", repo.name));
                }
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {

            }
        });
    }
}
