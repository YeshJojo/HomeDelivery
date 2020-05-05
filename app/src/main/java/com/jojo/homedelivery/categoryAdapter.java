package com.jojo.homedelivery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class categoryAdapter extends RecyclerView.Adapter<categoryViewHolder> {
    List<categoryList> list = Collections.emptyList();
    Context context;

    public categoryAdapter(List<categoryList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public categoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View photoView = inflater.inflate(R.layout.card_product, parent, false);

        categoryViewHolder viewHolder = new categoryViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final categoryViewHolder viewHolder, final int position) {
        viewHolder.examName.setText("View "+list.get(position).name+" products");
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewProduct.class);
                intent.putExtra("categoryName", list.get(position).name);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}