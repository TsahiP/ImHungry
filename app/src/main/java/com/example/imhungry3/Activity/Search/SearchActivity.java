package com.example.imhungry3.Activity.Search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageButton;

import com.example.imhungry3.Adapter.AdapterViewPager;
import com.example.imhungry3.Fragment.FragmentSearchIngredients;
import com.example.imhungry3.Fragment.FragmentSearchName;
import com.example.imhungry3.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    //

    private FragmentSearchIngredients fragmentSearchIngredients;
    private FragmentSearchName fragmentSearchName;
    private ImageButton back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        viewPager = findViewById(R.id.view_pager2);//variable to display page fregment
        tabLayout = findViewById(R.id.tab_layout);//variable to display tabbs
        back = findViewById(R.id.back_search);
        back.setOnClickListener(this);
        //class fregments
        fragmentSearchIngredients = new FragmentSearchIngredients();
        fragmentSearchName = new FragmentSearchName();

        tabLayout.setupWithViewPager(viewPager);


        //make adapter for all fregment and add all fregment to adapter
        AdapterViewPager viewPagerAdapter = new AdapterViewPager(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(fragmentSearchIngredients, getString(R.string.ingredients));
        viewPagerAdapter.addFragment(fragmentSearchName, getString(R.string.name_recpie));
        viewPager.setAdapter(viewPagerAdapter);

        //set the number a tab and the icon
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_ingridents);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_search_text);



    }

    @Override
    public void onClick(View v) {
        if(v == back)
        {
            onBackPressed();
        }
    }
}