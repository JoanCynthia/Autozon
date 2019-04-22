package redeem.com.autozon;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceCompliance extends Fragment
{
    CircleImageView circleImageView;
    TextView changeAdd,adD, name, phone;
    RecyclerView recyclerView;
    RadioGroup group;
    RadioButton  repair,install;
    Button cancel,sub;
    CustomAdapter customAdapter;
    ArrayList<String> arrayList, arrayProduct;
    Task task;
    MainActivity mainActivity;
    LinearLayoutManager manager;
    String products, service, address1;
    String ticket_id;
    TaskCompliance taskCompliance;
    FirebaseDatabase database;
    DatabaseReference myRef;
    LoginPage loginPage;
    LinearLayout btnbtnAddAddress;
    LinearLayout address;
    Boolean flag = false;

    public class Task extends AsyncTask<Void, Void, ArrayList>
    {
        @Override
        protected ArrayList doInBackground(Void... voids) {
            try {
                for (DataSnapshot snapshot :MainActivity.dataSnapshotOpen.getChildren()) {
                    if (snapshot.getKey().equals("catalog_mapping")) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String name = (String) snapshot1.child("name").getValue();
                            arrayList.add(name);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.d("exception", e.toString());
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
            return arrayList;

        }
        @Override
        protected void onPostExecute(ArrayList arrayList) {
            super.onPostExecute(arrayList);
            if (arrayList != null) {

                customAdapter.notifyDataSetChanged();
            }
        }
    }

   public void uncaughtException(Thread t,Throwable e){

        Log.d("uncaught exception",e.toString());
   }
    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>
    {
        @Override
        public int getItemCount() {
            if(arrayList != null) {
                return arrayList.size();
            }
            return 0;
        }
        @NonNull
        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.row_product, parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }
        public class ViewHolder extends RecyclerView.ViewHolder
        {
            CheckBox checkDevice;
            public ViewHolder(View itemView) {
                super(itemView);
                checkDevice = itemView.findViewById(R.id.checkDevice);
            }
        }
        @Override
        public void onBindViewHolder(@NonNull final CustomAdapter.ViewHolder holder, final int position)
        {
            try {
                String deviceName = arrayList.get(position);
                holder.checkDevice.setText(deviceName);

                holder.checkDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked)
                        {
                            products = holder.checkDevice.getText().toString();
                            arrayProduct.add(products);
                        }
                        else
                        {
                            products = holder.checkDevice.getText().toString();
                            arrayProduct.remove(products);
                        }
                    }
                });
            }
            catch (Throwable e)
            {
                Log.d("joan", e.toString());
            }
        }
    }

    public ServiceCompliance() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_compliance, container, false);
        btnbtnAddAddress = v.findViewById(R.id.btnAddAddress);
        address=v.findViewById(R.id.address);
        mainActivity= (MainActivity) getActivity();
        cancel=v.findViewById(R.id.cancel);
        sub = v.findViewById(R.id.submit);
        adD = v.findViewById(R.id.add);
        changeAdd = v.findViewById(R.id.changeAddress);
        circleImageView = v.findViewById(R.id.profile_pic);
        name = v.findViewById(R.id.name);
