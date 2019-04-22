package redeem.com.autozon;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceInfo extends Fragment implements Thread.UncaughtExceptionHandler {

    RecyclerView recyclerView;
    CustomAdapter customAdapter;
    GridLayoutManager manager;
    MainActivity mainActivity;
    ArrayList<CatalogBean> arrayList;
    ProgressBar progressBar;
    Task task;
    Button add;
    public static final int PICK_IMAGES = 1;
    ArrayList<Uri> mArrayUri=new ArrayList<Uri>();
    FirebaseDatabase database;
    DatabaseReference myRef, updateRef;
    Register register;
    ArrayList<String> image = new ArrayList<String>();
    Bitmap bitmap;
    LoginPage loginPage;
    static String productName;



    public class Task extends AsyncTask<DataSnapshot, Void, ArrayList> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected ArrayList doInBackground(DataSnapshot... dataSnapshots) {
            try {
                for (DataSnapshot snapshot : MainActivity.dataSnapshotOpen.getChildren()) {
                    if (snapshot.getKey().equals("catalog_mapping")) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String imageDetails = (String) snapshot1.child("urlImageDetails").getValue();
                            String imageName = (String) snapshot1.child("urlImageName").getValue();
                            String name = (String) snapshot1.child("name").getValue();
                            String link = (String) snapshot1.child("link").getValue();
                            arrayList.add(new CatalogBean(name, imageName, imageDetails, link));
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
            progressBar.setVisibility(View.GONE);
            customAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(mainActivity, "Loading..", Toast.LENGTH_SHORT).show();
        }
    }
}

    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        Log.d("uncaught exception", e.toString());

    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>
    {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.catalog_list_row, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        {

            if(arrayList != null) {
                try {
                    CatalogBean catalogBean = arrayList.get(position);
                    final String name = catalogBean.getName();
                    final String imageName = catalogBean.getImageName();
                    final String imageDescription = catalogBean.getDescription();
                    final String link = catalogBean.getLink();
                    if(!imageName.contains("https:"))
                    {
                        Bitmap photo = loginPage.getBitmapFromString(imageName);
                        holder.imageView.setImageBitmap(photo);
                    }else {

                        Glide.with(getActivity())
                                .load(imageName)
                                .override(700, 609)
                                .fitCenter()
                                .into(holder.imageView);

                    }
                    if(MainActivity.account.equalsIgnoreCase("user")) {
                        holder.deviceName.setText("Book");
                    }
                    else if(MainActivity.account.equalsIgnoreCase("worker") || MainActivity.account.equalsIgnoreCase("admin"))
                    {
                        holder.deviceName.setVisibility(View.GONE);
                    }
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mainActivity.loadCatalog(name, imageName, imageDescription, link);
                        }
                    });
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

        }

        @Override
        public int getItemCount() {
            if(arrayList != null) {
                return arrayList.size();
            }
            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            ImageView imageView;
            CardView cardView;
            TextView deviceName;
            public ViewHolder(View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.image);
                cardView = itemView.findViewById(R.id.card1);
                deviceName = itemView.findViewById(R.id.name);
            }
        }
    }
    public DeviceInfo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_info, container, false);
        recyclerView = view.findViewById(R.id.recycler1);
        customAdapter = new CustomAdapter();
        recyclerView.setAdapter(customAdapter);
        add = view.findViewById(R.id.add);
        register = new Register();
        loginPage = new LoginPage();
        if(MainActivity.account.equalsIgnoreCase("admin"))
        {
            add.setVisibility(View.VISIBLE);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGES);

                }
            });
        }
        else
        {
            add.setVisibility(View.GONE);
        }
        mainActivity = (MainActivity) getActivity();
        manager = new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mainActivity.getSupportActionBar()).show();
            mainActivity.getSupportActionBar().setTitle("Products");
        }
        arrayList = new ArrayList<>();
        recyclerView.setLayoutManager(manager);
        progressBar = view.findViewById(R.id.spinner);
        task = new Task();
        Thread.setDefaultUncaughtExceptionHandler(this);

        setHasOptionsMenu(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mainActivity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }

        if(MainActivity.dataSnapshotOpen == null)
        {
            while (mainActivity.reentrantLock.tryLock()) {
                Toast.makeText(mainActivity, "Loading catalog", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.VISIBLE);
            }
            task.execute(MainActivity.dataSnapshotOpen);

        }
        else
        {
//            Toast.makeText(mainActivity, "Loading catalog", Toast.LENGTH_LONG).show();
            task.execute(MainActivity.dataSnapshotOpen);
        }
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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if(requestCode==PICK_IMAGES){

            if(resultCode==RESULT_OK){
                //data.getParcelableArrayExtra(name);
                //If Single image selected then it will fetch from Gallery
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                }else{
                    if(data.getClipData()!=null){
                        ClipData mClipData=data.getClipData();
                        for(int i=0;i<mClipData.getItemCount();i++){

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
//                            mArrayUri.add(uri);
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(),uri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            try {
                                String im = register.getStringFromBitmap(bitmap);
                                image.add(im);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                        }
                        Log.v("LOG_TAG", "Selected Images"+ mArrayUri.size());
                    }
                }
                mainActivity.loadProductDialog(DeviceInfo.this);

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public void doTask()
    {
        try {
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();
            updateRef = myRef.child("catalog_mapping").push();
            Map<String, Object> updates = new HashMap<>();
//                updates.put("nickname", "Amazing Grace");
            updates.put("urlImageName", image.get(0));
            updates.put("urlImageDetails", image.get(1));
            updates.put("link", " ");
            updates.put("name", productName);
            updateRef.updateChildren(updates);
        }
        catch (Throwable e)
        {
            Log.d("joan", e.toString());
        }
    }
}
