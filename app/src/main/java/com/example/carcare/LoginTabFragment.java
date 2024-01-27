package com.example.carcare;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class LoginTabFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);


        // Find the Button within the inflated view
        Button loginButton = view.findViewById(R.id.login_button);

        // Set OnClickListener on yourButton
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMapActivity();
            }
        });

        return view;
    }

    private void moveToMapActivity() {
        // Start NavigationBarActivity
        Intent intent = new Intent(getActivity(), MapActivity.class);
        startActivity(intent);

        // Optionally, you can add an animation transition
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            // If you want to finish the current activity (fragment is part of it)
            getActivity().finish();
        }
    }
}





