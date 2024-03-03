package iitr.collector.quest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIManager {

    public interface ApiCallback {
        void onSuccess(String result);

        void onFailure(Exception e);
    }

    private Context context;
    private OkHttpClient client;

    public APIManager(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
    }

    public void apiReq(String url, String content, Map<String, String> headers, ApiCallback callback) {
        new NetworkTask(url, content, headers, callback).execute();
    }

    private class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private String content;
        private Map<String, String> headers;
        private ApiCallback callback;

        public NetworkTask(String url, String content, Map<String, String> headers, ApiCallback callback) {
            this.url = url;
            this.content = content;
            this.headers = headers;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestBody requestBody = RequestBody.create(content, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .headers(Headers.of(headers))
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                return response.body().string();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                callback.onSuccess(result);
            } else {
                callback.onFailure(new IOException("API request failed."));
            }
        }
    }
}
