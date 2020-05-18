package com.example.proiect_dam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.series.XYSeries;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.androidplot.xy.YLayoutStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyProfile extends AppCompatActivity implements View.OnClickListener{

    private TextView name;
    private ImageButton profilePic;
    private TextView userClass;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private Button logoutButton;
    private Button generateFileButton;
    private final static int SELECT_PHOTO = 12345;
    public static final String PREFERENCES_FILE_NAME = "USER";
    private ExpandableHeightListView lv;
    private XYPlot plot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setTitle("Loading..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        profilePic = findViewById(R.id.imagePick);
        name = findViewById(R.id.name);
        userClass = findViewById(R.id.userClass);
        lv = findViewById(R.id.myCoursesLV);
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this);
        generateFileButton = findViewById(R.id.generateFileButton);
        generateFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ExportDatabaseCSVTask().execute();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        StorageReference profileref = storageRef.child(user.getEmail() + ".jpg");

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        final long ONE_MEGABYTE = 1024 * 1024;

        if(profileref!=null) {
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
                    // Handle any errors
                }
            });
        }
        DocumentReference docRef = db.collection("users").document(user.getEmail().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                     name.setText(String.format("Name: %s", document.get("name").toString()));
                     userClass.setText(String.format("Class: %s",document.get("class").toString()));
                     progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                }
            }
        });

        SharedPreferences settingsFile = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        AppDatabase database = AppDatabase.getInstance(this);
        List<MySubscriptedCourses> myList = new ArrayList<>();

        for(Subscription s : database.getSubscriptionDao().getAll(settingsFile.getInt("userID",0))){
            SubscriptedCourses sc = database.getCoursesDao().getCourse(s.subscriptedCourseId);
            MySubscriptedCourses msc = new MySubscriptedCourses(sc.getTitle(), s.getData());
            myList.add(msc);
        }
        ArrayAdapter<MySubscriptedCourses> adaptor = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, myList);
        lv.setAdapter(adaptor);


        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        System.out.println(database.getUserDao().getAll().size());

        List<Double> usersProgress = new ArrayList<>();
        for(User u : database.getUserDao().getAll()){
            usersProgress.add(Double.valueOf(database.getSubscriptionDao().getUserCourses(u.getId()).size()));
        }

        XYSeries s1 = new SimpleXYSeries(usersProgress,
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Users Progress");
        plot.addSeries(s1, new LineAndPointFormatter(
                getApplicationContext(), R.layout.f1));

        plot.position(plot.getDomainLabelWidget(),45, XLayoutStyle.ABSOLUTE_FROM_LEFT, 0,
                YLayoutStyle.ABSOLUTE_FROM_BOTTOM,
                AnchorPosition.LEFT_BOTTOM);

        plot.setTitle("Your status");
        plot.centerOnRangeOrigin(10);//60
        plot.centerOnDomainOrigin(Math.ceil(usersProgress.size()/2));//5
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 1);
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
        plot.getLayoutManager().remove(plot.getDomainLabelWidget());
        plot.getLayoutManager().remove(plot.getRangeLabelWidget());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {

            Uri pickedImage = data.getData();

            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            final String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            profilePic.setImageBitmap(bitmap);


            new Thread(new Runnable() {
                public void run() {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference profileref = storageRef.child(mAuth.getCurrentUser().getEmail().toString() + ".jpg");

                    Uri file = Uri.fromFile(new File(imagePath));

                    UploadTask uploadTask = profileref.putFile(file);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
                }
            }).start();
            cursor.close();
        }
    }

    class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(MyProfile.this);
        private AppDatabase database;

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting database...");
            this.dialog.show();

            database = AppDatabase.getInstance(MyProfile.this);
        }

        protected Boolean doInBackground(final String... args) {


            Log.e("file Director", Environment.getExternalStorageDirectory().toString());
            File file = new File(Environment.getExternalStorageDirectory(), "Courses.csv");

            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                String[] column = {"Id", "Title"};
                csvWrite.writeNext(column);
                List<SubscriptedCourses> sc = database.getCoursesDao().getAll();

                for(int i=0; i<sc.size(); i++){
                    String[] mySecondStringArray ={String.valueOf(sc.get(i).getId()), sc.get(i).getTitle()};
                    csvWrite.writeNext(mySecondStringArray);
                }

                csvWrite.close();
                return true;
            } catch (IOException e) {
                Log.e("CSV",e.getCause().toString());
                return false;
            }


        }

        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) { this.dialog.dismiss(); }
            if (success) {
                Toast.makeText(getApplicationContext(), "Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Export failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    class CSVWriter {
        private PrintWriter pw;
        private char separator;
        private char escapechar;
        private String lineEnd;
        private char quotechar;
        public static final char DEFAULT_SEPARATOR = ',';
        public static final char NO_QUOTE_CHARACTER = '\u0000';
        public static final char NO_ESCAPE_CHARACTER = '\u0000';
        public static final String DEFAULT_LINE_END = "\n";
        public static final char DEFAULT_QUOTE_CHARACTER = '"';
        public static final char DEFAULT_ESCAPE_CHARACTER = '"';
        public CSVWriter(Writer writer) {
            this(writer, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER,
                    DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);
        }

        public CSVWriter(Writer writer, char separator, char quotechar, char escapechar, String lineEnd) {
            this.pw = new PrintWriter(writer);
            this.separator = separator;
            this.quotechar = quotechar;
            this.escapechar = escapechar;
            this.lineEnd = lineEnd;
        }
        public void writeNext(String[] nextLine) {
            if (nextLine == null)
                return;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < nextLine.length; i++) {

                if (i != 0) {
                    sb.append(separator);
                }
                String nextElement = nextLine[i];
                if (nextElement == null)
                    continue;
                if (quotechar != NO_QUOTE_CHARACTER)
                    sb.append(quotechar);
                for (int j = 0; j < nextElement.length(); j++) {
                    char nextChar = nextElement.charAt(j);
                    if (escapechar != NO_ESCAPE_CHARACTER && nextChar == quotechar) {
                        sb.append(escapechar).append(nextChar);
                    } else if (escapechar != NO_ESCAPE_CHARACTER && nextChar == escapechar) {
                        sb.append(escapechar).append(nextChar);
                    } else {
                        sb.append(nextChar);
                    }
                }
                if (quotechar != NO_QUOTE_CHARACTER)
                    sb.append(quotechar);
            }
            sb.append(lineEnd);
            pw.write(sb.toString());
        }
        public void close() throws IOException {
            pw.flush();
            pw.close();
        }
        public void flush() throws IOException {
            pw.flush();
        }
    }
    @Override
    public void onClick(View view) {
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finishAffinity();
    }

    class MySubscriptedCourses{

        String title;
        String date;

        public MySubscriptedCourses(String title, String date){
            this.title = title;
            this.date = date;
        }

        @Override
        public String toString() {
            return title + '\n'+ "Started on: " + date;
        }
    }


}
