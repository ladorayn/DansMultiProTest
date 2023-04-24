package com.lado.dansmultiprotest.viewmodel;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lado.dansmultiprotest.model.Job;
import com.lado.dansmultiprotest.network.JobsApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JobListViewModel extends ViewModel {

    private MutableLiveData<List<Job>> jobList = new MutableLiveData<>();;
    private int currentPage = 0;

    public boolean isLoading() {
        return isLoading;
    }

    private boolean isLoading = false;

    public void setFillterApplied(boolean fillterApplied) {
        isFilterApplied = fillterApplied;
    }

    private boolean isFilterApplied = false;

    public LiveData<List<Job>> getJobList() {
        return jobList;
    }

    public void loadMoreJobs(String searchTerm, String location, boolean isFullTime) {
        Log.i("HALAMAN", String.valueOf(currentPage));

        if(!isFilterApplied) {

            if (!isLoading) {

                Log.i("HALAMAN2", String.valueOf(currentPage));

                isLoading = true;
                currentPage++;

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://dev3.dansmultipro.co.id/api/recruitment/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                JobsApi jobsApi = retrofit.create(JobsApi.class);

                Call<List<Job>> call = jobsApi.getJobs(searchTerm, location, isFullTime, currentPage);

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
                        isLoading = false;
                    }
                });
            }
        }

    }

    public void loadFilteredJobs(String searchTerm, String location, boolean isFullTime) {
        Log.i("HALAMAN", String.valueOf(currentPage));

        isFilterApplied = true;
        if (!isLoading) {

            Log.i("HALAMAN2", String.valueOf(currentPage));

            isLoading = true;

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://dev3.dansmultipro.co.id/api/recruitment/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            JobsApi jobsApi = retrofit.create(JobsApi.class);

            Call<List<Job>> call = jobsApi.getFilteredJobs(searchTerm, location, isFullTime);

            call.enqueue(new Callback<List<Job>>() {
                @Override
                public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                    Log.i("ISINYA", response.toString());
                    if (response.isSuccessful()) {
                        List<Job> jobs = response.body();
                        Log.i("ISINYA", jobs.toString());

                        if (jobs != null) {
                            jobList.setValue(null);
                            jobList.setValue(jobs);
                            isLoading = false;
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Job>> call, Throwable t) {
                    isLoading = false;
                }
            });
        }
    }

    public void resetLoadJobs(String searchTerm, String location, boolean isFullTime) {
        Log.i("HALAMAN", String.valueOf(currentPage));

        currentPage = 0;
        jobList.setValue(null);
        isFilterApplied = false;
        isLoading = false;

        loadMoreJobs(searchTerm,location,isFullTime);
    }

}

