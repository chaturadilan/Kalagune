package wso2.org.kalagune.forecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import wso2.org.kalagune.R;

/**
 * Created by chatura on 7/3/15.
 */
public class ForeCastItemsAdapter extends ArrayAdapter<ForecastItem> {

    public ForeCastItemsAdapter(Context context, List<ForecastItem> items) {
        super(context, R.layout.list_item_forecast, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_forecast, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imageViewForecastImage = (ImageView) convertView.findViewById(R.id.imageViewForecastImage);
            viewHolder.textViewForecastText = (TextView) convertView.findViewById(R.id.textViewForecastText);
            viewHolder.textViewForecastDate = (TextView) convertView.findViewById(R.id.textViewForecastDate);
            viewHolder.textViewForecastTemp = (TextView) convertView.findViewById(R.id.textViewForecastTemp);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ForecastItem forecastItem = getItem(position);
        viewHolder.imageViewForecastImage.setImageDrawable(forecastItem.getForecastImage());
        viewHolder.textViewForecastText.setText(forecastItem.getForecastText());
        viewHolder.textViewForecastDate.setText(forecastItem.getForecastDate());
        viewHolder.textViewForecastTemp.setText(forecastItem.getForecastTemp());

        return convertView;
    }


    private static class ViewHolder{
        ImageView imageViewForecastImage;
        TextView textViewForecastText;
        TextView textViewForecastDate;
        TextView textViewForecastTemp;

    }
}
