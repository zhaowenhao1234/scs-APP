package com.example.zwh.scs.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.zwh.scs.Bean.MsgItem;
import com.example.zwh.scs.Data.MsgAdapter;
import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.IntentUtils;
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


    private FloatingActionButton newMsg;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_message);
        initToolbarView("留言板", true, R.mipmap.im_titlebar_back_p);
        newMsg = findViewById(R.id.fab_newMsg);
        newMsg.setOnClickListener(this);
        initData();
        initView();
    }

    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new MsgAdapter(getData());
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.rv_msglist);
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
                IntentUtils.SetIntent(getApplicationContext(), EditMsgActivity.class);
                break;
            default:
                break;
        }
    }

    private List<MsgItem> getData() {
        List<MsgItem> msgItemList = new ArrayList<MsgItem>();
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
            }
        }).start();
        return msgItemList;
    }

}
