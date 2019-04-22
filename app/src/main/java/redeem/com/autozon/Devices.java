package redeem.com.autozon;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Devices extends Fragment
{
    ImageView on, off;
    Button cancel;
    MainActivity mainActivity;
    String title;
    Bundle bundle;
    public Bluetoothservice mBluetoothLeService;
    public boolean mConnected = false;
    public BluetoothGattCharacteristic characteristicTX;
    public BluetoothGattCharacteristic characteristicRX;
    String deviceName, deviceAddress;
    String str;
    static final int MESSAGE_READ = 1;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    TextView data;

    ServerClass serverClass;
    ClientClass clientClass;
    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel channel;
    Task task;
    static Socket clientSocket;
    String hostAdd;
    public InputStream inputStream;
    Socket socket;
    ServerSocket serverSocket;
    public OutputStream outputStream;
    ProgressBar progressBar;
    WifiP2pInfo info1;
    int PORT;

    public final static UUID HM_RX_TX =
            UUID.fromString(SampleGattAttributes.HM_RX_TX);

    public final String LIST_NAME = "NAME";
    public final String LIST_UUID = "UUID";
    public Devices() {
        // Required empty public constructor
    }

    public final ServiceConnection mServiceConnection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((Bluetoothservice.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.v("check", "Unable to initialize Bluetooth");
                return;
            }
            // Automatically connects to the device upon successful start-up initialization.
            try {
                mBluetoothLeService.connect(deviceAddress);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.d("check1", e.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    public final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                final String action = intent.getAction();
                if (Bluetoothservice.ACTION_GATT_CONNECTED.equals(action)) {
                    mConnected = true;
//                Toast.makeText(mainActivity, "Connected", Toast.LENGTH_SHORT).show();
                } else if (Bluetoothservice.ACTION_GATT_DISCONNECTED.equals(action)) {
                    mConnected = false;
//                Toast.makeText(mainActivity, "DisConnected", Toast.LENGTH_SHORT).show();
                } else if (Bluetoothservice.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    Log.i("Swapnil", "reached");
                    // Show all the supported services and characteristics on the user interface.
                    displayGattServices(mBluetoothLeService.getSupportedGattServices());
                    if (characteristicTX == null) {
                        Log.i("Sw", "Its not nullllllll");
//                    makeChange();
                    }
                } else if (Bluetoothservice.ACTION_DATA_AVAILABLE.equals(action)) {
                    Toast.makeText(mainActivity, "Data : ", Toast.LENGTH_SHORT).show();
                }
            }catch (Throwable e){
                e.printStackTrace();
                Log.d("sus",e.toString());
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            View view = inflater.inflate(R.layout.fragment_devices, container, false);
            mainActivity = (MainActivity) getActivity();
            on = view.findViewById(R.id.on);
            off = view.findViewById(R.id.off);
            data = view.findViewById(R.id.data);
            cancel = view.findViewById(R.id.cancel);
            progressBar = view.findViewById(R.id.pBar);
            setHasOptionsMenu(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(mainActivity.getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            }
            deviceName = DeviceControl.Name;
            deviceAddress = DeviceControl.address;
            Intent gattServiceIntent = new Intent(getActivity(), Bluetoothservice.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getActivity()).bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
            }

            wifiP2pManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
            channel = wifiP2pManager.initialize(mainActivity, mainActivity.getMainLooper(), null);

            receiver = new WiFiReceiver(wifiP2pManager, channel, this);
            intentFilter = new IntentFilter();

            intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
            Random random = new Random();
            PORT = random.nextInt(899) + 8000;
            try
            {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = DeviceControl.device.deviceAddress;
                wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(mainActivity, "Connected to : "+DeviceControl.device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
//                        Toast.makeText(mainActivity, "Not connected", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.d("check2", e.toString());
            }

            bundle = getArguments();
            if(bundle != null)
            {
                title = bundle.getString("device");
                mainActivity.getSupportActionBar().setTitle(title);
            }

            if(!DeviceControl.isBluetooth)
            {
                progressBar.setVisibility(View.VISIBLE);
                on.setVisibility(View.INVISIBLE);
                off.setVisibility(View.INVISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        on.setVisibility(View.INVISIBLE);
                        off.setVisibility(View.VISIBLE);
                    }
                }, 4000);
            }
            mainActivity.getSupportActionBar().setTitle(title);
            if(DeviceControl.isBluetooth) {

                off.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onClick(View v) {
                        try {
                            off.setVisibility(View.INVISIBLE);
                            on.setVisibility(View.VISIBLE);
                            str = "1";//HASH DEFINE FOR ON AND OFF
                            makeChange();
                        }catch (Throwable e){
                            e.printStackTrace();
                            Log.d("sus",e.toString());
                        }
                    }
                });

                on.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onClick(View v) {
                        try {
                            on.setVisibility(View.INVISIBLE);
                            off.setVisibility(View.VISIBLE);
                            str = "0";
                            makeChange();
                        }catch (Throwable e){
                            e.printStackTrace();
                            Log.d("sus",e.toString());
                        }
                    }
                });
            }
            else
            {
                off.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onClick(View v) {

                        try {
                            off.setVisibility(View.INVISIBLE);
                            on.setVisibility(View.VISIBLE);
                            str = "1";//HASH DEFINE FOR ON AND OFF
                            if(str != null) {
                                task = new Task(str.getBytes());
                                task.start();
                            }
                        }
                        catch (Throwable e)
                        {
                            e.printStackTrace();
                            Log.d("check3", e.toString());
                        }
                    }
                });

                on.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onClick(View v) {

                        try {
                            off.setVisibility(View.VISIBLE);
                            on.setVisibility(View.INVISIBLE);
                            str = "0";//HASH DEFINE FOR ON AND OFF
                            Log.d("joan", str);

                            if(str != null) {
                                task = new Task(str.getBytes());
                                Log.d("joan", task == null?"null":"no");
                                task.start();
                            }
                        }
                        catch (Throwable e)
                        {
                            e.printStackTrace();
                            Log.d("check4", e.toString());
                        }
                    }
                });
            }

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(socket != null && serverSocket != null && clientSocket != null
                                && outputStream != null && inputStream != null) {
                            if(socket.isConnected()) {
                                socket.close();
                            }
//                            socket = null;
                            Log.d("joan", "socket null");

                            serverSocket.close();
                            serverSocket = null;
                            Log.d("joan", "server socket null");

                            clientSocket.close();
                            clientSocket = null;
                            Log.d("joan", "client socket null");

//                            outputStream.flush();
//                            outputStream.close();
//                            Log.d("joan", "op close");
//
//                            inputStream.close();
//                            Log.d("joan", "ip close");

                        }
                        disconnect();
