package com.example.bar_retina_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    private final List<Product> filteredProducts;
    private final Runnable onProductAdded;

    public ProductsAdapter(String tagName, Runnable onProductAdded) {
        AppData appData = AppData.getInstance();
        filteredProducts = new ArrayList<>();

        for (Product product : appData.products) {
            if (product.getTag().getName().equals(tagName)) {
                filteredProducts.add(product);
            }
        }
        this.onProductAdded = onProductAdded;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_template, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = filteredProducts.get(position);

        holder.productName.setText(product.getName());
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText(String.format("%.2f€", product.getPrice()));

        holder.productLayout.setOnClickListener(v -> {
            AppData appData = AppData.getInstance();
            if(appData.containsProduct(product.getName())) {
                OrderItem item = appData.getOrderItemByName(product.getName());
                item.addOne();
            }else {
                appData.order.add(new OrderItem(product));
            }

            holder.productQuantity.setText(String.valueOf(appData.getProductQuantity(product.getName())));
            onProductAdded.run();
        });
    }


    @Override
    public int getItemCount() {
        return filteredProducts.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        LinearLayout productLayout;
        TextView productName;
        TextView productDescription;
        TextView productPrice;
        TextView productQuantity;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productLayout = itemView.findViewById(R.id.productLayout);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
        }
    }
}
