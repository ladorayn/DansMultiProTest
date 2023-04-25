package com.lado.dansmultiprotest;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lado.dansmultiprotest.adapter.JobListAdapter;
import com.lado.dansmultiprotest.databinding.FragmentHomeBinding;
import com.lado.dansmultiprotest.model.Job;
import com.lado.dansmultiprotest.network.JobsApi;
import com.lado.dansmultiprotest.network.RetrofitClient;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentHomeBinding fragmentHomeBinding;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private JobListAdapter adapter;
    private boolean isFullTime = false;

    private String searchTerm = "";
    private String location = "";
    private int page = 1;
    private int totalPage = 2;
    private boolean isLoading = false;

    private List<Job> jobs;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentHomeBinding = FragmentHomeBinding.inflate(getLayoutInflater());

        fragmentHomeBinding.filterToggleButton.setOnClickListener(filterToggleListener);

        recyclerView = fragmentHomeBinding.recyclerView;

        linearLayoutManager = new LinearLayoutManager(getActivity());
        fragmentHomeBinding.swipeRefresh.setOnRefreshListener(this);
        setupRecycleView();
        getJobs(false);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = linearLayoutManager.getChildCount();
                int pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                int total = adapter.getItemCount();
                if (!isLoading && page < totalPage) {
                    if (visibleItemCount + pastVisibleItem >= total) {
                        page++;
                        getJobs(false);
                    }
                }
            }
        });

        fragmentHomeBinding.searchView.setOnQueryTextListener(queryListener);
        fragmentHomeBinding.applyFilterButton.setOnClickListener(submitListener);

        fragmentHomeBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                page = 1;
                getJobs(true);
            }
        });

        adapter.setOnJobItemClickListener(new JobListAdapter.OnJobItemClickListener() {
            @Override
            public void onJobItemClick(Job job) {
                // Handle the click event here
                Intent i = new Intent(getContext(), DetailActivity.class);
                i.putExtra("job", job);
                startActivity(i);
            }
        });

        return fragmentHomeBinding.getRoot();
    }

    private void getJobs(boolean isOnRefresh) {
        isLoading = true;
        if (!isOnRefresh) {
            fragmentHomeBinding.progressBar.setVisibility(View.VISIBLE);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("page", Integer.toString(page));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    JobsApi jobsApi = RetrofitClient.getInstance().create(JobsApi.class);
                    jobsApi.getJobs(parameters).enqueue(new Callback<List<Job>>() {
                        @Override
                        public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                            if (response.isSuccessful()) {
                                List<Job> jobs = response.body();

                                if (jobs != null) {
                                    adapter.addJobs(jobs);
                                }

                                fragmentHomeBinding.progressBar.setVisibility(View.GONE);
                                isLoading = false;
                                fragmentHomeBinding.swipeRefresh.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Job>> call, Throwable t) {
                            Toast.makeText(getContext(), "Failed to render", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, 100);

        }
    }

    private void setupRecycleView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new JobListAdapter(getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        adapter.clear();
        page = 1;
        getJobs(true);
    }

    private View.OnClickListener filterToggleListener = v -> {
        if (fragmentHomeBinding.filterCard.getVisibility() == View.VISIBLE) {
            fragmentHomeBinding.filterCard.setVisibility(View.GONE);
            fragmentHomeBinding.filterToggleButton.setImageResource(R.drawable.ic_arrow_down);
        } else {
            fragmentHomeBinding.filterCard.setVisibility(View.VISIBLE);
            fragmentHomeBinding.filterToggleButton.setImageResource(R.drawable.ic_arrow_up);
        }
    };

    private View.OnClickListener submitListener = v -> {
        searchTerm = fragmentHomeBinding.searchView.getQuery().toString();
        location = fragmentHomeBinding.locationEditText.getText().toString().trim();
        isFullTime = fragmentHomeBinding.fullnameSwitch.isChecked();

        if (searchTerm.equals("") && location.equals("") && !isFullTime) {
            adapter.clear();
            page = 1;
            getJobs(true);
        }

        getJobsFilter(false, searchTerm, location, isFullTime);
        fragmentHomeBinding.filterCard.setVisibility(View.GONE);
    };

    private void getJobsFilter(boolean isOnRefresh, String searchTerm, String location, boolean isFullTime) {
        adapter.clear();
        isLoading = true;
        if (!isOnRefresh) {
            fragmentHomeBinding.progressBar.setVisibility(View.VISIBLE);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("description", searchTerm);
            parameters.put("location", location);
            parameters.put("type", Boolean.toString(isFullTime));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    JobsApi jobsApi = RetrofitClient.getInstance().create(JobsApi.class);
                    jobsApi.getJobs(parameters).enqueue(new Callback<List<Job>>() {
                        @Override
                        public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                            if (response.isSuccessful()) {
                                List<Job> jobs = response.body();

                                if (jobs != null) {
                                    adapter.addJobs(jobs);
                                }

                                fragmentHomeBinding.progressBar.setVisibility(View.GONE);
                                isLoading = false;
                                fragmentHomeBinding.swipeRefresh.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Job>> call, Throwable t) {
                            Toast.makeText(getContext(), "Failed to render", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, 100);

        }
    }

    private SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {

            searchTerm = query;
            getJobsFilter(false, searchTerm, location, isFullTime);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };


}