//                        Task task = new Task();
//                        task.stop();
                        Log.d("joan", "disconnect called");
                        mainActivity.getSupportFragmentManager().popBackStack("deviceControl", 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return view;

        }
        catch (OutOfMemoryError e)
        {
            Toast.makeText(mainActivity, "Running out of memory", Toast.LENGTH_SHORT).show();
            Log.d("devices", e.toString());
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            Log.d("exception", e.toString());
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onResume() {
        super.onResume();
        try {
            mainActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            mainActivity.registerReceiver(receiver, intentFilter);
            if (mBluetoothLeService != null) {
                final boolean result = mBluetoothLeService.connect(deviceAddress);
                Log.v("check", "Connect request result=" + result);
            }
        }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        try {
            mainActivity.unregisterReceiver(mGattUpdateReceiver);
            mainActivity.unregisterReceiver(receiver);
        }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            if(socket != null && serverSocket != null && clientSocket != null
                    && outputStream != null && inputStream != null) {
                socket.close();
                serverSocket.close();
                clientSocket.close();
//                outputStream.close();
//                inputStream.close();
            }
            mainActivity.getSupportFragmentManager().popBackStack("deviceControl", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mainActivity.unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
    }
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId())
//        {
//            case android.R.id.home:
//                getFragmentManager().popBackStack();
//                return false;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void displayGattServices(List<BluetoothGattService> gattServices) {
        try {
        if (gattServices == null) {
            Log.i("Sw","Services not availabele");
            return;
        }
        String uuid = null;
        String unknownServiceString = "Unknown Service";
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            try {
                HashMap<String, String> currentServiceData = new HashMap<String, String>();
                uuid = gattService.getUuid().toString();
                Log.i("Sw ", "UUID: " + uuid);
                currentServiceData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));

                // If the service exists for HM 10 Serial, say so.
                if (SampleGattAttributes.lookup(uuid, unknownServiceString) == "HM 10 Serial") {
//                    Toast.makeText(mainActivity, "Serial Present", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(mainActivity, "Serial Absent", Toast.LENGTH_SHORT).show();
                }
                currentServiceData.put(LIST_UUID, uuid);
                gattServiceData.add(currentServiceData);

                // get characteristic when UUID matches RX/TX UUID
                characteristicTX = gattService.getCharacteristic(Bluetoothservice.UUID_HM_RX_TX);
                characteristicRX = gattService.getCharacteristic(Bluetoothservice.UUID_HM_RX_TX);

                Log.i("Sw", characteristicTX == null ? "Itsnull" : "wsbhd");

////            if(characteristicTX.getProperties()& BluetoothGattCharacteristic.PROPERTY_WRITE)
//                if (((characteristicTX.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE ) {
//                    // writing characteristic functions
//                    mWriteCharacteristic = characteristic;
//                }
                Log.i("Sw", "About to call make change");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.d("check5", e.toString());
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.d("check", e.toString());
            }
