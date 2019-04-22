package redeem.com.autozon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

public class WiFiReceiver extends BroadcastReceiver
{
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    Devices mainActivity;

    public WiFiReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, Devices mainActivity) {
        this.manager = manager;
        this.channel = channel;
        this.mainActivity = mainActivity;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        try {
            if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                if (manager == null) {
                    return;
                }
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                Log.d("check", "network info");

                if (networkInfo.isConnected()) {
                    manager.requestConnectionInfo(channel, mainActivity.connectionInfoListener);
                    Log.d("check", "request info");

                } else {
                    Toast.makeText(context, "Device disconnected", Toast.LENGTH_SHORT).show();
                    Log.d("check", "device disconnected");

                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            }
        }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
    }
}
