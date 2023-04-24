package com.lado.dansmultiprotest.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.lado.dansmultiprotest.model.Job;
import com.lado.dansmultiprotest.network.JobsApi;
import com.lado.dansmultiprotest.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepo {

    private JobsApi endpoints;
    private MutableLiveData<List<Job>> jobList = new MutableLiveData<>();
    private int currentPage = 0;
    private boolean isLoading = false;

    public AppRepo() {
        endpoints = RetrofitClient.getInstance().create(JobsApi.class);
    }

    public MutableLiveData<List<Job>> getJobs(String searchTerm, String location, boolean isFullTime) {

        Log.i("MASOK", "MASOK");
        if (!isLoading) {
            Log.i("DALEM", "MASOK");
            currentPage++;
            Call<List<Job>> call = endpoints.getJobs(searchTerm, location, isFullTime, currentPage);

            call.enqueue(new Callback<List<Job>>() {
                @Override
                public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                    Log.i("ISINYA", response.toString());
                    if (response.isSuccessful()) {
                        List<Job> jobs = response.body();
                        Log.i("ISINYA", jobs.toString());

                        if (jobs != null) {
                            jobList.setValue(jobs);
                            isLoading = false;
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Job>> call, Throwable t) {
                    Log.e("API", "Failed: " + t.getMessage());
                    isLoading = false;
                }
            });
        }

        return jobList;
    }
}
