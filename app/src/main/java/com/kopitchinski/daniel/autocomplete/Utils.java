package com.kopitchinski.daniel.autocomplete;

class Utils {
    //Compares two objects using Equals
    //Allows both or either to be null
    static boolean Equals(Object a, Object b)
    {
        if (a == null && b == null) {
            return true;
        } else if (a == null || b == null) {
            return false;
        } else return a.equals(b);
    }
}
