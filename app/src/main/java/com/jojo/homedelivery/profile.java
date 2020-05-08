package com.jojo.homedelivery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class profile extends Fragment {
    FirebaseAuth auth;
    String phoneNum;
    TextView userName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, null);
        auth = FirebaseAuth.getInstance();
        userName = view.findViewById(R.id.username);
        if(auth.getCurrentUser() != null){
            phoneNum = auth.getCurrentUser().getPhoneNumber();
            userName.setText(phoneNum);
        }

        return view;
    }
}
