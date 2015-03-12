package wso2.org.kalagune;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import wso2.org.kalagune.forecast.ForecastItem;
import wso2.org.kalagune.util.Constants;
import wso2.org.kalagune.util.NetworkOperations;


public class HomeActivity extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {
    private static final String TAG = HomeActivity.class.getName();

    private TextView textViewTime, textViewDate, textViewTemp, textViewCity, textViewWeather;
    private ProgressDialog progressDialog;
    private BroadcastReceiver broadcastReceiver;
    private static final String DATE_FORMAT = "EEEE, MMMM dd";
    private static final String TIME_FORMAT = "HH:mm";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private SimpleDateFormat time;
    private Location location;
    private GoogleApiClient mGoogleApiClient;
    private ImageView imageViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }
        textViewDate = (TextView)findViewById(R.id.textViewDate);
        textViewTime = (TextView)findViewById(R.id.textViewTime);
        textViewTemp = (TextView)findViewById(R.id.textViewTemp);
        textViewCity = (TextView)findViewById(R.id.textViewCity);
        textViewWeather = (TextView)findViewById(R.id.textViewWeather);
        imageViewWeather = (ImageView)findViewById(R.id.imageViewWeather);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));

        time = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        textViewDate.setText(time.format(new Date()));

        time = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        textViewTime.setText(time.format(new Date()));
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent)
            {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    textViewTime.setText(time.format(new Date()));
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        LoadWeatherService task = new LoadWeatherService();
        task.execute();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void setCity(){
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

        try
        {
            List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0)
            {
                cityName = addresses.get(0).getLocality();
                // you should also try with addresses.get(0).toSring();
                textViewCity.setText(cityName);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public class LoadWeatherService extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return NetworkOperations.makeGetRequest(Constants.SERVER_NAME+Constants.LOCATION_TEMPERATURE);
        }

        @Override
        protected void onPostExecute(String result) {
            ForecastItem item = new ForecastItem();
            try {
                JSONObject json = new JSONObject(result);
                String condition=json.getString(Constants.CONDITION);
                int temperature=json.getInt(Constants.TEMPERATURE);
                textViewTemp.setText(temperature+"°c");
                textViewWeather.setText(condition);
                int drawableId;
                switch (condition) {
                    case Constants.SUNNY:
                        drawableId=R.drawable.sunny;
                        break;
                    case Constants.CLOUDY:
                        drawableId=R.drawable.cloudy;
                        break;
                    case Constants.THUNDER:
                        drawableId=R.drawable.thunder;
                        break;
                    case Constants.SHOWERS:
                        drawableId=R.drawable.showers;
                        break;
                    default:
                        drawableId=R.drawable.sunny;
                }
                imageViewWeather.setImageDrawable(getResources().getDrawable(drawableId));
            }
            catch (JSONException e){
                Log.e(TAG, e.toString());
            }
            super.onPostExecute(result);
            if(progressDialog != null){
                progressDialog.dismiss();
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_forecast) {
            Intent intent = new Intent(this, ForecastActivity.class);
            intent.putExtra("city", textViewCity.getText());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }

    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            setCity();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("HomeActivity", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }
}
