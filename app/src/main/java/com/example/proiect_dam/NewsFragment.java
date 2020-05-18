package com.example.proiect_dam;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {


    private String _title;
    private String _description;
    private String _link;

    private TextView titleET;
    private TextView descriptionET;
    private RelativeLayout rl;

    public NewsFragment(String _title, String _description, String _link) {
        this._title = _title;
        this._description = _description;
        this._link = _link;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news, container, false);

        titleET = view.findViewById(R.id.newsTitle);
        descriptionET = view.findViewById(R.id.newsDescription);

        titleET.setText(_title);
        descriptionET.setText(_description);
        rl = view.findViewById(R.id.newsRL);

        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(_link); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        return view;
    }

}
