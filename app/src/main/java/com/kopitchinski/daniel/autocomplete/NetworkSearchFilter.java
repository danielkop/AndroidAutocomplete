package com.kopitchinski.daniel.autocomplete;

import android.util.Log;
import android.widget.Filter;
import java.util.ArrayList;
import java.util.List;

class NetworkSearchFilter extends Filter {
    final private int maxPerPage; //Maximum results to fetch from network
    private CharSequence lastSearched;
    private final Object lastSearchedLock = new Object();

    public interface FilterResultCallback {
        void publishResults(CharSequence constraint, List<String> values);
    }

    private final FilterResultCallback filterResultCallback;
    private final NetworkSearcher networkSearcher;

    public NetworkSearchFilter(NetworkSearcher networkSearch, FilterResultCallback filterResultCallback, int maxPerPage) {
        this.filterResultCallback = filterResultCallback;
        this.networkSearcher = networkSearch;
        this.maxPerPage = maxPerPage;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        //Store the most recent search.
        //We need to synchronized because performFiltering is called on a different thread than publishResults.
        synchronized (lastSearchedLock) {
            lastSearched = constraint;
        }
        FilterResults results = new FilterResults();
        List<String> values = new ArrayList<>();
        if(constraint != null && constraint.length() > 0)
            values = networkSearcher.search(constraint, maxPerPage);

        results.values = values;
        results.count = values.size();
        return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //On a slow network, we might have multiple pending autocomplete results.
        //If we have multiple requests - only display the results for the last search.
        synchronized (lastSearchedLock) {
            if (!Utils.Equals(lastSearched, constraint)) {
                Log.i("NetworkSearchFilter", "Ignoring search results from: " + constraint + " last searched was: " + lastSearched);
                return;
            }
        }

        //Notify adapter
        if(filterResultCallback != null)
            filterResultCallback.publishResults(constraint, (List<String>) results.values);
    }

}
