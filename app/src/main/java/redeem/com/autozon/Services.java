package redeem.com.autozon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class Services extends Fragment
{
    CardView serviceComplain,serviceReport;
    MainActivity mainActivity;

    public Services() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_services, container, false);
        mainActivity= (MainActivity) getActivity();
        serviceComplain=v.findViewById(R.id.service);
        serviceReport=v.findViewById(R.id.report);
        if(MainActivity.account.equalsIgnoreCase("admin"))
        {
            serviceComplain.setVisibility(View.VISIBLE);
            serviceReport.setVisibility(View.INVISIBLE);
        }
        else if(MainActivity.account.equalsIgnoreCase("worker"))
        {
            serviceComplain.setVisibility(View.INVISIBLE);
            serviceReport.setVisibility(View.VISIBLE);
        }
        else if(MainActivity.account.equalsIgnoreCase("user"))
        {
            serviceComplain.setVisibility(View.VISIBLE);
            serviceReport.setVisibility(View.INVISIBLE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mainActivity.getSupportActionBar()).setTitle("Services");
            mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true);

        serviceComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.loadServiceCompliance();

            }
        });
        serviceReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.loadServiceReport();
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
