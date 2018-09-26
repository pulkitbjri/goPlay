package com.goplaybook.frags;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.goplaybook.IsDetailsFilled;
import com.goplaybook.R;
import com.goplaybook.storage.MySharedPreferences;
import com.goplaybook.storage.SPConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class BirthDayFrag extends Fragment implements IsDetailsFilled {


    public BirthDayFrag() {
        // Required empty public constructor
    }
    Calendar myCalendar ;
    MySharedPreferences sp;
    EditText dob;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_birth_day, container, false);
        sp=MySharedPreferences.getInstance(getActivity());
        dob=v.findViewById(R.id.dob);
        myCalendar = Calendar.getInstance();
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        return v;
    }
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dob.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean isDetailsFilled() {
        if (dob.getText().length()==0)
        {
            Toast.makeText(getActivity(),"DOB not valid",Toast.LENGTH_LONG).show();

            return false;
        }
        else
        {
            sp.set(SPConstants.bday,dob.getText().toString());
            return true;
        }
    }
}