//        phone = v.findViewById(R.id.phone);
        group = v.findViewById(R.id.group);
        repair = v.findViewById(R.id.radio1);
        install = v.findViewById(R.id.radio2);
        loginPage = new LoginPage();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mainActivity.getSupportActionBar()).setTitle("Service Compliance");
        }
        arrayList = new ArrayList<>();
        arrayProduct = new ArrayList<>();
        customAdapter = new CustomAdapter();
        recyclerView = v.findViewById(R.id.row);
        recyclerView.setAdapter(customAdapter);
        manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        setHasOptionsMenu(true);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.d("joan", "snapshot not null");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        setAllValues();
        if(MainActivity.dataSnapshotOpen == null) {
            Log.d("lock", "data null");
            while (mainActivity.reentrantLock.tryLock()) {
                Log.d("lock", "while running");
//                continue;
            }
            Log.d("lock", "lock released");
            task = new Task();
            task.execute();
        }
        else {
            task = new Task();
            task.execute();
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainActivity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    mainActivity.getSupportFragmentManager().popBackStack();
                }

            }
        });
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ADD TRY BLOCK
                try {
                    long time = System.nanoTime();
                    double random = Math.random()*1000;
                    long number = (long) (time * random);
                    ticket_id = (number+"").substring(0, 9);
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                    Log.d("joan", e.toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d("joan", e.toString());
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                    Log.d("joan", e.toString());
                }
                String pro = "";
                if(arrayProduct != null) {
                    for (int i = 0; i < arrayProduct.size(); i++) {
                        pro = pro + arrayProduct.get(i);
                        if (i < arrayProduct.size() - 1) {
                            pro = pro + ", ";
                        }
                    }
                }
                if (pro.equals(""))
                {
                    Toast.makeText(getActivity(), "Select atleast one product", Toast.LENGTH_SHORT).show();
                    return;
                }
                int id = group.getCheckedRadioButtonId();
                switch (id)
                {
                    case R.id.radio1:
                        service = "Product Repair";
                        break;
                    case R.id.radio2:
                        service = install.getText().toString();
                        break;
                        default:
                            Toast.makeText(getActivity(), "Select any one service", Toast.LENGTH_SHORT).show();
                            return;

                }

                try {
                    for (DataSnapshot dataSnapshot1 : MainActivity.dataSnapshotOpen.getChildren())
                    {
                        if(dataSnapshot1.getKey().equals("Service")) {
                            for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {

                                String stored_name = dataSnapshot.getKey();
                                if(MainActivity.login_name.equals(stored_name))
                                {
                                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                    {
                                        String key = snapshot.getKey();
                                        if((ticket_id).equals(key))
                                        {
                                            Toast.makeText(getActivity(), "Order Number already existing.." +
                                                    "Please load the page again", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                }

                            }
                        }
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d("exception", e.toString());
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
                address1 = adD.getText().toString();
                if(address1.equals(""))
                {
                    Toast.makeText(getActivity(), "Add your address", Toast.LENGTH_SHORT).show();
                    return;
                }
                address1 = address1.replaceAll("\n", " ");
                try {
                    //DO IN BACKGROUND
                    Compliance compliance = new Compliance(MainActivity.login_phone, address1);
//                    myRef.child("Service").child(MainActivity.login_name).child("details").setValue(compliance);
                    Compliance compliance1 = new Compliance(ticket_id);
//                    myRef.child("Service").child(MainActivity.login_name).child(ticket_id).setValue(compliance2);
                    Compliance compliance2 = new Compliance(service, pro, "Pending", "");
                    Object[] obj = new Object[]{MainActivity.login_name, compliance, compliance1, compliance2, ticket_id};
                    taskCompliance = new TaskCompliance();
                    taskCompliance.execute(obj);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d("exception", e.toString());
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
        btnbtnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.loadAddress();
            }
        });

        changeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.loadAddress();
            }
        });

        Log.i("sw",MainActivity.flag+"");

        return v;
    }

    private void setAllValues()
    {
        name.setText(MainActivity.login_name);
//        phone.setText(MainActivity.login_phone);
        if(MainActivity.login_image.length() > 1) {
            Bitmap photo = loginPage.getBitmapFromString(MainActivity.login_image);
            circleImageView.setImageBitmap(photo);
        }
        address1 = Address.address;
        if(address1 != null) {
            try {
                address.setVisibility(View.VISIBLE);
                btnbtnAddAddress.setVisibility(View.INVISIBLE);
                adD.setText(address1);
                Log.d("joan", "new address");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.d("joan", e.toString());
            }
        }
        else {
            try {
                btnbtnAddAddress.setVisibility(View.GONE);
                Log.d("joan", "existing address");
                for (DataSnapshot dataSnapshot1 : MainActivity.dataSnapshotOpen.getChildren()) {
                    if (dataSnapshot1.getKey().equals("Service")) {
                        Log.d("joan", "existing address");

                        for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {

                            String stored_name = dataSnapshot.getKey();
                            Log.d("joan", stored_name);

                            if (MainActivity.login_name.equals(stored_name)) {
                                Log.d("joan", "name matched");
                                    flag = true;
                                    address1 = (String) dataSnapshot.child("details").child("address").getValue();
                                    Log.d("joan", address1);
                                    address1 = address1.replaceAll(", ", (","+"\n"));
                                    address.setVisibility(View.VISIBLE);
                                    adD.setText(address1);
                                    Log.d("joan", "check");
                            }
                        }
                    }
                }
                if(!flag)
                {
                    btnbtnAddAddress.setVisibility(View.VISIBLE);
                }
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.d("joan", e.toString());
            }
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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

    public class TaskCompliance extends AsyncTask<Object, Void, MainActivity>
    {
        String ticket;
        @Override
        protected MainActivity doInBackground(Object... objects) {
            try {
                String name = (String) objects[0];
                Compliance compliance = (Compliance) objects[1];
                Compliance compliance1 = (Compliance) objects[2];
                Compliance compliance2 = (Compliance) objects[3];

                ticket = (String) objects[4];
                myRef.child("Service").child(MainActivity.login_name).child("details").setValue(compliance);
                myRef.child("Service").child(MainActivity.login_name).child(ticket_id).setValue(compliance1);
                myRef.child("Service").child(name).child(ticket).setValue(compliance2);

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
            Toast.makeText(mainActivity, "Service compliance ticket raised successfully", Toast.LENGTH_SHORT).show();
            mainActivity.loadTicketDialog(ticket);
        }
    }
}
