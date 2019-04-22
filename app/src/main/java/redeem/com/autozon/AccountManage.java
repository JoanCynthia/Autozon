package redeem.com.autozon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static redeem.com.autozon.MainActivity.account;
import static redeem.com.autozon.MainActivity.login_image;
import static redeem.com.autozon.MainActivity.login_name;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountManage extends Fragment
{
    CircleImageView imageView;
    TextView changePhoneNumber;
    TextView changePassword;
    TextView changeEmail;
    MainActivity mainActivity;

    String item;
    static Uri imageUri;
    public static final int PICK_IMAGE = 1;
    Bitmap bitmap;
    String image;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    LoginPage loginPage;

    public AccountManage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_account_manage, container, false );
        imageView = view.findViewById( R.id.profile_pic );
//        button = (Button) findViewById( R.id.idBtnChangeImage );
        changePhoneNumber=view.findViewById( R.id.change_phone_num );
        loginPage = new LoginPage();
        mainActivity= (MainActivity) getActivity();
        setHasOptionsMenu(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mainActivity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        mainActivity.getSupportActionBar().show();
        mainActivity.getSupportActionBar().setTitle("My Account");
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        if(MainActivity.login_image.length() > 1) {
            Bitmap photo = loginPage.getBitmapFromString(MainActivity.login_image);

            imageView.setImageBitmap(photo);
        }
        changePhoneNumber.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item="Phone Number";
                mainActivity.loadChangeDetails(item);
            }
        } );

        imageView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intentPickImage = new Intent();
                    intentPickImage.setType("image/*");
                    intentPickImage.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intentPickImage, "Select Picture"), PICK_IMAGE);
                }
                catch (Exception e){

                    Log.d("pic", e.toString());
                }

                catch (OutOfMemoryError e){

                    e.printStackTrace();
                    Log.d("exception", e.toString());

                }

                catch (Throwable e){
                    e.printStackTrace();
                    Log.d("exception", e.toString());
                }
            }
        });

        changeEmail=view.findViewById( R.id.change_email );
        mainActivity= (MainActivity) getActivity();
        changeEmail.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item="Email";
                mainActivity.loadChangeDetails( item );
            }
        } );



        changePassword=view.findViewById( R.id.change_password );
        mainActivity= (MainActivity) getActivity();
        changePassword.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item="Password";
                mainActivity.loadChangeDetails( item );
            }
        } );


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
                    imageView.setImageBitmap(bitmap);
                    image=getStringFromBitmap( bitmap );
                    login_image = image;
                    if (MainActivity.account.equalsIgnoreCase("admin"))
                    {
                        myRef.child("admin").child(login_name).child("image").setValue(image);
                    }
                    else if (MainActivity.account.equalsIgnoreCase("worker"))
                    {
                        myRef.child("worker").child(login_name).child("image").setValue(image);
                    }else if (MainActivity.account.equalsIgnoreCase("user"))
                    {
                        myRef.child("user").child(login_name).child("image").setValue(image);
                    }

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

    private String getStringFromBitmap(Bitmap bitmapPicture) throws UnsupportedEncodingException {

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
