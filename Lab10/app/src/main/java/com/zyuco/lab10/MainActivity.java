package com.zyuco.lab10;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.zyuco.lab10.lib.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "Lab10.Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Map<String, String>> list = new ArrayList<>();
        list.add(new Person("1", "@", "2").toMap());
        list.add(new Person("2", "@", "2").toMap());
        list.add(new Person("3", "@", "2").toMap());
        SimpleAdapter adapter = new SimpleAdapter(
            this,
            list,
            R.layout.item_layout,
            new String[]{"name", "birthday", "gift"},
            new int[]{R.id.name, R.id.birthday, R.id.gift}
        );

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO: goto detail
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO: delete item
                return true;
            }
        });

        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                    new Intent(MainActivity.this, CreateActivity.class),
                    CreateActivity.REQUEST_CREATE_PERSON
                );
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CreateActivity.REQUEST_CREATE_PERSON:
                if (resultCode == RESULT_OK) {
                    Person person = (Person) data.getExtras().getSerializable("person");
                    // TODO: handle create result
                    Log.i(TAG, String.format("get %s", person.name));
                }
            default:
        }
    }
}
