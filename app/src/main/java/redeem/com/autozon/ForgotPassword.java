package redeem.com.autozon;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPassword extends Fragment
{
    MainActivity mainActivity;
    EditText otp, newPassword, confirmPassword;
    Button cancel, reset, clear;
    String OTP, newPw, confirmPw;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Pattern pPw;
    Matcher mPw;
    boolean checkPw;

    public ForgotPassword() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.getSupportActionBar().show();
            mainActivity.getSupportActionBar().setTitle("Change Password");
        }
        otp = view.findViewById(R.id.otp);
        newPassword = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirmPassword);
        cancel = view.findViewById(R.id.cancel);
        reset = view.findViewById(R.id.reset);
        clear = view.findViewById(R.id.clear);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //REFRESH THE SAME PAGE AND REMAIN THERE -- DONE
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.loadLogin();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.loadForgotFragment();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OTP = otp.getText().toString().trim();
                newPw = newPassword.getText().toString().trim();
                confirmPw = confirmPassword.getText().toString().trim();

                String PW_STRING = "^[A-Za-z0-9_@!#$%^&*]*$";
                pPw = Pattern.compile(PW_STRING);
                mPw = pPw.matcher(newPw);
                checkPw = mPw.matches();
                if(!checkPw)
                {
                    Toast.makeText(mainActivity, "Invalid password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(OTP.equals(""))
                {
                    Toast.makeText(mainActivity, "Enter OTP", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newPw.equals("") || confirmPassword.equals(""))
                {
                    Toast.makeText(getActivity(), "Field's can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newPw.contains(" "))
                {
                    Toast.makeText(mainActivity, "Password should not contain space", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!OTP.equals(LoginPage.captcha1+""))
                {
                    Toast.makeText(mainActivity, "You are not allowed to reset password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!newPw.equals(confirmPw))
                {
                    Toast.makeText(mainActivity, "Passwords are not matching", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newPw.length() < 8 && confirmPw.length() < 8 || newPw.length() > 20 && confirmPw.length() > 20)
                {
                    Toast.makeText(getActivity(), "Password should not be less than 8 or more than 20 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if(OTP.equals(LoginPage.captcha1+"")) {
                        if (LoginPage.account.equalsIgnoreCase("admin")) {
                            myRef.child("admin").child(LoginPage.user_name_chg_pw + "").child("password").setValue(newPw);
                        } else if(LoginPage.account.equalsIgnoreCase("worker")) {
                            myRef.child("worker").child(LoginPage.user_name_chg_pw + "").child("password").setValue(newPw);
                        }
                        else if(LoginPage.account.equalsIgnoreCase("user")) {
                            myRef.child("user").child(LoginPage.user_name_chg_pw + "").child("password").setValue(newPw);
                        }
                        Toast.makeText(mainActivity, "Password reset successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (OutOfMemoryError e)
                {
                    e.printStackTrace();
                    Log.d("check", e.toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d("check", e.toString());
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                    Log.d("check", e.toString());
                }
            }
        });
        return view;
    }
}
