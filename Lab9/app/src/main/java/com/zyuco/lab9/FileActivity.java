package com.zyuco.lab9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileActivity extends AppCompatActivity {
    private final static String TAG = "Lab9.FileEdit";

    private EditText filenameView;
    private EditText contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        filenameView = findViewById(R.id.edit_file_name);
        contentView = findViewById(R.id.edit_file_content);

        addListeners();
    }

    private void addListeners() {
        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filename = filenameView.getText().toString();
                if (filename.equals("")) {
                    Toast.makeText(FileActivity.this, R.string.toast_file_name_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                String content = contentView.getText().toString();
                if (content.equals("")) {
                    Toast.makeText(FileActivity.this, R.string.toast_file_content_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                // write to file
                try (FileOutputStream file = openFileOutput(filename, MODE_PRIVATE)) {
                    file.write(content.getBytes());
                    Toast.makeText(FileActivity.this, R.string.toast_file_save_success, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.e(TAG, "onClick: ", e);
                }
            }
        });

        findViewById(R.id.button_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filename = filenameView.getText().toString();
                if (filename.equals("")) {
                    Toast.makeText(FileActivity.this, R.string.toast_file_name_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                try (FileInputStream file = openFileInput(filename)) {
                    byte[] content = new byte[file.available()];
                    file.read(content);

                    // apply content
                    contentView.setText(new String(content, "UTF-8"));
                    Toast.makeText(FileActivity.this, R.string.toast_file_load_success, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.e(TAG, "onClick: ", e);
                    Toast.makeText(FileActivity.this, R.string.toast_file_load_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.button_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentView.setText("");
            }
        });

        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filename = filenameView.getText().toString();
                if (filename.equals("")) {
                    Toast.makeText(FileActivity.this, R.string.toast_file_name_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                deleteFile(filename);
                Toast.makeText(FileActivity.this, R.string.toast_file_delete_success, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
