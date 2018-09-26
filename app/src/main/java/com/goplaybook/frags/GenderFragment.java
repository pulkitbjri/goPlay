package com.goplaybook.frags;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.goplaybook.IsDetailsFilled;
import com.goplaybook.R;
import com.goplaybook.storage.MySharedPreferences;
import com.goplaybook.storage.SPConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenderFragment extends Fragment implements IsDetailsFilled
{

    MySharedPreferences sp;

    public GenderFragment() {
        // Required empty public constructor
    }
    RadioGroup radioGroup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_genger, container, false);
        sp=MySharedPreferences.getInstance(getActivity());
        radioGroup=v.findViewById(R.id.radioGroup);


        return v;
    }

    @Override
    public boolean isDetailsFilled() {
        if (radioGroup.getCheckedRadioButtonId()==R.id.male)
            sp.set(SPConstants.gender,"male");
        else
            sp.set(SPConstants.gender,"female");

        return true;
    }
}
