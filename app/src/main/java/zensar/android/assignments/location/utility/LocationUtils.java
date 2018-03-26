package zensar.android.assignments.location.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by RK51670 on 29-01-2018.
 */
public class LocationUtils{

    private LocationUtils(){

    }

        public static int  checkPlayServicesAvailbility(Context context) {
            GoogleApiAvailability googleApiAvail= GoogleApiAvailability.getInstance();
            int checkApiAvailability = googleApiAvail.isGooglePlayServicesAvailable(context);
            if (checkApiAvailability == ConnectionResult.SUCCESS) {
                return Constants.TRUE;
            } else {
                if (googleApiAvail.isUserResolvableError(checkApiAvailability)) {
                    googleApiAvail.getErrorDialog(((Activity)context), checkApiAvailability, Constants.PLAY_SERVICES_RESOLUTION_REQUEST);
                    return Constants.ERROR;
                }
            }
            return Constants.FALSE;
        }


        /**
         * Runtime permission for Api Level 23 and above
         */
        public static boolean checkRuntimePermission(Context context) {
            int checkPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(((Activity)context), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(((Activity)context), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_ACCESS_LOCATION);
                }
            } else if (checkPermission == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            return false;
        }

        /**
         * check the api level
         */
       public static  boolean checkApiLevelForRuntimePermission(Context context){
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return checkRuntimePermission(context);
            }
            return true;
        }


}