package com.jojo.homedelivery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class home extends Fragment {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView email;
    String mailstr;
    RecyclerView recyclerView;
    categoryAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, null);
        email = view.findViewById(R.id.email);
        if(auth.getCurrentUser()!=null){
            mailstr = auth.getCurrentUser().getPhoneNumber();
            email.setText("Hey! "+mailstr);
        }
        List<categoryList> list = new ArrayList<>();
        list = getData();

        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new categoryAdapter(list, getActivity().getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    private List<categoryList> getData() {
        List<categoryList> list = new ArrayList<>();
        list.add(new categoryList("grocery"));
        list.add(new categoryList("baby"));
        list.add(new categoryList("beauty"));
        list.add(new categoryList("bakery"));
        return list;
    }
}
