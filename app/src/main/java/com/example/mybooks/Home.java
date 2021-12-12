package com.example.mybooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    RecyclerView recyclerView;
    private String USGS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";
    private EditText search;
    SpinKitView loadmoreProgressBar,progressBar;
    private int t=0;
    private BookAdapter adapter;
    private List<BooksModal> finalList=new ArrayList<>();
    private LinearLayoutManager manager;
    private boolean isScrolling=false,isFinished=true;
    private int currentItems,scrolledItems,totalItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = findViewById(R.id.recycler_id);
        manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        loadmoreProgressBar=findViewById(R.id.spin_kit);
        progressBar=findViewById(R.id.spin_kit_fading);
        search = findViewById(R.id.search);
        adapter=new BookAdapter(finalList,Home.this);
        recyclerView.setAdapter(adapter);
        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (search.getRight() - search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        //Here is your code when you click drawable right
                        if (search.getText().toString() == "") {
                            Toast.makeText(Home.this, "Empty search bar", Toast.LENGTH_SHORT).show();
                        } else {
                            t=0;
                            recyclerView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            String sentence = search.getText().toString().replaceAll("\\s", "")+"&maxResults="+(t+5);
                            EarthQuakeAsyncTask task = new EarthQuakeAsyncTask();

                            task.execute(USGS_REQUEST_URL+sentence);
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling=true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);
                currentItems=manager.getChildCount();
                totalItems=manager.getItemCount();
                scrolledItems=manager.findFirstVisibleItemPosition();
                if(isScrolling&& totalItems==(currentItems+scrolledItems)&&isFinished&&! recyclerView.canScrollVertically(1)){
                    loadmoreProgressBar.setVisibility(View.VISIBLE);
                    isFinished=false;
                    t+=5;
                    String sentence = search.getText().toString().replaceAll("\\s", "")+"&maxResults="+(t+5);

                    EarthQuakeAsyncTask task = new EarthQuakeAsyncTask();
                    task.execute(USGS_REQUEST_URL+sentence);
                }
            }
        });

    }
    private class EarthQuakeAsyncTask extends AsyncTask<String, Void, List<BooksModal>> {

        @Override
        protected List<BooksModal> doInBackground(String... strings) {
            if (strings.length <= 0 || strings[0] == null) {
                return null;
            }
            List<BooksModal> result = Query.fetchEarthquakeData(strings[0],t);
            return result;
        }

        @Override
        protected void onPostExecute(final List<BooksModal> earthQuakes) {
            isFinished=true;

            if (earthQuakes == null || earthQuakes.size() == 0) {
                return;
            }
            adapter.notifyDataSetChanged();
            finalList.addAll(earthQuakes);
            adapter.notifyDataSetChanged();
            if (adapter.getItemCount() == 0) {
                loadmoreProgressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                loadmoreProgressBar.setVisibility(View.GONE);
            }
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}
