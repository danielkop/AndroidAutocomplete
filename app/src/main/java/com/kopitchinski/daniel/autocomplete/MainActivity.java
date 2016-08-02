package com.kopitchinski.daniel.autocomplete;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.kopitchinski.daniel.autocomplete.GithubApi.GithubServer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final DebouncedAutoCompleteTextView debounceAutoCompleteView = (DebouncedAutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        final DebouncedAutoCompleteEditText debounceAutoCompleteEditView = (DebouncedAutoCompleteEditText)findViewById(R.id.autoCompleteEditText);
        final int maxPerPage = 20;

        GithubServer githubServer = new GithubServer();
        debounceAutoCompleteView.setAdapter(new NetworkAutoCompleteAdapter(githubServer.searcher(), maxPerPage, this));

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        debounceAutoCompleteView.setFilterProgressNotifier(new FilterProgress() {
            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onComplete(int count) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        final ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        debounceAutoCompleteEditView.setAdapter(new NetworkAutoCompleteAdapter(githubServer.searcher(), maxPerPage, this));
        debounceAutoCompleteEditView.setFilterProgressNotifier(new FilterProgress() {
            @Override
            public void onStart() {
                progressBar2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onComplete(int count) {
                progressBar2.setVisibility(View.INVISIBLE);
            }
        });

    }
}
