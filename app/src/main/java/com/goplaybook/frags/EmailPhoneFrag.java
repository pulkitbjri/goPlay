package com.goplaybook.frags;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.goplaybook.IsDetailsFilled;
import com.goplaybook.R;
import com.goplaybook.storage.MySharedPreferences;
import com.goplaybook.storage.SPConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPhoneFrag extends Fragment implements IsDetailsFilled {


    public EmailPhoneFrag() {
        // Required empty public constructor
    }

    MySharedPreferences sp;
    EditText email,phone;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_email_phone, container, false);

        sp=MySharedPreferences.getInstance(getActivity());
        email=v.findViewById(R.id.email);
        phone=v.findViewById(R.id.phone);


        return v;
    }

    @Override
    public boolean isDetailsFilled() {
        if (email.getText().length()==0 || !btnValidateEmailAddress(email.getText().toString())){

            Toast.makeText(getActivity(),"Email not valid",Toast.LENGTH_LONG).show();
            return false;

        }
        else if (phone.getText().length()!=10){
            Toast.makeText(getActivity(),"Mobile not valid",Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            sp.set(SPConstants.email,email.getText().toString());
            sp.set(SPConstants.phone,phone.getText().toString());
            return true;
        }

    }

    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";

    public boolean btnValidateEmailAddress(String strEmailAddress)
    {
        Matcher matcherObj = Pattern.compile(emailRegex).matcher(strEmailAddress);

        if (matcherObj.matches()) {
            return true;
        } else {
            return false;
        }
    }
}
