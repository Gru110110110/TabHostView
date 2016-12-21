package com.seek.tabhostview;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TabHostView.TabDataProvider {

    private TabHostView tabHostView;

    private int[] resId = new int[]{R.drawable.tab_main, R.drawable.tab_dis, R.drawable.tab_me};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabHostView = (TabHostView) findViewById(R.id.tab);
        List<Fragment> fragments = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            fragments.add(TestFragment.newInstance("hi,i am fragment " + i));
        }
//        tabHostView.addFragments(R.id.container, fragments).setItemRes(new int[]{R.drawable.tab_cap_nom, R.drawable
//                .tab_discover_nom, R.drawable.tab_me_nom},
//                new int[]{R.drawable.tab_cap_sel, R.drawable.tab_discover_sel, R.drawable.tab_me_sel}, getResources()
//                        .getStringArray(R.array.tabStrs)).createItems();
        tabHostView.addFragments(R.id.container, fragments, this);
        tabHostView.setTabTextSize(12);
    }

    @Override
    public int getTabIconDrawable(int position) {
        return resId[position];
    }

    @Override
    public String getTabText(int position) {
        return getResources().getStringArray(R.array.tabStrs)[position];
    }
}
