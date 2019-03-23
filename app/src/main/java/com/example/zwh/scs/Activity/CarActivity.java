package com.example.zwh.scs.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.zwh.scs.Bean.CarItem;
import com.example.zwh.scs.Bean.MsgItem;
import com.example.zwh.scs.Data.CarAdapter;
import com.example.zwh.scs.Data.MsgAdapter;
import com.example.zwh.scs.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CarActivity extends BaseActivity implements View.OnClickListener  {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView mRecyclerView;
    private CarAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<CarItem> carItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_car);
        initToolbarView("司机列表", true, R.mipmap.im_titlebar_back_p);

        swipeRefresh = findViewById(R.id.swipe_refresh_car);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.black));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        initData();
        mRecyclerView = findViewById(R.id.car_list);
        mAdapter = new CarAdapter(carItemList);
        mLayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_scanner).setVisible(false);
        menu.findItem(R.id.menu_newMeg).setVisible(false);
        return true;
    }


    private void initData() {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://129.204.119.172:8080/driver/getAll")
                .build();
        final Call call = client.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
                    Log.d("MSG", "find response" + responseDate);

                    JSONObject jsonObject = new JSONObject(responseDate);
                    JSONArray jsonArray = jsonObject.getJSONArray("drivers");
                    Log.d("MSG", "???" + jsonArray.toString());
                    Gson gson = new Gson();
                    List<CarItem> carItems = gson.fromJson(jsonArray.toString(), new TypeToken<List<CarItem>>
                            () {
                    }.getType());
                    Log.d("MSG", "SIZE" + carItems.size());
                    for (CarItem msg : carItems) {
                        carItemList.add(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }

    private void refreshData() {
        carItemList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                        //mAdapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refreshData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        carItemList.clear();
        if (requestCode == 1 && resultCode == 2) {
            initData();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
