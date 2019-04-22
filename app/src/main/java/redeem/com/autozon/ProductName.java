package redeem.com.autozon;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductName extends DialogFragment
{
    EditText productName;
    DeviceInfo deviceInfo;


    public ProductName() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ProductName(DeviceInfo deviceInfo)
    {
        this.deviceInfo = deviceInfo;
    }


    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog d = null;
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_product_name, null);
        productName = v.findViewById(R.id.name);
        ab.setMessage("Enter relevant admin's phone number");
        ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                DeviceInfo.productName = productName.getText().toString().trim();
                if (DeviceInfo.productName.equals(""))
                {
                    Toast.makeText(getActivity(), "Enter product name", Toast.LENGTH_SHORT).show();
                    return;
                }
                deviceInfo.doTask();

            }
        });
        ab.setView(v);
        d = ab.create();
        return d;
    }

}
