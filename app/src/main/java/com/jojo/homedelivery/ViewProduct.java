package com.jojo.homedelivery;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jojo.customfonts.MyTextView_Poppins_Bold;

import java.util.ArrayList;
import java.util.List;

public class ViewProduct extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    MyTextView_Poppins_Bold textView;
    String catname;
    TextView back;

    ProgressDialog progressDialog;
    List<productDetails> list = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter ;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        textView = findViewById(R.id.catProducts);
        back = findViewById(R.id.tv_back);
        if(getIntent().hasExtra("categoryName"))
            catname = getIntent().getStringExtra("categoryName");
        database = FirebaseDatabase.getInstance();

        textView.setText(catname.substring(0, 1).toUpperCase() + catname.substring(1) +" Products");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Log.d("catName",catname);
        databaseReference = database.getReference().child("Product").child(catname);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(ViewProduct.this);
        progressDialog.setMessage("Loading Data...");
        progressDialog.show();
        fetch();
    }

    private void fetch() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    productDetails productDetails = dataSnapshot.getValue(productDetails.class);
                    list.add(productDetails);
                }
                adapter = new RecyclerViewAdapter(ViewProduct.this, list);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }
}
