package com.goplaybook.frags;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goplaybook.R;
import com.goplaybook.storage.MySharedPreferences;
import com.goplaybook.storage.SPConstants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class AadharFrag extends BottomSheetDialogFragment {
    TextView location,name,dob,email,mobile,primary,other;
    ImageView image;
    MySharedPreferences sp;
    CardView card;
    public AadharFrag() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.activity_aadhaar, container, false);


        sp=MySharedPreferences.getInstance(getActivity());
        image=v.findViewById(R.id.image);
        name=v.findViewById(R.id.name);
        location=v.findViewById(R.id.location);
        dob=v.findViewById(R.id.dob);
        mobile=v.findViewById(R.id.mobile);
        email=v.findViewById(R.id.email);
        primary=v.findViewById(R.id.primary);
        other=v.findViewById(R.id.other);
        card=v.findViewById(R.id.card);

        location.setText("Location: "+sp.get(SPConstants.location));
        email.setText("Email: "+sp.get(SPConstants.email)+"("+sp.get(SPConstants.gender)  +")");
        dob.setText("Date Of Birth: "+sp.get(SPConstants.bday));
        primary.setText("Primary Sport: "+sp.get(SPConstants.primarysport));
        mobile.setText("Mobile: "+sp.get(SPConstants.phone));
        other.setText("Other Sports: "+sp.get(SPConstants.othersport));

        Picasso.get()
                .load(sp.get(SPConstants.imageurl))
                .into(image);


        v.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap b=createBitmapFromView(card);
//                image.setImageBitmap(b);
                try {

                    File cachePath = new File(getActivity().getCacheDir(), "images");
                    cachePath.mkdirs(); // don't forget to make the directory
                    FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                    b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                File imagePath = new File(getActivity().getCacheDir(), "images");
                File newFile = new File(imagePath, "image.png");
                Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.goplaybook.fileprovider", newFile);

                if (contentUri != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                    shareIntent.setDataAndType(contentUri, getActivity().getContentResolver().getType(contentUri));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    startActivity(Intent.createChooser(shareIntent, "Choose an app"));
                }

            }
        });
        return v;

    }
    public Bitmap createBitmapFromView(View v) {
        v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return bitmap;
    }
}
