package name.example.dengfuping.tianqi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Deng Fuping on 2016/5/24.
 */
public class DBHelper extends SQLiteOpenHelper {
    //表名
    private static String TableName = "cities";
    //数据库名
    private static String DBName = "test.db";
    //数据库版本号
    private static int DBVersion = 1;
    private Context context;
    //数据库实例
    private SQLiteDatabase database;
    //此类自己的实例
    public static DBHelper dbHelper;
    //创建数据库的语句
    private String createDBSql =
            "create table cities(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "areaId TEXT NOT NULL UNIQUE," +
                    "cityname TEXT NOT NULL);";

    public DBHelper(Context context){
        super(context, DBName, null, DBVersion);
        this.context = context;
    }

    //DBHepler单例模式，节省资源，防止访问冲突
    public static synchronized DBHelper getInstance(Context context){
        if(dbHelper == null){
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createDBSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //插入数据，使用ContentValues方式传入
    public void insertData(String keys[], String values[]){
        ContentValues contentValues = new ContentValues();
        for(int i = 0; i<keys.length; i++){
            contentValues.put(keys[i], values[i]);
        }
        database = getWritableDatabase();
        database.insert(TableName, null, contentValues);
    }

    //通过id删除数据
    public void deleteDataById(String areaId){
        String[] args = {areaId};
        //这里需要可写的数据库
        database = getWritableDatabase();
        database.delete(TableName, "areaId=?", args);
    }

    //查询所有数据
    public List<Map<String, Object>> queryAllCities(){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //这里需要可读的数据库
        database = getReadableDatabase();
        Cursor cursor = database.query(TableName, null, null, null, null, null, null, null);
        while(cursor.moveToNext()){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", cursor.getInt(cursor.getColumnIndex("id")));
            map.put("areaId", cursor.getString(cursor.getColumnIndex("areaId")));
            map.put("cityname", cursor.getString(cursor.getColumnIndex("cityname")));
            list.add(map);
        }
        return list;
    }


}
