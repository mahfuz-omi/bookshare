package com.example.omi.goodreadsapidemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.omi.goodreadsapidemo.adapter.BooksAdapter;
import com.example.omi.goodreadsapidemo.view.BooksCustomAutoCompleteTextView;

public class SearchActivity extends AppCompatActivity {

    BooksCustomAutoCompleteTextView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        searchView = (BooksCustomAutoCompleteTextView) findViewById(R.id.et_book_title);
        searchView.setThreshold(3);
        searchView.setAdapter(new BooksAdapter(this, searchView)); // 'this' is Activity instance
        searchView.setLoadingIndicator(
                (android.widget.ProgressBar) findViewById(R.id.pb_loading_indicator));

    }
}
