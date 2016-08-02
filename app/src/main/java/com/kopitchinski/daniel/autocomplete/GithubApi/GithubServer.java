package com.kopitchinski.daniel.autocomplete.GithubApi;


import android.util.Log;

import com.kopitchinski.daniel.autocomplete.NetworkSearcher;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GithubServer {
    private static final String GITHUB_BASE_URL = "https://api.github.com";
    private static final String SEARCH_IN_NAME = " in:name";

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(GITHUB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private final GithubApi api = retrofit.create(GithubApi.class);
    private final GithubSearch mSearcher = new GithubSearch(this);

    public List<GithubApi.User> getUsers(String searchQuery, int maxPerPage)
    {
        List<GithubApi.User> result = new ArrayList<>();
        try {
            //Search for users on github API.
            Response<GithubApi.UserSearchResponse> userSearch = api.getUsers(searchQuery + SEARCH_IN_NAME, maxPerPage).execute();
            Log.i("GithubServer", "User search: " + searchQuery + "Success: " + Boolean.toString(userSearch.isSuccessful()));
            if(userSearch.isSuccessful()) {
                for (GithubApi.User user : userSearch.body().users) {
                    //Update user object with user's full name, and add the user to the results list
                    result.add(updateUserFullName(user));
                }
            }
            else
                throw new Exception("Failed to fetch users: " + userSearch.code() + " " + userSearch.errorBody().string() + " query: \"" + searchQuery + "\"");
            Log.i("GithubServer", "# of users: " + result.size());
            return result;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return result;
        }
    }

    //Returns a user structure with a full name for this user.
    //Either extracts the full name from the text_matches field
    //Or fetches the entire user object from the server
    private GithubApi.User updateUserFullName(GithubApi.User user) throws Exception {
        if(user.getTextMatchesName() != null)
        {
            //Attempt to fetch user's full name from the search result itself
            user.name = user.getTextMatchesName();
            return user;
        }
        else
        {
            //No "text_matches", Need to actually fetch the user object in order to get user's full name.
            Log.i("GithubServer", "Fetching user: " + user.login);
            Response<GithubApi.User> userDetails = api.getUser(user.login).execute();
            if (userDetails.isSuccessful()) {
                Log.i("GithubServer", "Fetched user: " + userDetails.body().name);
                return user;
            } else {
                throw new Exception("Failed to fetch user: " + userDetails.code() + " " + userDetails.errorBody().string());
            }
        }
    }

    public NetworkSearcher searcher() {
        return mSearcher;
    }
}
