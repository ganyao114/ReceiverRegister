package trendmicro.swift.receiverregister;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.util.Log;

import java.util.List;

import swift.trend.receiver.ReceiverDynamicRegister;

/**
 * Created by swift_gan on 2017/8/8.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ReceiverDynamicRegister.registerAsync(this, new ReceiverDynamicRegister.Callback() {
            @Override
            public void onSuccess(List<BroadcastReceiver> receivers) {
                Log.v("MyApplication", "register receiver success!");
            }

            @Override
            public void onError(Throwable throwable) {

            }
        }, null);
    }
}
