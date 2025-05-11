package com.example.bar_retina_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final AppData appData;
    private Runnable onDataChanged;

    public OrderAdapter(Runnable onDataChanged) {
        appData = AppData.getInstance();
        this.onDataChanged = onDataChanged;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_template, parent, false);
        return new OrderAdapter.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        OrderItem item = appData.order.get(position);

        holder.buttonDecrease.setOnClickListener(v -> {
            item.deleteOne();
            if (item.getQuantity() <= 0) {
                int pos = holder.getAdapterPosition();
                appData.order.remove(pos);
                notifyItemRemoved(pos);
                if (onDataChanged != null) onDataChanged.run();
            } else {
                holder.orderAmount.setText(String.valueOf(item.getQuantity()));
                notifyItemChanged(holder.getAdapterPosition());
                if (onDataChanged != null) onDataChanged.run();
            }
        });

        holder.buttonIncrease.setOnClickListener(v -> {
            item.addOne();
            holder.orderAmount.setText(String.valueOf(item.getQuantity()));
            notifyItemChanged(holder.getAdapterPosition());
            if (onDataChanged != null) onDataChanged.run();
        });


        holder.orderProductPrice.setText(String.format("%.2f€", item.getProduct().getPrice()));
        holder.orderAmount.setText(String.valueOf(item.getQuantity()));

        holder.orderName.setText(item.getProduct().getName());
        holder.orderDescription.setText(item.getProduct().getDescription());
    }

    @Override
    public int getItemCount() {
        return appData.order.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        Button buttonDecrease;
        Button buttonIncrease;
        TextView orderProductPrice;
        TextView orderAmount;
        TextView orderName;
        TextView orderDescription;


        public OrderViewHolder(View itemView) {
            super(itemView);
            buttonDecrease = itemView.findViewById(R.id.button_decrease);
            buttonIncrease = itemView.findViewById(R.id.button_increase);
            orderAmount = itemView.findViewById(R.id.orderAmount);
            orderName = itemView.findViewById(R.id.orderName);
            orderDescription = itemView.findViewById(R.id.orderDescription);
            orderProductPrice = itemView.findViewById(R.id.orderProductPrice);
        }
    }

    public void setOnDataChanged(Runnable callback) {
        this.onDataChanged = callback;
    }

}
