package example.naoki.ble_myo.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import example.naoki.ble_myo.MyoCommandList;
import example.naoki.ble_myo.MyoGattCallback;
import example.naoki.ble_myo.R;


/**
 * Created by Leki on 2016-08-03.
 */
public class FindMyoFragment extends android.support.v4.app.Fragment implements BluetoothAdapter.LeScanCallback {

    public static String TAG = "BluetoothList";

    /**
     * Device Scanning Time (ms)
     */
    private static final long SCAN_PERIOD = 2500;

    /**
     * Intent code for requesting Bluetooth enable
     */
    private static final int REQUEST_ENABLE_BT = 1;

    BluetoothDevice device;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<String> deviceNames = new ArrayList<>();
    private String myoName = null;

    private ArrayAdapter<String> adapter;
    private Handler readyHandler;

    MainActivity mainActivity;
    TextView findMyoText;
    Button findMyoButton;
    ListView findMyoList;
    public SwitchScreen switchScreen;


    public interface SwitchScreen
    {
        void switchNextScreen();
        void switchPrevScreen();
        void selectDevice(BluetoothDevice device, String deviceName);
    }


    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        // Device Log
        ParcelUuid[] uuids = device.getUuids();
        String uuid = "";
        if (uuids != null) {
            for (ParcelUuid puuid : uuids) {
                uuid += puuid.toString() + " ";
            }
        }

        String msg = "name=" + device.getName() + ", bondStatus="
                + device.getBondState() + ", address="
                + device.getAddress() + ", type" + device.getType()
                + ", uuids=" + uuid;

        Log.d("BLEActivity", msg);

        if (device.getName() != null && !deviceNames.contains(device.getName())) {
            deviceNames.add(device.getName());
            this.device = device;
        }
    }

    public FindMyoFragment(Handler handler, MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        this.readyHandler = handler;
    }

    public void setSwitchScreen(SwitchScreen switchScreen)
    {
        this.switchScreen = switchScreen;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothManager mBluetoothManager = (BluetoothManager)mainActivity.getSystemService(mainActivity.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        adapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_expandable_list_item_1, deviceNames);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_findmyo, container, false);

        findMyoText = (TextView) viewGroup.findViewById(R.id.findMyoText);
        findMyoButton = (Button) viewGroup.findViewById(R.id.findMyoButton);
        findMyoList = (ListView) viewGroup.findViewById(R.id.findMyoList);

        findMyoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanDevice();
            }
        });

        findMyoList.setAdapter(adapter);
        findMyoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);

                myoName = item;
                switchScreen.selectDevice(device, myoName);
                switchScreen.switchNextScreen();
            }
        });

        return viewGroup;
    }

    public void scanDevice() {
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            deviceNames.clear();
            // Scanning Time out by Handler.
            // The device scanning needs high energy.
            readyHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(FindMyoFragment.this);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(mainActivity.getApplicationContext(), "Stop Device Scan", Toast.LENGTH_SHORT).show();
                }
            }, SCAN_PERIOD);

            mBluetoothAdapter.startLeScan(FindMyoFragment.this);   // find BLE device
        }
    }
}
