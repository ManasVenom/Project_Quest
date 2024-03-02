package iitr.collector.quest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BuddiesFragment extends Fragment {

    String url = "https://timetable.iitr.ac.in:4400/api/external/studentscourse";
    TextView tv;
    public BuddiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buddies, container, false);

        tv = view.findViewById(R.id.tv);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new NetworkTask(requireContext()).execute(url);
    }

    private static class NetworkTask extends AsyncTask<String, Void, String> {

        OkHttpClient client = new OkHttpClient();
        private Context context;// Constructor to pass the TextView reference
        public NetworkTask(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected String doInBackground(String... urls) {
            String url = urls[0];
            SharedPreferences userdata = context.getSharedPreferences("userdata", Context.MODE_PRIVATE);
            String enroll = userdata.getString("enroll", "23117110");
            String jsonContent = "{\"EnrollmentNo\":"+ enroll + ",\"StSessionYear\":\"2023-24\",\"Semester\":\"Spring\"}";

            RequestBody requestBody = RequestBody.create(jsonContent, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json, text/plain, */*")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Host", "timetable.iitr.ac.in:4400")
                    .addHeader("Origin", "https://timetable.iitr.ac.in:4400")
                    .addHeader("Referer", "https://timetable.iitr.ac.in:4400/")
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    Log.d("dcs", result);

                    // Handle the response here
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray resultArray = jsonObject.getJSONArray("result");
                    JSONObject tutorialRequest = new JSONObject();

                    JSONArray courses = new JSONArray();

                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject entry = resultArray.getJSONObject(i);

                        JSONObject formattedEntry = new JSONObject();
                        formattedEntry.put("Course_code", entry.getString("SubjectCode"));
                        formattedEntry.put("SubjectArea", entry.getString("SubjectArea"));
                        formattedEntry.put("Program_id", entry.getInt("ProgramID"));
                        formattedEntry.put("years", entry.getInt("SemesterID"));
                        formattedEntry.put("Session", entry.getString("StSessionYear"));
                        formattedEntry.put("Semester", entry.getString("StSemester"));

                        courses.put(formattedEntry);
                    }

                    JSONObject entry = resultArray.getJSONObject(0);
                    tutorialRequest.put("semid",entry.getString("SemesterID"));
                    tutorialRequest.put("Semester", entry.getString("StSemester"));
                    tutorialRequest.put("Session", entry.getString("StSessionYear"));
                    tutorialRequest.put("Branch_Name",entry.getString("Program_id"));
                    tutorialRequest.put("SubBatch",entry.getString("SubBatch"));

                    String turorialReq = tutorialRequest.toString();

                    String coursesString = courses.toString();
                    SharedPreferences sharedPreferences = context.getSharedPreferences("userdata", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("courses", coursesString);
                    editor.putString("tutorial", turorialReq);
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.getStackTraceString(e);
                }
            }
        }

    }

}
