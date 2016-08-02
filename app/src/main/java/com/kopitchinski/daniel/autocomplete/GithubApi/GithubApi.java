package com.kopitchinski.daniel.autocomplete.GithubApi;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import retrofit2.*;
import retrofit2.http.*;

public interface GithubApi
{
    //We need this header so the search results contain the matched text.
    //This way we can fetch the user's full name without fetching each user individually
    @Headers({"Accept: application/vnd.github.v3.text-match+json"})
    @GET("search/users")
    Call<UserSearchResponse> getUsers(@Query("q") String query, @Query("per_page") int perPage);
    @GET("users/{user}")
    Call<User> getUser(@Path("user") String user);

    class TextMatch {
        public String text;
        public List<Integer> indices;
    }

    class TextMatches
    {
        public String property;
        public String fragment;
        public List<TextMatch> matches;
    }

    class User {
        public String avatar_url;
        public String html_url;
        public String login;
        public String name;
        public List<TextMatches> text_matches;

        //Fetch the user name from the search results inside the text_matches field
        public String getTextMatchesName() {
            if(text_matches == null)
                return null;
            for(TextMatches textMatch : text_matches)
            {
                if(textMatch.property.equals("name"))
                    return textMatch.fragment;
            }
            return null;
        }
    }

    class UserSearchResponse {
        @SerializedName("items")
        ArrayList<User> users;
    }
}