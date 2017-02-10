package tuananh.com.sdcardtest;

import android.app.ListActivity;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.os.StatFs;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends ListActivity {
    private static final String TAG = "anhlt2";
    private static final int MY_PERMISSION_REQUEST_CODE = 0;
    private String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPermissions();

        // Use the current directory as title
            File sdcard = null;
            if(!Environment.isExternalStorageRemovable())
            {
                File[] listStorage = ContextCompat.getExternalFilesDirs(getApplicationContext(), null);
                Log.d(TAG, "onCreate: number of storage " + listStorage.length);
                for (File i : listStorage) {
                    if (i==null) Log.d(TAG, "onCreate: slkdfjaslkfjasklfjsklfjasdl;kfsjadfklsdjfklsd");
                    Log.d(TAG, "onCreate: list storage_____ " + i.toString()+ " free space= "+ i.getFreeSpace() +" usable space = "+i.getUsableSpace());
                    if (i.toString() != Environment.getExternalStorageDirectory().toString()) {
                        sdcard = i;
                    }
                }
            }else sdcard = Environment.getExternalStorageDirectory();

            String realSDCard = sdcard.getAbsolutePath().substring(0,sdcard.getAbsolutePath().indexOf(getPackageName()));
            Log.d(TAG, "onCreate: sdcard is " + sdcard.toString()+ " tmpsdcard =  "+realSDCard);
            StatFs stat = new StatFs(realSDCard);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "onCreate: available space by stat = "+stat.getAvailableBlocksLong()* stat.getBlockSizeLong());
        }
        else
        {
            Log.d(TAG, "onCreate: available space by stat = "+(long)stat.getAvailableBlocks()* (long)stat.getBlockSize());
        }
        path = "/";
            if (getIntent().hasExtra("path")) {
        path = getIntent().getStringExtra("path");
        }
        setTitle(path);

        // Read all files sorted into the values-array
        List values = new ArrayList();
        File dir = new File(path);
        if (!dir.canRead()) {
        setTitle(getTitle() + " (inaccessible)");
        }
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    values.add(file);
                }
            }
        }
        Collections.sort(values);

        // Put the data into the list
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, values);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String filename = (String) getListAdapter().getItem(position);
        if (path.endsWith(File.separator)) {
            filename = path + filename;
        } else {
            filename = path + File.separator + filename;
        }
        if (new File(filename).isDirectory()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("path", filename);
            startActivity(intent);
        } else {
            Toast.makeText(this, filename + " is not a directory", Toast.LENGTH_LONG).show();
        }
    }

    private void checkAndRequestPermissions()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M )
        {
            Log.d(TAG, "checkAndRequestPermissions: read_external_storage: "+ checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)+" WRITE_EXTERNAL_STORAGE: "+ checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)+ "permission grand: "+PackageManager.PERMISSION_GRANTED);
            if((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
                    &&(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED))
            {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                Log.d(TAG, "onRequestPermissionsResult: "+ grantResults.length);
               if(grantResults.length>0)
               {
                   for (int i =0;i<grantResults.length;i++) {
                       Log.d(TAG, "onRequestPermissionsResult: result = "+i);
                   }
               }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public static File getConfirmedRemovableSDCardDirectory(Context context) {
        if (Environment.isExternalStorageRemovable()) {
            return Environment.getExternalStorageDirectory();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (File directory : ContextCompat.getExternalFilesDirs(context, null)) {
                if (Environment.isExternalStorageRemovable(directory)) {
                    return directory;
                }
            }
        }
        return null;
    }

    public static File[] getPossibleRemovableSDCardDirectories(Context context) {
        File confirmedLocation = getConfirmedRemovableSDCardDirectory(context);
        Log.d(TAG, "getPossibleRemovableSDCardDirectories: confirmedLocation ---- "+confirmedLocation.toString() );
        if (confirmedLocation != null) {
            return new File[]{confirmedLocation};
        }
        return ContextCompat.getExternalFilesDirs(context, null);
    }

}
