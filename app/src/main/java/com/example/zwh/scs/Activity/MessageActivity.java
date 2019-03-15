package com.example.zwh.scs.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.zwh.scs.R;

public class MessageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_message);
        initToolbarView("留言板", true, R.mipmap.im_titlebar_back_p);
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
        return true;
    }
}
