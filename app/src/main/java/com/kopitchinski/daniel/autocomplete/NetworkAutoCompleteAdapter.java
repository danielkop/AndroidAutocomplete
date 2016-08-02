package com.kopitchinski.daniel.autocomplete;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

//List adapter and filter for auto completing from a network
public class NetworkAutoCompleteAdapter extends BaseAdapter implements Filterable, NetworkSearchFilter.FilterResultCallback {

    private static final StyleSpan BOLD_SPAN = new StyleSpan(Typeface.BOLD);

    //List of matching strings
    private List<String> matchingValues = new ArrayList<>();

    //Searched text, store in order to highlight found occurrences
    private String searchText;

    //filter for searching autocomplete results in the network
    final private Filter networkSearchFilter;

    public NetworkAutoCompleteAdapter(NetworkSearcher networkSearcher, int maxPerPage, Context context) {
        this.context = context;
        this.networkSearchFilter = new NetworkSearchFilter(networkSearcher, this, maxPerPage);
    }

    private final Context context;

    @Override
    public int getCount() {
        return matchingValues.size();
    }

    @Override
    public String getItem(int position) {
        return matchingValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.dropdown_menu_item, parent, false);
        }
        String value = getItem(position);
        value = value != null ? value : "";
        TextView textView = (TextView) v.findViewById(R.id.text1);
        textView.setText(highlight(searchText, value));
        return v;
    }

    //Remove letter accents from text
    private String deAccent(String str) {
        //http://stackoverflow.com/questions/1008802/converting-symbols-accent-letters-to-english-alphabet
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    //Highlight all occurrences of needle in haystack
    private CharSequence highlight(String needle, String haystack) {
        //Case insensitive, and de-accent
        String lowerHaystack = deAccent(haystack.toLowerCase());
        needle = deAccent(needle.toLowerCase());

        if(needle.length() == 0) //nothing to highlight
            return haystack;

        //Iterate through haystack, finding each occurrence of needle.
        int pos = lowerHaystack.indexOf(needle);
        Spannable highlighted = new SpannableString(haystack);
        while (pos >= 0) {
            int end = pos + needle.length();
            highlighted.setSpan(BOLD_SPAN, pos, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            pos = lowerHaystack.indexOf(needle, end);
        }
        return highlighted;
    }

    @Override
    public Filter getFilter() {
        return networkSearchFilter;
    }

    @Override
    public void publishResults(CharSequence constraint, List<String> values) {
        this.searchText = constraint != null ? constraint.toString() : "";
        this.matchingValues = values;
        if(matchingValues == null || matchingValues.size() == 0)
            notifyDataSetInvalidated();
        else
            notifyDataSetChanged();
    }
}
