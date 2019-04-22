package redeem.com.autozon;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.fabric.sdk.android.Fabric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;

public class MainActivity extends AppCompatActivity implements Thread.UncaughtExceptionHandler
{
    static final int REQUEST_CODE = 3;
    Menu myMenu;
    FloatingActionButton fab;
    static String adminPhone, login_name, login_phone, login_image;
    static String account;
    static String adminName, adminEmail, adminNumber, adminImage;
    static DataSnapshot dataSnapshotOpen;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Task task;
    ReentrantLock reentrantLock;
    public static int flag;

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.d("uncaught-exception", e.toString());

    }

    public void loadVideo(String link)
    {
        Intent intent = new Intent(MainActivity.this, Video.class);
        intent.putExtra("link", link);
        startActivity(intent);
    }

    public void loadForgotFragment()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction= manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ForgotPassword forgotPassword = new ForgotPassword();
        transaction.replace(R.id.container, forgotPassword);
        myMenu.findItem(R.id.action_settings).setVisible(false);
        myMenu.findItem(R.id.user).setVisible(false);
        fab.hide();
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadServiceCompliance()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction= manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ServiceCompliance serviceCompliance = new ServiceCompliance();
        transaction.replace(R.id.container, serviceCompliance);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadServiceReport()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction= manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ServiceReport serviceReport = new ServiceReport();
        transaction.replace(R.id.container, serviceReport);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadServices() 
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction= manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        Services services = new Services();
        transaction.replace(R.id.container, services);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadAddress()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction= manager.beginTransaction();
        Address address = new Address();
        transaction.replace(R.id.container, address);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadTicketDialog(String ticket)
    {
        TicketDialog dialog = new TicketDialog();
        Bundle bundle = new Bundle();
        bundle.putString("ticket", ticket);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), null);
    }

    public void loadProductDialog(DeviceInfo deviceInfo)
    {
       ProductName productName = new ProductName(deviceInfo);
       productName.show(getFragmentManager(), null);
    }

    public void loadChangeDetails(String item)
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction= manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ChangeDetails changeDetails = new ChangeDetails();
        Bundle bundle = new Bundle();
        bundle.putString("item", item);
        changeDetails.setArguments(bundle);
        transaction.replace(R.id.container, changeDetails);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public class Task extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            Process.setThreadPriority(Thread.MAX_PRIORITY);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Log.d("lock", "entered");
                    try {
                        reentrantLock.lock();
                        dataSnapshotOpen = dataSnapshot;
                        reentrantLock.unlock();
                        Log.d("lock", "data obtaining");
                    }
                    catch (Exception exception)
                    {
                        Log.d("lock", exception.toString());
                    }
                    catch (OutOfMemoryError e)
                    {
                        e.printStackTrace();
                        Log.d("exception", e.toString());
                        Log.d("lock", "caught");
                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                        Log.d("exception", e.toString());
                    }
                    finally {
                        Log.d("lock", "released");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("exception-firebase", databaseError.toString());
                }
            });
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();


        Boolean check = isNetworkAvailable();
        if(!check)
        {
            Toast.makeText(this, "Please connect to internet", Toast.LENGTH_SHORT).show();
            finish();
        }
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        catch (RuntimeException exception)
        {
            Log.d("persistence", "checked");
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

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        reentrantLock = new ReentrantLock();
        task = new Task();
        task.execute();
        Thread.setDefaultUncaughtExceptionHandler(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA},REQUEST_CODE);
        }

        getSupportFragmentManager().beginTransaction().add(R.id.container,new Splash()).commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    LoginPage loginPage = new LoginPage();
                    myMenu.findItem(R.id.action_settings).setVisible(false);
                    myMenu.findItem(R.id.user).setVisible(false);
                    transaction.replace(R.id.container, loginPage);
                    transaction.commit();
                    fab.hide();

                }
                catch (Exception e)
                {
                    Log.d("splash", e.toString());

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
        },2000);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }
    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null) {
                if(info.isConnected()==true);
                return true;
            }
        }
        return false;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.menu_main, menu);
         myMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            loadLogin();
            return true;
        }
        else if(id == R.id.user)
        {
            Devices devices = new Devices();
            try {
                if(devices.socket != null && devices.serverSocket != null && Devices.clientSocket != null
                        && devices.outputStream != null &&  devices.inputStream != null) {
                    devices.socket.close();
                    devices.serverSocket.close();
                    Devices.clientSocket.close();
                    devices.outputStream.close();
                    devices.inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            getSupportFragmentManager().popBackStack("personal", 2);
            return true;
        }
        else if(id == R.id.version)
        {
            Version version = new Version();
            version.show(getSupportFragmentManager(), null);
        }
        else if(id == R.id.account)
        {
            loadAccountManage();
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadAccountManage()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        AccountManage accountManage = new AccountManage();
        transaction.replace(R.id.container, accountManage);
        myMenu.findItem(R.id.action_settings).setVisible(true);
        myMenu.findItem(R.id.user).setVisible(true);
        fab.hide();
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadRegister() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        Register register = new Register();
        transaction.replace(R.id.container, register);
        myMenu.findItem(R.id.action_settings).setVisible(false);
        myMenu.findItem(R.id.user).setVisible(false);
        fab.hide();
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void loadDisclaimer()
    {
        Disclaimer disclaimer = new Disclaimer();
        disclaimer.show(getSupportFragmentManager(), null);
    }

    public void loadLogin()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        LoginPage loginPage = new LoginPage();
        transaction.replace(R.id.container, loginPage);
        transaction.addToBackStack("loginPage");
        transaction.commit();
        fab.hide();
    }

    public boolean loadAdminDialog(Register register)
    {
        AdminDialog adminDialog = new AdminDialog(register);
        adminDialog.show(getSupportFragmentManager(), null);
        return true;
    }

    public void loadWarning()
    {
        Warning warning = new Warning();
        warning.show(getSupportFragmentManager(), null);
    }

    public void loadPersonalCentre()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        PersonalCenter personalCenter = new PersonalCenter();
        getSupportActionBar().setTitle("Personal Center");
        getSupportActionBar().show();
        myMenu.findItem(R.id.action_settings).setVisible(true);
        myMenu.findItem(R.id.user).setVisible(true);
        transaction.replace(R.id.container, personalCenter);
        transaction.addToBackStack("personal");
        transaction.commit();
        fab.hide();
    }



    public void loadDeviceInfo()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        DeviceInfo deviceInfo = new DeviceInfo();
        getSupportActionBar().show();
        myMenu.findItem(R.id.action_settings).setVisible(true);
        myMenu.findItem(R.id.user).setVisible(true);
        transaction.replace(R.id.container, deviceInfo);
        transaction.addToBackStack(null);
        transaction.commit();
        fab.hide();
    }

    public void loadCatalog(String name, String imageName, String imageDescription, String link)
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.zoom_enter, R.anim.zoom_exit, R.anim.enter_from_left, R.anim.exit_to_right);
        Catalog catalog = new Catalog();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("imageName", imageName);
        bundle.putString("description", imageDescription);
        bundle.putString("link", link);
        catalog.setArguments(bundle);
        getSupportActionBar().show();
        myMenu.findItem(R.id.action_settings).setVisible(true);
        myMenu.findItem(R.id.user).setVisible(true);
        transaction.replace(R.id.container, catalog);
        transaction.addToBackStack(null);
        transaction.commit();
        fab.hide();
    }

    public void loadDeviceControl()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        DeviceControl deviceControl = new DeviceControl();
        getSupportActionBar().show();
        myMenu.findItem(R.id.action_settings).setVisible(true);
        myMenu.findItem(R.id.user).setVisible(true);
        transaction.replace(R.id.container, deviceControl);
        transaction.addToBackStack(null);
        transaction.commit();
        fab.hide();
    }

    public void loadRouterDetails()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        RouterDetails routerDetails = new RouterDetails();
        getSupportActionBar().show();
        myMenu.findItem(R.id.action_settings).setVisible(true);
        myMenu.findItem(R.id.user).setVisible(true);
        transaction.replace(R.id.container, routerDetails);
        transaction.addToBackStack("deviceControl");
        transaction.commit();
        fab.hide();
    }

    public void loadBluetoothDetails()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        BluetoothDetails bluetoothDetails = new BluetoothDetails();
        getSupportActionBar().show();
        myMenu.findItem(R.id.action_settings).setVisible(true);
        myMenu.findItem(R.id.user).setVisible(true);
        transaction.replace(R.id.container, bluetoothDetails);
        transaction.addToBackStack("deviceControl");
        transaction.commit();
        fab.hide();
    }

    public void loadDevices(String device)
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        Devices devices = new Devices();
        Bundle bundle = new Bundle();
        bundle.putString("device", device);
        devices.setArguments(bundle);
        getSupportActionBar().setTitle(device);
        getSupportActionBar().show();
        myMenu.findItem(R.id.action_settings).setVisible(true);
        myMenu.findItem(R.id.user).setVisible(true);
        transaction.replace(R.id.container, devices);
//        transaction.addToBackStack(null);
        transaction.commit();
        fab.hide();
    }

}
