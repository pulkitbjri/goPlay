package com.goplaybook.frags;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.goplaybook.IsDetailsFilled;
import com.goplaybook.R;
import com.goplaybook.storage.MySharedPreferences;
import com.goplaybook.storage.SPConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class SportsFragment extends Fragment implements IsDetailsFilled {


    public SportsFragment() {
        // Required empty public constructor
    }

    EditText other,primary;
    MySharedPreferences sp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_sports, container, false);
        sp=MySharedPreferences.getInstance(getActivity());
        other=v.findViewById(R.id.other);
        primary=v.findViewById(R.id.primary);

        return v;
    }

    @Override
    public boolean isDetailsFilled() {
        if (other.getText().length()==0)
        {
            Toast.makeText(getActivity(),"Primary Sport not valid",Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            sp.set(SPConstants.primarysport,primary.getText().toString());
            sp.set(SPConstants.othersport,other.getText().toString());
            return true;
        }
    }
}
