package example.naoki.ble_myo.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.LineGraph;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import example.naoki.ble_myo.DataProcess.ClientSocket;
import example.naoki.ble_myo.view.SetupDialog;
import example.naoki.ble_myo.Interface.GetSetupData;
import example.naoki.ble_myo.MyoCommandList;
import example.naoki.ble_myo.MyoGattCallback;
import example.naoki.ble_myo.R;
import example.naoki.ble_myo.view.TextProgressBar;

public class MainActivity extends AppCompatActivity {

    public static final int MENU_MODE = 0;
    public static final int MENU_BYE = 1;

    /**
     * Intent code for requesting Bluetooth enable
     */
    private static final int REQUEST_ENABLE_BT = 1;

    public Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    public BluetoothGatt mBluetoothGatt;
    public BluetoothDevice device;
    private MyoCommandList commandList = new MyoCommandList();

    private MyoGattCallback mMyoCallback;

    public String healthName;
    public int healthType;
    public String deviceName;
    public int healthStandard;

    Fragment nowFragment;
    ReadyFragment readyFragment;
    MainFragment mainFragment;

    public final static int FRAGMENT_READY = 0;
    public final static int FRAGMENT_MAIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        mHandler = new Handler();
        readyFragment = new ReadyFragment(this);
        readyFragment.setFragmentChange(new ReadyFragment.ReadyListener() {
            @Override
            public void setGraph(LineGraph lineGraph) {
                mMyoCallback.setLineGraph(lineGraph);
                if (mBluetoothGatt == null || !mMyoCallback.setMyoControlCommand(commandList.sendEmgOnly())) {
                    System.out.println("False EMG");
                }
            }

            @Override
            public void fragmentChange() {
                healthStandard = mMyoCallback.countingProcess.getStandard();
                mMyoCallback.setMode(MyoGattCallback.BREAK_MODE);

                getSupportActionBar().show();
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                fragmentReplace(1);
            }

            @Override
            public void send(BluetoothDevice device, String deviceName) {

                if ((MainActivity.this.deviceName = deviceName) != null) {

                    // Ensures Bluetooth is available on the device and it is enabled. If not,
                    // displays a dialog requesting user permission to enable Bluetooth.
                    if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    } else {

                        Toast.makeText(MainActivity.this, "Test를 해 주세요.", Toast.LENGTH_SHORT).show();

                        mMyoCallback = new MyoGattCallback(mHandler);
                        mBluetoothGatt = device.connectGatt(MainActivity.this, false, mMyoCallback); // connect to GATT Server, , if this command is success, call method onServicesDiscovered in MyoGattCallback Class
                        mMyoCallback.setBluetoothGatt(mBluetoothGatt);

                        mMyoCallback.setMode(MyoGattCallback.TEST_MODE);
                    }
                }
            }
        });
        mainFragment = new MainFragment(this);
        mainFragment.setMainFragmentListener(new MainFragment.MainFragmentListener() {

            @Override
            public void startButtonClick(HashMap<String, View> views) {

                mMyoCallback.setType(healthType);
                mMyoCallback.setStandard(healthStandard);
                mMyoCallback.setMode(MyoGattCallback.MAIN_MODE);
                mMyoCallback.setMainInit(views);

//                mMyoCallback.setMyoControlCommand(commandList.sendEmgOnly());
            }

            @Override
            public void initialButtonClick() {

            }
        });

        fragmentReplace(0);

        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menu.add(0, MENU_MODE, 0, "모드 설정");
        menu.add(0, MENU_BYE, 0, "Good Bye");
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();

        if(mMyoCallback != null)
        this.closeBLEGatt();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case MENU_MODE:
                return true;
            case MENU_BYE:
                closeBLEGatt();
                Toast.makeText(getApplicationContext(), "Close GATT", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    public void closeBLEGatt() {

        mMyoCallback.stopCallback();
        mMyoCallback = null;

        mBluetoothGatt.close();
        mBluetoothGatt = null;
        device = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMyoCallback == null) return;
        closeBLEGatt();
    }

    public void fragmentReplace(int position) {
        nowFragment = getFragment(position);

        final android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainLayout, nowFragment);
        transaction.commit();
    }

    public Fragment getFragment(int position) {
        Fragment fragment = null;

        switch (position) {
            case FRAGMENT_READY:
                fragment = readyFragment;
                break;
            case FRAGMENT_MAIN:
                fragment = mainFragment;
                break;
        }

        return fragment;
    }

//    public void setupButtonClick(View v)
//    {
//        setupDialog.showDialog();
//    }

}

