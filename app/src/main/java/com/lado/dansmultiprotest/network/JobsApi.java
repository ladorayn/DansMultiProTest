package com.lado.dansmultiprotest.network;

import com.lado.dansmultiprotest.model.Job;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JobsApi {

    @GET("positions.json")
    Call<List<Job>> getJobs(@Query("description") String searchTerm,
                            @Query("location") String location,
                            @Query("full_time") boolean isFullTime,
                            @Query("page") int page);

    @GET("positions.json")
    Call<List<Job>> getFilteredJobs(@Query("description") String searchTerm,
                            @Query("location") String location,
                            @Query("full_time") boolean isFullTime);
}

