package com.example.cycling.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cycling.DBHelper;
import com.example.cycling.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imagePreview;
    private Uri imageUri;
    private TextInputEditText etTrailName, etLocationName;
    private Button btnSelectImage, btnUpload;

    DBHelper db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
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
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        imagePreview = view.findViewById(R.id.imagePreview);
        etTrailName = view.findViewById(R.id.etTrailName);
        etLocationName = view.findViewById(R.id.etLocationName);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnUpload = view.findViewById(R.id.btnUpload);

        db = DBHelper.getInstance(getContext());

        btnSelectImage.setOnClickListener(v -> openFileChooser());
        btnUpload.setOnClickListener(v -> saveTrail());

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Odaberite sliku"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
        }
    }

    private void saveTrail() {
        String trailName = etTrailName.getText().toString().trim();
        String locationName = etLocationName.getText().toString().trim();

        if (trailName.isEmpty() || locationName.isEmpty() || imageUri == null) {
            Toast.makeText(getContext(), "Popunite sve podatke i izaberite sliku", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 1. Snimi sliku u internal storage
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String filename = "trail_" + timestamp + ".jpg";
            File file = new File(getContext().getFilesDir(), filename);

            InputStream is = getContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            if (is != null) is.close();

            // 2. Uzmi userId iz SharedPreferences
            SharedPreferences prefs = getContext().getSharedPreferences("session", Context.MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);
            if (userId == -1) {
                Toast.makeText(getContext(), "Korisnik nije prijavljen", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Ubaci stazu u SQLite
            DBHelper db = DBHelper.getInstance(getContext());

            ContentValues trailValues = new ContentValues();
            trailValues.put("title", trailName);
            trailValues.put("image_path", file.getAbsolutePath());
            trailValues.put("user_id", userId);
            trailValues.put("created_at", timestamp);

            long trailId = db.getWritableDatabase().insert("trails", null, trailValues);

            // 4. Ubaci lokaciju u trail_locations
            ContentValues locationValues = new ContentValues();
            locationValues.put("trail_id", trailId);
            locationValues.put("location_name", locationName);

            db.getWritableDatabase().insert("trail_locations", null, locationValues);

            // 5. Reset forme
            Toast.makeText(getContext(), "Staza uspešno sačuvana", Toast.LENGTH_SHORT).show();
            etTrailName.setText("");
            etLocationName.setText("");
            imagePreview.setImageResource(R.drawable.ic_trail_placeholder);
            imageUri = null;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Greška pri čuvanju staze", Toast.LENGTH_SHORT).show();
        }
    }
}