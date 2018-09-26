package com.goplaybook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.goplaybook.frags.AadharFrag;
import com.goplaybook.storage.MySharedPreferences;
import com.goplaybook.storage.SPConstants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;


public class HomeActivity extends AppCompatActivity {
    MySharedPreferences sp;
    Toolbar toolbar;
    TextView location,name,dob,email,mobile,gender,primary,other;
    Button details;
    ImageView image,back;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp=MySharedPreferences.getInstance(this);
        back=findViewById(R.id.back);
        image=findViewById(R.id.image);
        name=findViewById(R.id.name);
        location=findViewById(R.id.location);
        toolbar=findViewById(R.id.toolbar);
        details=findViewById(R.id.details);
        linearLayout=findViewById(R.id.rv);
        dob=findViewById(R.id.dob);
        mobile=findViewById(R.id.mobile);
        gender=findViewById(R.id.gender);
        email=findViewById(R.id.email);
        primary=findViewById(R.id.primary);
        other=findViewById(R.id.other);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sports Aadhaar");

        if (sp.get(SPConstants.location).equalsIgnoreCase(""))
            location.setVisibility(View.GONE);
        location.setText(sp.get(SPConstants.location));
        name.setText(sp.get(SPConstants.name));
        setimage();

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sp.chk(SPConstants.isGeneeated)) {
                    Intent intent = new Intent(HomeActivity.this, GetDetailsActivity.class);
                    View viewStart = findViewById(R.id.card);
                    ActivityOptionsCompat options =

                            ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this,
                                    viewStart,   // Starting view
                                    "image"    // The String
                            );
                    ActivityCompat.startActivity(HomeActivity.this, intent, options.toBundle());
                }
                else
                {
                    BottomSheetDialogFragment bottomSheetDialogFragment=new AadharFrag();
                    bottomSheetDialogFragment.show(getSupportFragmentManager(),"");
//                    startActivity(new Intent(HomeActivity.this,AadhaarActivity.class));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sp.chk(SPConstants.isGeneeated))
        {
            linearLayout.setVisibility(View.VISIBLE);
            details.setText("View Your Aadhar");

            email.setText("Email: "+sp.get(SPConstants.email));
            dob.setText("Date Of Birth: "+sp.get(SPConstants.bday));
            primary.setText("Primary Sport: "+sp.get(SPConstants.primarysport));
            mobile.setText("Mobile: "+sp.get(SPConstants.phone));
            other.setText("Other Sports: "+sp.get(SPConstants.othersport));
            gender.setText("Gender: "+sp.get(SPConstants.gender));
        }
    }

    private void setimage() {
        Log.i("", "setimage: "+sp.get(SPConstants.imageurl));
        Picasso.get()
                .load(sp.get(SPConstants.imageurl))
//                .into(image);
                .into(new Target() {
                          @Override
                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                              image.setImageBitmap(bitmap);
                          }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                          @Override
                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                          }
                      }
                );
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.logout){
            if (sp.get("from").equalsIgnoreCase("fb")){
                LoginManager.getInstance().logOut();
                sp.removeall();
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                HomeActivity.this.finish();
            }
            else {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                GoogleSignIn.getClient(this, gso).signOut()
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sp.removeall();
                                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                                HomeActivity.this.finish();
                            }
                        });
            }



        }

        return super.onOptionsItemSelected(item);
    }
}
