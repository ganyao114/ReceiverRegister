package swift.trend.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;

/**
 * Created by swift_gan on 2017/8/7.
 */

public class BaseRegisterDelegate implements IRegisterDelegate {

    private IRegisterDelegate delegate;

    public BaseRegisterDelegate(IRegisterDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean register(Context context, BroadcastReceiver broadcastReceiver, IntentFilter filter, ActivityInfo info) {
        if (IWhiteList.CREATE.create().needRegister(info) && filter.countActions() != 0) {
            if (delegate != null) {
                return delegate.register(context, broadcastReceiver, filter, info);
            } else {
                context.registerReceiver(broadcastReceiver, filter);
                return true;
            }
        }
        return false;
    }
}
