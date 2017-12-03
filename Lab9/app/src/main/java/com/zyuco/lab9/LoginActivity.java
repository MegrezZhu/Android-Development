package com.zyuco.lab9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private final static String PREFRENCE_NAME = "preference";
    private final static String TAG = "Lab9.Login";

    private final static String KEY_PASSWORD = "password";

    private SharedPreferences pref;
    private boolean hasPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences(PREFRENCE_NAME, MODE_PRIVATE);

        hasPassword = pref.contains(KEY_PASSWORD);

        ViewGroup group = findViewById(R.id.input_group);
        if (hasPassword) {
            group.removeView(findViewById(R.id.new_password));
            group.removeView(findViewById(R.id.confirm_password));
        } else {
            group.removeView(findViewById(R.id.input_password));
        }

        addListeners();
    }

    private void addListeners() {
        final EditText newPw = findViewById(R.id.new_password);
        final EditText conPw = findViewById(R.id.confirm_password);
        final EditText pw = findViewById(R.id.input_password);

        findViewById(R.id.ok_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPassword) {
                    String password = pref.getString(KEY_PASSWORD, null);
                    if (pw.getText().toString().equals(password)) {
                        // password OK
                        // goto file activity
                        startActivity(new Intent(LoginActivity.this, FileActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.toast_mismatched_confirm_password, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (newPw.getText().toString().equals("") || conPw.getText().toString().equals("")) {
                        Toast.makeText(LoginActivity.this, R.string.toast_empty_password, Toast.LENGTH_SHORT).show();
                    } else {
                        String password = newPw.getText().toString();
                        if (password.equals(conPw.getText().toString())) {
                            // OK, then save the password
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(KEY_PASSWORD, password);
                            editor.apply();
                            // and goto file activity
                            startActivity(new Intent(LoginActivity.this, FileActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.toast_mismatched_confirm_password, Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });

        findViewById(R.id.clear_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newPw != null) newPw.setText("");
                if (conPw != null) conPw.setText("");
                if (pw != null) pw.setText("");
            }
        });
    }
}
