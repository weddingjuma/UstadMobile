package com.ustadmobile.port.android.netwokmanager;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;


import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.port.android.impl.UstadMobileSystemImplAndroid;
import com.ustadmobile.port.sharedse.impl.UstadMobileSystemImplSE;

import edu.rit.se.wifibuddy.WifiDirectHandler;

import static com.ustadmobile.core.buildconfig.CoreBuildConfig.NETWORK_SERVICE_NAME;
import static com.ustadmobile.port.android.netwokmanager.NetworkManagerAndroid.EXTRA_SERVICE_NAME;
import static com.ustadmobile.port.android.netwokmanager.NetworkManagerAndroid.PREF_KEY_SUPERNODE;
import static com.ustadmobile.port.android.netwokmanager.NetworkManagerAndroid.SERVICE_NAME;


public class NetworkServiceAndroid extends Service{

    private WifiDirectHandler wifiDirectHandler;
    private final IBinder mBinder = new LocalServiceBinder();
    private NetworkManagerAndroid networkManagerAndroid;


    public NetworkServiceAndroid(){

    }


    @Override
    public void onCreate() {
        super.onCreate();
        Intent wifiServiceIntent = new Intent(this, WifiDirectHandler.class);
        bindService(wifiServiceIntent, wifiP2PServiceConnection, BIND_AUTO_CREATE);
        networkManagerAndroid = (NetworkManagerAndroid) UstadMobileSystemImplAndroid.getInstanceAndroid().getNetworkManager();
        networkManagerAndroid.init(NetworkServiceAndroid.this);
    }




    @Override
    public void onDestroy() {
        networkManagerAndroid.onDestroy();

        if(wifiDirectHandler!=null){
            wifiDirectHandler.removeGroup();
            wifiDirectHandler.stopServiceDiscovery();
            wifiDirectHandler.removeService();
            UstadMobileSystemImpl.getInstance().setAppPref("devices","",getApplicationContext());
        }
        unbindService(wifiP2PServiceConnection);


        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    ServiceConnection wifiP2PServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            wifiDirectHandler = ((WifiDirectHandler.WifiTesterBinder) iBinder).getService();
            wifiDirectHandler.setStopDiscoveryAfterGroupFormed(false);

            boolean isSuperNodeEnabled = Boolean.parseBoolean(UstadMobileSystemImpl.getInstance().getAppPref(
                    PREF_KEY_SUPERNODE, "false", NetworkServiceAndroid.this.getApplicationContext()));
            networkManagerAndroid.setSuperNodeEnabled(NetworkServiceAndroid.this.getApplicationContext(),
                    isSuperNodeEnabled);
        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            wifiDirectHandler = null;
        }
    };




    public class LocalServiceBinder extends Binder {
        public NetworkServiceAndroid getService(){
            return NetworkServiceAndroid.this;
        }

    }

    /**
     *
     * @return instance of the WifiDirectHandler API
     */
    public WifiDirectHandler getWifiDirectHandlerAPI(){
        return wifiDirectHandler;
    }

}