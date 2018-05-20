package net.daum.www.soleil.View.Activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import net.daum.www.soleil.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thswl on 2018-05-09.
 */

public class Permission extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission);


    }
    public static boolean checkAndRequestPermission(Activity activity, int permissionRequestCode, String... permissions) {
        String[] requiredPermissions = getRequiredPermissions(activity, permissions);

        if (requiredPermissions.length > 0 && !activity.isDestroyedCompat()) {
            ActivityCompat.requestPermissions(activity, requiredPermissions, permissionRequestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkAndRequestPermission(Fragment fragment, int permissionRequestCode, String... permissions) {
        String[] requiredPermissions = getRequiredPermissions(fragment.getContext() != null ?
                fragment.getContext() : fragment.getActivity(), permissions);

        if (requiredPermissions.length > 0 && fragment.isAdded()) {
            fragment.requestPermissions(requiredPermissions, permissionRequestCode);
            return false;
        } else {
            return true;
        }
    }

    public static String[] getRequiredPermissions(Context context, String... permissions) {
        List<String> requiredPermissions = new ArrayList<>();

        // Context가 null이면 무조건 권한을 요청하도록 requiredPermissions가 존재한다고 reutrn 한다
        if (context == null) return requiredPermissions.toArray(new String[1]);

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(permission);
            }
        }

        return requiredPermissions.toArray(new String[requiredPermissions.size()]);
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) return false;

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    public static String getRationalMessage(Context context, int code) {
        switch (code) {
            case PERMISSION_CAMERA:
                return getRationalMessage(context,
                        context.getString(R.string.permission_camera_rational), context.getString(R.string.permission_camera));
            case PERMISSION_CONTACT:
                return getRationalMessage(context,
                        context.getString(R.string.permission_contact_rational), context.getString(R.string.permission_contact));
            case PERMISSION_STORAGE:
                return getRationalMessage(context,
                        context.getString(R.string.permission_storage_rational), context.getString(R.string.permission_storage));
            case PERMISSION_READ_PHONE_STATE:
                return getRationalMessage(context,
                        context.getString(R.string.permission_read_phone_state_rational), context.getString(R.string.permission_read_phone_state));
        }
        return "";
    }

    public static String getRationalMessage(Context context, String rational, String permission) {
        return String.format(context.getString(R.string.permission_request), rational, permission);
    }

    public static void showRationalDialog(Context context, int message) {
        showRationalDialog(context, context.getString(message));
    }

/*
    static final Integer CAMERA = 0x5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission);

        askForPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, CAMERA);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       super.onRequestPermissionsResult(requestCode, permissions,grantResults);

       if(ActivityCompat.checkSelfPermission(this,permissions[0])== PackageManager.PERMISSION_GRANTED){
           switch (requestCode){
               //read External Storage
               case 4:
                   Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                   startActivityForResult(imageIntent,11);
                   break;

           }
       }
    }

    private void askForPermission(String permission, Integer requestCode){
        //갤러리 사용 권한 체크
        if(ContextCompat.checkSelfPermission(Permission.this,permission) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우

            //최초 권한 요청인지 촉은 사용자에 의한 재요청인지 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(Permission.this,permission)) {
                //사용자가 임의로 권한을 취소시킨 경우
                //권한 재요청
                ActivityCompat.requestPermissions(Permission.this, new String[]{permission}, requestCode);
            } else {
                //최초로 권한을 요청하는 경우(첫실행)
                ActivityCompat.requestPermissions(Permission.this, new String[]{permission}, requestCode);
            }
        }else{
            Toast.makeText(this,""+permission+"is already granted",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 4) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(Permission.this, LoginPageActivity.class);
                startActivity(intent);
            }
        }
    }
    */
}
