package com.jojo.homedelivery;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class categoryViewHolder extends RecyclerView.ViewHolder {
    TextView examName;
    TextView examMessage;
    LinearLayout layout;

    categoryViewHolder(View itemView) {
        super(itemView);
        examName = itemView.findViewById(R.id.examName);
        layout = itemView.findViewById(R.id.layout);
    }
}
