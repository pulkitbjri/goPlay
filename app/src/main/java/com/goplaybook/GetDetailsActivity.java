package com.goplaybook;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.goplaybook.frags.BirthDayFrag;
import com.goplaybook.frags.EmailPhoneFrag;
import com.goplaybook.frags.GenderFragment;
import com.goplaybook.frags.SportsFragment;
import com.goplaybook.storage.MySharedPreferences;
import com.goplaybook.storage.SPConstants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class GetDetailsActivity extends AppCompatActivity {

    Button next;
    ImageView imageView;
    TextView text;
    MySharedPreferences sp;
    int pos=0;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_details);

        sp=MySharedPreferences.getInstance(this);
        imageView=findViewById(R.id.image);
        next=findViewById(R.id.next);
        text=findViewById(R.id.text);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate())
                    changefrags();
                text.setVisibility(View.GONE);

            }
        });

        Picasso.get()
                .load(sp.get(SPConstants.imageurl))
                .into(imageView);

    }

    private boolean validate() {
        IsDetailsFilled detailsFilled;
        if (pos==0)
            return true;
        else if (pos==1){
            detailsFilled=(EmailPhoneFrag)fragment;
        }
        else if (pos==2){
            detailsFilled=(BirthDayFrag)fragment;
        }
        else if (pos==3){
            detailsFilled=(GenderFragment)fragment;
        }
        else if (pos==4){
            detailsFilled=(SportsFragment)fragment;
        }
        else
            return false;

        if (detailsFilled.isDetailsFilled())
            return true;

        return false;
    }

    private void changefrags() {
        if (pos==0)
            next.setText("next");

        if (pos==0)
            fragment =new EmailPhoneFrag();
        else if (pos==1)
            fragment =new BirthDayFrag();
        else if (pos==2)
            fragment =new GenderFragment();
        else if (pos==3)
        {
            fragment =new SportsFragment();
            next.setText("Generate Your Sports Aadhaar");
        }
        else if (pos==4)
        {
            sp.set(SPConstants.isGeneeated,"true");
            onBackPressed();
        }
        else
            return;
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.fraglay, fragment);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        trans.addToBackStack(EmailPhoneFrag.class.getName());
        trans.commit();


        pos++;

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to close");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GetDetailsActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No",null);
        builder.create();
        builder.show();
    }
}
