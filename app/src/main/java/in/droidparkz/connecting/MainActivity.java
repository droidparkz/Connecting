package in.droidparkz.connecting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout bt,qr;

    TextView content,device;

    String name;

    private IntentIntegrator qrScan;

    private BluetoothAdapter BA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int Permission_All = 1;

        String[] Permissions = {Manifest.permission.CAMERA, Manifest.permission.BLUETOOTH_ADMIN };
        if(!hasPermissions(this, Permissions)){
            ActivityCompat.requestPermissions(this, Permissions, Permission_All);
        }

        qr = (RelativeLayout) findViewById(R.id.qr);
        qr.setOnClickListener(this);

        bt = (RelativeLayout) findViewById(R.id.bt);
        bt.setOnClickListener(this);


        qrScan = new IntentIntegrator(this);

        content = (TextView) findViewById(R.id.content);

        BA = BluetoothAdapter.getDefaultAdapter();

        checkConnected();

    }

    public static boolean hasPermissions(Context context, String... permissions){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && context!=null && permissions!=null){
            for(String permission: permissions){
                if(ActivityCompat.checkSelfPermission(context, permission)!= PackageManager.PERMISSION_GRANTED){
                    return  false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null)
            {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            }
            else
            {
                content.setText("QR Content : "+result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void checkConnected()
    {

        BluetoothAdapter.getDefaultAdapter().getProfileProxy(this, serviceListener, BluetoothProfile.HEADSET);
    }

    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener()
    {


        @Override
        public void onServiceDisconnected(int profile)
        {
            content.setText("Device : ");
        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy)
        {

            for (BluetoothDevice device : proxy.getConnectedDevices())
            {
                name = device.getName();
                content.setText("Device : " + name);
            }
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
        }
    };

    @Override
    public void onClick(View view) {

        if (view == qr)
        {
            qrScan.setOrientationLocked(false);
            qrScan.initiateScan();
        }

        if (view == bt)
        {
            startActivity(new Intent(getApplicationContext(),Bt.class));
        }

    }
}
