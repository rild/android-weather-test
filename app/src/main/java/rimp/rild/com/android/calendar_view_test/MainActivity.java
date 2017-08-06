package rimp.rild.com.android.calendar_view_test;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager mLocationManager;
    private APITask task;

    private Handler mHandler;
    TextView mDateTextView;
    TextView mLabelTextView;
    TextView mTempTextView;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        task = new APITask(textView);

        // インスタンスの作成
        mHandler = new android.os.Handler();
// Viewの関連付け
        mDateTextView = (TextView) findViewById(R.id.textViewDate);
        mLabelTextView = (TextView) findViewById(R.id.textViewLabel);
        mTempTextView = (TextView) findViewById(R.id.textViewTemp);
        mImageView = (ImageView) findViewById(R.id.imageView);

        getWeather();

    }

    private void getWeather() {
        // リクエストオブジェクトを作って
        Request request = new Request.Builder()
                // URLを生成
                .url("http://weather.livedoor.com/forecast/webservice/json/v1?city=130010")
                .get()
                .build();
        // クライアントオブジェクトを作成する
        OkHttpClient client = new OkHttpClient();
        // 新しいリクエストを行う
        client.newCall(request).enqueue(new Callback() {
            // 通信が失敗した時
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            // 通信が成功した時
            @Override
            public void onResponse(Response response) throws IOException {
                // 通信結果をログに出力する
                Log.d("onResponse", response.toString());
                final String json = response.body().string();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        parseJson(json);
                    }
                });
            }
        });

    }

    private void parseJson(String json) {
        Log.d("Json", json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            // {forecasts[] -> 0 -> {dataLabel, telop, tem}}
            JSONArray forecastsArray = jsonObject.getJSONArray("forecasts");
            // 0番目のものが今日の天気なので取得する
            JSONObject todayWeatherJson = forecastsArray.getJSONObject(0);
            // 今日
            String date = todayWeatherJson.getString("date");
            mDateTextView.setText(date);
            String telop = todayWeatherJson.getString("telop");
            String dataLabel = todayWeatherJson.getString("dateLabel");
            mLabelTextView.setText(telop + "\n" + dataLabel);


            JSONObject temperatureJson = todayWeatherJson.getJSONObject("temperature");
            JSONObject minJson = temperatureJson.get("min") != null ? temperatureJson.getJSONObject("min") : null;
            String min = "";
            if (minJson != null) {
                min = minJson.getString("celsius");
            }
            JSONObject maxJson = temperatureJson.get("max") != null ? temperatureJson.getJSONObject("max") : null;
            String max = "";
            if (maxJson != null) {
                max = maxJson.getString("celsius");
            }
            mTempTextView.setText("最低気温:" + min + "〜最高気温:" + max);


            JSONObject imageJson = todayWeatherJson.getJSONObject("image");
            String imageUrl = imageJson.getString("url");
            Picasso.with(MainActivity.this).load(imageUrl).into(mImageView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = mLocationManager.getBestProvider(criteria, true);
//        mLocationManager.requestLocationUpdates(provider, 0, 0, this);

    }

    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();

        double longitude = location.getLongitude();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
