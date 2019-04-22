package redeem.com.autozon;

import android.content.Context;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouterDetails extends Fragment
{
    TextView macAdd, ipAdd1;
    RadioGroup group;
    RadioButton light, door, shutter, curtain;
    Button control;
    String device;
    MainActivity mainActivity;
    byte[] mac;

    public RouterDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_router_details, container, false);
        ipAdd1 = view.findViewById(R.id.ip);
        macAdd = view.findViewById(R.id.mac);
        mainActivity = (MainActivity) getActivity();

        if (mainActivity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(mainActivity.getSupportActionBar()).show();
                mainActivity.getSupportActionBar().setTitle("Router Details");
            }
        }
        setHasOptionsMenu(true);
        group = view.findViewById(R.id.group);
        light = view.findViewById(R.id.light);
        door = view.findViewById(R.id.door);
        shutter = view.findViewById(R.id.shutter);
        curtain = view.findViewById(R.id.curtain);
        control = view.findViewById(R.id.control);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mainActivity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }

        ipAdd1.setText(DeviceControl.device.deviceName);
        macAdd.setText(DeviceControl.device.deviceAddress);

        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int id = group.getCheckedRadioButtonId();

                    if(id == R.id.light)
                    {
                        device = light.getText().toString();
                        mainActivity.loadDevices(device);
                    }
                    else if(id == R.id.door)
                    {
                        device = door.getText().toString();
                        mainActivity.loadDevices(device);
                    }
                    else if(id == R.id.shutter)
                    {
                        device = shutter.getText().toString();
                        mainActivity.loadDevices(device);
                    }
                    else if(id == R.id.curtain)
                    {
                        device = curtain.getText().toString();
                        mainActivity.loadDevices(device);
                    }
                    else
                    {
                        Toast.makeText(mainActivity, "Select any device", Toast.LENGTH_SHORT).show();
//                        return;
                    }
                }
                catch (OutOfMemoryError e)
                {
                    e.printStackTrace();
                    Log.d("exception", e.toString());
                }
                catch (Exception e)
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
        });

            return view;
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
