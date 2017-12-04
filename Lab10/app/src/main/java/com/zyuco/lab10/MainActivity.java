package com.zyuco.lab10;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.zyuco.lab10.lib.DBAdapter;
import com.zyuco.lab10.lib.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "Lab10.Main";

    DBAdapter dbAdapter;
    List<Map<String, String>> list;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbAdapter = DBAdapter.getInstance(this);

        render();
    }

    private void render () {
        final DBAdapter.CRUD cruder = dbAdapter.getCRUD();
        list = cruder.getAll();

        adapter = new SimpleAdapter(
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
                raiseRemoveItemDialog(i);
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

    private void raiseRemoveItemDialog(final int index) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder
            .setTitle(R.string.dialog_remove_item)
            .setNegativeButton(
                R.string.dialog_no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                }
            )
            .setPositiveButton(
                R.string.dialog_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = list.get(index).get("name");
                        Log.i(TAG, String.format("%s removed", name));
                        list.remove(index);
                        dbAdapter.getCRUD().remove(name);
                        adapter.notifyDataSetChanged();
                    }
                }
            )
            .create();

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CreateActivity.REQUEST_CREATE_PERSON:
                if (resultCode == RESULT_OK) {
                    Person person = (Person) data.getExtras().getSerializable("person");
                    Log.i(TAG, String.format("get %s", person.name));

                    dbAdapter.getCRUD().insert(person);

                    list.add(person.toMap());
                    adapter.notifyDataSetChanged();
                }
            default:
        }
    }
}
