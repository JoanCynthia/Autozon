package redeem.com.autozon;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.locks.Lock;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginPage extends Fragment implements Thread.UncaughtExceptionHandler {
    ImageView profilePic;
    RadioButton admin, worker, user;
    RadioGroup group;
    EditText userName, login_details, password;
    CheckBox checkBox;
    TextView register, forgotPw;
    Button login;
    MainActivity mainActivity;
    String user_name, login_details1, password1;
    static String account;
    TaskLogin taskLogin;
    DataSnapshot dataSnapshot;
    static int captcha1;
    static String user_name_chg_pw;

    public LoginPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_page, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        profilePic = view.findViewById(R.id.profile_pic);
        group = view.findViewById(R.id.group);
        admin = (RadioButton) view.findViewById(R.id.radio1);
        worker = (RadioButton) view.findViewById(R.id.radio2);
        user = view.findViewById(R.id.user);
        userName = view.findViewById(R.id.username);
        login_details = view.findViewById(R.id.login_details);
        password = view.findViewById(R.id.password);
        register = view.findViewById(R.id.register);
        checkBox=view.findViewById(R.id.showpwd);
        forgotPw = view.findViewById(R.id.forgotPassword);
        login = view.findViewById(R.id.login);
        mainActivity = (MainActivity) getActivity();
        dataSnapshot = MainActivity.dataSnapshotOpen;
        Thread.setDefaultUncaughtExceptionHandler(this);

        setHasOptionsMenu(false);

        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.getSupportActionBar().hide();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClick();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerClick();
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        forgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (userName.getText().toString().equals("")) {
                        Toast.makeText(mainActivity, "Please enter user name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                forgotPwClick();
                }
              catch (Throwable e)
              {
                  Log.d("check", e.toString());
              }
            }
        });
        return view;
    }

    public void forgotPwClick()
    {
        int id = group.getCheckedRadioButtonId();

        if (id == R.id.radio1) {
            account = "Admin";
            MainActivity.account = "Admin";
        } else if (id == R.id.radio2) {
            account = "Worker";
            MainActivity.account = "Worker";
        }
        else if (id == R.id.radio3) {
            account = "User";
            MainActivity.account = "User";
        } else {
            Toast.makeText(getActivity(), "Select what type of user you are", Toast.LENGTH_SHORT).show();
            return;
        }
        user_name_chg_pw = userName.getText().toString();
        try {
            if (MainActivity.dataSnapshotOpen != null) {
                if (account.equalsIgnoreCase("admin")) {
                    try {
                        boolean flag = true;
                        for (DataSnapshot dataSnapshot1 : MainActivity.dataSnapshotOpen.getChildren()) {
                            if (dataSnapshot1.getKey().equals("admin")) {
                                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                                    String stored_name = (String) dataSnapshot.getKey();
                                    if (user_name_chg_pw.equals(stored_name)) {
                                        String login_phone = (String) dataSnapshot.child("phoneNumber").getValue();
                                        try {
                                            flag = false;
                                            captcha1 = (int) (Math.random() * 10000);
                                            SmsManager sms = SmsManager.getDefault();
                                            sms.sendTextMessage(login_phone, null, "" + captcha1, null, null);
                                            Toast.makeText(mainActivity, "You will receive otp shortly", Toast.LENGTH_SHORT).show();
                                            mainActivity.loadForgotFragment();
                                        } catch (Exception e) {
                                            Log.d("sms", e.toString());
                                        } catch (OutOfMemoryError e) {
                                            e.printStackTrace();
                                            Log.d("exception", e.toString());
                                        } catch (Throwable e) {
                                            e.printStackTrace();
                                            Log.d("exception", e.toString());
                                        }
                                    }
                                }
                            }

                        }
                        if(flag)
                        {
                            Toast.makeText(mainActivity, "Check user name and try again", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("exception-login", e.toString());
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                        Log.d("exception", e.toString());
                    } catch (Throwable e) {
                        e.printStackTrace();
                        Log.d("exception", e.toString());
                    }

                }
                else if(account.equalsIgnoreCase("worker")){
                    try {
                        boolean flag = true;
                        for (DataSnapshot dataSnapshot1 : MainActivity.dataSnapshotOpen.getChildren()) {
                            if (dataSnapshot1.getKey().equals("worker")) {
                                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                                    String stored_name = (String) dataSnapshot.getKey();
                                    if (user_name_chg_pw.equals(stored_name)) {
                                        String login_phone = (String) dataSnapshot.child("phoneNumber").getValue();
                                        try {
                                            flag = false;
                                            captcha1 = (int) (Math.random()*10000);
                                            SmsManager sms = SmsManager.getDefault();
                                            sms.sendTextMessage(login_phone, null, ""+captcha1, null, null);
                                            Toast.makeText(mainActivity, "You will receive otp shortly", Toast.LENGTH_SHORT).show();
                                            mainActivity.loadForgotFragment();
                                        }catch (Exception e){
                                            Log.d("sms",e.toString());
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

                            }
                        }
                        if(flag)
                        {
                            Toast.makeText(mainActivity, "Check user name and try again", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        Log.d("exception-login", e.toString());
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
                else if(account.equalsIgnoreCase("user")){
                    try {
                        boolean flag = true;
                        for (DataSnapshot dataSnapshot1 : MainActivity.dataSnapshotOpen.getChildren()) {
                            if (dataSnapshot1.getKey().equals("user")) {
                                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                                    String stored_name = (String) dataSnapshot.getKey();
                                    if (user_name_chg_pw.equals(stored_name)) {
                                        String login_phone = (String) dataSnapshot.child("phoneNumber").getValue();
                                        try {
                                            flag = false;
                                            captcha1 = (int) (Math.random()*10000);
                                            SmsManager sms = SmsManager.getDefault();
                                            sms.sendTextMessage(login_phone, null, ""+captcha1, null, null);
                                            Toast.makeText(mainActivity, "You will receive otp shortly", Toast.LENGTH_SHORT).show();
                                            mainActivity.loadForgotFragment();
                                        }catch (Exception e){
                                            Log.d("sms",e.toString());
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

                            }
                        }
                        if(flag)
                        {
                            Toast.makeText(mainActivity, "Check user name and try again", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        Log.d("exception-login", e.toString());
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

        }
        catch (Throwable e)
        {
            Log.d("check", e.toString());
        }
    }

    public void loginClick() {
        int id = group.getCheckedRadioButtonId();

        if (id == R.id.radio1) {
            account = "Admin";
            MainActivity.account = "Admin";
        } else if (id == R.id.radio2) {
            account = "Worker";
            MainActivity.account = "Worker";
        }
        else if (id == R.id.radio3) {
            account = "User";
            MainActivity.account = "User";
        }else {
            Toast.makeText(getActivity(), "Select what type of user you are", Toast.LENGTH_SHORT).show();
            return;
        }

        user_name = userName.getText().toString().trim();
        login_details1 = login_details.getText().toString().trim();
        password1 = password.getText().toString().trim();
        if (user_name.equals("") || login_details1.equals("") || password1.equals("")) {
            Toast.makeText(mainActivity, "Fields can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(MainActivity.dataSnapshotOpen == null) {
            Log.d("lock", "data null");


            while (mainActivity.reentrantLock.tryLock()) {
                Log.d("lock", "while running");
                continue;
            }
            Log.d("lock", "lock released");
            taskLogin = new TaskLogin();
            taskLogin.execute(MainActivity.dataSnapshotOpen);
        }
        else {
            taskLogin = new TaskLogin();
            taskLogin.execute(MainActivity.dataSnapshotOpen);
        }

//        if(MainActivity.dataSnapshotOpen == null)
//        {
//            Toast.makeText(mainActivity, "loading", Toast.LENGTH_SHORT).show();
////            progressBar.setVisibility(View.VISIBLE);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    taskLogin = new TaskLogin();
//                    taskLogin.execute(MainActivity.dataSnapshotOpen);
//                }
//            }, 10000);
//        }
//        else
//        {
//            taskLogin = new TaskLogin();
//            taskLogin.execute(MainActivity.dataSnapshotOpen);
//        }

    }

    public void registerClick()
    {
        final int id = group.getCheckedRadioButtonId();
        Log.d("radio_id", id+"");
        if(id == R.id.radio1)
        {
            account = "Admin";
            register.setVisibility(View.VISIBLE);
        }
        else if(id == R.id.radio2)
        {
            account = "Worker";
            Toast.makeText(getActivity(), "Only admin can register", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(id == R.id.radio3)
        {
            account = "User";
        }

        else
        {
            Toast.makeText(getActivity(), "Select what type of user you are", Toast.LENGTH_SHORT).show();
            return;
        }

//        if(isAdmin == true)
//        {
            mainActivity.loadRegister();
//        }

    }

    public Bitmap getBitmapFromString(String jsonString) {
        try {
            byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;
        }catch (NumberFormatException exception)
        {
            Log.d("bitmap-exception", exception.toString());
            return null;
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
            Log.d("exception", e.toString());
            return null;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            Log.d("exception", e.toString());
            return null;
        }
     }

    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        Log.d("uncaught exception", e.toString());

    }
   public class TaskLogin extends AsyncTask<DataSnapshot, Void, Object[]>
    {
        @Override
        protected Object[] doInBackground(DataSnapshot... dataSnapshots) {
            Process.setThreadPriority(Thread.MIN_PRIORITY);
            if(dataSnapshots[0] != null) {
                if (account.equalsIgnoreCase("admin")) {
                    try {
                        boolean flag = true;
                        for (DataSnapshot dataSnapshot1 : dataSnapshots[0].getChildren()) {
                            if (dataSnapshot1.getKey().equals("admin")) {
                                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                                    String stored_name = (String) dataSnapshot.getKey();
                                    if (user_name.equals(stored_name)) {
                                        String login_email = (String) dataSnapshot.child("email").getValue();
                                        String login_phone = (String) dataSnapshot.child("phoneNumber").getValue();
                                        String login_password = (String) dataSnapshot.child("password").getValue();
                                        String image = (String) dataSnapshot.child("image").getValue();

                                        MainActivity.adminName = user_name;
                                        MainActivity.adminEmail = login_email;
                                        MainActivity.adminNumber = login_phone;
                                        MainActivity.adminImage = image;

                                        if ((login_details1.equals(login_email) || login_details1.equals(login_phone)) && password1.equals(login_password)) {
                                            flag = false;
                                            publishProgress();
                                            Object[] obj = new Object[]{mainActivity, 0, login_phone, image};
                                            return obj;

                                        } else if ((login_details1.equals(login_email) || login_details1.equals(login_phone)) && !password1.equals(login_password)) {
                                            Object[] obj = new Object[]{null, 1};
                                            return obj;
                                        } else if (!((login_details1.equals(login_email) || login_details1.equals(login_phone))) && password1.equals(login_password)) {
                                            Object[] obj = new Object[]{null, 4};
                                            return obj;
                                        } else if (!((login_details1.equals(login_email) || login_details1.equals(login_phone))) && !password1.equals(login_password)) {
                                            Object[] obj = new Object[]{null, 4};
                                            return obj;
                                        }
                                    }
                                }
                            }
                        }
                        if (flag) {
                            Object[] obj = new Object[]{null, 2};
                            return obj;

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("exception-login", e.toString());
                    }
                    catch (OutOfMemoryError e)
                    {
                        e.printStackTrace();
                        Log.d("exception", e.toString());
                        return null;
                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                        Log.d("exception", e.toString());
                        return null;
                    }

                } else if(account.equalsIgnoreCase("worker")){
                    try {
                        boolean flag = true;
                        for (DataSnapshot dataSnapshot1 : dataSnapshots[0].getChildren()) {
                            if (dataSnapshot1.getKey().equals("worker")) {
                                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                                    String stored_name = (String) dataSnapshot.getKey();
                                    if (user_name.equals(stored_name)) {
                                        String login_email = (String) dataSnapshot.child("email").getValue();
                                        String login_phone = (String) dataSnapshot.child("phoneNumber").getValue();
                                        String login_password = (String) dataSnapshot.child("password").getValue();
                                        String image = (String) dataSnapshot.child("image").getValue();


                                        if ((login_details1.equals(login_email) || login_details1.equals(login_phone)) && password1.equals(login_password)) {
                                            flag = false;
                                            publishProgress();
                                            Object[] obj = new Object[]{mainActivity, 0, login_phone, image};
                                            return obj;
                                        } else if((login_details1.equals(login_email) || login_details1.equals(login_phone)) && !password1.equals(login_password)){
                                            Object[] obj = new Object[]{null, 1};
                                            return obj;
                                        }
                                        else if(!((login_details1.equals(login_email) || login_details1.equals(login_phone))) && password1.equals(login_password))
                                        {
                                            Object[] obj = new Object[]{null, 4};
                                            return obj;
                                        }
                                        else if (!((login_details1.equals(login_email) || login_details1.equals(login_phone))) && !password1.equals(login_password)) {
                                            Object[] obj = new Object[]{null, 4};
                                            return obj;
                                }
                                    }
                                }

                            }
                        }
                        if (flag) {
                            Object[] obj = new Object[]{null, 2};
                            return obj;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("exception-login", e.toString());
                    }
                    catch (OutOfMemoryError e)
                    {
                        e.printStackTrace();
                        Log.d("exception", e.toString());
                        return null;
                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                        Log.d("exception", e.toString());
                        return null;
                    }
                }
                else if(account.equalsIgnoreCase("user")){
                    try {
                        boolean flag = true;
                        for (DataSnapshot dataSnapshot1 : dataSnapshots[0].getChildren()) {
                            if (dataSnapshot1.getKey().equals("user")) {
                                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                                    String stored_name = (String) dataSnapshot.getKey();
                                    if (user_name.equals(stored_name)) {
                                        String login_email = (String) dataSnapshot.child("email").getValue();
                                        String login_phone = (String) dataSnapshot.child("phoneNumber").getValue();
                                        String login_password = (String) dataSnapshot.child("password").getValue();
                                        String image = (String) dataSnapshot.child("image").getValue();


                                        if ((login_details1.equals(login_email) || login_details1.equals(login_phone)) && password1.equals(login_password)) {
                                            flag = false;
                                            publishProgress();
                                            Object[] obj = new Object[]{mainActivity, 0, login_phone, image};
                                            return obj;
                                        } else if((login_details1.equals(login_email) || login_details1.equals(login_phone)) && !password1.equals(login_password)){
                                            Object[] obj = new Object[]{null, 1};
                                            return obj;
                                        }
                                        else if(!((login_details1.equals(login_email) || login_details1.equals(login_phone))) && password1.equals(login_password))
                                        {
                                            Object[] obj = new Object[]{null, 4};
                                            return obj;
                                        }
                                        else if (!((login_details1.equals(login_email) || login_details1.equals(login_phone))) && !password1.equals(login_password)) {
                                            Object[] obj = new Object[]{null, 4};
                                            return obj;
                                        }
                                    }
                                }

                            }
                        }
                        if (flag) {
                            Object[] obj = new Object[]{null, 2};
                            return obj;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("exception-login", e.toString());
                    }
                    catch (OutOfMemoryError e)
                    {
                        e.printStackTrace();
                        Log.d("exception", e.toString());
                        return null;
                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                        Log.d("exception", e.toString());
                        return null;
                    }
                }
            }
            else
            {
             Object[] obj = new Object[]{null, 3};
                return obj;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Object[] objects) {
            super.onPostExecute(objects);
            int task = (int) objects[1];
            if(task == 0)
            {
                MainActivity mainActivity = (MainActivity) objects[0];
                String login_phone = (String) objects[2];
                String image = (String) objects[3];
                MainActivity.login_name = user_name;
                MainActivity.login_phone = login_phone;
                MainActivity.login_image = image;
                mainActivity.loadPersonalCentre();
                userName.setText("");
                login_details.setText("");
                password.setText("");
                userName.requestFocus();
            }
            else if(task == 1)
            {
                Toast.makeText(mainActivity, "Invalid password", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(task == 2)
            {
                Toast.makeText(mainActivity, "Check user name and try again", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(task == 3)
            {
                Toast.makeText(mainActivity, "Request time out...click login again", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(task == 4)
            {
                Toast.makeText(mainActivity, "Invalid phone number or email id", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity.finish();
    }
}