//            makeChange();
        }
        }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void makeChange() {
//        String str = RGBFrame[0] + "," + RGBFrame[1] + "," + RGBFrame[2] + "\n";


        Log.v("check", "Sending result=" + str);
        final byte[] tx = str.getBytes();
        if(mConnected) {
            try {
                Log.i("Sw","mConnected");
                characteristicTX.setValue(tx);
                mBluetoothLeService.writeCharacteristic(characteristicTX);
                mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
                Toast.makeText(mainActivity, "sent", Toast.LENGTH_SHORT).show();
                characteristicRX.getValue();
                mBluetoothLeService.readCharacteristic(characteristicRX);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.d("check", e.toString());
            }
        }
    }
    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Bluetoothservice.ACTION_GATT_CONNECTED);
        intentFilter.addAction(Bluetoothservice.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(Bluetoothservice.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(Bluetoothservice.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
//        bundle.clear();
    }

    public class ServerClass extends Thread
    {
        @Override
        public void run() {
//            super.run();
            try {
                serverSocket = new ServerSocket(8888);
                Log.d("joan", "server class called");

                serverSocket.setReuseAddress(true);
//                serverSocket.bind(new InetSocketAddress(PORT));
                Log.d("joan", "reuse address");

                socket = serverSocket.accept();
                Log.d("joan", "socket accept");

                task = new Task(socket);
                Log.d("joan", "send task obj");

                task.start();
                Log.d("joan", "send task start");


            } catch (IOException e) {
                e.printStackTrace();
                Log.d("check", e.toString());
                Log.d("joan", "accept failed");

            }
        }
    }

    public class Task extends Thread
    {
        public Socket socket;

        byte[] bytes;
        int len;
        public Task(Socket skt){
            socket = skt;
            Log.d("check", "socket constructor");

            try {
                inputStream = socket.getInputStream();
                Log.d("check", "socket ip stream");

                outputStream = socket.getOutputStream();
                Log.d("check", "socket op stream");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public Task()
        {

        }

        public Task(byte[] bytes)
        {
            this.bytes = bytes;
        }

        @Override
        public void run() {
//            super.run();
            Log.d("check", "socket run");

            byte[] buffer = new byte[1024];
            Log.d("check", "socket buffer");

            int bytes1;

            while (socket != null)
            {
                try {
                    bytes1 = inputStream.read(buffer);
                    final int finalBytes = bytes1;
                    Log.d("check", "socket read buffer");
                    if(bytes1 > 0)
                    {
                        handler.obtainMessage(MESSAGE_READ, bytes1, -1, buffer).sendToTarget();
                        Log.d("check", "socket handler call");
                    }
                    else {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("check", e.toString());
                }

            }
            try {
                if(bytes != null && outputStream != null) {
                    outputStream.write(bytes);
                    outputStream.flush();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mainActivity, "sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("check", "socket op stream write");
                }
                else {
                    Log.d("check", "output stream null");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("check", e.toString());
                Log.d("check", "write");
            }
            finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                            socket = null;
                            Log.d("joan", "socket closed");
                        } catch (IOException e) {
                            //catch logic
                            e.printStackTrace();
                            Log.d("joan", e.toString());
                        }
                    }
                }
            }
        }
    }

    public class ClientClass extends Thread
    {

        public ClientClass(InetAddress hostAddress)
        {
            hostAdd = hostAddress.getHostAddress();
            Log.d("check","host address"+hostAdd);


            Log.d("check", "Socket created");
        }

        @Override
        public void run() {
//            super.run();
            try {
                clientSocket = new Socket();
                Log.d("check", "client class executed");
                clientSocket.bind(null);
                clientSocket.connect(new InetSocketAddress(hostAdd, 8888), 30000);
                Log.d("check", clientSocket == null?"null":"no");
                task = new Task(clientSocket);
                Log.d("check", "send task object");
                task.start();
                Log.d("check", "send task start");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("check", e.toString());
            }
        }
    }

    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_READ:
                    try {
                        byte[] readBuff = (byte[]) msg.obj;
                        String tempMsg = new String(readBuff, 0, msg.arg1);
                        data.setText("Data : "+tempMsg);
                        Toast.makeText(mainActivity, tempMsg, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                        Log.d("check", e.toString());
                    }
            }
            return true;
        }
    });

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            try {
                info1 = info;
                InetAddress groupOwnerAddress = info.groupOwnerAddress;
                Log.d("check", groupOwnerAddress+"group owner address");
                if(info.groupFormed && info.isGroupOwner)
                {
                    try {
                    serverClass = new ServerClass();
                    serverClass.start();
                    }
                    catch (Throwable e)
                    {
                        Log.d("check", e.toString());
                        Log.d("joan", "server class not executed");

                    }
                }
                else if(info.groupFormed)
                {
                    try {
                        clientClass = new ClientClass(groupOwnerAddress);
                        clientClass.start();
                    }
                    catch (Throwable e)
                    {
                        Log.d("check", e.toString());
                    }
                }
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.d("check", e.toString());
            }
        }
    };
   public void disconnect()
   {
       Log.d("joan", "disconnect entered");

       if(wifiP2pManager != null && channel != null)
       {
           Log.d("joan", "manager and channel not null");

           wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
               @Override
               public void onGroupInfoAvailable(WifiP2pGroup group) {
                   Log.d("joan", "group info available");

                   if (info1 != null && wifiP2pManager != null && channel != null && info1.isGroupOwner)//&& info1.groupFormed && info1.isGroupOwner )
                   {
                       Log.d("joan", "nothing is null");

                       wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                           @Override
                           public void onSuccess() {
                               Toast.makeText(mainActivity, "device disconnected client successfully", Toast.LENGTH_SHORT).show();
                           }

                           @Override
                           public void onFailure(int reason) {
                               Toast.makeText(mainActivity, "device disconnecting client failed", Toast.LENGTH_SHORT).show();

                           }
                       });
                   }
//                   if( info1 != null && wifiP2pManager != null && channel != null && info1.isGroupOwner )//&& info1.groupFormed && info1.isGroupOwner )
//                   {
//                       Log.d("joan", "nothing is null");
//
//                       wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
//                           @Override
//                           public void onSuccess() {
//                               Toast.makeText(mainActivity, "device disconnected server successfully", Toast.LENGTH_SHORT).show();
//                           }
//
//                           @Override
//                           public void onFailure(int reason) {
//                               Toast.makeText(mainActivity, "device disconnecting server failed", Toast.LENGTH_SHORT).show();
//
//                           }
//                       });
//                   }

//               }
               }
           });
       }
   }
}
