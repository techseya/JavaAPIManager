# JavaAPIManager for Android
**Provides better API handling options for Android natively.**

JavaAPIManager is an Android library designed to handle API calls and responses effortlessly, even for developers with limited experience in working with JSON or HTTP requests. With minimal configuration, it abstracts the complexities of API integration, allowing users to send requests and process responses with ease.The library requires a minimum SDK version of 26 and supports Java 17. Whether you're building small projects or complex apps, APIHelper simplifies network operations, saving development time while ensuring clean and maintainable code.

 ### Setup
>Step 1. Add the JitPack repository to your build file
```gradle
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
  ```
  >Step 2. Add the dependency
  ```gradle
  dependencies {
	        implementation 'com.github.techseya:JavaAPIManager:1.1.1'
	}
  ```
  ### Usage 
 
  >Standard guide for implementation is added here.However, latest android versions restrict running on main ui thread and the fix for that is added in restricted mode section.
  ```java
  public class Main {
    public static void main(String[] args) {
        // Initialize the SendRequest object with the base URL
        APIManager.SendRequest request = new APIManager.SendRequest("https://example.com/api/");

        // Add parameters to the request
        request.setEndpoint("otp"); // Set endpoint
        request.setMethod("POST");  // Set HTTP method

        // Add the required parameters
        request.addParam("method_name", "otp_request");
        request.addParam("mobile", "1234567890");
        request.addParam("applicationHash", "SAMPLE_HASH_KEY");
        request.addParam("app_url", "https://example.com");
        request.addParam("app_key", "APP_KEY");

        try {
            // Send the request and get the response as a JSON string
            String responseString = request.send();

            // Parse the response using CatchResponse
            APIManager.CatchResponse response = APIManager.CatchResponse.fromJSONString(responseString);

            // Extract the "statusDetail" value from the nested JSON response
            Object statusDetail = response.getNestedResponseValue("APP_OTP", "statusDetail");

            // Print the extracted value
            System.out.println("Status Detail: " + statusDetail);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
  ```
  >Restricted mode Guide
  ```java
  public class Main {
    public static void main(String[] args) {
        // Call the network request using AsyncTask
        new NetworkTask().execute();
    }

    static class NetworkTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Initialize the SendRequest object with the base URL
                APIManager.SendRequest request = new APIManager.SendRequest("https://example.com/api/");
                request.setEndpoint("otp"); // Set endpoint
                request.setMethod("POST");  // Set HTTP method

                // Add the required parameters
                request.addParam("method_name", "otp_request");
                request.addParam("mobile", "1234567890");
                request.addParam("applicationHash", "SAMPLE_HASH_KEY");
                request.addParam("app_url", "https://example.com");
                request.addParam("app_key", "APP_KEY");

                // Send the request and get the response as a JSON string
                return request.send();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);

            if (responseString != null) {
                try {
                    // Parse the response using CatchResponse
                    APIManager.CatchResponse response = APIManager.CatchResponse.fromJSONString(responseString);

                    // Extract the "statusDetail" value from the nested JSON response
                    Object statusDetail = response.getNestedResponseValue("APP_OTP", "statusDetail");

                    // Log the extracted value
                    Log.d("NetworkTask", "Status Detail: " + statusDetail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("NetworkTask", "Failed to get response.");
            }
        }
    }
}
  ```
### Options

|    option     | Status        |
| ------------- | ------------- |
| .setEndpoint()  | optional  |
| .setMethod()  | optional  |
| response.getNestedResponseValue()  | Navigate through child values in JSON  |
  
  _Intellectual property of ©Techseya 2024_
