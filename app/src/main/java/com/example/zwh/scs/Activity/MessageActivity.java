package com.example.zwh.scs.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.zwh.scs.Bean.MsgItem;
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

public class MessageActivity extends BaseActivity implements View.OnClickListener {


    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton newMsg;
    private RecyclerView mRecyclerView;
    private MsgAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<MsgItem> msgItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_message);
        initToolbarView("留言板", true, R.mipmap.im_titlebar_back_p);
        newMsg = findViewById(R.id.fab_newMsg);
        newMsg.setOnClickListener(this);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.black));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        initData();
        mRecyclerView = findViewById(R.id.rv_msglist);
        mAdapter = new MsgAdapter(msgItemList);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_newMsg:
                Intent intent = new Intent(MessageActivity.this, EditMsgActivity.class);
                startActivityForResult(intent, 1);
                break;
            default:
                break;
        }
    }

    private void initData() {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://129.204.119.172:8080/found/getAll")
                .build();
        final Call call = client.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
                    Log.d("MSG", "find response" + responseDate);
                    //parseJSONWithGSON(response.body().string());
                    JSONObject jsonObject = new JSONObject(responseDate);
                    JSONArray jsonArray = jsonObject.getJSONArray("founds");
                    Log.d("MSG", "???" + jsonArray.toString());
                    Gson gson = new Gson();
                    List<MsgItem> msgItems = gson.fromJson(jsonArray.toString(), new TypeToken<List<MsgItem>>
                            () {
                    }.getType());
                    Log.d("MSG", "SIZE" + msgItems.size());
                    for (MsgItem msg : msgItems) {
                        msgItemList.add(msg);
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
        msgItemList.clear();
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
        msgItemList.clear();
        if (requestCode == 1 && resultCode == 2) {
            initData();
            mAdapter.notifyDataSetChanged();
        }
    }
}
