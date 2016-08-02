package com.kopitchinski.daniel.autocomplete;

import java.util.List;

//Interface for searching for a list of items based on a given constraint
public interface NetworkSearcher {
    List<String> search(CharSequence constraint, int maxPerPage);
}