package swift.trend.receiver;

import android.content.pm.ActivityInfo;
import android.os.Build;

import java.util.HashSet;
import java.util.Set;

import static android.accounts.AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION;
import static android.app.admin.DevicePolicyManager.ACTION_DEVICE_OWNER_CHANGED;
import static android.bluetooth.BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED;
import static android.content.Intent.ACTION_BOOT_COMPLETED;
import static android.content.Intent.ACTION_HEADSET_PLUG;
import static android.content.Intent.ACTION_LOCALE_CHANGED;
import static android.content.Intent.ACTION_LOCKED_BOOT_COMPLETED;
import static android.content.Intent.ACTION_MEDIA_BAD_REMOVAL;
import static android.content.Intent.ACTION_MEDIA_CHECKING;
import static android.content.Intent.ACTION_MEDIA_EJECT;
import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.content.Intent.ACTION_MEDIA_REMOVED;
import static android.content.Intent.ACTION_MEDIA_UNMOUNTABLE;
import static android.content.Intent.ACTION_MEDIA_UNMOUNTED;
import static android.content.Intent.ACTION_MY_PACKAGE_REPLACED;
import static android.content.Intent.ACTION_NEW_OUTGOING_CALL;
import static android.content.Intent.ACTION_PACKAGE_DATA_CLEARED;
import static android.content.Intent.ACTION_PACKAGE_FULLY_REMOVED;
import static android.content.Intent.ACTION_TIMEZONE_CHANGED;
import static android.content.Intent.ACTION_USER_INITIALIZE;
import static android.hardware.usb.UsbManager.ACTION_USB_ACCESSORY_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_ACCESSORY_DETACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static android.os.Build.VERSION_CODES.O;
import static android.provider.CalendarContract.ACTION_EVENT_REMINDER;
import static android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION;
import static android.provider.Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION;
import static android.telephony.CarrierConfigManager.ACTION_CARRIER_CONFIG_CHANGED;

/**
 * Created by swift_gan on 2017/8/8.
 */

public interface IWhiteList {

    boolean isWhiteAction(String action);
    boolean needRegister(ActivityInfo activityInfo);
    void addWhiteAction(String... action);

    class CREATE {

        private static IWhiteList whiteList;

        public synchronized static IWhiteList create() {
            if (whiteList != null) {
                return whiteList;
            }
            if (Build.VERSION.SDK_INT >= O) {
                whiteList = new WhiteListO();
            } else {
                whiteList = new WhiteListEmpty();
            }
            return whiteList;
        }
    }

    class WhiteListEmpty implements IWhiteList {
        @Override
        public boolean isWhiteAction(String action) {
            return true;
        }

        @Override
        public boolean needRegister(ActivityInfo activityInfo) {
            return false;
        }

        @Override
        public void addWhiteAction(String... action) {

        }
    }

    class WhiteListO implements IWhiteList {


        public final static String[] WHITE_ACTIONS = new String[]{ACTION_LOCKED_BOOT_COMPLETED
                , ACTION_BOOT_COMPLETED
                , ACTION_USER_INITIALIZE
                , "android.intent.action.USER_ADDED"
                , "android.intent.action.USER_REMOVED"
                , "android.intent.action.TIME_SET"
                , ACTION_TIMEZONE_CHANGED
                , ACTION_LOCALE_CHANGED
                , ACTION_USB_ACCESSORY_ATTACHED
                , ACTION_USB_ACCESSORY_DETACHED
                , ACTION_USB_DEVICE_ATTACHED
                , ACTION_USB_DEVICE_DETACHED
                , ACTION_HEADSET_PLUG
                , ACTION_CONNECTION_STATE_CHANGED
                , ACTION_CONNECTION_STATE_CHANGED
                , ACTION_CARRIER_CONFIG_CHANGED
                , LOGIN_ACCOUNTS_CHANGED_ACTION
                , ACTION_PACKAGE_DATA_CLEARED
                , ACTION_PACKAGE_FULLY_REMOVED
                , ACTION_NEW_OUTGOING_CALL
                , ACTION_DEVICE_OWNER_CHANGED
                , ACTION_EVENT_REMINDER
                , ACTION_MEDIA_MOUNTED
                , ACTION_MEDIA_CHECKING
                , ACTION_MEDIA_UNMOUNTED
                , ACTION_MEDIA_EJECT
                , ACTION_MEDIA_UNMOUNTABLE
                , ACTION_MEDIA_REMOVED
                , ACTION_MEDIA_BAD_REMOVAL
                , SMS_RECEIVED_ACTION
                , WAP_PUSH_RECEIVED_ACTION
                , "android.provider.Telephony.SECRET_CODE"
                , ACTION_MY_PACKAGE_REPLACED};

        public final static Set<String> WHITE_ACTIONS_MAP = new HashSet<String>(WHITE_ACTIONS.length);

        static {
            for (String WHITE_ACTION:WHITE_ACTIONS) {
                WHITE_ACTIONS_MAP.add(WHITE_ACTION);
            }
        }

        @Override
        public boolean isWhiteAction(String action) {
            return WHITE_ACTIONS_MAP.contains(action);
        }

        @Override
        public boolean needRegister(ActivityInfo activityInfo) {
            if (activityInfo.permission == null) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void addWhiteAction(String... action) {
            for (String a:action) {
                WHITE_ACTIONS_MAP.add(a);
            }
        }
    }
}
