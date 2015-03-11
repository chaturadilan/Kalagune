package wso2.org.kalagune.forecast;

import android.graphics.drawable.Drawable;

/**
 * Created by chatura on 7/3/15.
 */
public class ForecastItem {

    private Drawable forecastImage;
    private String forecastText;
    private String forecastDate;
    private String forecastTemp;

    public ForecastItem(Drawable forecastImage, String forecastText,  String forecastDate,  String forecastTemp) {
        this.forecastImage = forecastImage;
        this.forecastText = forecastText;
        this.forecastDate = forecastDate;
        this.forecastTemp = forecastTemp;
    }

    public Drawable getForecastImage() {
        return forecastImage;
    }

    public String getForecastText() {
        return forecastText;
    }

    public String getForecastDate() {
        return forecastDate;
    }

    public String getForecastTemp() {
        return forecastTemp;
    }

    public ForecastItem(){}
}
