package com.example.cycling.ui;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.cycling.DBHelper;
import com.example.cycling.R;
import com.example.cycling.Trail;
import com.example.cycling.TrailsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrailsFragment extends Fragment {

    EditText searchEt;
    ImageView profileIv;
    RecyclerView trailsRv;
    TrailsAdapter adapter;
    List<Trail> trailList = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TrailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrailsFragment newInstance(String param1, String param2) {
        TrailsFragment fragment = new TrailsFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trails, container, false);

        searchEt = view.findViewById(R.id.searchEt);
        profileIv = view.findViewById(R.id.profileIv);
        trailsRv = view.findViewById(R.id.trailsRv);

        // RecyclerView
        trailsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TrailsAdapter(trailList);
        trailsRv.setAdapter(adapter);


        loadTrails("");

        // Search listener
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadTrails(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        return view;
    }

    private void loadTrails(String filter) {
        trailList.clear();
        DBHelper db = DBHelper.getInstance(getContext());
        Cursor c;

        if(filter.isEmpty()){
            c = db.getAllTrails();
        } else {
            c = db.searchTrailsByLocation(filter);
        }

        if(c.moveToFirst()){
            do{
                Trail t = new Trail();
                t.setId(c.getInt(c.getColumnIndexOrThrow("id")));
                t.setImagePath(c.getString(c.getColumnIndexOrThrow("image_path")));
                t.setUserId(c.getInt(c.getColumnIndexOrThrow("user_id")));

                // povuci lokacije
                List<String> locations = db.getTrailLocations(t.getId());
                t.setLocations(locations);

                trailList.add(t);
            } while(c.moveToNext());
        }
        c.close();
        adapter.notifyDataSetChanged();
    }
}