package com.jojo.homedelivery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    Context context;
    List<productDetails> info;
    public RecyclerViewAdapter(Context context, List<productDetails> TempList) {
        this.info = TempList;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final productDetails productDetails = info.get(position);
        holder.productNameTextView.setText(productDetails.getName());
        holder.productPriceTextView.setText("₹ "+productDetails.getPrice());
        holder.productDescTextView.setText(productDetails.getDesc());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detail = new Intent(context, Checkout.class);
                detail.putExtra("name", productDetails.getName());
                detail.putExtra("price", productDetails.getPrice());
                context.startActivity(detail);
            }
        });
    }
    @Override
    public int getItemCount() {
        return info.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productNameTextView, productDescTextView, productPriceTextView;
        private LinearLayout layout;
        public ViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productName);
            productPriceTextView = itemView.findViewById(R.id.productPrice);
            productDescTextView = itemView.findViewById(R.id.productDesc);

            layout = itemView.findViewById(R.id.cardlayout);
        }
    }
}