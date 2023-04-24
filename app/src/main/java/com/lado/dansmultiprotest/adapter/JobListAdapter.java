package com.lado.dansmultiprotest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lado.dansmultiprotest.R;
import com.lado.dansmultiprotest.databinding.JobListItemLayoutBinding;
import com.lado.dansmultiprotest.model.Job;

import java.util.ArrayList;
import java.util.List;

public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.ViewHolder> {
    private List<Job> jobs;
    private Context context;

    public interface OnJobItemClickListener {
        void onJobItemClick(Job job);
    }

    private OnJobItemClickListener listener;

    public void setOnJobItemClickListener(OnJobItemClickListener listener) {
        this.listener = listener;
    }

    public JobListAdapter(Context context) {
        jobs = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public JobListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        JobListItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.job_list_item_layout, parent, false );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Job job = jobs.get(position);

        if (job != null) {
            holder.binding.jobTitle.setText(job.getTitle());
            holder.binding.jobCompany.setText(job.getCompany());
            holder.binding.jobLocation.setText(job.getLocation());
            Glide.with(context)
                    .load(job.getCompany_logo())
                    .fitCenter()
                    .placeholder(R.drawable.ic_image_job_foreground)
                    .into(holder.binding.imageJob);

        }

    }

    @Override
    public int getItemCount() {
        return jobs == null ? 0 : jobs.size();
    }

    public void setJobs(List<Job> jobsList) {
        jobs = jobsList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        JobListItemLayoutBinding binding;

        public ViewHolder(@NonNull JobListItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.detailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        Job job = jobs.get(position);
                        listener.onJobItemClick(job);
                    }
                }
            });
        }
    }
}

