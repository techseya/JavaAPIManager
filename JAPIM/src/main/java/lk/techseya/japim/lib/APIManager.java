package lk.techseya.japim.lib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIManager {

    public static class SendRequest {
        private String baseUrl;
        private HashMap<String, Object> params;
        private String endpoint;
        private String method;

        public SendRequest(String baseUrl) {
            this.baseUrl = baseUrl;
            this.params = new HashMap<>();
            this.method = "POST";
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public void setMethod(String method) {
            if (method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("POST")) {
                this.method = method;
            } else {
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }
        }

        public void addParam(String key, Object value) {
            if (value instanceof String || value instanceof Number) {
                this.params.put(key, value);
            } else {
                throw new IllegalArgumentException("Value must be a String or Number.");
            }
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public HashMap<String, Object> getParams() {
            return params;
        }

        public JSONObject buildRequestBody() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
            return jsonObject;
        }

        public String send() throws IOException, JSONException {
            OkHttpClient client = new OkHttpClient();
            String url = baseUrl;

            if (endpoint != null && !endpoint.isEmpty()) {
                url += endpoint;
            }

            Request request;
            if (method.equalsIgnoreCase("POST")) {
                JSONObject requestBody = buildRequestBody();
                RequestBody body = RequestBody.create(requestBody.toString(), MediaType.parse("application/json"));

                request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
            } else {
                HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    urlBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString());
                }

                request = new Request.Builder()
                        .url(urlBuilder.build())
                        .get()
                        .build();
            }

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }

            return response.body().string();
        }
    }

    public static class CatchResponse {
        private HashMap<String, Object> responseBody;

        public CatchResponse() {
            this.responseBody = new HashMap<>();
        }

        public void addResponse(String key, Object value) {
            this.responseBody.put(key, value);
        }

        public Object getResponseValue(String key) {
            return this.responseBody.get(key);
        }

        public HashMap<String, Object> getAllResponses() {
            return responseBody;
        }

        public static CatchResponse fromJSONString(String jsonString) throws JSONException {
            CatchResponse catchResponse = new CatchResponse();
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);
                catchResponse.addResponse(key, value);
            }

            return catchResponse;
        }

        public Object getNestedResponseValue(String... keys) throws JSONException {
            Object current = responseBody;

            for (String key : keys) {
                if (current instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) current;
                    current = jsonObject.opt(key);
                } else if (current instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) current;

                    if (jsonArray.length() > 0) {
                        current = jsonArray.get(0);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            return current;
        }
    }
}
