package com.example.bar_retina_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private final AppData appData;
    Context context;
    Runnable onFinish;

    public TableAdapter(Context context, Runnable onFinish) {
        appData = AppData.getInstance();
        this.context = context;
        this.onFinish = onFinish;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_template, parent, false);
        return new TableAdapter.TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Table table = appData.tables.get(position);

        holder.tableNum.setText("Taula: "+table.getNumber());
        holder.clientsNum.setText("Clients: "+table.getClients());
        holder.totalBill.setText(String.format("%.2f€", table.getTotalBill()));

        holder.tableLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            appData.table = table;
            context.startActivity(intent);
            onFinish.run();
        });
    }

    @Override
    public int getItemCount() {
        return appData.tables.size();
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {

        TextView tableNum;
        TextView clientsNum;
        TextView totalBill;
        LinearLayout tableLayout;

        public TableViewHolder(View itemView) {
            super(itemView);
            tableNum = itemView.findViewById(R.id.tableNum);
            clientsNum = itemView.findViewById(R.id.clientsNum);
            totalBill = itemView.findViewById(R.id.totalBill);
            tableLayout = itemView.findViewById(R.id.tableLayout);
        }
    }
}
