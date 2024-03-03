package iitr.collector.quest;

import android.content.Context;
import android.content.Intent;
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
    private APIManager apiManager2;
    private APIManager apiManager3;
    SharedPreferences userdata;
    private Map<String, String> headers;
    private String Sub_Batch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userdata = getSharedPreferences("userdata", Context.MODE_PRIVATE);

        apiManager = new APIManager(this);
        apiManager2 = new APIManager(this);
        apiManager3 = new APIManager(this);
        String enroll = userdata.getString("enroll", "23117110");
        String content = "{\"EnrollmentNo\":" + enroll + ",\"StSessionYear\":\"2023-24\",\"Semester\":\"Spring\"}";

        headers = new HashMap<>();
        headers.put("Accept", "application/json, text/plain, */*");
        //headers.put("Connection", "keep-alive");
        headers.put("Host", "timetable.iitr.ac.in:4400");
        headers.put("Origin", "https://timetable.iitr.ac.in:4400");
        headers.put("Referer", "https://timetable.iitr.ac.in:4400/");

        String url1 = "https://timetable.iitr.ac.in:4400/api/external/studentscourse";
        apiManager.apiReq(url1, content, headers, new APIManager.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                // Handle successful API response
                try {
                    Log.d("API_UPDATE", "API Request Successful");
                    Log.d("API_RESULT", result);
                    JSONObject jsonObject = new JSONObject(result);
                    Log.d("JSONArray", jsonObject.get("result").toString());

                    JSONArray resultArray = new JSONArray();
                    resultArray = jsonObject.getJSONArray("result");
                        Log.d("JSONArray", "is instance of json array");

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
                    tutorialRequest.put("Branch_Name", entry.getString("ProgramID"));
                    tutorialRequest.put("SubBatch", entry.getString("SubBatch"));
                    Sub_Batch = entry.getString("SubBatch");

                    SharedPreferences.Editor editor = userdata.edit();
                    editor.putString("courses", courses.toString());
                    editor.putString("tutorial", tutorialRequest.toString());
                    editor.apply();

                    getTut(tutorialRequest.toString());
                    getLectures(courses.toString());

                    JSONObject timetable = new JSONObject();
                    String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                    for (String day : daysOfWeek) {
                        timetable.put(day, new JSONArray());
                    }



                    JSONArray Lectures = new JSONArray(userdata.getString("lectures", "not found"));
                    JSONArray Tutes = new JSONArray(userdata.getString("tutes", "not found"));

                    for (int i = 0; i < Lectures.length(); i++) {
                        JSONObject Lecture = Lectures.getJSONObject(i);
                        JSONObject formattedLecture = new JSONObject();
                        formattedLecture.put("Course_code", Lecture.getString("Course_code"));
                        formattedLecture.put("Room_no", Lecture.getString("Room_no"));
                        formattedLecture.put("Time", Lecture.getString("Time"));
                        formattedLecture.put("Day", Lecture.getString("Day"));
                        formattedLecture.put("Slot_Type", Lecture.getString("Slot_Type"));
                        formattedLecture.put("Faculty_name", Lecture.getString("Faculty_name"));
                        formattedLecture.put("", Lecture.getString(""));


                        timetable.getJSONArray(Lecture.getString("Day")).put(formattedLecture);
                    }

                    for (int i = 0; i < Tutes.length(); i++) {
                        JSONObject Tute = Tutes.getJSONObject(i);
                        JSONObject formattedTute = new JSONObject();

                        formattedTute.put("Course_code", Tute.getString("subjectAlphaCode"));
                        formattedTute.put("Room_no", Tute.getString("LHall"));
                        formattedTute.put("Slot_Type", Tute.getString("Slot_Type"));
                        formattedTute.put("Day", Tute.getString("Day"));
                        formattedTute.put("Time", Tute.getString("Time"));
                        formattedTute.put("FacultyName", Tute.getString("FacultyName"));


                        timetable.getJSONArray(Tute.getString("Day")).put(formattedTute);
                    }

                    editor.putString("timetable", timetable.toString());
                    Log.d("timetable", timetable.toString());
                    editor.apply();

                    Intent i = new Intent(SetupActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.getStackTraceString(e);
                    Log.e("JSON_ERROR", "Error parsing JSON response in url1 getting list of courses: " + Log.getStackTraceString(e));
                }

            }

            @Override
            public void onFailure(Exception e) {
                // Handle API request failure
                e.printStackTrace();
                Log.e("API_ERROR", "API Request Failed api1: " + Log.getStackTraceString(e));
            }
        });

    }

    private void getTut(String contenttt) {
        String url2 = "https://timetable.iitr.ac.in:4400/api/aao/dep/lecturecoursbatch";
        apiManager2.apiReq(url2, contenttt, headers, new APIManager.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.d("API_UPDATE", "API Request for Tutes Successful");
                    Log.d("API_RESULT", result);
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray tutes = jsonObject.getJSONArray("result");

                    SharedPreferences.Editor editor = userdata.edit();
                    editor.putString("tutes", tutes.toString());
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("API_ERROR", "Error parsing JSON response in getting tutes: " + Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle API request failure
                e.printStackTrace();
                Log.e("API_ERROR", "API Request for Tutes Failed: " + Log.getStackTraceString(e));
            }
        });
    }

    private void getLectures(String contentt) {
        String url3 = "https://timetable.iitr.ac.in:4400/api/external/studentscourse";
        apiManager3.apiReq(url3, contentt, headers, new APIManager.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.d("API_UPDATE", "API Request for Lectures Successful");
                    Log.d("API_RESULT", result);
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
                    Log.e("API_ERROR", "Error parsing JSON response in getting lectures: " + Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle API request failure
                e.printStackTrace();
                Log.e("API_ERROR", "API Request for Lectures Failed: " + Log.getStackTraceString(e));
            }
        });
    }
}