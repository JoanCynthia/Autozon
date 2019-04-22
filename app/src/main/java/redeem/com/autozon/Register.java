package redeem.com.autozon;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static redeem.com.autozon.R.drawable.user;


/**
 * A simple {@link Fragment} subclass.
 */
public class Register extends Fragment implements Thread.UncaughtExceptionHandler
{
    MainActivity mainActivity;
    CircleImageView profile_pic;
    EditText userName, email, password, confirmPassword, phoneNumber, captcha, countryCode;
    TextView sendCaptcha, disclaimer;
    Button register;
    Spinner spinner;
    CheckBox checkBox;
    RadioButton admin, worker, user;
    RadioGroup group;
    static Uri imageUri;
    int captcha1;
    CountryCodes countryCodes;
    ProgressBar spinner1;
    boolean checkEmail, checkPw;
    Pattern pEmail, pPw;
    Matcher mEmail, mPw;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String user_name;
    String email1;
    String pw1;
    String p_number;
    TaskRegister taskRegister;
    String account;
    public static final int PICK_IMAGE = 1;
    Bitmap bitmap;
    String image;

    public Register() {
        // Required empty public constructor
    }

    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        Log.d("uncaught exception", e.toString());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mainActivity = (MainActivity) getActivity();
        profile_pic = view.findViewById(R.id.profile_pic);
        userName = view.findViewById(R.id.username);
        countryCode = view.findViewById(R.id.countryCode);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirmPassword);
        spinner = view.findViewById(R.id.spinner);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        sendCaptcha = view.findViewById(R.id.sendCaptcha);
        captcha = view.findViewById(R.id.captcha);
        checkBox = view.findViewById(R.id.checkBox);
        disclaimer = view.findViewById(R.id.disclaimer);
        register = view.findViewById(R.id.register);
        countryCodes = new CountryCodes(getActivity());
        spinner.setAdapter( countryCodes );
        spinner1 = view.findViewById(R.id.spinner1);
        group = view.findViewById(R.id.group);
        admin = (RadioButton)view.findViewById(R.id.radio1);
        worker =(RadioButton) view.findViewById(R.id.radio2);
        user = view.findViewById(R.id.radio3);
        mainActivity.getSupportActionBar().show();
        mainActivity.getSupportActionBar().setTitle("Register");
        Thread.setDefaultUncaughtExceptionHandler(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intentPickImage = new Intent();
                    intentPickImage.setType("image/*");
                    intentPickImage.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intentPickImage, "Select Picture"), PICK_IMAGE);
                }
                catch (Exception e)
                {
                    Log.d("pic", e.toString());
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
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String code = countryCodes.getCode(position);
                countryCode.setText(code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sendCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String c_code = countryCode.getText().toString().trim();
                final String phoneNumber1 = phoneNumber.getText().toString().trim();
                String finalPhoneNumber = c_code+phoneNumber1;

                if(phoneNumber1.equals(""))
                {
                    Toast.makeText(mainActivity, "Enter phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(c_code.equals(""))
                {
                    Toast.makeText(mainActivity, "Select country code", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(phoneNumber1.length() < 6 || phoneNumber1.length() > 13)
                {
                    Toast.makeText(getActivity(), "Enter valid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                captcha1 = (int) (Math.random()*10000);
                try {
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(finalPhoneNumber, null, ""+captcha1, null, null);
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

                new CountDownTimer(5000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        sendCaptcha.setText("" + millisUntilFinished / 1000);
                        spinner1.setVisibility(View.VISIBLE);
                    }

                    public void onFinish()
                    {
                        spinner1.setVisibility(View.GONE);
                        sendCaptcha.setText("Send Captcha");
                        Toast.makeText(getActivity(), "If you didn't receive captcha" +
                                " please check phone number and try again", Toast.LENGTH_SHORT).show();
                    }
                }.start();
            }
        });
                disclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();

                mainActivity.loadDisclaimer();
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                int id = group.getCheckedRadioButtonId();
                if(id == R.id.radio1)
                {
                    account = "Admin";
                }
                else if(id == R.id.radio2)
                {
                    account = "Worker";
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

                user_name = userName.getText().toString().trim();
                email1 = email.getText().toString().trim();
                pw1 = password.getText().toString().trim();
                String cpw = confirmPassword.getText().toString().trim();
                String c_code = countryCode.getText().toString().trim();
                p_number = phoneNumber.getText().toString().trim();
                String cap = captcha.getText().toString().trim();
//                if(imageUri == null)
//                {
//                    profile_pic.setCircleBackgroundColorResource(R.drawable.user);
//                    Toast.makeText(getActivity(), "Select a profile image", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if(c_code.equals("") || p_number.equals("") || cap.equals("")){

                    Toast.makeText(getActivity(), "Fields cant be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(user_name.equals("") || email1.equals("") || pw1.equals("") || cpw.equals("")){

                    Toast.makeText(getActivity(), "Fields cant be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

                pEmail = Pattern.compile(EMAIL_STRING);

                mEmail = pEmail.matcher(email1);
                checkEmail = mEmail.matches();
                String PW_STRING = "^[ A-Za-z0-9_@!#$%^&*]*$";
                pPw = Pattern.compile(PW_STRING);
                mPw = pPw.matcher(pw1);
                checkPw = mPw.matches();


                if(user_name.length() > 70)
                {
                    Toast.makeText(getActivity(), "User name should not exceed 70 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!checkEmail) {
                    email.setError("Not Valid Email");
                    return;
                }
                if(!checkPw)
                {
                    Toast.makeText(mainActivity, "Invalid password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pw1.length() < 8 || pw1.length() > 20)
                {
                    Toast.makeText(getActivity(), "Password should not be less than 8 or more than 20 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pw1.contains(" "))
                {
                    Toast.makeText(mainActivity, "Password should not contain space", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!pw1.equals(cpw))
                {
                    Toast.makeText(getActivity(), "Invalid Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                int cap1 = Integer.parseInt(cap);
                if(!(cap1==captcha1)){
                    Toast.makeText(getActivity(), "Invalid Captcha", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!checkBox.isChecked()){
                    Toast.makeText(getActivity(), "Select the Checkbox", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(account.equalsIgnoreCase("admin")) {
                    for (DataSnapshot dataSnapshot1 : MainActivity.dataSnapshotOpen.getChildren())
                    {
                        if(dataSnapshot1.getKey().equals("admin")) {
                            for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                                String stored_name = (String) dataSnapshot.getKey();
                                Log.d("key", stored_name);
                                if (user_name.equals(stored_name)) {
                                    Toast.makeText(getActivity(), "Username already exists..Provide some other name", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                    }
                    try {
                        if(bitmap != null) {
                            image = getStringFromBitmap(bitmap);
                        }
                        else {
                            image = "";
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Log.d("exception-encode", e.toString());
                    }

                    AdminRegister adminRegister = new AdminRegister(image, email1, pw1, p_number);
                    Object[] obj = new Object[]{user_name, adminRegister, 0};
                    taskRegister = new TaskRegister();
                    taskRegister.execute(obj);

                }
                else if(account.equalsIgnoreCase("worker"))
                {
                    mainActivity.loadAdminDialog(Register.this);
                }
                else if(account.equalsIgnoreCase("user"))
                {
                    for (DataSnapshot dataSnapshot1 : MainActivity.dataSnapshotOpen.getChildren())
                    {
                        if(dataSnapshot1.getKey().equals("user")) {
                            for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                                String stored_name = (String) dataSnapshot.getKey();
                                Log.d("key", stored_name);
                                if (user_name.equals(stored_name)) {
                                    Toast.makeText(getActivity(), "Username already exists..Provide some other name", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                    }
                    try {
                        if(bitmap != null) {
                            image = getStringFromBitmap(bitmap);
                        }
                        else {
                            image = "";
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Log.d("exception-encode", e.toString());
                    }

                    AdminRegister adminRegister = new AdminRegister(image, email1, pw1, p_number);
                    Object[] obj = new Object[]{user_name, adminRegister, 2};
                    taskRegister = new TaskRegister();
                    taskRegister.execute(obj);

                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE)
        {
            if(resultCode == RESULT_OK && data != null)
            {
                imageUri = data.getData();//data store in image uri
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(),imageUri);
                    }
                    profile_pic.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    //NEWLY ADDED
                } catch (OutOfMemoryError e){
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
                catch (Throwable e)
                {

                    e.printStackTrace();
                    Log.d("exception", e.toString());
                }
                //HANDLE MEMORY EXCEPTION - DONE
                //OTHER POSSIBLE EXCEPTION - DONE
            }
        }
    }
    public String getStringFromBitmap(Bitmap bitmapPicture) throws UnsupportedEncodingException {

            try {
                final int COMPRESSION_QUALITY = 100;
                String encodedImage;
                ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
                bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                        byteArrayBitmapStream);
                byte[] b = byteArrayBitmapStream.toByteArray();
                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                //WRITE EXCEPTION HANDLING FOR MEMORY ISSUES - DONE
                return encodedImage;
            }
            catch (OutOfMemoryError e){
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.d("exception", e.toString());
            }
            return null;
    }

    public void doTask(){
        String user_name = userName.getText().toString().trim();
        for (DataSnapshot dataSnapshot1 : MainActivity.dataSnapshotOpen.getChildren()) {
            if(dataSnapshot1.getKey().equals("worker")) {
                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                    String stored_name = dataSnapshot.getKey();
                    if (user_name.equals(stored_name))
                    {
                        Toast.makeText(getActivity(), "Username already exists..Provide some other name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

        }

        try {
            if(bitmap != null) {
                image = getStringFromBitmap(bitmap);
            }
            else {
                image = "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d("exception-encode", e.toString());
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
        WorkerRegister workerRegister = new WorkerRegister(image, email1, pw1, p_number, MainActivity.adminPhone);
        Object[] obj = new Object[]{user_name, workerRegister, 1};
        taskRegister = new TaskRegister();
        taskRegister.execute(obj);

    }

    public class TaskRegister extends AsyncTask<Object, Void, MainActivity>
    {
        @Override
        protected MainActivity doInBackground(Object[] objects) {

            try {
                String user_name = (String) objects[0];
                int isAdmin = (int) objects[2];
                if(isAdmin == 0) {
                    AdminRegister adminRegister = (AdminRegister) objects[1];
                    myRef.child("admin").child(user_name + "").setValue(adminRegister);
                }
                else if(isAdmin == 1)
                {
                    WorkerRegister workerRegister = (WorkerRegister) objects[1];
                    myRef.child("worker").child(user_name+"").setValue(workerRegister);
                }
                else if(isAdmin == 2)
                {
                    AdminRegister adminRegister = (AdminRegister) objects[1];
                    myRef.child("user").child(user_name + "").setValue(adminRegister);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.d("exception", e.toString());
            }
            catch (OutOfMemoryError e){
                e.printStackTrace();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.d("exception", e.toString());
            }
            return mainActivity;
        }

        @Override
        protected void onPostExecute(MainActivity mainActivity) {
            super.onPostExecute(mainActivity);
            mainActivity.loadWarning();
            userName.setText("");
            email.setText("");
            password.setText("");
            confirmPassword.setText("");
            phoneNumber.setText("");
            captcha.setText("");
            countryCode.setText("");
            userName.requestFocus();

        }
    }
}
