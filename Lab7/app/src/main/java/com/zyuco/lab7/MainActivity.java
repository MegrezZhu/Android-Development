package com.zyuco.lab7;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.zyuco.lab7.widget.Provider;
import com.zyuco.lab7.widget.UpdateReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

public class MainActivity extends AppCompatActivity {
    private boolean isMainPage = true;
    List<Map<String, String>> items = new LinkedList<>();
    List<Map<String, String>> cart = new LinkedList<>();
    SimpleAdapter cartAdapter;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("123", "new intent!");
        goCart(intent);
    }

    private void goCart(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey("goCart") && bundle.get("goCart").equals(true)) {
            togglePage(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        bindClickListeners();

        RecyclerView list = findViewById(R.id.item_list);

        if (list != null) {
            list.setLayoutManager(new LinearLayoutManager(this));
        }
        final CommonAdapter<Map<String, String>> adapter = new CommonAdapter<Map<String, String>>(this, R.layout.list_item, items) {
            @Override
            public void convert(ViewHolder holder, Map<String, String> s) {
                TextView nameView = holder.getView(R.id.name);
                String name = s.get("name");
                nameView.setText(name);
                TextView first = holder.getView(R.id.first);
                first.setText(s.get("first"));
            }
        };
        adapter.setOnItemClickListemer(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                openItemDetail(items.get(position));
            }

            @Override
            public void onLongClick(int position) {
                items.remove(position);
                adapter.notifyDataSetChanged();
                Toast
                    .makeText(
                        MainActivity.this,
                        String.format(MainActivity.this.getString(R.string.item_deleted_toast), position + 1),
                        Toast.LENGTH_SHORT)
                    .show();
            }
        });
        ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(adapter);
        animationAdapter.setDuration(1000);
        list.setAdapter(animationAdapter);
        list.setItemAnimator(new OvershootInLeftAnimator());

        final ListView list1 = findViewById(R.id.cart_list);
        final SimpleAdapter adapter1 = new SimpleAdapter(
            this,
            cart,
            R.layout.cart_item,
            new String[]{"first", "name", "price"},
            new int[]{R.id.first, R.id.name, R.id.price});
        list1.setAdapter(adapter1);
        cartAdapter = adapter1;
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openItemDetail(cart.get(i));
            }
        });
        list1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int index, long l) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final AlertDialog dialog = builder
                    .setTitle(R.string.dialog_remove_item)
                    .setView(R.layout.dialog_remove_item)
                    .setNegativeButton(
                        R.string.dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }
                    )
                    .setPositiveButton(
                        R.string.dialog_confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cart.remove(index);
                                adapter1.notifyDataSetChanged();
                            }
                        }
                    )
                    .create();

                dialog.show();

                TextView message = dialog.findViewById(R.id.message);
                message.setText(String.format(getString(R.string.dialog_remove_item_from_cart), cart.get(index).get("name")));
                return true;
            }
        });

        subscribeEvents();
        sendInitialBroadcast();
        goCart(getIntent());
    }

    private void sendInitialBroadcast() {
        Intent broadcast = new Intent(this, StaticReceiver.class); // a fxxking must-do in API 26!
        broadcast.setAction(StaticReceiver.SHOW_RECOMMEND);
        Random random = new Random();
        int selected = random.nextInt(items.size());
        Map<String, String> item = items.get(selected);
        Bundle bundle = new Bundle();
        bundle.putString("name", item.get("name"));
        bundle.putString("price", item.get("price"));
        bundle.putString("type", item.get("type"));
        bundle.putString("info", item.get("info"));
        bundle.putString("sname", item.get("sname"));
        bundle.putString("first", item.get("first"));
        broadcast.putExtras(bundle);
        sendBroadcast(broadcast);
        Log.i("123", "static sent");

        // and sent to widget
        Intent toWidget = new Intent(this, Provider.class);
        toWidget.setAction(Provider.ITEM_UPDATE_STATIC);
        toWidget.putExtras(bundle);
        sendBroadcast(toWidget);
    }

    private void subscribeEvents() {
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCartEvent(MessageEvent event) {
        Log.i("123", "event received");
        cart.add(event.itemData);
        cartAdapter.notifyDataSetChanged();
    }

    private void bindClickListeners() {
        final FloatingActionButton switcher = findViewById(R.id.switchCart);
        final RecyclerView list = findViewById(R.id.item_list);
        final ListView list1 = findViewById(R.id.cart_list);
        final View cartTitle = findViewById(R.id.cart_title);
        final View line = findViewById(R.id.cart_title_line);
        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePage();
            }
        });
    }

    private void togglePage() {
        togglePage(!isMainPage);
    }

    private void togglePage(boolean isMain) {
        final FloatingActionButton switcher = findViewById(R.id.switchCart);
        final RecyclerView list = findViewById(R.id.item_list);
        final ListView list1 = findViewById(R.id.cart_list);
        final View cartTitle = findViewById(R.id.cart_title);
        final View line = findViewById(R.id.cart_title_line);

        isMainPage = isMain;
        if (isMainPage) {
            switcher.setImageResource(R.mipmap.shoplist);
            list.setVisibility(View.VISIBLE);
            list1.setVisibility(View.INVISIBLE);
            cartTitle.setVisibility(View.INVISIBLE);
            line.setVisibility(View.INVISIBLE);
        } else {
            switcher.setImageResource(R.mipmap.mainpage);
            list.setVisibility(View.INVISIBLE);
            list1.setVisibility(View.VISIBLE);
            cartTitle.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        }
    }

    private void openItemDetail(Map<String, String> map) {
        Intent intent = new Intent(this, ItemDetailActivity.class);
        intent.putExtra("name", map.get("name"));
        intent.putExtra("price", map.get("price"));
        intent.putExtra("type", map.get("type"));
        intent.putExtra("info", map.get("info"));
        intent.putExtra("sname", map.get("sname"));
        intent.putExtra("first", map.get("first"));
        startActivity(intent);
    }

    private void initData() {
        String[][] arr = new String[][]{
            {"Enchated Forest", "￥ 5.00", "作者", "Johanna Basford", "enchatedforest"},
            {"Arla Milk", "￥ 59.00", "产地", "德国", "arla"},
            {"Devondale Milk", "￥ 79.00", "产地", "澳大利亚", "devondale"},
            {"Kindle Oasis", "￥ 2399.00", "版本", "8GB", "kindle"},
            {"waitrose 早餐麦片", "￥ 179.00", "重量", "2Kg", "waitrose"},
            {"Mcvitie's 饼干", "￥ 14.90", "产地", "英国", "mcvitie"},
            {"Ferrero Rocher", "￥ 132.59", "重量", "300g", "ferrero"},
            {"Maltesers", "￥ 141.43", "重量", "118g", "maltesers"},
            {"Lindt", "￥ 139.43", "重量", "249g", "lindt"},
            {"Borggreve", "￥ 28.90", "重量", "640g", "borggreve"}
        };

        for (String[] obj : arr) {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", obj[0]);
            map.put("price", obj[1]);
            map.put("type", obj[2]);
            map.put("info", obj[3]);
            map.put("sname", obj[4]);
            map.put("first", obj[0].substring(0, 1).toUpperCase());
            items.add(map);
        }
    }
}

abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    private Context mContext;
    private int mLayoutId;
    private List<T> mDatas;
    private OnItemClickListener mOnItemClickListener;

    public CommonAdapter(Context context, int layoutId, List<T> datas) {
        mContext = context;
        mLayoutId = layoutId;
        mDatas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ViewHolder.get(mContext, parent, mLayoutId);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        convert(holder, mDatas.get(position));

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnItemClickListener.onLongClick(holder.getAdapterPosition());
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public abstract void convert(ViewHolder holder, T s);

    public void setOnItemClickListemer(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    interface OnItemClickListener {
        void onClick(int position);

        void onLongClick(int position);
    }
}

class ViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;

    public ViewHolder(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        mConvertView = itemView;
        mViews = new SparseArray<>();
    }

    public static ViewHolder get(Context context, ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder holder = new ViewHolder(context, itemView, parent);
        return holder;
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
}