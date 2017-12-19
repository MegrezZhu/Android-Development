package com.zyuco.lab11;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zyuco.lab11.lib.CommonAdapter;
import com.zyuco.lab11.lib.ViewHolder;
import com.zyuco.lab11.model.Repository;
import com.zyuco.lab11.model.User;
import com.zyuco.lab11.service.Github;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepoActivity extends AppCompatActivity {
    private User user;
    private List<Repository> repoList = new ArrayList<>();

    private CommonAdapter<Repository> repoListAdapter;
    private ViewGroup loadingMask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_repo);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        render();
        loadRepos(user);
    }

    private void render() {
        loadingMask = findViewById(R.id.loading_mask);

        RecyclerView list = findViewById(R.id.repo_list);
        repoListAdapter = new CommonAdapter<Repository>(this, R.layout.repo_card, repoList) {
            @Override
            public void convert(ViewHolder holder, Repository repo) {
                ((TextView) holder.getView(R.id.name)).setText(repo.name);
                ((TextView) holder.getView(R.id.language)).setText(repo.language);
                ((TextView) holder.getView(R.id.description)).setText(repo.description);
            }
        };

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(repoListAdapter);
    }

    private void loadRepos(User user) {
        Github.getInstance().getRepos(user.name, new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                try {
                    List<Repository> data = response.body();
                    if (data == null) {
                        Toast.makeText(UserRepoActivity.this, R.string.toast_network_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    repoList.addAll(data);
                    repoListAdapter.notifyDataSetChanged();
                } finally {
                    setMaskVisible(false);
                }
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                Toast.makeText(UserRepoActivity.this, R.string.toast_network_error, Toast.LENGTH_SHORT).show();
                setMaskVisible(false);
            }
        });
    }

    private void setMaskVisible(boolean visibility) {
        loadingMask.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
    }
}
