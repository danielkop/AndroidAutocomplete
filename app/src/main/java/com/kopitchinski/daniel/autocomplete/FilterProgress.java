package com.kopitchinski.daniel.autocomplete;


public interface FilterProgress {
    void onStart();
    void onComplete(int count);
}