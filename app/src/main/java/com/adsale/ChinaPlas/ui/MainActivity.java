package com.adsale.ChinaPlas.ui;

import android.databinding.DataBindingUtil;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";

    private ActivityMainBinding binding;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);


        mDrawerLayout = binding.drawerLayout;
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);

        MainFragment mainFragment = MainFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.frame_main_content,mainFragment).commit();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            Log.i(TAG, "onOptionsItemSelected");
        }
        return super.onOptionsItemSelected(item);
    }
}
