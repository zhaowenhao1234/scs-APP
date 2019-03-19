package com.example.zwh.scs.Data;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zwh.scs.Bean.MsgItem;
import com.example.zwh.scs.R;

import java.util.List;

/**
 * desc
 * author 杨肇鹏
 * created on 2019/3/18 19:44
 */
public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private List<MsgItem> msgList;

    public MsgAdapter(List<MsgItem> data) {
        this.msgList = data;
    }

    public void updateData(List<MsgItem> data) {
        this.msgList = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.msg_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        MsgItem msage = msgList.get(i);
        viewHolder.msgContent.setText(msage.getContents());
        viewHolder.msgTime.setText(msage.getTime());
    }

    @Override
    public int getItemCount() {
        return msgList == null ? 0 : msgList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView msgContent;
        TextView msgTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msgContent = itemView.findViewById(R.id.tv_msg_content);
            msgTime = itemView.findViewById(R.id.tv_msg_time);
        }
    }
}
