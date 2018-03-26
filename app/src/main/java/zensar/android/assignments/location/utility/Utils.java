package zensar.android.assignments.location.utility;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import zensar.android.assignments.location.interactors.IPermissionResultTransfer;
import zensar.android.assignments.R;

/**
 * Created by RK51670 on 31-01-2018.
 */
public class Utils {

    private Utils() {

    }

    static AlertDialog mDialog = null;

    /**
     * Show AlertDialog on denying location repoRequest
     */
    public static void showAlertDialog(final Context context, final IPermissionResultTransfer iPermissionResultTransfer) {
        dismissDialog();
        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(context).create();
            mDialog.setTitle(context.getString(R.string.permission));
            mDialog.setMessage(context.getString(R.string.permission_message));
            mDialog.setCancelable(false);
            mDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.retry), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    iPermissionResultTransfer.setResultCode(Constants.SUCCESS_RESULT, context);
                }
            });
            mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    iPermissionResultTransfer.setResultCode(Constants.FAILURE_RESULT, context);
                }
            });
        }
        mDialog.show();
    }

    /**
     * Dismiss Dialog
     */
    public static void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * Show play services error
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Load Images
     */
    public static void loadImages(Context context, ImageView imageView, String url) {
        Picasso.with(context).load(url).into(imageView);
    }

    /**
     * Start activity intent function
     */
    public static void startAcivity(Context context, Class classname) {
        Intent startActivity = new Intent(context, classname);
        startActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(startActivity);
    }

    /**
     * Start activity for result intent function
     */
    public static void startAcivityForResult(Context context, Class classname, int requestCode) {
        Intent startActivity = new Intent(context, classname);
        startActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((Activity) context).startActivityForResult(startActivity, requestCode);
    }

    /**
     * Start activity intent function
     */
    public static void startAcivityForResultWithData(Context context, Class classname, Bundle bundle, int requestCode) {
        Intent startActivity = new Intent(context, classname);
        startActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity.putExtra(Constants.DATA, bundle);
        ((Activity) context).startActivityForResult(startActivity, requestCode);
    }

    /**
     * Start activity intent function
     */
    public static void startAcivityWithData(Context context, Class classname, Bundle bundle) {
        Intent startActivity = new Intent(context, classname);
        startActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity.putExtra(Constants.DATA, bundle);
        ((Activity) context).startActivity(startActivity);
    }

    /**
     * Check network connectivity
     */
    public static Boolean checkConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}