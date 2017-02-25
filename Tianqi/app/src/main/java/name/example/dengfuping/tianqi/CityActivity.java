package name.example.dengfuping.tianqi;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityActivity extends AppCompatActivity {
    private ListView listView;
    private String[] cities = {"北京 朝阳", "江苏 宿迁", "江苏 南京", "江苏 徐州", "辽宁 朝阳"};
    private List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
    private Button addButton;

    private MyAdapter myAdapter;
    private String[] areaIds = {"1","1","1","1","1"};
    private Button deleteButton;

    private DBHelper dbHelper;
    private String[] name, ids;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        this.getSupportActionBar().hide();

        for (int i = 0; i < cities.length; i++) {
            Map<String, Object> listem = new HashMap<String, Object>();
            listem.put("name", cities[i]);
            listems.add(listem);
        }
        listView = (ListView) findViewById(R.id.city_listview);
        listView.setAdapter(new SimpleAdapter(getApplication(), listems,
                R.layout.activity_search_listview_item, new String[]{"name"},
                new int[]{R.id.result_text}));

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                bundle.putString("city", cities[position]);
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
                bundle.putString("areaId", ids[position]);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });


        addButton = (Button) findViewById(R.id.add_city_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CityActivity.this, SearchActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

        deleteButton = (Button)findViewById(R.id.delete_city_button);
        myAdapter = new MyAdapter(cities, areaIds, deleteButton);
        listView.setAdapter(myAdapter);

        /*deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), myAdapter.getCheckedCities().length + "", Toast.LENGTH_LONG).show();
            }
        });*/

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] result = myAdapter.getCheckedCities();
                for(int i=0; i<result.length; i++){
                    dbHelper.deleteDataById(result[i]);
                }
                changeAdapter(dbHelper.queryAllCities());
            }
        });


        dbHelper = DBHelper.getInstance(this);
        changeAdapter(dbHelper.queryAllCities());


    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            Toast.makeText(this, bundle.getString("city"), Toast.LENGTH_LONG).show();
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            dbHelper.insertData(new String[]{"areaId", "cityname"},
                    new String[]{bundle.getString("areaId"), bundle.getString("city")});
            changeAdapter(dbHelper.queryAllCities());
        }
    }


    private void changeAdapter(List<Map<String, Object>> list){
        ids = new String[list.size()];
        name = new String[list.size()];
        for(int i=0;i<list.size();i++){
            ids[i] = list.get(i).get("areaId").toString();
            name[i] = list.get(i).get("cityname").toString();
        }
        myAdapter = new MyAdapter(name, ids, deleteButton);
        listView.setAdapter(myAdapter);
    }


    private class MyAdapter extends BaseAdapter {
        private class ViewSet{
            TextView textView;
            CheckBox checkBox;
        }
        private String[] cities;
        private int checkedNum = 0;
        private Button button;
        private boolean[] checkedArray;
        private String[] cityIds;

        public MyAdapter(String[] cities, String[] cityIds, Button button){
            this.cities = cities;
            this.button = button;
            this.cityIds = cityIds;
            this.checkedArray = new boolean[cities.length];
            for(int i=0;i<cities.length;i++){
                this.checkedArray[i] = false;
            }
        }

        @Override
        public int getCount() {
            return cities.length;
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewSet viewSet = null;
            if(convertView == null){
                viewSet = new ViewSet();
                convertView = LayoutInflater.from(getApplication()).inflate(R.layout.activity_city_listview_item, null);
                viewSet.textView = (TextView)convertView.findViewById(R.id.listview_item_textview);
                viewSet.checkBox = (CheckBox)convertView.findViewById(R.id.listview_item_checkbox);
                convertView.setTag(viewSet);
            }else{
                viewSet = (ViewSet)convertView.getTag();
            }
            viewSet.textView.setText(cities[position]);
            viewSet.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkedArray[position] = true;
                        checkedNum++;
                        if (checkedNum == 1) {
                            button.setEnabled(true);
                        }
                    } else {
                        checkedArray[position] = false;
                        checkedNum--;
                        if (checkedNum == 0) {
                            button.setEnabled(false);
                        }
                    }
                }
            });
            return convertView;
        }

        //返回选择的城市代码
        public String[] getCheckedCities(){
            List<String> checkedCityIdList = new ArrayList<String>();
            for(int i=0;i<checkedArray.length;i++){
                if(checkedArray[i] == true){
                    checkedCityIdList.add(cityIds[i]);
                }
            }
            String[] checkedCityIdArray = new String[checkedCityIdList.size()];
            return checkedCityIdList.toArray(checkedCityIdArray);
        }


    }

}

