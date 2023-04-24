package com.lado.dansmultiprotest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.lado.dansmultiprotest.databinding.ActivityDetailBinding;
import com.lado.dansmultiprotest.model.Job;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding activityDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar

        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_button);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setElevation(0);

        actionBar.setTitle("JOB DETAIL");

        activityDetailBinding = ActivityDetailBinding.inflate(getLayoutInflater());

        setContentView(activityDetailBinding.getRoot());

        Job job = getIntent().getParcelableExtra("job");

        if (job != null) {

            activityDetailBinding.jobTitle.setText(!Objects.equals(job.getTitle(), "") ? job.getTitle() : activityDetailBinding.jobTitle.getText());
            activityDetailBinding.titleDesc.setText(!Objects.equals(job.getTitle(), "") ? job.getTitle() : activityDetailBinding.titleDesc.getText());
            activityDetailBinding.jobCompany.setText(!Objects.equals(job.getCompany(), "") ? job.getCompany() : activityDetailBinding.jobCompany.getText());
            Glide.with(this).load(!Objects.equals(job.getCompany_logo(), "") ? job.getCompany_logo() : activityDetailBinding.imageJob.getDrawable()).placeholder(R.drawable.ic_image_job_foreground).fitCenter().into(activityDetailBinding.imageJob);
            activityDetailBinding.urlJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(job.getUrl()));
                    startActivity(browserIntent);
                }
            });

            activityDetailBinding.titleDesc.setText(!Objects.equals(job.getTitle(), "") ? job.getTitle() : activityDetailBinding.titleDesc.getText());
            activityDetailBinding.fulltime.setText(job.isFullTime() ? "Yes" : "No");
            activityDetailBinding.desc.setText(job.getDescription() != "" ? job.getDescription() : activityDetailBinding.desc.getText());
        }



    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}