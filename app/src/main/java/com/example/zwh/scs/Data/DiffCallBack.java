package com.example.zwh.scs.Data;

import android.support.v7.util.DiffUtil;

import com.example.zwh.scs.Bean.MsgItem;

import java.util.List;

/**
 * desc
 * author 杨肇鹏
 * created on 2019/3/21 22:20
 */
public class DiffCallBack extends DiffUtil.Callback {

    private List<MsgItem> mOldDatas, mNewDatas;

    public DiffCallBack(List<MsgItem> mOldDatas, List<MsgItem> mNewDatas) {
        this.mOldDatas = mOldDatas;
        this.mNewDatas = mNewDatas;
    }

    @Override
    public int getOldListSize() {
        return mOldDatas != null ? mOldDatas.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewDatas == null ? 0 : mNewDatas.size();
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return mOldDatas.get(i).getTime().
                equals(mNewDatas.get(i1));
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
        MsgItem itemOld = mOldDatas.get(i);
        MsgItem itemNew = mNewDatas.get(i1);
        if (!itemOld.getContents().equals(itemNew.getContents())) {
            return false;
        }
        return itemNew.getTime().equals(itemOld.getTime());
    }
}
