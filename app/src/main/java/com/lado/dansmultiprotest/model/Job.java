package com.lado.dansmultiprotest.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Job implements Parcelable {


    @SerializedName("title")
    private String title;
    @SerializedName("location")
    private String location;
    @SerializedName("company_logo")
    private String company_logo;
    @SerializedName("company")
    private String company;

    protected Job(Parcel in) {
        title = in.readString();
        location = in.readString();
        company_logo = in.readString();
        company = in.readString();
        url = in.readString();
        description = in.readString();
        isFullTime = in.readByte() != 0;
    }

    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @SerializedName("url")
    private String url;

    public Job(String title, String location, String company_logo, String company, String description, boolean isFullTime) {
        this.title = title;
        this.location = location;
        this.company_logo = company_logo;
        this.company = company;
        this.description = description;
        this.isFullTime = isFullTime;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    private String description;
    private boolean isFullTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCompany_logo() {
        return company_logo;
    }

    public void setCompany_logo(String company_logo) {
        this.company_logo = company_logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFullTime() {
        return isFullTime;
    }

    public void setFullTime(boolean fullTime) {
        isFullTime = fullTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(location);
        dest.writeString(company_logo);
        dest.writeString(company);
        dest.writeString(url);
        dest.writeString(description);
        dest.writeByte((byte) (isFullTime ? 1 : 0));
    }

    // Getters and setters omitted for brevity
}
