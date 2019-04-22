package redeem.com.autozon;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class Address extends Fragment
{
    Button sub,can;
    MainActivity mainActivity;
    EditText pin,st,addr,town,city;
    static String address;

    public Address() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_address, container, false);
        pin = v.findViewById(R.id.pinCode);
        st = v.findViewById(R.id.state);
        addr = v.findViewById(R.id.addres);
        town = v.findViewById(R.id.town);
        city = v.findViewById(R.id.city);

        sub = v.findViewById(R.id.save);
        can = v.findViewById(R.id.cancel);

        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(mainActivity.getSupportActionBar()).setTitle("Add new address");
                mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        setHasOptionsMenu(true);
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pIn = pin.getText().toString();
                String State = st.getText().toString();
                String Add = addr.getText().toString();
                String tn = town.getText().toString();
                String City = city.getText().toString();
                if(pIn.equals("") || State.equals("") || Add.equals("") || tn.equals("") || City.equals(""))
                {
                    Toast.makeText(getActivity(), "Fields can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pIn.length() < 3)
                {
                    Toast.makeText(getActivity(), "Invalid pincode", Toast.LENGTH_SHORT).show();
                    return;
                }
                address = Add+","+"\n"+tn+","+"\n"+City+","+"\n"+State+","+"\n"+pIn;
                MainActivity.flag = 1;
                if(mainActivity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    mainActivity.getSupportFragmentManager().popBackStack();
                }
                pin.setText("");
                st.setText("");
                addr.setText("");
                town.setText("");
                city.setText("");
//                mainActivity.loadServiceComplaint1();
            }
        });

        can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(mainActivity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    mainActivity.getSupportFragmentManager().popBackStack();
                }
            }
        });
        return v;
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
