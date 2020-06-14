package com.jojo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.jojo.homedelivery.ProductItemClickListener;
import com.jojo.homedelivery.ProductRecyclerAdapter;
import com.jojo.homedelivery.R;
import com.jojo.homedelivery.SlidingImage_Adapter;
import com.jojo.homedelivery.ViewProduct;
import com.jojo.homedelivery.categoryList;
import com.jojo.homedelivery.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class home extends Fragment implements ProductItemClickListener {
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    String mailstr;
    RecyclerView recyclerView;
    private ProductRecyclerAdapter adapter;
    private List<product> productCards;
    ViewPager viewPager;
    private static final Integer[] IMAGES = {R.drawable.headsale, R.drawable.headbaby, R.drawable.headkitchen, R.drawable.headparty};
    private static final String[] NAMES = {"Grocery Products", "Baby Products", "Bakery Products", "Beauty Products"};
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    private ArrayList<String> NameArray = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, null);
        for (int i = 0; i < IMAGES.length; i++) {
            ImagesArray.add(IMAGES[i]);
            NameArray.add(NAMES[i]);
        }
        viewPager = view.findViewById(R.id.viewPagerHeader);
        viewPager.setAdapter(new SlidingImage_Adapter(getActivity(), ImagesArray, NameArray));

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );
        recyclerView.setClipToPadding(false);
        recyclerView.setHasFixedSize(true);

        productCards = new ArrayList<>();
        productCards.add(new product(1,R.drawable.groceryicon, "grocery", "19 products"));
        productCards.add(new product(2,R.drawable.babyicon, "baby", "14 products"));
        productCards.add(new product(3,R.drawable.beautyicon, "beauty", "24 products"));
        productCards.add(new product(4,R.drawable.bakeryicon, "bakery", "18 products"));

        adapter = new ProductRecyclerAdapter(getContext(), productCards, this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onDashboardProductClick(product courseCard, ImageView imageView) {
        Intent intent = new Intent(getActivity(), ViewProduct.class);
        intent.putExtra("categoryName", courseCard.getProductTitle());
        requireContext().startActivity(intent);
    }

}
