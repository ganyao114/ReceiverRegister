# ReceiverRegister
Android 8.0 Broadcast 静态注册自动转为动态注册


# Android 8.0 广播限制

如果应用注册为接收广播，则在每次发送广播时，应用的接收器都会消耗资源。 如果多个应用注册为接收基于系统事件的广播，这会引发问题；触发广播的系统事件会导致所有应用快速地连续消耗资源，从而降低用户体验。  
为了缓解这一问题，Android 7.0（API 级别 25）对广播施加了一些限制，如后台优化中所述。
Android O 让这些限制更为严格。  
针对 Android O 的应用无法继续在其清单中为隐式广播注册广播接收器。 隐式广播是一种不专门针对该应用的广播。 例如，ACTION_PACKAGE_REPLACED 就是一种隐式广播，因为它将发送到注册的所有侦听器，让后者知道设备上的某些软件包已被替换。  
不过，ACTION_MY_PACKAGE_REPLACED 不是隐式广播，因为不管已为该广播注册侦听器的其他应用有多少，它都会只发送到软件包已被替换的应用。  
应用可以继续在它们的清单中注册显式广播。  
应用可以在运行时使用 Context.registerReceiver()   为任意广播（不管是隐式还是显式）注册接收器。  
需要签名权限的广播不受此限制所限，因为这些广播只会发送到使用相同证书签名的应用，而不是发送到设备上的所有应用。  
在许多情况下，之前注册隐式广播的应用使用 JobScheduler 作业可以获得类似的功能。
例如，一款社交照片应用可能需要不时地执行数据清理，并且倾向于在设备连接到充电器时执行此操作。  
之前，应用已经在清单中为 ACTION_POWER_CONNECTED   注册了一个接收器；当应用接收到该广播时，它会检查清理是否必要。 为了迁移到 Android O，应用将该接收器从其清单中移除。  
应用将清理作业安排在设备处于空闲状态和充电时运行。
注：很多隐式广播当前均已不受此限制所限。 应用可以继续在其清单中为这些广播注册接收器，不管应用针对哪个 API 级别。 有关已豁免广播的列表，请参阅隐式广播例外。

# 使用
1. 导入项目中的 registerLib 模块。
2. 在 Application onCreate 时调用

```
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
```

# 注意
## 关于白名单
Android O 对于在 Menifest 中静态注册的广播有如下规则:
1. 经常被大量 App 监听的 Action 将失效。
2. 偶尔发生的 Action 依然生效：例如 BOOT_COMPLETE,白名单 [link](https://developer.android.com/preview/features/background-broadcasts.html)
3. 有签名权限的依然生效。
4. 只会发给自己的 Receiver 依然生效。

以上情况本项目基本都有考虑，不过用户依然可以使用 addWhiteAction 添加白名单，注意要在注册之前调用。

# 其他
1. 可以使用 register 或者 registerAsync 同步或者异步注册。
2. register 时可以传入 delegate，针对自身业务实现注册，比如某些 Receiver 需要改成本地注册的。
3. 当一个 Receiver 中的 Action 既有白名单 Action，又有黑名单 Action 时，Android 8.0 依然会注册 Receiver 只不过 黑名单 Action 不会触发。这样的话内存中将会有两个有效的 Receiver 实例，这点需要注意。但是白名单 Action 不会被动态注册。
