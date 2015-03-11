package wso2.org.kalagune;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class HomeActivity extends ActionBarActivity {

    private TextView textViewTime, textViewDate, textViewTemp, textViewCity, textViewWeather;
    ProgressDialog progressDialog;

    private BroadcastReceiver broadcastReceiver;
    private static final String DATE_FORMAT = "EEEE, MMMM dd";
    private static final String TIME_FORMAT = "HH:mm";
    private SimpleDateFormat time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textViewDate = (TextView)findViewById(R.id.textViewDate);
        textViewTime = (TextView)findViewById(R.id.textViewTime);
        textViewTemp = (TextView)findViewById(R.id.textViewTemp);
        textViewCity = (TextView)findViewById(R.id.textViewCity);
        textViewWeather = (TextView)findViewById(R.id.textViewCity);

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


        new LoadWeatherService().execute();


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

    public class LoadWeatherService extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.hide();
        }
    }
}
