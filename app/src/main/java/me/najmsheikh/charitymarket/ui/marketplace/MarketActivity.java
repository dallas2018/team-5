package me.najmsheikh.charitymarket.ui.marketplace;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;

import me.najmsheikh.charitymarket.R;
import me.najmsheikh.charitymarket.ui.BaseActivity;

public class MarketActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
    }
}
