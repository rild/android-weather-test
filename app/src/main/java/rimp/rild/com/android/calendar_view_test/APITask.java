package rimp.rild.com.android.calendar_view_test;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by rild on 2017/08/06.
 */

public class APITask extends AsyncTask<Integer, Integer, Integer> {

    private TextView textView;

    /**
     * コンストラクタ
     */
    public APITask(TextView textView) {
        super();
        this.textView   = textView;
    }

    /**
     * バックグランドで行う処理
     */
    @Override
    protected Integer doInBackground(Integer... value) {
        try {
            //15秒停止します。
            Thread.sleep(15000);
        } catch (InterruptedException e) {
        }
        return value[0] + 2;
    }

    /**
     * バックグランド処理が完了し、UIスレッドに反映する
     */
    @Override
    protected void onPostExecute(Integer result) {
        textView.setText(String.valueOf(result));
    }

    @Nullable
    private JSONObject getJsonObject(double latitude, double longitude) {
        String requestURL = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + latitude
                + "8&amp;lon=" + longitude
                +"&amp;mode=json&amp;cnt=14";

        try {
            URL url = new URL(requestURL);

            InputStream is = url.openConnection().getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuilder sb = new StringBuilder();

            String line;

            while (null != (line = reader.readLine())) {

                sb.append(line);

            }
            String data = sb.toString();

            JSONObject jsonObject = new JSONObject(data);
            return jsonObject;

        } catch (IOException e) {

        } catch (JSONException e) {

        }
        return null;
    }
}
