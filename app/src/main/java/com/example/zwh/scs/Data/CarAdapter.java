package com.example.zwh.scs.Data;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zwh.scs.Bean.CarItem;
import com.example.zwh.scs.Bean.MsgItem;
import com.example.zwh.scs.R;

import java.util.List;

/**
 * created at 2019/3/23 14:22 by wenhaoz
 */
public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder>{
    private List<CarItem> carList;

    public CarAdapter(List<CarItem> data) {
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
        viewHolder.is_online.setText(""+carItem.getIsOnline());
    }

    @Override
    public int getItemCount() {
        return carList == null ? 0 : carList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView driver_name;
        TextView is_online;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            driver_name = itemView.findViewById(R.id.driver_name);
            is_online = itemView.findViewById(R.id.is_online);
        }
    }
}
