package com.example.zwh.scs.Data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwh.scs.Activity.MainActivity;
import com.example.zwh.scs.Bean.CarItem;
import com.example.zwh.scs.Bean.MsgItem;
import com.example.zwh.scs.R;
import com.example.zwh.scs.Util.IntentUtils;

import java.security.Principal;
import java.util.List;

/**
 * created at 2019/3/23 14:22 by wenhaoz
 */
public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder>{
    private List<CarItem> carList;
    private Context context;

    public CarAdapter(List<CarItem> data, Context context) {
        this.context = context;
        this.carList = data;
    }

    public void updateData(List<CarItem> data) {
        this.carList = data;
    }


    @NonNull
    @Override
    public CarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.car_item, viewGroup, false);
        CarAdapter.ViewHolder holder = new CarAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CarAdapter.ViewHolder viewHolder, int i) {
        CarItem carItem = carList.get(i);
        viewHolder.driver_name.setText(carItem.getName());
        try {
            if ((Boolean) carItem.getIsOnline()) {
                viewHolder.is_online.setText("在线");
            } else if ((Boolean) carItem.getIsOnline() == false) {
                viewHolder.is_online.setText("离线");
            }
        } catch (Exception e) {
            viewHolder.is_online.setText("离线");
        }


        viewHolder.phone_number.setText("" + carItem.getPhoneNum());
        viewHolder.call_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + viewHolder.phone_number.getText());
                intent.setData(data);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return carList == null ? 0 : carList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView driver_name;
        TextView is_online;
        TextView phone_number;
        ImageView call_phone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            driver_name = itemView.findViewById(R.id.driver_name);
            is_online = itemView.findViewById(R.id.is_online);
            phone_number = itemView.findViewById(R.id.phone_number);
            call_phone = (ImageView) itemView.findViewById(R.id.call_phone);
        }
    }

}
