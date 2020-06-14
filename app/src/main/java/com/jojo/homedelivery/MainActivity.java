package com.jojo.homedelivery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jojo.fragments.cart;
import com.jojo.fragments.home;
import com.jojo.fragments.profile;
import com.jojo.fragments.search;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private TabLayout tabLayout;
    private View view;
    TextView textView, username_disp;
    ImageView imageView, profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username_disp = findViewById(R.id.username_disp);
        profileImage = findViewById(R.id.profileImage);

        tabLayout = findViewById(R.id.tabLayout);
        view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_background, null);
        textView = view.findViewById(R.id.tv1);
        imageView = view.findViewById(R.id.iv1);

        loadFragment(new home());
        if(auth.getCurrentUser()!=null){
            username_disp.setText(auth.getCurrentUser().getPhoneNumber());
        }
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) tabLayout.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());
        setCustomView(0, 1, 2, 3);
        setTextAndImageWithAnimation("HOME", R.drawable.ic_home);
        loadFragment(new home());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1:
                        setCustomView(1, 0, 2, 3);
                        setTextAndImageWithAnimation("SEARCH", R.drawable.ic_search);
                        loadFragment(new search());
                        break;
                    case 2:
                        setCustomView(2, 1, 0, 3);
                        setTextAndImageWithAnimation("CART", R.drawable.ic_cart);
                        loadFragment(new cart());
                        break;
                    case 3:
                        setCustomView(3, 1, 2, 0);
                        setTextAndImageWithAnimation("PROFILE", R.drawable.ic_user);
                        //change to the fragment which you want to display
                        loadFragment(new profile());
                        break;
                    case 0:

                    default:
                        setCustomView(0, 1, 2, 3);
                        setTextAndImageWithAnimation("HOME", R.drawable.ic_home);
                        //change to the fragment which you want to display
                        loadFragment(new home());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setCustomView(int selectedtab, int non1, int non2, int non3) {
        Objects.requireNonNull(tabLayout.getTabAt(selectedtab)).setCustomView(view);
        Objects.requireNonNull(tabLayout.getTabAt(non1)).setCustomView(null);
        Objects.requireNonNull(tabLayout.getTabAt(non2)).setCustomView(null);
        Objects.requireNonNull(tabLayout.getTabAt(non3)).setCustomView(null);
    }

    private void setTextAndImageWithAnimation(String text, int images) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        textView.setText(text);
        imageView.setImageResource(images);
        textView.startAnimation(animation);
        imageView.startAnimation(animation);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
        }
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
