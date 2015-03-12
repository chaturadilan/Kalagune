package wso2.org.kalagune.util;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by inoshp on 3/11/15.
 */
public class NetworkOperations {
    private static final String TAG = NetworkOperations.class.getName();

    public static String makeGetRequest(String url) {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        HttpResponse response;
        BufferedReader reader = null;
        InputStreamReader streamReader = null;
        try {
            response = client.execute(request);
            streamReader = new InputStreamReader(response.getEntity().getContent(), Constants.ENCODING);
            reader = new BufferedReader(streamReader);
            return reader.readLine();
        } catch (ClientProtocolException e) {
            Log.e(TAG, e.toString());
            return null;
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (streamReader != null) {
                    streamReader.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }

        }
    }
}
