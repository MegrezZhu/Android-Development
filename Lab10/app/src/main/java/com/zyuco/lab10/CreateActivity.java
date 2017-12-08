package com.zyuco.lab10;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zyuco.lab10.lib.DBAdapter;
import com.zyuco.lab10.lib.Person;

public class CreateActivity extends AppCompatActivity {
    final public static int REQUEST_CREATE_PERSON = 0x2333;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        setListeners();
    }

    private void setListeners() {
        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((EditText)findViewById(R.id.name)).getText().toString();
                if (name.equals("")) {
                    Toast.makeText(CreateActivity.this, R.string.toast_empty_name, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (DBAdapter.getInstance().getCRUD().duplicated(name)) {
                    Toast.makeText(CreateActivity.this, R.string.toast_duplicate_name, Toast.LENGTH_SHORT).show();
                    return;
                }

                String birthday = ((EditText)findViewById(R.id.birthday)).getText().toString();
                String gift = ((EditText)findViewById(R.id.gift)).getText().toString();

                Person person = new Person(name, birthday, gift);

                Intent result = new Intent();
                result.putExtra("person", person);

                setResult(RESULT_OK, result);
                finish();
            }
        });
    }
}
