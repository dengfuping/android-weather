package name.example.dengfuping.tianqi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ImageButton imageButton;
    //百度天气API，根据城市代号查询历史及未来天气的地址
    private String url = "http://apis.baidu.com/apistore/weatherservice/recentweathers?cityid=";
    //需要更新内容的控件
    private TextView lowText, nowText, highText;
    private TextView infoLeft, infoRight;
    private ImageView nowView;
    private ImageView firstImg, secondImg, thirdImg, fourthImg;
    private TextView firstWeather, firstDate, secondWeather, secondDate,
            thirdWeather, thirdDate, fourthWeather, fourthDate;

    /*private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("com.weather.refresh")){
                Toast.makeText(getApplication(),"refresh",Toast.LENGTH_LONG).show();
                Getweather getweather = new Getweather();
                getweather.execute(url, "101010100");
            }
        }
    };*/

    private String defaultId = "101010100";

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("com.weather.refresh")){
                //Toast.makeText(getApplication(),"refresh",Toast.LENGTH_LONG).show();
                Getweather getweather = new Getweather();
                getweather.execute(url, defaultId);
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().hide();

        //当天
        lowText = (TextView)findViewById(R.id.lowest_text);
        nowText = (TextView)findViewById(R.id.now_text);
        highText = (TextView)findViewById(R.id.highest_text);
        infoLeft = (TextView)findViewById(R.id.info_left_text);
        infoRight = (TextView)findViewById(R.id.info_right);
        nowView = (ImageView)findViewById(R.id.now_img);
        //后四天天气
        firstImg = (ImageView)findViewById(R.id.first_img);
        firstWeather = (TextView)findViewById(R.id.first_weather);
        firstDate = (TextView)findViewById(R.id.first_date);
        secondImg = (ImageView)findViewById(R.id.second_img);
        secondWeather = (TextView)findViewById(R.id.second_weather);
        secondDate = (TextView)findViewById(R.id.second_date);
        thirdImg = (ImageView)findViewById(R.id.third_img);
        thirdWeather = (TextView)findViewById(R.id.third_weather);
        thirdDate = (TextView)findViewById(R.id.third_date);
        fourthImg = (ImageView)findViewById(R.id.fourth_img);
        fourthWeather = (TextView)findViewById(R.id.fourth_weather);
        fourthDate = (TextView)findViewById(R.id.fourth_date);

        Getweather getweather = new Getweather();
        //101010100是北京的天气城市代码
        getweather.execute(url,"101010100");

        imageButton = (ImageButton)findViewById(R.id.city_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CityActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

        IntentFilter inf = new IntentFilter();
        inf.addAction("com.weather.refresh");
        registerReceiver(broadcastReceiver, inf);

        startService(new Intent(this, RefreshService.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            Toast.makeText(this, bundle.getString("city"), Toast.LENGTH_LONG).show();
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            defaultId = bundle.getString("areaId");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, RefreshService.class));
        unregisterReceiver(broadcastReceiver);

    }


    //访问网络的内部类
    private class Getweather extends AsyncTask<String, String, String> {

        private String openConnection(String address, String cityId){
            String result = "";
            try{
                URL url = new URL(address + cityId);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("apikey", "604b54161bec3b49182ae99961bba059");
                connection.connect();
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result = result + line;
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected String doInBackground(String... params) {
            return openConnection(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String result){
            try{
                JSONObject object = new JSONObject(result);
                JSONObject retData = (JSONObject)object.get("retData");
                //获取当天天气
                JSONObject today = (JSONObject)retData.get("today");
                highText.setText(today.getString("hightemp"));
                lowText.setText(today.getString("lowtemp"));
                nowText.setText(today.getString("curTemp"));
                infoLeft.setText(retData.getString("city")+"\n\n"+today.getString("week")+"\n\n"+today.getString("type"));
                infoRight.setText(today.getString("date")+"\n\n"+today.getString("fengli")+"\n\n"+today.getString("fengxiang"));
                //获取后四天的预告
                JSONArray forecast = (JSONArray)retData.get("forecast");
                firstWeather.setText(((JSONObject)forecast.get(0)).getString("type")+"\n\n"+
                        ((JSONObject)forecast.get(0)).getString("hightemp")+"/"+((JSONObject)forecast.get(0)).getString("lowtemp"));
                secondWeather.setText(((JSONObject)forecast.get(1)).getString("type")+"\n\n"+
                        ((JSONObject)forecast.get(1)).getString("hightemp")+"/"+((JSONObject)forecast.get(0)).getString("lowtemp"));
                thirdWeather.setText(((JSONObject)forecast.get(2)).getString("type")+"\n\n"+
                        ((JSONObject)forecast.get(2)).getString("hightemp")+"/"+((JSONObject)forecast.get(0)).getString("lowtemp"));
                fourthWeather.setText(((JSONObject)forecast.get(3)).getString("type")+"\n\n"+
                        ((JSONObject)forecast.get(3)).getString("hightemp")+"/"+((JSONObject)forecast.get(0)).getString("lowtemp"));
                firstDate.setText(((JSONObject)forecast.get(0)).getString("week"));
                secondDate.setText(((JSONObject)forecast.get(1)).getString("week"));
                thirdDate.setText(((JSONObject)forecast.get(2)).getString("week"));
                fourthDate.setText(((JSONObject)forecast.get(3)).getString("week"));
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }


}
