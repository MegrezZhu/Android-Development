package com.zyuco.lab4;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addLoginCheck();
        addRegistCheck();

        final RadioButton radioButtonA = (RadioButton) findViewById(R.id.radio_student);
        radioButtonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar
                    .make(
                        radioButtonA,
                        makeSelectToast(R.string.radio_student),
                        Snackbar.LENGTH_SHORT
                    )
                    .setActionTextColor(getColor(R.color.colorPrimary))
                    .setAction(R.string.action_confirm, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            makeToast(R.string.action_snackbar_clicked);
                        }
                    })
                    .show();
            }
        });

        final RadioButton radioButtonB = (RadioButton) findViewById(R.id.radio_teacher);
        radioButtonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar
                    .make(
                        radioButtonA,
                        makeSelectToast(R.string.radio_teacher),
                        Snackbar.LENGTH_SHORT
                    )
                    .setActionTextColor(getColor(R.color.colorPrimary))
                    .setAction(R.string.action_confirm, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            makeToast(R.string.action_snackbar_clicked);
                        }
                    })
                    .show();
            }
        });
    }

    public void uploadAvatar(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder
            .setTitle(R.string.upload_dialog_title)
            .setView(R.layout.upload_avatar_dialog)
            .setNegativeButton(
                R.string.action_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        makeToast(makeSelectToast(R.string.action_cancel));
                    }
                }
            )
            .create();

        dialog.show();
        Button select1 = (Button) dialog.findViewById(R.id.select1);
        select1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeToast(makeSelectToast(R.string.upload_avatar_select1));
            }
        });

        Button select2 = (Button) dialog.findViewById(R.id.select2);
        select2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeToast(makeSelectToast(R.string.upload_avatar_select2));
            }
        });
    }

    private String makeSelectToast(int id) {
        return getString(R.string.select_toast) + '[' + getString(id) + ']';
    }

    private void makeToast(String what) {
        Toast.makeText(
            getApplication(),
            what,
            Toast.LENGTH_SHORT
        ).show();
    }

    private void makeToast(int id) {
        makeToast(getString(id));
    }

    private void addLoginCheck() {
        Button loginButton = (Button) findViewById(R.id.button_login);
        final TextInputLayout netidInput = (TextInputLayout) findViewById(R.id.id_input);
        final TextInputLayout passwordInput = (TextInputLayout) findViewById(R.id.password_input);
        final EditText netidEdit = (EditText) findViewById(R.id.edit_id);
        final EditText passwordEdit = (EditText) findViewById(R.id.edit_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable netid = netidEdit.getText();
                Editable password = passwordEdit.getText();

                if (netid != null && TextUtils.isEmpty(netid.toString())) {
                    netidInput.setError(getString(R.string.login_failed_empty_id));
                    return;
                }
                if (password != null && TextUtils.isEmpty(password.toString())) {
                    passwordInput.setError(getString(R.string.login_failed_empty_password));
                    return;
                }

                if (!netid.toString().equals("123456") || !password.toString().equals("6666")) {
                    // id & pw error
                    Snackbar
                        .make(
                            netidInput,
                            R.string.login_failed,
                            Snackbar.LENGTH_SHORT
                        )
                        .setActionTextColor(getColor(R.color.colorPrimary))
                        .setAction(R.string.action_confirm, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                makeToast(R.string.action_snackbar_clicked);
                            }
                        })
                        .show();
                    return;
                }

                Snackbar
                    .make(
                        netidInput,
                        R.string.login_succeeded,
                        Snackbar.LENGTH_SHORT
                    )
                    .setActionTextColor(getColor(R.color.colorPrimary))
                    .setAction(R.string.action_confirm, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            makeToast(R.string.action_snackbar_clicked);
                        }
                    })
                    .show();
            }
        });

        netidEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                netidInput.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordInput.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void addRegistCheck() {
        final Button registButton = (Button) findViewById(R.id.button_regist);
        final RadioButton student = (RadioButton) findViewById(R.id.radio_student);
        final RadioButton teacher = (RadioButton) findViewById(R.id.radio_teacher);

        registButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (student.isChecked()) {
                    Snackbar
                        .make(
                            registButton,
                            R.string.regist_student_not_supported,
                            Snackbar.LENGTH_SHORT
                        )
                        .setActionTextColor(getColor(R.color.colorPrimary))
                        .setAction(R.string.action_confirm, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                makeToast(R.string.action_snackbar_clicked);
                            }
                        })
                        .show();
                    return;
                }

                if (teacher.isChecked()) {
                    makeToast(R.string.regist_teacher_not_supported);
                    return;
                }

            }
        });
    }
}
