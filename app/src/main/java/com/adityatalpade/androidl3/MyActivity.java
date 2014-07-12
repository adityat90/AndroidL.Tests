package com.adityatalpade.androidl3;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class MyActivity extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DemoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //mLayoutManager = new LinearLayoutManager(this);
        //mLayoutManager = new GridLayoutManager(this, GridLayoutManager.HORIZONTAL, false);
        mLayoutManager = new GridLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final ArrayList data = new ArrayList<String>();
        for (int j = 0; j < 100; j++) {
            data.add(String.valueOf(j + 1));
        }

        mAdapter = new DemoAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
