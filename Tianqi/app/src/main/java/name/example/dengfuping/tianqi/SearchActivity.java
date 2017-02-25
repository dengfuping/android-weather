package name.example.dengfuping.tianqi;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private ListView listView;
    private Button button;
    private EditText editText;
    private String[] cities = {"北京 朝阳","江苏 宿迁","江苏 南京","江苏 徐州","辽宁 朝阳"};
    private List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
    private String urlAdress = "http://apis.baidu.com/apistore/weatherservice/citylist?cityname=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.getSupportActionBar().hide();

        listView = (ListView)findViewById(R.id.result_list);
        button = (Button)findViewById(R.id.search_button);
        editText = (EditText)findViewById(R.id.search_text);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用于存储搜索结果数据
                Getcity getcity = new Getcity();
                getcity.execute(urlAdress, editText.getText().toString());
            }
        });


        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                bundle.putString("city", listems.get(position).get("name").toString());
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                bundle.putString("city", listems.get(position).get("name").toString());
                bundle.putString("areaId", listems.get(position).get("cityId").toString());
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });



    }

    private class Getcity extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return openConnect(params[0], params[1]);
        }
        @Override
        protected void onPostExecute(String result){
            try{
                listems.clear();
                JSONObject object = new JSONObject(result);
                JSONArray retData = (JSONArray)object.get("retData");
                for(int i=0;i<retData.length();i++){
                    Map<String, Object> listem = new HashMap<String, Object>();
                    JSONObject temp = (JSONObject)retData.get(i);
                    listem.put("name", temp.getString("province_cn")+" "+temp.getString("district_cn")+" "+temp.getString("name_cn"));
                    listem.put("cityId", temp.getString("area_id"));
                    listems.add(listem);
                }
                listView.setAdapter(new SimpleAdapter(getApplication(), listems,
                        R.layout.activity_search_listview_item, new String[]{"name"},
                        new int[]{R.id.result_text}));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        private String openConnect(String address, String cityName){
            String result = "";
            try{
                //传入的城市名因为是中文，url是无法识别中文的，会乱码而找不到城市，所以需要转码
                URL url = new URL(address + URLEncoder.encode(cityName, "utf-8"));
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
    }

}
