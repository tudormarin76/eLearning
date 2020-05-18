package com.example.proiect_dam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

public class Course extends AppCompatActivity {

    private TextView title;
    private TextView description;
    private TextView content;
    private RoundedImageView image;

    public String _title;
    public String _description;
    public String _content;
    public String _id;
    public int _image;


    public Course(int image, String title, String description, String content, String id){
        _image = image;
        _title = title;
        _description = description;
        _content = content;
        _id = id;
    }
    public Course(){ }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        Bundle bundle = getIntent().getExtras();

        title = findViewById(R.id.courseTitle);
        description = findViewById(R.id.courseDescription);
        image = findViewById(R.id.courseImage);
        content = findViewById(R.id.courseContent);



        if(bundle.getString("title")!= null)
        {
            title.setText(bundle.getString("title"));
        }
        if(bundle.getString("description")!= null)
        {
            description.setText(bundle.getString("description"));
        }
        if(bundle.getString("content")!= null) {
            content.setText(bundle.getString("content"));
        }
        image.setImageResource(bundle.getInt("image"));

        if(!title.getText().equals("Using maps in your app")){
            findViewById(R.id.map).setVisibility(View.GONE);
        }
    }
}
