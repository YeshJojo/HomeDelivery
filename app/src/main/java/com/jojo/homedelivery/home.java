package com.jojo.homedelivery;

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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class home extends Fragment implements ProductItemClickListener{
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView email;
    String mailstr;
    RecyclerView recyclerView;
    private ProductRecyclerAdapter adapter;
    private List<product> productCards;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, null);
        email = view.findViewById(R.id.email);
        if(auth.getCurrentUser()!=null){
            mailstr = auth.getCurrentUser().getPhoneNumber();
            email.setText("Hey! " + mailstr);
        }
        List<categoryList> list = new ArrayList<>();

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
