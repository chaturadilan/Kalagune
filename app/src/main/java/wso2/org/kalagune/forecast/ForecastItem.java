package wso2.org.kalagune.forecast;

import android.graphics.drawable.Drawable;

/**
 * Created by chatura on 7/3/15.
 */
public class ForecastItem {

    public final Drawable forecastImage;
    public final String forecastText;
    public final String forecastDate;
    public final String forecastTemp;

    public ForecastItem(Drawable forecastImage, String forecastText,  String forecastDate,  String forecastTemp) {
        this.forecastImage = forecastImage;
        this.forecastText = forecastText;
        this.forecastDate = forecastDate;
        this.forecastTemp = forecastTemp;
    }
}
