package com.example.bar_retina_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    private final List<String> tags;
    private final Context context;
    private final Runnable onProductAdded;

    public TagAdapter(Context context, Runnable onProductAdded) {
        this.context = context;
        this.tags = new ArrayList<>();
        AppData appData = AppData.getInstance();

        Set<String> uniqueTags = new LinkedHashSet<>();
        for (Product product : appData.products) {
            uniqueTags.add(product.getTag().getName());
        }

        tags.addAll(uniqueTags);
        this.onProductAdded = onProductAdded;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_tempalte, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        String tagName = tags.get(position);
        holder.tagName.setText(tagName);
        holder.productsRecycler.setLayoutManager(new LinearLayoutManager(context));
        holder.productsRecycler.setAdapter(new ProductsAdapter(tagName, onProductAdded));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tagName;
        RecyclerView productsRecycler;

        public TagViewHolder(View itemView) {
            super(itemView);
            tagName = itemView.findViewById(R.id.tagName);
            productsRecycler = itemView.findViewById(R.id.productsRecycler);

            tagName.setOnClickListener(v -> {
                if (productsRecycler.getVisibility() == View.GONE) {
                    productsRecycler.setVisibility(View.VISIBLE);
                } else {
                    productsRecycler.setVisibility(View.GONE);
                }
            });
        }
    }
}
