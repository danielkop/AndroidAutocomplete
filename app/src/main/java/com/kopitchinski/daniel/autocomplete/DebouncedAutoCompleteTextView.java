package com.kopitchinski.daniel.autocomplete;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class DebouncedAutoCompleteTextView extends AutoCompleteTextView {

    private final Handler handler = new Handler();

    //Callback to notify about filtering progres. Uses to show/hide a progress spinner
    private FilterProgress filterProgressNotifier;

    //How many millis need to pass since last keystroke in order to start searching for users?
    private int debounceMillis = 500;

    public void setDebounceMillis(int debounceMillis) {
        this.debounceMillis = debounceMillis;
    }

    public DebouncedAutoCompleteTextView(Context context) {
        super(context);
    }

    public DebouncedAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        debounceMillis = attrs.getAttributeIntValue(R.attr.debounce, debounceMillis);
    }

    public DebouncedAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        debounceMillis = attrs.getAttributeIntValue(R.attr.debounce, debounceMillis);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DebouncedAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        debounceMillis = attrs.getAttributeIntValue(R.attr.debounce, debounceMillis);
    }

    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        //Debounce the filtering function.
        //Clear timer and reset it every time this function is called.
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (filterProgressNotifier != null)
                    filterProgressNotifier.onStart();
                //Call original filtering function
                DebouncedAutoCompleteTextView.super.performFiltering(text, keyCode);
            }
        }, debounceMillis);
    }

    @Override
    public void onFilterComplete(int count) {
        if(filterProgressNotifier != null)
            filterProgressNotifier.onComplete(count);
        super.onFilterComplete(count);
    }

    public void setFilterProgressNotifier(FilterProgress filterProgressNotifier) {
        this.filterProgressNotifier = filterProgressNotifier;
    }

}
