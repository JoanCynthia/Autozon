package redeem.com.autozon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver
{
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    DeviceControl mainActivity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, DeviceControl mainActivity) {
        this.manager = manager;
        this.channel = channel;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try {
//        mainActivity = new DeviceControl();
        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            {
//                Toast.makeText(context, "WiFi is On", Toast.LENGTH_SHORT).show();
                Log.d("check", "wifi on");
            }
            else
            {
//                Toast.makeText(context, "WiFi is Off", Toast.LENGTH_SHORT).show();
                Log.d("check", "wifi off");
            }
        }
        else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
        {
            if(manager != null)
            {
                manager.requestPeers(channel, mainActivity.peerListListener);
                Log.d("check", "request peers");
            }
        }

    }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
    }
}
