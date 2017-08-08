package swift.trend.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swift_gan on 2017/8/7.
 * Register from Menifest
 */

public class ReceiverDynamicRegister {

    public static List<BroadcastReceiver> register(Application application, IRegisterDelegate delegate) {
        IReceiverParser parser = IReceiverParser.CREATE.create();
        if (parser == null)
            return null;
        List<IReceiverParser.ReceiverEntity> receiverEntities = null;
        try {
            receiverEntities = parser.parseReceiverFromMenifest(new File(application.getPackageResourcePath()));
        } catch (IReceiverParser.ParseException e) {
            return null;
        }
        if (receiverEntities == null || receiverEntities.size() == 0)
            return null;
        IRegisterDelegate registerDelegate = new BaseRegisterDelegate(delegate);
        List<BroadcastReceiver> receivers = new ArrayList<>(receiverEntities.size());
        IWhiteList whiteList = IWhiteList.CREATE.create();
        for (IReceiverParser.ReceiverEntity receiverEntity: receiverEntities) {
            IntentFilter filter = new IntentFilter();
            for (IntentFilter rawFilter:receiverEntity.filters) {
                for (int i = 0;i < rawFilter.countActions();i++) {
                    if (!whiteList.isWhiteAction(rawFilter.getAction(i))) {
                        filter.addAction(rawFilter.getAction(i));
                    }
                }
                for (int i = 0;i < rawFilter.countCategories();i++) {
                    filter.addCategory(rawFilter.getCategory(i));
                }
                for (int i = 0;i < rawFilter.countDataSchemes();i++) {
                    filter.addDataScheme(rawFilter.getDataScheme(i));
                }
                for (int i = 0;i < rawFilter.countDataAuthorities();i++) {
                    filter.addDataAuthority(rawFilter.getDataAuthority(i).getHost(), rawFilter.getDataAuthority(i).getPort() + "");
                }
                for (int i = 0;i < rawFilter.countDataPaths();i++) {
                    filter.addDataPath(rawFilter.getDataPath(i).getPath(), rawFilter.getDataPath(i).getType());
                }
                for (int i = 0;i < rawFilter.countDataSchemeSpecificParts();i++) {
                    filter.addDataSchemeSpecificPart(rawFilter.getDataSchemeSpecificPart(i).getPath(), rawFilter.getDataSchemeSpecificPart(i).getType());
                }
                filter.setPriority(rawFilter.getPriority());
            }
            BroadcastReceiver receiver = receiverEntity.generateReceiver();
            if (receiver != null) {
                if (registerDelegate.register(application, receiver, filter, receiverEntity.activityInfo)) {
                    receivers.add(receiver);
                }
            }
        }
        return receivers;
    }

    public static void registerAsync(final Application application, final Callback callback, final IRegisterDelegate delegate) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                IReceiverParser parser = IReceiverParser.CREATE.create();
                if (parser == null){
                    callback.onError(new IReceiverParser.ParseException("Device System Version may < Android 5.0"));
                    return;
                }
                List<IReceiverParser.ReceiverEntity> receiverEntities = null;
                try {
                    receiverEntities = parser.parseReceiverFromMenifest(new File(application.getPackageResourcePath()));
                } catch (IReceiverParser.ParseException e) {
                   callback.onError(e);
                    return;
                }
                if (receiverEntities == null || receiverEntities.size() == 0) {
                    callback.onError(new IReceiverParser.ParseException("Can not find receiver in menifest"));
                    return;
                }
                final IRegisterDelegate registerDelegate = new BaseRegisterDelegate(delegate);
                final List<BroadcastReceiver> receivers = new ArrayList<>(receiverEntities.size());
                final IWhiteList whiteList = IWhiteList.CREATE.create();
                final List<IReceiverParser.ReceiverEntity> finalReceiverEntities = receiverEntities;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        for (IReceiverParser.ReceiverEntity receiverEntity: finalReceiverEntities) {
                            IntentFilter filter = new IntentFilter();
                            for (IntentFilter rawFilter:receiverEntity.filters) {
                                for (int i = 0;i < rawFilter.countActions();i++) {
                                    if (!whiteList.isWhiteAction(rawFilter.getAction(i))) {
                                        filter.addAction(rawFilter.getAction(i));
                                    }
                                }
                                for (int i = 0;i < rawFilter.countCategories();i++) {
                                    filter.addCategory(rawFilter.getCategory(i));
                                }
                                for (int i = 0;i < rawFilter.countDataSchemes();i++) {
                                    filter.addDataScheme(rawFilter.getDataScheme(i));
                                }
                                for (int i = 0;i < rawFilter.countDataAuthorities();i++) {
                                    filter.addDataAuthority(rawFilter.getDataAuthority(i).getHost(), rawFilter.getDataAuthority(i).getPort() + "");
                                }
                                for (int i = 0;i < rawFilter.countDataPaths();i++) {
                                    filter.addDataPath(rawFilter.getDataPath(i).getPath(), rawFilter.getDataPath(i).getType());
                                }
                                for (int i = 0;i < rawFilter.countDataSchemeSpecificParts();i++) {
                                    filter.addDataSchemeSpecificPart(rawFilter.getDataSchemeSpecificPart(i).getPath(), rawFilter.getDataSchemeSpecificPart(i).getType());
                                }
                                filter.setPriority(rawFilter.getPriority());
                            }
                            BroadcastReceiver receiver = receiverEntity.generateReceiver();
                            if (receiver != null) {
                                if (registerDelegate.register(application, receiver, filter, receiverEntity.activityInfo)) {
                                    receivers.add(receiver);
                                }
                            }
                        }
                        callback.onSuccess(receivers);
                    }
                });
            }
        }).start();
    }

    public static void unRegisterReceivers(Context context, List<BroadcastReceiver> receivers) {
        if (context != null && receivers != null && receivers.size() > 0) {
            for (BroadcastReceiver register: receivers) {
                context.unregisterReceiver(register);
            }
        }
    }

    public interface Callback {
        void onSuccess(List<BroadcastReceiver> receivers);
        void onError(Throwable throwable);
    }

    public static void addWhiteAction(String... action) {
        IWhiteList.CREATE.create().addWhiteAction(action);
    }

}
