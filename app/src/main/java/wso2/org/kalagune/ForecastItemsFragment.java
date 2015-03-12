package wso2.org.kalagune;


import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import wso2.org.kalagune.util.Constants;
import wso2.org.kalagune.util.NetworkOperations;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastItemsFragment extends ListFragment {
    private static final String TAG = ForecastItemsFragment.class.getName();


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
            String result = NetworkOperations.makeGetRequest(Constants.SERVER_NAME+Constants.FORECAST_API);
            List<ForecastItem> forecastItemList = new ArrayList<>();
            try {
                JSONArray resultArray = new JSONArray(result);
                for (int x=0;x<resultArray.length();x++){
                    JSONObject json=resultArray.getJSONObject(x);
                    String dateTime = json.getString(Constants.DATE);
                    String condition=json.getString(Constants.CONDITION);
                    int temperature = json.getInt(Constants.TEMPERATURE);

                    SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy");

                        Date date = format.parse(dateTime);
                        format = new SimpleDateFormat("EEE dd,MMM");
                        String formattedDate = format.format(date);
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
                        forecastItemList.add(new ForecastItem(getResources().getDrawable(drawableId), condition, formattedDate, temperature+"°c"));

                }
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
             catch (ParseException e) {
                 Log.e(TAG, e.toString());
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
