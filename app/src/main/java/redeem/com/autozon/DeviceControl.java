package redeem.com.autozon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceControl extends Fragment
{
    Switch bluetooth, wifi;
    MainActivity mainActivity;
    BluetoothAdapter myBluetooth;
    Intent btnEnabling;
    int requestCodeForEnable;
    ListView listView;
    ArrayList<String> stringArrayList;
    Button scan, scan1;
    ArrayList<BluetoothDevice> mLeDevices;
    BroadcastReceiver myreceiver, broadcastReceiver;
    LeDeviceListAdapter mLeDeviceListAdapter;
    public boolean mScanning;
    private static final long SCAN_PERIOD = 10000;
    static String Name, address;
    static Boolean isBluetooth;
    WifiManager wifiManager;
    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;
    static WifiP2pDevice device;


    public class LeDeviceListAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = getActivity().getLayoutInflater().inflate(R.layout.router, parent, false);
            TextView name, status;
            name = view.findViewById(R.id.name);
            status = view.findViewById(R.id.status);
            BluetoothDevice device = mLeDevices.get(position);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                name.setText(deviceName);
            else
                name.setText("Unknown Device");
            status.setText(device.getAddress());
            Log.v("check","2");
            //ADD EXCEPTION HANDLING
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    isBluetooth = true;
                    final BluetoothDevice device = mLeDevices.get(position);
                    Name = device.getName();
                    address = device.getAddress();
                    if (device == null) return;
                    mainActivity.loadBluetoothDetails();
                }
            });
            return view;
        }


    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (!mLeDevices.contains(device) && mLeDevices != null) {
                                        mLeDevices.add(device);
                                        Log.v("check", "adapter notified");
                                    }
                                }catch (Throwable e){
                                    e.printStackTrace();
                                    Log.d("sus",e.toString());
                                }
                            }
                        });
                    }
                    if(mLeDevices.size() != 0) {
                        mLeDeviceListAdapter.notifyDataSetChanged();
                    }
                }

    };

    public DeviceControl() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_control, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.getSupportActionBar().show();
            mainActivity.getSupportActionBar().setTitle("Device Control");
        }
        bluetooth = view.findViewById(R.id.bluetooth);
        wifi = view.findViewById(R.id.wifiSwitch);
        listView = view.findViewById(R.id.list);
        stringArrayList = new ArrayList<String>();
        mLeDevices = new ArrayList<BluetoothDevice>();

        scan = view.findViewById(R.id.scan);
        scan1 = view.findViewById(R.id.scanWiFi);
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiP2pManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(mainActivity, mainActivity.getMainLooper(), null);

        receiver = new WiFiDirectBroadcastReceiver(wifiP2pManager, channel, this);
        intentFilter = new IntentFilter();

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        setHasOptionsMenu(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mainActivity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        btnEnabling = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForEnable = 1;

        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mainActivity, "BLE not supported", Toast.LENGTH_SHORT).show();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            myBluetooth = bluetoothManager.getAdapter();
        }

        // Checks if Bluetooth is supported on the device.
        if (myBluetooth == null) {
            Toast.makeText(mainActivity, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }
        if(myBluetooth.isEnabled())
        {
            bluetooth.setChecked(true);
        }
        else
        {
            bluetooth.setChecked(false);
        }
        if(wifiManager.isWifiEnabled())
        {
            wifi.setChecked(true);
        }
        else {
            wifi.setChecked(false);
        }

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!bluetooth.isChecked()) {
                        Toast.makeText(mainActivity, "Switch on bluetooth", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    stringArrayList.clear();
                    mLeDevices.clear();
                    mLeDeviceListAdapter = new LeDeviceListAdapter();
                    listView.setAdapter(mLeDeviceListAdapter);
                    scanLeDevice(true);
                }catch (Throwable e){
                    e.printStackTrace();
                    Log.d("sus",e.toString());
                }

            }
        });
        scan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!wifi.isChecked()) {
                        Toast.makeText(mainActivity, "Switch on Wi-Fi", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
//                            status.setText("Discovery started");
                            Toast.makeText(mainActivity, "Discovery started", Toast.LENGTH_SHORT).show();
                            Log.d("check", "discovery started");

                        }

                        @Override
                        public void onFailure(int reason) {
//                            status.setText("Discovery failed");
                            Toast.makeText(mainActivity, "Discovery failed", Toast.LENGTH_SHORT).show();
                            Log.d("check", "discovery failed");


                        }
                    });
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                    Log.d("check", e.toString());
                }
            }
        });
        bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                try {

                    if (isChecked) {
                        wifi.setChecked(false);

                        if (myBluetooth == null) {
                            Toast.makeText(mainActivity, "Bluetooth Dose not support on this device", Toast.LENGTH_SHORT).show();
//                            return;
                        } else {
                            if (!myBluetooth.isEnabled()) {
                                startActivityForResult(btnEnabling, requestCodeForEnable);
                            }
                        }
                    } else {
                        if (myBluetooth.isEnabled()) {
                            myBluetooth.disable();
                        }
                    }
                }catch (Throwable e){
                    e.printStackTrace();
                    Log.d("sus",e.toString());
                }
            }
        });
        wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if(isChecked)
                    {
                        bluetooth.setChecked(false);
                        wifiManager.setWifiEnabled(true);
                    }
                    else
                    {
                        wifiManager.setWifiEnabled(false);
                    }
                }
                catch (Throwable e){
                    e.printStackTrace();
                    Log.d("sus",e.toString());
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                isBluetooth = false;
                device = deviceArray[position];
                mainActivity.loadRouterDetails();
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
//            new Handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mScanning = false;
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    invalidateOptionsMenu();
//                }
//            }, SCAN_PERIOD);
            mScanning = true;
            try {
                myBluetooth.startLeScan(mLeScanCallback);
                Log.v("check", "Le scan started");
            }
            catch (Throwable e)
            {
                Log.d("check", e.toString());
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    try {
                    myBluetooth.stopLeScan(mLeScanCallback);
                    }
                    catch (Throwable e)
                    {
                        Log.d("check", e.toString());
                    }
                }
            },SCAN_PERIOD);

        } else {
            mScanning = false;
            try {
                myBluetooth.stopLeScan(mLeScanCallback);
            }
            catch (Throwable e)
            {
                Log.d("check", e.toString());
            }        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestCodeForEnable){
            if (resultCode == RESULT_OK){
                Toast.makeText(getActivity(), "Bluetooth is Enabled", Toast.LENGTH_SHORT).show();
            }else if (resultCode == RESULT_CANCELED){
                Toast.makeText(getActivity(), "Bluetooth Enabling Cancelled", Toast.LENGTH_SHORT).show();

            }
        }
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList)
        {
            try {

                if (!peerList.getDeviceList().equals(peers) && peerList != null) {
                    peers.clear();
                    peers.addAll(peerList.getDeviceList());
                    //ADD NULL CHECK AND EXCEPTION HANDLING
                    deviceNameArray = new String[peerList.getDeviceList().size()];
                    Log.d("check", "name array");

                    deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                    Log.d("check", "device array");

                    int index = 0;

                    for (WifiP2pDevice device : peerList.getDeviceList()) {
                        deviceNameArray[index] = device.deviceName;
                        Log.d("check", deviceNameArray[index]);
                        deviceArray[index] = device;
                        index++;
                    }
                    if(deviceNameArray.length != 0) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, deviceNameArray);
                        Log.d("check", deviceNameArray.length + "");
                        listView.setAdapter(adapter);
                    }
                }

                if (peers.size() == 0) {
                    Toast.makeText(mainActivity, "No device found..Try again", Toast.LENGTH_SHORT).show();
//                    return;
                }
            }catch (Throwable e){
                e.printStackTrace();
                Log.d("sus",e.toString());
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onPause() {
        super.onPause();
        try {
            scanLeDevice(false);
            if(receiver != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Objects.requireNonNull(getActivity()).unregisterReceiver(receiver);
                }
                mLeDevices.clear();
            }
        }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getActivity()).registerReceiver(receiver, intentFilter);
            }
        }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(myreceiver != null && broadcastReceiver != null) {
            try {
                mainActivity.unregisterReceiver(myreceiver);
                mainActivity.unregisterReceiver(broadcastReceiver);
            }
            catch (IllegalArgumentException e)
            {
                Log.d("exception-receiver", e.toString());
            }
            catch (Exception e)
            {
                Log.d("exception-receiver", e.toString());
            }
            catch (OutOfMemoryError e)
            {
                e.printStackTrace();
                Log.d("exception", e.toString());
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.d("exception", e.toString());
            }

        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
