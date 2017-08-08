package swift.trend.receiver;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by swift_gan on 2017/8/7.
 */

public interface IReceiverParser {

    List parseReceiverFromMenifest(File file) throws ParseException;

    class CREATE {

        private static IReceiverParser parser;

        public synchronized static IReceiverParser create() {
            if (parser != null) {
                return parser;
            }
            if (Build.VERSION.SDK_INT >= LOLLIPOP) {
                parser = new ReceiverParserM();
            }
            return parser;
        }
    }


    class ParseException extends RuntimeException {
        public ParseException(String message) {
            super(message);
        }
    }

    class ReceiverEntity {

        public ActivityInfo activityInfo;
        public List<IntentFilter> filters;

        public Class<? extends BroadcastReceiver> getReceiverClass() {
            try {
                return (Class<? extends BroadcastReceiver>) Class.forName(activityInfo.name);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        public BroadcastReceiver generateReceiver() {
            Class<? extends BroadcastReceiver> receiverType = getReceiverClass();
            if (receiverType != null) {
                try {
                    return receiverType.newInstance();
                } catch (ReflectiveOperationException e) {
                    return null;
                }
            }
            return null;
        }

    }


    class ReceiverParserM implements IReceiverParser {

        public static String parserClassName = "android.content.pm.PackageParser";

        @Override
        public List parseReceiverFromMenifest(File file) throws ParseException {
            Object pkgParser = getPackageParser();
            if (pkgParser == null)
                throw new ParseException("can not get PackageParser");
            Method parsePackage = null;
            try {
                parsePackage = pkgParser.getClass().getDeclaredMethod("parsePackage", File.class, int.class);
            } catch (NoSuchMethodException e) {
                throw new ParseException("can not get PackageParser");
            }
            Object pkg = null;
            try {
                pkg = parsePackage.invoke(pkgParser, file, 0);
            } catch (ReflectiveOperationException e) {
                throw new ParseException("can not get pkg");
            }
            if (pkg == null)
                throw new ParseException("pkg is null");
            List activityEntitys = null;
            try {
                Field receiversEntityField = pkg.getClass().getDeclaredField("receivers");
                activityEntitys = (List) receiversEntityField.get(pkg);
            } catch (ReflectiveOperationException e) {
                throw new ParseException("can not get receiver entity");
            }
            if (activityEntitys == null)
                throw new ParseException("can not get receiver entity");
            Field intentFilterListField = null;
            try {
                Class componentClass = Class.forName(parserClassName + "$Component");
                intentFilterListField = componentClass.getDeclaredField("intents");
            } catch (ReflectiveOperationException e) {
                throw new ParseException("can not get intents entity");
            }
            Field activityInfoField = null;
            try {
                Class activityClass = Class.forName(parserClassName + "$Activity");
                activityInfoField = activityClass.getDeclaredField("info");
            } catch (ReflectiveOperationException e) {
                throw new ParseException("can not get activityInfo");
            }
            List<ReceiverEntity> receiverEntities = new ArrayList<>(activityEntitys.size());
            for (Object activityEntity: activityEntitys) {
                try {
                    List<IntentFilter> intents = (List<IntentFilter>) intentFilterListField.get(activityEntity);
                    ActivityInfo activityInfo = (ActivityInfo) activityInfoField.get(activityEntity);
                    ReceiverEntity entity = new ReceiverEntity();
                    entity.activityInfo = activityInfo;
                    entity.filters = intents;
                    receiverEntities.add(entity);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
            return receiverEntities;
        }

        public Object getPackageParser() {
            try {
                Class parserClass = Class.forName(parserClassName);
                Constructor constructor = parserClass.getConstructor();
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                return constructor.newInstance();
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
