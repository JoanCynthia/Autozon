package redeem.com.autozon;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminDialog extends DialogFragment
{
    EditText phoneNumber;
    Register r;

    public AdminDialog() {
        // Required empty public constructor
    }
    @SuppressLint("ValidFragment")
    public AdminDialog(Register r){
        this.r = r;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog d = null;
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_admin_dialog, null);
        phoneNumber = v.findViewById(R.id.phoneNumber);
        ab.setMessage("Enter relevant admin's phone number");
        ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                MainActivity.adminPhone = phoneNumber.getText().toString().trim();
                r.doTask();

            }
        });
        ab.setView(v);
        d = ab.create();
        return d;
    }
}
