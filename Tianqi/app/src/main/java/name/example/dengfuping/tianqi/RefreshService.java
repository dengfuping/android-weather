package name.example.dengfuping.tianqi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Deng Fuping on 2016/5/24.
 */
public class RefreshService extends Service {
    Timer timer;
    TimerTask timerTask;

    @Override
    public void onCreate() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction("com.weather.refresh");
                sendBroadcast(intent);
            }
        };
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.schedule(timerTask, 0, 10000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
