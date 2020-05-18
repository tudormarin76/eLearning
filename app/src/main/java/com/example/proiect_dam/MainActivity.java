package com.example.proiect_dam;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Static variables
    private static final int STORAGE_PERMISSION_CODE = 101;
    public static TextView progressText;
    public static ProgressBar progressBar;
    public static int myCourses;

    private FirebaseAuth mAuth;

    //Buttons
    private Button introToAndroidBtn;
    private Button javaDeepDiveButton;
    private Button mediaButton;
    private Button advancedAndroidFeaturesButton;
    private Button mapsAndGeolocButton;
    private Button permanentStorageButton;
    private Button updateBtn;

    //Views
    private ImageView profilePic;
    private ExpandableHeightGridView coursesGl;

    private File root;
    private File file;
    private List<Course> coursesList;
    private CourseAdapter courseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = new File(Environment.getExternalStorageDirectory() + File.separator + "/ProiectDAM/App_cache");
        file = new File(root, "data.json");

        profilePic = findViewById(R.id.profilePicture);
        coursesGl = findViewById(R.id.glCourses);


        coursesList = new ArrayList<>();

        courseAdapter = new CourseAdapter(this, coursesList);
        courseAdapter.notifyDataSetChanged();
        coursesGl.setAdapter(courseAdapter);
        coursesGl.setExpanded(true);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        AppDatabase database = AppDatabase.getInstance(getApplicationContext());

        System.out.println(database.getUserDao().getAll().toString());
        SharedPreferences settingsFile = getSharedPreferences("USER", 0);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences.Editor editor = getSharedPreferences("USER", MODE_PRIVATE).edit();
        editor.putInt("userID", (user.getUid()).hashCode());
        editor.apply();

        progressText = findViewById(R.id.textMyProgress);
        progressBar = findViewById(R.id.myProgress);

        myCourses = database.getSubscriptionDao().getAll(settingsFile.getInt("userID",0)).size();
        progressText.setText("My progress: " + myCourses + "/20");
        progressBar.setProgress(myCourses);


        updateBtn = findViewById(R.id.updateButton);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findViewById(R.id.loadingPanelCourses).setVisibility(View.VISIBLE);
                loadCoursesFromFirebase(); }});
        introToAndroidBtn = findViewById(R.id.introToAndroidStudioButton);
        introToAndroidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { sortCourses("Intro to android studio"); }});
        javaDeepDiveButton = findViewById(R.id.javaDeepDiveButton);
        javaDeepDiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { sortCourses("Java Deep Dive"); }});
        mediaButton = findViewById(R.id.mediaButton);
        mediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortCourses("Media");
            }
        });
        advancedAndroidFeaturesButton = findViewById(R.id.advancedAndroidFeaturesButton);
        advancedAndroidFeaturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { sortCourses("Advanced Android Features"); }});
        mapsAndGeolocButton = findViewById(R.id.mapsAndGeolocButton);
        mapsAndGeolocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { sortCourses("Maps And Geolocation"); }});
        permanentStorageButton = findViewById(R.id.permanentStorageButton);
        permanentStorageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { sortCourses("Permanent Storage"); }});


        checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE);

        loadNews();


        //Load user profile image
        new Thread(new Runnable() {
            public void run() {
                mAuth = FirebaseAuth.getInstance();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                FirebaseUser user = mAuth.getCurrentUser();
                StorageReference profileref = storageRef.child(user.getEmail() + ".jpg");
                final long ONE_MEGABYTE = 1024 * 1024;
                profileref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        if (bitmap != null)
                            profilePic.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        return;
                    }
                });
            }
        }).start();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyProfile.class);
                startActivity(intent);
                finish(); }});
    }

    public void sortCourses(String constraint) {
        final List<Course> list = new ArrayList<>(coursesList);
        final CourseAdapter ca = new CourseAdapter(MainActivity.this, list);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                coursesGl.setAdapter(ca);
                findViewById(R.id.loadingPanelCourses).setVisibility(View.GONE);
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 2000);
        ca.getFilter().filter(constraint);
        findViewById(R.id.loadingPanelCourses).setVisibility(View.VISIBLE);
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{permission},
                    requestCode);
        } else {

            if (!file.exists()) {
                loadCoursesFromFirebase();
            } else {
                loadCourses();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
                if (!file.exists()) {
                    loadCoursesFromFirebase();
                } else {

                    loadCourses();
                }
            } else {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onClick(View view) {
    }

    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void loadCourses() {
        try {
            JSONObject mJsonArray = new JSONObject(loadJSONFromAsset(this));
            final AppDatabase database = AppDatabase.getInstance(getApplicationContext());
            JSONArray courses = mJsonArray.getJSONArray("Courses");
            coursesList.clear();
            for (int i = 0; i < courses.length(); i++) {
                JSONObject mJsonObjectProperty = courses.getJSONObject(i);

                String id = mJsonObjectProperty.getString("id");
                String title = mJsonObjectProperty.getString("title");
                String description = mJsonObjectProperty.getString("description");
                String content = mJsonObjectProperty.getString("content");
                String image = mJsonObjectProperty.getString("image");

                if(database.getCoursesDao().getCourse(id.hashCode())==null) {
                    database.getCoursesDao().insert(new SubscriptedCourses(id.hashCode(), title));
                }
                int resourceId = getApplicationContext().getResources().getIdentifier(image, "drawable", getApplicationContext().getPackageName());
                coursesList.add(new Course(resourceId, title, description, content, id));

            }


            courseAdapter.notifyDataSetChanged();
            coursesGl.setAdapter(courseAdapter);
            findViewById(R.id.loadingPanelCourses).setVisibility(View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadCoursesFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final JSONArray courses = new JSONArray();
        final AppDatabase database = AppDatabase.getInstance(getApplicationContext());

        db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        JSONObject course = new JSONObject();
                        try {
                            course.put("id", document.getId());
                            course.put("title", document.get("title").toString());
                            course.put("description", document.get("description").toString());
                            course.put("content", document.get("content").toString());
                            course.put("image", document.get("image").toString());

                            if(database.getCoursesDao().getCourse((document.getId()).hashCode())==null) {
                                database.getCoursesDao().insert(new SubscriptedCourses((document.getId()).hashCode(), document.get("title").toString()));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        courses.put(course);
                    }
                    try {
                        JSONObject coursesOBJ = new JSONObject();
                        coursesOBJ.put("Courses", courses);
                        WriteObjectFile o = new WriteObjectFile(new AsyncResponse() {
                            @Override
                            public Void processFinish(Void output) {
                            findViewById(R.id.loadingPanelCourses).setVisibility(View.GONE);
                            loadCourses();
                            return null;
                            }
                        });
                        o.WriteJSON(coursesOBJ);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });

    }

    public void loadNews() {
        @SuppressLint("StaticFieldLeak") ReadXMLAsync o = new ReadXMLAsync() {
            @Override
            protected void onPostExecute(List<News> news) {
                System.out.println(news.size());
                findViewById(R.id.loadingPanelNews).setVisibility(View.GONE);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                for (News n : news) {
                    fragmentTransaction.add(R.id.newsLL, new NewsFragment(n.getTitle(), n.getDescription(), n.getLink()));
                }
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        try {
            o.execute(new URL("https://www.reddit.com/r/androiddev/.rss?format=xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
