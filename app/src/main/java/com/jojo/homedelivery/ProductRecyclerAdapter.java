package com.jojo.homedelivery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter._ViewHolder> {
    Context mContext;
    private List<product> mData;
    private ProductItemClickListener productItemClickListener;

    public ProductRecyclerAdapter(Context mContext, List<product> mData, ProductItemClickListener listener) {
        this.mContext = mContext;
        this.mData = mData;
        this.productItemClickListener = listener;
    }

    @NonNull
    @Override
    public ProductRecyclerAdapter._ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_product, viewGroup, false);
        return new _ViewHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ProductRecyclerAdapter._ViewHolder viewHolder, final int i) {
        viewHolder.mItem = mData.get(i);
        final int pos = viewHolder.getAdapterPosition();
        String title = mData.get(i).getProductTitle();
        title = title.substring(0, 1).toUpperCase() + title.substring(1);
        viewHolder.itemView.setTag(pos);
        viewHolder.setPostImage(mData.get(i));
        viewHolder.course.setText(title + " Products");
        viewHolder.quantity_courses.setText(mData.get(i).getQuantityCourses());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productItemClickListener.onDashboardProductClick(mData.get(i), viewHolder.imageView);
            }
        });
    }

    public int getDimensionValuePixels(int dimension) {
        return (int) mContext.getResources().getDimension(dimension);
    }

    @Override
    public long getItemId(int position) {
        product product = mData.get(position);
        return product.getId();
    }
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }
    public class _ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView course;
        TextView quantity_courses;
        CardView card_item;
        public product mItem;
        public _ViewHolder(@NonNull View itemView) {
            super(itemView);
            card_item = itemView.findViewById(R.id.card_item);
            imageView = itemView.findViewById(R.id.card_view_image);
            course = itemView.findViewById(R.id.stag_item_course);
            quantity_courses = itemView.findViewById(R.id.stag_item_quantity_course);
        }
        void setPostImage(product courseCard) {
            imageView.setImageResource(courseCard.getImageCourse());
        }
    }
}
