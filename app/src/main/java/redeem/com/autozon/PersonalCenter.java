package redeem.com.autozon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalCenter extends Fragment
{
    CircleImageView profilePic;
    TextView name;
    MainActivity mainActivity;
    LoginPage loginPage;
    Bundle bundle;
    CardView deviceManage, services, deviceControl;


    public PersonalCenter() {
        // Required empty public constructor
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_center, container, false);
        profilePic = view.findViewById(R.id.profile_pic);
        name = view.findViewById(R.id.name);
        deviceManage = view.findViewById(R.id.devicemanage);
        services = view.findViewById(R.id.placemanage);
        deviceControl = view.findViewById(R.id.control);
        mainActivity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mainActivity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        mainActivity.getSupportActionBar().show();
        mainActivity.getSupportActionBar().setTitle("Personal Center");

        loginPage = new LoginPage();

        name.setText(MainActivity.login_name);
        if(MainActivity.login_image.length() > 1) {
            Bitmap photo = loginPage.getBitmapFromString(MainActivity.login_image);

            profilePic.setImageBitmap(photo);
        }
        deviceManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.loadDeviceInfo();
            }
        });

        deviceControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.loadDeviceControl();
            }
        });

        services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.loadServices();
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }
}
