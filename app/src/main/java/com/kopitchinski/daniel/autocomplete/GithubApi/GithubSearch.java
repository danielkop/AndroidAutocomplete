package com.kopitchinski.daniel.autocomplete.GithubApi;

import com.kopitchinski.daniel.autocomplete.Caching;
import com.kopitchinski.daniel.autocomplete.LruCaching;
import com.kopitchinski.daniel.autocomplete.NetworkSearcher;

import java.util.ArrayList;
import java.util.List;

public class GithubSearch implements NetworkSearcher {
    final private GithubServer server;
    final static private boolean DEBUG_SIMULATE_SLOW_NETWORK = false;

    //Cache network results in an LRU Cache.
    //Weak/Soft references forget too quickly.
    //Consider Guava Cache in future.
    final private int cacheSize = 40;
    final private Caching<CharSequence, List<String>> cache = new LruCaching<>(cacheSize);

    public GithubSearch(GithubServer server) {
        this.server = server;
    }

    @Override
    public List<String> search(CharSequence constraint, int maxPerPage) {
        //Check for cached result
        List<String> cached = cache.get(constraint);
        if(cached != null)
            return cached;

        //Fetch users from github api
        String query = (String) constraint;
        List<String> values = new ArrayList<>();
        List<GithubApi.User> users = server.getUsers(query.trim(), maxPerPage);

        //Return only user names
        for (GithubApi.User user : users)
            values.add(user.name);

        //For testing purposes - simulate a slow network
        if(DEBUG_SIMULATE_SLOW_NETWORK) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Store user lists in cache
        if(values.size() > 0)
            cache.put(constraint, values);

        return values;
    }
}
