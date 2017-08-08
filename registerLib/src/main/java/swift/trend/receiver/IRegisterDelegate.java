package swift.trend.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;

/**
 * Created by swift_gan on 2017/8/7.
 */

public interface IRegisterDelegate {
    boolean register(Context context, BroadcastReceiver broadcastReceiver, IntentFilter filter, ActivityInfo info);
}
