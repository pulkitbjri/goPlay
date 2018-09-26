package com.goplaybook;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.goplaybook.storage.MySharedPreferences;
import com.goplaybook.storage.SPConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 2;
    static public final int REQUEST_LOCATION = 1;

    CallbackManager callbackManager;
    LoginButton loginButton;
    SignInButton signInButton;

    private static final String EMAIL = "email";
    GoogleSignInClient mGoogleSignInClient;
    MySharedPreferences sp;
    String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
    FusedLocationProviderClient mFusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize SharedPrefrence
        sp = MySharedPreferences.getInstance(this);
        //initialize FB SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        //Location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        //initialize GOOGLE SDK
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (sp.chk("isLogin")){
            if (sp.get("isLogin").equalsIgnoreCase("y"))
            {
                startActivity(new Intent(this,HomeActivity.class));
                this.finish();
            }
        }
        int PERMISSION_ALL = 1;

        setContentView(R.layout.activity_main);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        //Init VIews
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        signInButton = findViewById(R.id.sign_in_button);

        //FB login
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("Main", response.toString());
                                JSONObject mUser = response.getJSONObject();
                                try {

                                    saveAndStart(mUser.getString("name")
                                            ,mUser.getJSONObject("picture").getJSONObject("data").getString("url")
                                            ,mUser.getString("id"),loginResult.getAccessToken().getToken(), "fb");

                                } catch (JSONException e) {
                                    Log.e("", e.toString());
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "picture.type(large),id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {

            }
        });

        //Google Login
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }


    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            saveAndStart(account.getDisplayName(),""+account.getPhotoUrl(),account.getId(),account.getIdToken(),"google");
            Log.i("", "handleGoogleSignInResult: "+account.getPhotoUrl());
        } catch (ApiException e) {
            Log.w("", "signInResult:failed code=" + e.getStatusCode());
        }
    }


    private void saveAndStart(String name, String imageurl, String id, String token, String from){
        sp.set(SPConstants.name,name);
        sp.set(SPConstants.imageurl,imageurl);
        sp.set(SPConstants.id,id);
        sp.set(SPConstants.token,token);
        sp.set(SPConstants.from,from);
        sp.set(SPConstants.isLogin,"y");

        if (hasPermissions(this,PERMISSIONS))
        {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.

                            if (location != null) {
                                // Logic to handle location object

                                Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (addresses.size() > 0) {
                                    System.out.println(addresses.get(0).getLocality());
                                    sp.set(SPConstants.location,addresses.get(0).getLocality()+", "+addresses.get(0).getCountryName());
                                }
                                else {
                                    sp.set(SPConstants.location,"");

                                    // do your stuff
                                }

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,"fail",Toast.LENGTH_LONG).show();

                }
            });
        }


        startActivity(new Intent(this,HomeActivity.class));
        this.finish();
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
