package com.zyuco.lab5;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ItemDetailActivity extends AppCompatActivity {
    private boolean starState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        updateData(getIntent().getExtras());

        Button back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemDetailActivity.this.finish();
            }
        });

        final Button star = findViewById(R.id.star_button);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starState = !starState;
                star.setBackgroundResource(starState ? R.mipmap.full_star : R.mipmap.empty_star);
            }
        });

        final Button shoplist = findViewById(R.id.shop_list);
        shoplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = ItemDetailActivity.this.getIntent().getExtras();
                Map<String, String> map = new HashMap<>();
                map.put("name", bundle.get("name").toString());
                map.put("price", bundle.get("price").toString());
                map.put("first", bundle.get("first").toString());
                map.put("type", bundle.get("type").toString());
                map.put("info", bundle.get("info").toString());
                map.put("sname", bundle.get("sname").toString());
                ((Lab5Application)getApplicationContext()).cart.add(map);
                ((Lab5Application)getApplicationContext()).cartAdapter.notifyDataSetChanged();

                Toast
                    .makeText(ItemDetailActivity.this, R.string.item_add_toast,Toast.LENGTH_SHORT)
                    .show();
            }
        });

        final ListView actionList = findViewById(R.id.action_list);
        actionList.setAdapter(new ArrayAdapter<String>(
            this,
            R.layout.action_sheet_item,
            new String[]{
                getString(R.string.item_purchase),
                getString(R.string.item_share),
                getString(R.string.item_no_interest),
                getString(R.string.item_more_sale)
            }
        ));
    }

    private void updateData(Bundle bundle) {
        if (bundle == null) return;

        TextView name = findViewById(R.id.name);
        name.setText(bundle.get("name").toString());

        ImageView image = findViewById(R.id.head_image);
        image.setImageResource(getResources().getIdentifier(bundle.get("sname").toString(), "mipmap", getPackageName()));

        TextView price = findViewById(R.id.price);
        price.setText(bundle.get("price").toString());

        TextView more = findViewById(R.id.more);
        more.setText(bundle.get("type").toString() + " " + bundle.get("info"));
    }
}
