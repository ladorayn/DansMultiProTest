package com.lado.dansmultiprotest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationBarView;
import com.lado.dansmultiprotest.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;
    private String fullname;
    private TextView appbarTitle;

    public String getUrlPhoto() {
        return urlPhoto;
    }

    private String urlPhoto;
    private LoginBy loginBy;


    public String getFullName() {
        return fullname;
    }

    public LoginBy checkThirdPartyLogin() {
        if (AccessToken.getCurrentAccessToken() != null) {
            return LoginBy.FACEBOOK;
        } else {
            return LoginBy.GOOGLE;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        appbarTitle = findViewById(R.id.tvTitle);


        getSupportActionBar().setElevation(0);

        setContentView(activityMainBinding.getRoot());


        loginBy = checkThirdPartyLogin();

        switch (loginBy) {
            case FACEBOOK:
                AccessToken accessToken = AccessToken.getCurrentAccessToken();

                GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
                        try {
                            assert jsonObject != null;
                            fullname = jsonObject.getString("name");
                            JSONObject pictureObject = jsonObject.getJSONObject("picture");
                            JSONObject dataObject = pictureObject.getJSONObject("data");
                            urlPhoto = dataObject.getString("url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();

                parameters.putString("fields", "name,picture.type(large)");

                graphRequest.setParameters(parameters);

                graphRequest.executeAsync();
                break;
            case GOOGLE:
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                if (account != null) {
                    fullname = account.getDisplayName();
                    if (account.getPhotoUrl() != null) {
                        urlPhoto = account.getPhotoUrl().toString();
                    }
                }
                break;

        }



        appbarTitle.setText("JOB LIST");
        replaceFragment(new HomeFragment());


        activityMainBinding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    appbarTitle.setText("JOB LIST");
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.person:
                    appbarTitle.setText("ACCOUNT");
                    replaceFragment(new AccountFragment());
                    break;

            }
            return true;
        });


    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout_item) {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
                // Perform any additional actions after logging out of Facebook
            } else {
                GoogleSignInOptions gso = new GoogleSignInOptions.
                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                        build();

                GoogleSignInClient googleSignInClient=GoogleSignIn.getClient(this,gso);
                googleSignInClient.signOut();
            }
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}