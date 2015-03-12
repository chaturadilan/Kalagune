package wso2.org.kalagune;


import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wso2.org.kalagune.forecast.ForeCastItemsAdapter;
import wso2.org.kalagune.forecast.ForecastItem;
import wso2.org.kalagune.util.NetworkOperations;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastItemsFragment extends ListFragment {


    ProgressDialog progressDialog;

    public ForecastItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.loading));

        new LoadWeatherForecastService().execute();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setDivider(null);

    }

    private class LoadWeatherForecastService extends AsyncTask<Void, Void, List<ForecastItem>> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected List<ForecastItem> doInBackground(Void... params) {
            String result = NetworkOperations.makeGetRequest("http://10.100.5.70/weather/api.php?action=forcast");
            List<ForecastItem> forecastItemList = new ArrayList<>();
            try {
                JSONArray resultArray = new JSONArray(result);
                for (int x=0;x<resultArray.length();x++){
                    JSONObject json=resultArray.getJSONObject(x);
                    String dateTime = json.getString("date");
                    String condition=json.getString("condition");
                    int temperature = json.getInt("temperature");

                    SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date date = format.parse(dateTime);
                        format = new SimpleDateFormat("EEE dd,MMM");
                        String formattedDate = format.format(date);
                        int drawable=1;
                        forecastItemList.add(new ForecastItem(getResources().getDrawable(R.drawable.showers), condition, formattedDate, temperature+"°c"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }




                }
            } catch (JSONException e) {

            }
            return forecastItemList;
        }

        @Override
        protected void onPostExecute(List<ForecastItem> forecastItems) {
            setListAdapter(new ForeCastItemsAdapter(getActivity(), forecastItems));
            if(progressDialog != null){
                progressDialog.dismiss();
            }
        }

    }
}
