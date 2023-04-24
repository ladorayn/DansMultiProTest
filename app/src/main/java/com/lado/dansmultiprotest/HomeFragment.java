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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lado.dansmultiprotest.adapter.JobListAdapter;
import com.lado.dansmultiprotest.databinding.FragmentAccountBinding;
import com.lado.dansmultiprotest.databinding.FragmentHomeBinding;
import com.lado.dansmultiprotest.model.Job;
import com.lado.dansmultiprotest.viewmodel.JobListViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private JobListViewModel jobListViewModel;
    private FragmentHomeBinding fragmentHomeBinding;
    private JobListAdapter adapter;
    private boolean isFullTime = false;

    private String searchTerm = "";
    private String location = "";
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

        jobListViewModel = new ViewModelProvider(this).get(JobListViewModel.class);

        fragmentHomeBinding.filterToggleButton.setOnClickListener(filterToggleListener);

        RecyclerView recyclerView = fragmentHomeBinding.recyclerView;
        adapter = new JobListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(scrollListener);
        fragmentHomeBinding.searchView.setOnQueryTextListener(queryListener);
        fragmentHomeBinding.applyFilterButton.setOnClickListener(submitListener);

        adapter.setOnJobItemClickListener(new JobListAdapter.OnJobItemClickListener() {
            @Override
            public void onJobItemClick(Job job) {
                // Handle the click event here
                Intent i = new Intent(getContext(), DetailActivity.class);
                i.putExtra("job", job);
                startActivity(i);
            }
        });

        jobListViewModel.getJobList().observe(getViewLifecycleOwner(), jobs1 -> {
            adapter.setJobs(jobs1);
            recyclerView.setAdapter(adapter);
        });
        jobListViewModel.loadMoreJobs(searchTerm, location, isFullTime);

        return fragmentHomeBinding.getRoot();
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
            jobListViewModel.resetLoadJobs(searchTerm, location, false);
        }

        jobListViewModel.loadFilteredJobs(searchTerm, location, isFullTime);
        Log.i("CARI", searchTerm);
        Log.i("TEMPAT", location);
        fragmentHomeBinding.filterCard.setVisibility(View.GONE);
    };

    private SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {

            Log.i("QUERYSUB", query);

            searchTerm = query;
            jobListViewModel.loadFilteredJobs(searchTerm, location, isFullTime);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            Log.i("ONSCROLL", "DISINI");

            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int totalItemCount = layoutManager.getItemCount();
            int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

            if (!jobListViewModel.isLoading() && totalItemCount <= (lastVisibleItem + 5)) {
                isLoading = true;
                jobListViewModel.loadMoreJobs(searchTerm, location, isFullTime);
            }
        }
    };
}