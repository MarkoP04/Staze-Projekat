package com.example.cycling.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cycling.DBHelper;
import com.example.cycling.LoginActivity;
import com.example.cycling.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    TextView usernameTv, emailTv;
    Button logoutBtn, registerBtn;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameTv = view.findViewById(R.id.usernameTv);
        emailTv = view.findViewById(R.id.emailTv);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        registerBtn = view.findViewById(R.id.registerBtn);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("session", Context.MODE_PRIVATE);

        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            // GUEST
            usernameTv.setVisibility(View.GONE);
            emailTv.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.GONE);

            registerBtn.setOnClickListener(v ->
                    startActivity(new Intent(getActivity(), LoginActivity.class))
            );

        } else {
            // ULOGOVAN
            registerBtn.setVisibility(View.GONE);

            DBHelper db = DBHelper.getInstance(getContext());
            Cursor c = db.getUserById(userId);

            if (c.moveToFirst()) {
                usernameTv.setText(c.getString(c.getColumnIndexOrThrow("username")));
                emailTv.setText(c.getString(c.getColumnIndexOrThrow("email")));
            }
            c.close();

            logoutBtn.setOnClickListener(v -> {
                prefs.edit().clear().apply();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                requireActivity().finish();
            });
        }
        return view;
    }
}