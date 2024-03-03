package iitr.collector.quest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SetupActivity extends AppCompatActivity {

    private APIManager apiManager;
    SharedPreferences userdata;
    private Map<String, String> headers;
    private String Sub_Batch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userdata = getSharedPreferences("userdata", Context.MODE_PRIVATE);

        apiManager = new APIManager(this);
        String enroll = userdata.getString("enroll", "23117110");
        String content = "{\"EnrollmentNo\":" + enroll + ",\"StSessionYear\":\"2023-24\",\"Semester\":\"Spring\"}";

        headers = new HashMap<>();
        headers.put("Accept", "application/json, text/plain, */*");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "timetable.iitr.ac.in:4400");
        headers.put("Origin", "https://timetable.iitr.ac.in:4400");
        headers.put("Referer", "https://timetable.iitr.ac.in:4400/");

        String url1 = "https://timetable.iitr.ac.in:4400/api/external/studentscourse";
        apiManager.apiReq(url1, content, headers, new APIManager.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                // Handle successful API response
                try {
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
                    tutorialRequest.put("semid", entry.getString("SemesterID"));
                    tutorialRequest.put("Semester", entry.getString("StSemester"));
                    tutorialRequest.put("Session", entry.getString("StSessionYear"));
                    tutorialRequest.put("Branch_Name", entry.getString("Program_id"));
                    tutorialRequest.put("SubBatch", entry.getString("SubBatch"));
                    Sub_Batch = entry.getString("SubBatch");

                    getTut(tutorialRequest.toString());
                    getLectures(courses.toString());

                    SharedPreferences.Editor editor = userdata.edit();
                    editor.putString("courses", courses.toString());
                    editor.putString("tutorial", tutorialRequest.toString());
                    editor.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.getStackTraceString(e);
                }

            }

            @Override
            public void onFailure(Exception e) {
                // Handle API request failure
                e.printStackTrace();
            }
        });

    }

    private void getTut(String content) {
        String url2 = "https://timetable.iitr.ac.in:4400/api/aao/dep/lecturecoursbatch";
        apiManager.apiReq(url2, content, headers, new APIManager.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray tutes = jsonObject.getJSONArray("result");

                    SharedPreferences.Editor editor = userdata.edit();
                    editor.putString("lectures", tutes.toString());
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle API request failure
                e.printStackTrace();
            }
        });
    }

    private void getLectures(String content) {
        String url3 = "https://timetable.iitr.ac.in:4400/api/external/studentscourse";
        apiManager.apiReq(url3, content, headers, new APIManager.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray lectureArray = jsonObject.getJSONArray("result");
                    JSONArray finalLectures = new JSONArray();

                    for (int i = 0; i < lectureArray.length(); i++) {
                        JSONObject entry = lectureArray.getJSONObject(i);

                        if (entry.getString("Sub_Batches").contains(Sub_Batch)) {
                            finalLectures.put(entry);
                        }
                    }

                    SharedPreferences.Editor editor = userdata.edit();
                    editor.putString("lectures", finalLectures.toString());
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle API request failure
                e.printStackTrace();
            }
        });
    }
}