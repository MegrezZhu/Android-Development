package com.zyuco.lab10;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.zyuco.lab10.lib.DBAdapter;
import com.zyuco.lab10.lib.Person;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "Lab10.Main";
    final static int PERMISSION_REQUSET = 23333;

    DBAdapter dbAdapter;
    List<Map<String, String>> list;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyPermissions();

        dbAdapter = DBAdapter.getInstance(this);

        render();
    }

    private void render() {
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
                openEditDialog(Person.fromMap(list.get(i)), i);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                openRemoveItemDialog(i);
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

    private void openEditDialog(final Person person, final int index) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.item_edit_layout, null);

        renderEditDialog(dialogView, person);

        final AlertDialog dialog = builder
            .setTitle(R.string.dialog_edit_title)
            .setView(dialogView)
            .setNegativeButton(
                R.string.dialog_edit_discard,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                }
            )
            .setPositiveButton(
                R.string.dialog_edit_save,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        person.birthday = ((EditText) dialogView.findViewById(R.id.birthday)).getText().toString();
                        person.gift = ((EditText) dialogView.findViewById(R.id.gift)).getText().toString();

                        dbAdapter.getCRUD().update(person);
                        list.set(index, person.toMap());

                        adapter.notifyDataSetChanged();
                    }
                }
            )
            .create();

        dialog.show();
    }

    private void renderEditDialog(View dialogView, Person person) {
        ((TextView) dialogView.findViewById(R.id.name)).setText(person.name);
        ((EditText) dialogView.findViewById(R.id.birthday)).setText(person.birthday);
        ((EditText) dialogView.findViewById(R.id.gift)).setText(person.gift);

        String phone = getPhone(person.name);
        ((TextView) dialogView.findViewById(R.id.phone)).setText(String.format(getString(R.string.phone), phone != null ? phone : "无"));
    }

    private void openRemoveItemDialog(final int index) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder
            .setTitle(R.string.dialog_remove_title)
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

    private String getPhone(String name) {
        String[] projection = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
        };
        try (Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, ContactsContract.Contacts.DISPLAY_NAME + "= ?", new String[]{name}, null)) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Log.i(TAG, String.format("get id %d", id));
                try (Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null)) {
                    if (phone.moveToFirst()) {
                        return phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                }
            }
        }
        return null;
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

    private void verifyPermissions() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUSET);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUSET) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                MainActivity.this.finish();
                System.exit(0);
            }
        }
    }
}
