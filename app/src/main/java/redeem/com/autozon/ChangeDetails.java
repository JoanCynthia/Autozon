package redeem.com.autozon;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeDetails extends Fragment
{
    MainActivity mainActivity;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    Button submit;
    Button cancel;
    String item;
    EditText olddetails;
    EditText newdetails;
    String old,newdetails1;
    boolean checkEmail, checkPw;
    Pattern pEmail, pPw;
    Matcher mEmail, mPw;


    public ChangeDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_details, container, false);
        mainActivity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mainActivity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        mainActivity.getSupportActionBar().show();
        mainActivity.getSupportActionBar().setTitle("Change Details");
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        olddetails=view.findViewById( R.id.old_detail );
        newdetails=view.findViewById( R.id.new_detail );
        submit=(Button)view.findViewById( R.id.submit );
        Bundle b=getArguments();
        if(b!=null) {
            item = b.getString( "item" );

            if (item.equalsIgnoreCase( "Phone Number" )) {
                olddetails.setHint( "Old Phone Number" );
                newdetails.setHint( "New Phone Number" );
                submit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        old=olddetails.getText().toString().trim();
                        newdetails1 =newdetails.getText().toString().trim();

                        if(old.equals(""))
                        {
                            Toast.makeText(getActivity(), "Enter old phone number", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        if(newdetails1.equals(""))
                        {
                            Toast.makeText(getActivity(), "Enter new phone number", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(newdetails1.length() < 6 || newdetails1.length() > 13)
                        {
                            Toast.makeText(getActivity(), "Enter valid phone number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (MainActivity.account.equalsIgnoreCase("admin"))
                        {
                            myRef.child("admin").child(MainActivity.login_name).child("phoneNumber").setValue(newdetails1);
                            Toast.makeText(mainActivity, "Phone number changed", Toast.LENGTH_SHORT).show();
                        }else
                        if (MainActivity.account.equalsIgnoreCase("worker"))
                        {
                            myRef.child("worker").child(MainActivity.login_name).child("phoneNumber").setValue(newdetails1);
                            Toast.makeText(mainActivity, "Phone number changed", Toast.LENGTH_SHORT).show();

                        }else

                        if (MainActivity.account.equalsIgnoreCase("user"))
                        {
                            myRef.child("user").child(MainActivity.login_name).child("phoneNumber").setValue(newdetails1);
                            Toast.makeText(mainActivity, "Phone number changed", Toast.LENGTH_SHORT).show();
                        }



                    }
                } );
            } else if (item.equalsIgnoreCase( "Email" )) {
                olddetails.setHint( "Old Email" );
                newdetails.setHint( "New Email" );
                submit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        old=olddetails.getText().toString().trim();
                        newdetails1 =newdetails.getText().toString().trim();

                        if(old.equals(""))
                        {
                            Toast.makeText(getActivity(), "Enter old Email", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(newdetails1.equals(""))
                        {
                            Toast.makeText(getActivity(), "Enter new Email", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

                        pEmail = Pattern.compile(EMAIL_STRING);

                        mEmail = pEmail.matcher(newdetails1);
                        checkEmail = mEmail.matches();
                        if(!checkEmail) {
                            newdetails.setError("Not Valid Email");
                            return;
                        }
                        if (MainActivity.account.equalsIgnoreCase("admin"))
                        {
                            myRef.child("admin").child(MainActivity.login_name).child("email").setValue(newdetails1);
                            Toast.makeText(mainActivity, "Email changed", Toast.LENGTH_SHORT).show();

                        }else
                        if (MainActivity.account.equalsIgnoreCase("worker"))
                        {
                            myRef.child("worker").child(MainActivity.login_name).child("email").setValue(newdetails1);
                            Toast.makeText(mainActivity, "Email changed", Toast.LENGTH_SHORT).show();

                        }else

                        if (MainActivity.account.equalsIgnoreCase("user"))
                        {
                            myRef.child("user").child(MainActivity.login_name).child("email").setValue(newdetails1);
                            Toast.makeText(mainActivity, "Email changed", Toast.LENGTH_SHORT).show();

                        }

                    }
                } );
            } else if (item.equalsIgnoreCase( "Password" )) {
                olddetails.setHint( "Old Password" );
                newdetails.setHint( "New Password" );
                submit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        old=olddetails.getText().toString().trim();
                        newdetails1 =newdetails.getText().toString().trim();

                        if(old.equals(""))
                        {
                            Toast.makeText(getActivity(), "Enter old Password", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(newdetails1.equals(""))
                        {
                            Toast.makeText(getActivity(), "Enter new Password", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(newdetails1.length() < 8 || newdetails1.length() > 20)
                        {
                            Toast.makeText(getActivity(), "Password should not be less than 8 or more than 20 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(newdetails1.contains(" "))
                        {
                            Toast.makeText(mainActivity, "Password should not contain space", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String PW_STRING = "^[ A-Za-z0-9_@!#$%^&*]*$";
                        pPw = Pattern.compile(PW_STRING);
                        mPw = pPw.matcher(newdetails1);
                        checkPw = mPw.matches();
                        if(!checkPw)
                        {
                            Toast.makeText(getActivity(), "Invalid password", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (MainActivity.account.equalsIgnoreCase("admin"))
                        {
                            myRef.child("admin").child(MainActivity.login_name).child("password").setValue(newdetails1);
                            Toast.makeText(mainActivity, "Password changed", Toast.LENGTH_SHORT).show();

                        }else
                        if (MainActivity.account.equalsIgnoreCase("worker"))
                        {
                            myRef.child("worker").child(MainActivity.login_name).child("password").setValue(newdetails1);
                            Toast.makeText(mainActivity, "Password changed", Toast.LENGTH_SHORT).show();

                        }else

                        if (MainActivity.account.equalsIgnoreCase("user"))
                        {
                            myRef.child("user").child(MainActivity.login_name).child("password").setValue(newdetails1);
                            Toast.makeText(mainActivity, "Password changed", Toast.LENGTH_SHORT).show();
                        }

                    }
                } );
            }
        }


        cancel=(Button)view.findViewById( R.id.cancel );
        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();

            }
        } );


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
