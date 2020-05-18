package com.example.proiect_dam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CourseAdapter extends BaseAdapter implements Filterable {


    private LayoutInflater thisInflater;
    private List<Course> courses;

    public static final String PREFERENCES_FILE_NAME = "USER";
    private Filter courseFilter;

    public CourseAdapter(Context con, List<Course> list){
        this.thisInflater = LayoutInflater.from(con);
        courses = (list);
    }

    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public Object getItem(int position) {
        return courses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return courses.get(position).hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = thisInflater.inflate(R.layout.fragment_course, parent, false);

            ImageView image = convertView.findViewById(R.id.courseFragmentImage);
            TextView title = convertView.findViewById(R.id.courseFragmentTitle);
            TextView description = convertView.findViewById(R.id.courseFragmentDescription);

            image.setImageResource(courses.get(position)._image);
            title.setText(courses.get(position)._title);
            description.setText(courses.get(position)._description);

            final SharedPreferences settingsFile = convertView.getContext().getSharedPreferences(PREFERENCES_FILE_NAME, 0);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppDatabase appDatabase = AppDatabase.getInstance(thisInflater.getContext());

                    Subscription subscription = new Subscription(0,settingsFile.getInt("userID",0), (courses.get(position)._id).hashCode(), Calendar.getInstance().getTime().toString());


                    if(appDatabase.getSubscriptionDao().getSubscription(settingsFile.getInt("userID",0), (courses.get(position)._id).hashCode())==null)
                    {
                        appDatabase.getSubscriptionDao().insert(subscription);
                    }
                    MainActivity.myCourses = appDatabase.getSubscriptionDao().getAll(settingsFile.getInt("userID",0)).size();;
                    MainActivity.progressText.setText("My progress: " + MainActivity.myCourses + "/20");
                    MainActivity.progressBar.setProgress(MainActivity.myCourses);

                    System.out.println(appDatabase.getSubscriptionDao().getAll(settingsFile.getInt("userID",0)));
                    System.out.println(settingsFile.getInt("userID",0));
                    System.out.println(appDatabase.getCoursesDao().getAll());
                    System.out.println(appDatabase.getUserDao().getAll());

//                    appDatabase.getSubscriptionDao().deleteAll();
//                    appDatabase.getCoursesDao().deleteAll();
//                    appDatabase.getUserDao().deleteAll();

                    Intent intent = new Intent(thisInflater.getContext(),Course.class);
                    intent.putExtra("description", courses.get(position)._description);
                    intent.putExtra("title",courses.get(position)._title);
                    intent.putExtra("image",courses.get(position)._image);
                    intent.putExtra("content", courses.get(position)._content);
                    thisInflater.getContext().startActivity(intent);
                }
            });
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (courseFilter == null)
            courseFilter = new CourseFilter();
        return courseFilter;
    }

    private class CourseFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = courses;
                results.count = courses.size();
            }
            else {
                List<Course> nCourseList = new ArrayList<>();
                for (Course p : courses) {
                    if (p._description.equals(constraint.toString()))
                        nCourseList.add(p);
                }
                results.values = nCourseList;
                results.count = nCourseList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                courses.clear();
                courses.addAll((List<Course>) results.values);
                System.out.println("filter");
                notifyDataSetChanged();
            }

        }

    }
}
