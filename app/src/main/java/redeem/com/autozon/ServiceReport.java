package redeem.com.autozon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceReport extends Fragment
{
    CircleImageView circleImageView;
    RadioGroup group1, group2;
    RadioButton completed, pending, excellent, good, average, poor;
    Button cancel,sub;
    MainActivity mainActivity;
    LoginPage loginPage;
    String ticket, status, remarks1;
    TextView name;
    EditText ticket_id;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TaskReport taskReport;
    signature mSignature;
    LinearLayout mContent;
    String encodedImage, sign;


    public ServiceReport() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_report, container, false);
        mainActivity= (MainActivity) getActivity();
        cancel=view.findViewById(R.id.cancel);
        sub = view.findViewById(R.id.submit);
        circleImageView = view.findViewById(R.id.profile_pic);
        name = view.findViewById(R.id.name);
        group1 = view.findViewById(R.id.status);
        group2 = view.findViewById(R.id.group2);
        excellent = view.findViewById(R.id.excellent);
        good = view.findViewById(R.id.good);
        average = view.findViewById(R.id.average);
        poor = view.findViewById(R.id.poor);
        completed = view.findViewById(R.id.radio3);
        pending = view.findViewById(R.id.radio4);
        ticket_id = view.findViewById(R.id.ticket);
        mContent = view.findViewById(R.id.mysignature);
        loginPage = new LoginPage();
        database = FirebaseDatabase.getInstance();
        mSignature = new signature(mainActivity, null);
        mContent.addView(mSignature);
        myRef = database.getReference();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mainActivity.getSupportActionBar()).setTitle("Service Report");
            mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true);

        setAllValues();

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
                Boolean flag = false;
                int id1 = group1.getCheckedRadioButtonId();
                switch (id1)
                {
                    case R.id.radio3:
                        status = "Completed";
                        break;
                    case R.id.radio4:
                        status = "Pending";
                        break;
                    default:
                        Toast.makeText(getActivity(), "Task completed or pending?", Toast.LENGTH_SHORT).show();
                        return;

                }
                int id2 = group2.getCheckedRadioButtonId();
                switch (id2)
                {
                    case R.id.excellent:
                        remarks1 = "Excellent";
                        break;
                    case R.id.good:
                        remarks1 = "Good";
                        break;
                    case R.id.average:
                        remarks1 = "Average";
                        break;
                    case R.id.poor:
                        remarks1 = "Poor";
                        break;
                        default:
                            remarks1 = "";
                            break;
                }
                ticket = ticket_id.getText().toString().trim();
//                remarks1 = remarks.getText().toString();
                if(ticket.equals(""))
                {
                    Toast.makeText(mainActivity, "Ticket id is mandatory", Toast.LENGTH_SHORT).show();
                    return;
                }
                sign = mSignature.save();
                if(sign == null)
                {
                    Toast.makeText(mainActivity, "Get acknowledged from user", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    for (DataSnapshot dataSnapshot1 : MainActivity.dataSnapshotOpen.getChildren()) {
                        if (dataSnapshot1.getKey().equals("Service")) {
                            Log.d("joan", "existing address");

                            for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {

                                String stored_name = dataSnapshot.getKey();
                                Log.d("joan", stored_name);

                                if (MainActivity.login_name.equals(stored_name)) {
                                    for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                        String id = dataSnapshot2.getKey();
                                        if(id.equals(ticket)) {
                                            flag = true;
                                            Object[] obj = new Object[]{status, remarks1, ticket};
                                            taskReport = new TaskReport();
                                            taskReport.execute(obj);
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
                if(!flag)
                {
                    Toast.makeText(mainActivity, "Please check ticket id", Toast.LENGTH_SHORT).show();
//                    return;
                }
            }
        });
        return view;
    }

    private void setAllValues()
    {
        name.setText(MainActivity.login_name);
        if(MainActivity.login_image.length() > 1) {
            Bitmap photo = loginPage.getBitmapFromString(MainActivity.login_image);
            circleImageView.setImageBitmap(photo);
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
    public class TaskReport extends AsyncTask<Object, Void, MainActivity>
    {
        String ticket1;
        String status1;
        String remarks2;
        @Override
        protected MainActivity doInBackground(Object... objects) {
            try {
                status1 = (String) objects[0];
                Log.d("joan", status1);
                remarks2 = (String) objects[1];
                Log.d("joan", remarks2);
                ticket1 = (String) objects[2];
                if(ticket1 != null) {

                    myRef.child("Service").child(MainActivity.login_name).child(ticket1).child("status").setValue(status1);
                    Log.d("joan", "status changed");

                    myRef.child("Service").child(MainActivity.login_name).child(ticket1).child("remarks").setValue(remarks2);
                    Log.d("joan", "remarks changed");
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.d("joan", e.toString());
            }
            catch (OutOfMemoryError e){
                e.printStackTrace();
                Log.d("joan", e.toString());
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.d("joan", e.toString());
            }
            return mainActivity;
        }

        @Override
        protected void onPostExecute(MainActivity mainActivity) {
            super.onPostExecute(mainActivity);
            if(status1.equals("Completed")) {
                Toast.makeText(mainActivity, "Thank you for the feedback", Toast.LENGTH_SHORT).show();
            }
            else if(status1.equals("Pending")){
                Toast.makeText(mainActivity, "Customer support will get in touch soon", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class signature extends View {
        static final float STROKE_WIDTH = 8f;
        static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        Paint paint = new Paint();
        Path path = new Path();

        float lastTouchX;
        float lastTouchY;
        final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

//        public void clear() {
//            path.reset();
//            invalidate();
//            save.setEnabled(false);
//        }

        public String save() {
            Bitmap returnedBitmap = Bitmap.createBitmap(mContent.getWidth(),
                    mContent.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            Drawable bgDrawable = mContent.getBackground();
            if (bgDrawable != null)
                bgDrawable.draw(canvas);
            else
                canvas.drawColor(Color.WHITE);
            mContent.draw(canvas);

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            returnedBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
            byte[] b = bs.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            Log.d("joan", encodedImage);
            return encodedImage;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
//            save.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}
