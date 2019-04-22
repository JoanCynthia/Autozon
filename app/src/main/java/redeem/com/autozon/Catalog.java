package redeem.com.autozon;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * A simple {@link Fragment} subclass.
 */
public class Catalog extends Fragment
{
    ImageView product, description;
    Button video;
    MainActivity mainActivity;
    String link;
    LoginPage loginPage;

    public Catalog() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);
        product = view.findViewById(R.id.image);
        description = view.findViewById(R.id.image1);
        video = view.findViewById(R.id.video);
        mainActivity = (MainActivity) getActivity();
        loginPage = new LoginPage();
        if (mainActivity != null) {
            mainActivity.getSupportActionBar().show();
            mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        try {
        setHasOptionsMenu(true);
            Bundle bundle = getArguments();
            if(bundle != null)
            {
                String name = bundle.getString("name");
                String product1 = bundle.getString("imageName");
                String image = bundle.getString("description");
                link = bundle.getString("link");
                mainActivity.getSupportActionBar().setTitle(name);
                if(!product1.contains("https:"))
                {
                    Bitmap photo = loginPage.getBitmapFromString(product1);
                    product.setImageBitmap(photo);
                }else {
                    Glide.with(getActivity()).load(product1).centerCrop()
                            .fitCenter().into(product);
                }
                if(!image.contains("https:"))
                {
                    Bitmap desc = loginPage.getBitmapFromString(image);
                    description.setImageBitmap(desc);
                }
                else {
                    Glide.with(getActivity()).load(image).centerCrop()
                            .fitCenter().into(description);
                }
                PhotoViewAttacher pAttacher;
                pAttacher = new PhotoViewAttacher(product);
                pAttacher.update();
                pAttacher = new PhotoViewAttacher(description);
                pAttacher.update();

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

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.loadVideo(link);
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
}
