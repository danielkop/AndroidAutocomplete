package com.kopitchinski.daniel.autocomplete;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;

public class DebouncedAutoCompleteEditText extends EditText implements Filter.FilterListener {

    //Text change watcher
    private DebounceTextWatcher mWatcher;

    //Auto complete results popup window
    private ListPopupWindow autoCompleteResults;

    //List adapter for popup window
    private ListAdapter mAdapter;

    //Filter interface, returned from the Filterable interface implemented by mAdapter
    private Filter mFilter;

    //How many millis need to pass since last keystroke in order to start searching for users?
    private int debounceMillis = 500;

    //Minimum number of letters to start searching
    private int threshold = 2;

    private final Handler handler = new Handler();

    //Callback to notify about filtering progres. Uses to show/hide a progress spinner
    private FilterProgress filterProgressNotifier;

    public void setDebounceMillis(int debounceMillis) {
        this.debounceMillis = debounceMillis;
    }

    public DebouncedAutoCompleteEditText(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public DebouncedAutoCompleteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public DebouncedAutoCompleteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        //Todo: prevent text watcher showing popup and filtering
        super.setText(text, type);
    }

    public DebouncedAutoCompleteEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        //Fetch attributes from XML
        debounceMillis = attrs.getAttributeIntValue(R.attr.debounce, debounceMillis);
        threshold = attrs.getAttributeIntValue(R.attr.threshold, threshold);

        //Set text watcher
        mWatcher = new DebounceTextWatcher();
        addTextChangedListener(mWatcher);

        //Build results popup window
        autoCompleteResults = new ListPopupWindow(context);
        autoCompleteResults.setAnchorView(this);
        autoCompleteResults.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        autoCompleteResults.setHeight(200); //Todo - better value. in dps?
        autoCompleteResults.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //When a user picks a certain result, set the textview to that result and dismiss popup
        autoCompleteResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removeTextChangedListener(mWatcher);
                CharSequence newText = mFilter.convertResultToString(mAdapter.getItem(position));
                setText(newText);
                setSelection(newText.length());
                addTextChangedListener(mWatcher);
                autoCompleteResults.dismiss();
            }
        });
    }

    //We expect the list adapter to implement filterable as well
    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        mAdapter = adapter;
        if (mAdapter != null) {
            mFilter = adapter.getFilter();
        } else {
            mFilter = null;
        }

        autoCompleteResults.setAdapter(mAdapter);
    }

    private class DebounceTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(final CharSequence text, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            final String text = s.toString();
            //Debounce the filtering function.
            //Clear timer and reset it every time this function is called.
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mFilter == null)
                        return;
                    if (filterProgressNotifier != null)
                        filterProgressNotifier.onStart();

                    //Call original filtering function
                    //Make sure current text is at least <threshold> charachters long
                    if (text.length() >= threshold)
                        mFilter.filter(text, DebouncedAutoCompleteEditText.this);
                    else
                        mFilter.filter(null, DebouncedAutoCompleteEditText.this);
                }
            }, debounceMillis);
        }
    }
    public void setFilterProgressNotifier(FilterProgress filterProgressNotifier) {
        this.filterProgressNotifier = filterProgressNotifier;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
    public int getThreshold() {
        return threshold;
    }

    @Override
    public void onFilterComplete(int count) {
        //When filtering is over, show popup if necessary
        if(count > 0)
            autoCompleteResults.show();
        else
            autoCompleteResults.dismiss();

        //Notify progress callbacks
        if (filterProgressNotifier != null)
            filterProgressNotifier.onComplete(count);
    }
}