package redeem.com.autozon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TicketDialog extends DialogFragment
{
    String ticket;

    public TicketDialog() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = null;
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setTitle("Your Ticket ID");
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            ticket = bundle.getString("ticket");
        }
        ab.setMessage("Please note down your ticket id  -  "+ticket);
        ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        d = ab.create();
        return d;
    }
}
