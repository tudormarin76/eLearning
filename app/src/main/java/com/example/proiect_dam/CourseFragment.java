package com.example.proiect_dam;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseFragment extends Fragment {


    private String _title;
    private String _description;
    private String _content;
    private int _image;

    private TextView titleET;
    private TextView descriptionET;
    private ImageView backgroundImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_course, container, false);

        titleET = view.findViewById(R.id.courseFragmentTitle);
        descriptionET = view.findViewById(R.id.courseFragmentDescription);
        backgroundImage = view.findViewById(R.id.courseFragmentImage);

        titleET.setText(_title);
        descriptionET.setText(_description);

        backgroundImage.setImageResource(_image);


        backgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),Course.class);
                intent.putExtra("description", _description);
                intent.putExtra("title",_title);
                intent.putExtra("image",_image);
                intent.putExtra("content", _content);
                startActivity(intent);
            }
        });

        return view;
    }


}
