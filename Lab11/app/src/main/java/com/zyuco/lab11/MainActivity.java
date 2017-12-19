package com.zyuco.lab11;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zyuco.lab11.lib.CommonAdapter;
import com.zyuco.lab11.lib.ViewHolder;
import com.zyuco.lab11.model.Repository;
import com.zyuco.lab11.model.User;
import com.zyuco.lab11.service.Github;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Zyuco.main";

    private List<User> userList = new ArrayList<>();
    private CommonAdapter<User> userListAdapter;

    private ViewGroup loadingMask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setClickListeners();
        createUserListView();

        loadingMask = findViewById(R.id.loading_mask);
    }

    private void createUserListView() {
        RecyclerView list = findViewById(R.id.user_list);

        userListAdapter = new CommonAdapter<User>(this, R.layout.user_card, userList) {
            @Override
            public void convert(ViewHolder holder, User user) {
                ((TextView) holder.getView(R.id.name)).setText(user.name);
                ((TextView) holder.getView(R.id.id)).setText(String.format(getResources().getString(R.string.user_id), user.id));
                ((TextView) holder.getView(R.id.blog)).setText(String.format(getResources().getString(R.string.user_blog), user.blog));
            }
        };

        userListAdapter.setOnItemClickListemer(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, Object data) {
                User user = (User) data;
                Log.i(TAG, String.format("clicked: %s", user.name));

                Intent intent = new Intent(MainActivity.this, UserRepoActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position, Object data) {
                userList.remove(position);
                userListAdapter.notifyDataSetChanged();
            }
        });

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(userListAdapter);
    }

    private void setClickListeners() {
        final EditText input = findViewById(R.id.name);

        findViewById(R.id.button_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userList.clear();
                userListAdapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.button_fetch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchUser(input.getText().toString());
            }
        });
    }

    private void fetchUser(String name) {
        setMaskVisible(true);

        Github.getInstance().getUser(name, new Observer<User>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(User user) {
                userList.add(0, user);
                userListAdapter.notifyDataSetChanged();
                setMaskVisible(false);
            }

            @Override
            public void onError(Throwable e) {
                int toastId = R.string.toast_unknown_error;
                if (e instanceof HttpException) {
                    if (((HttpException) e).code() == 404) toastId = R.string.toast_user_not_exist;
                    else toastId = R.string.toast_network_error;
                }
                Toast.makeText(MainActivity.this, toastId, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onError: ", e);
                setMaskVisible(false);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void setMaskVisible(boolean visibility) {
        loadingMask.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
    }
}
