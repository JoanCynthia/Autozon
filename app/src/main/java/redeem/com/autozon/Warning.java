package redeem.com.autozon;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class Warning extends DialogFragment {

    public Warning() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = null;
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setTitle("Congratulations!");
        ab.setMessage("Registration completed successfully. Please login with your account.");
        ab.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.loadLogin();

            }
        });
        ab.setNegativeButton("Register again", null);

        d = ab.create();
        return d;
    }
}
