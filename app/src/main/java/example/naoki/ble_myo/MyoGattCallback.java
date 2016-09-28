package example.naoki.ble_myo;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import example.naoki.ble_myo.DataProcess.ByteReader;
import example.naoki.ble_myo.DataProcess.CountingProcess;
import example.naoki.ble_myo.DataProcess.MovingAverage;
import example.naoki.ble_myo.view.TextProgressBar;

/**
 * Created by naoki on 15/04/15.
 */

public class MyoGattCallback extends BluetoothGattCallback {

    // char * 2 is 1byte
    /**
     * Service ID
     */
    private static final String MYO_CONTROL_ID = "d5060001-a904-deb9-4748-2c7f4a124842";
    private static final String MYO_EMG_DATA_ID = "d5060005-a904-deb9-4748-2c7f4a124842";
    /**
     * Characteristics ID
     */
    private static final String MYO_INFO_ID = "d5060101-a904-deb9-4748-2c7f4a124842";
    private static final String FIRMWARE_ID = "d5060201-a904-deb9-4748-2c7f4a124842";
    private static final String COMMAND_ID = "d5060401-a904-deb9-4748-2c7f4a124842";
    private static final String EMG_0_ID = "d5060105-a904-deb9-4748-2c7f4a124842";

    public static final int TEST_MODE = 0;
    public static final int MAIN_MODE = 1;
    public static final int BREAK_MODE = 2;

    /**
     * android Characteristic ID (from Android Samples/BluetoothLeGatt/SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG)
     */
    private static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    private Queue<BluetoothGattDescriptor> descriptorWriteQueue = new LinkedList<BluetoothGattDescriptor>();
    private Queue<BluetoothGattCharacteristic> readCharacteristicQueue = new LinkedList<BluetoothGattCharacteristic>();

    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mCharacteristic_command;
    private BluetoothGattCharacteristic mCharacteristic_emg0;

    private MyoCommandList commandList = new MyoCommandList();

    private String TAG = "MyoGatt";

    private String callback_msg;
    private Handler mHandler;
    private int[] emgDatas = new int[16];

    private LineGraph lineGraph;

    private MovingAverage movingAverage;
    public CountingProcess countingProcess;

    private TextView gestureText;
    private TextView healthText;
    private TextView timeText;

    private TextProgressBar counterProgressBar;
    private TextProgressBar setProgressBar;
    private ProgressBar timeProgressBar;

    public int counting = 0;
    public int totalCount = 0;
    public int setCount = 0;

    int nowGraphIndex = 0;
    int nowMode = 0;

    int[][] dataList1_a = new int[8][50];
    int[][] dataList1_b = new int[8][50];

    long last_send_never_sleep_time_ms = System.currentTimeMillis();
    final static long NEVER_SLEEP_SEND_TIME = 10000;  // Milli Second


    MyoDataFileReader myoDataFileReader;

    private long breakTime;
    long systemTime_ms;


    public MyoGattCallback(final Handler handler) {
        mHandler = handler;

        movingAverage = new MovingAverage();
        setCountingProcess();
    }

    public void setCountingProcess()
    {
        countingProcess = new CountingProcess();
        countingProcess.setAddCount(new CountingProcess.CountingListener() {

            @Override
            public void addCount() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (countingProcess.getState() == CountingProcess.MAIN) {
                            counting++;
                            if (counting > counterProgressBar.getMax()) {
                                setCount++;
                                setProgressBar.setProgress(setCount);
                                setProgressBar.setText(String.format("%d / %d 세트", setCount, setProgressBar.getMax()));
                                viberation();
                            }

                            gestureText.setText("Counting : " + ++totalCount);
                            counterProgressBar.setProgress(counting);
                            counterProgressBar.setText(String.format("%d / %d 회", counting, counterProgressBar.getMax()));
                        }
                    }
                });
            }

            @Override
            public void setTime(final String string) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        gestureText.setText("Time : " + string);
                    }
                });
            }
        });
    }



    public void setLineGraph(LineGraph lineGraph) {
        this.lineGraph = lineGraph;
    }

    public void setMode(int mode)
    {
        this.nowMode = mode;
    }

    public void setMainInit(HashMap<String, View> views) {

        lineGraph = (LineGraph) views.get("graph");
        gestureText = (TextView) views.get("gestureText");
        counterProgressBar = (TextProgressBar) views.get("counterProgressBar");
        setProgressBar = (TextProgressBar) views.get("setProgressBar");
        timeProgressBar = (ProgressBar) views.get("timeProgressBar");
        timeText = (TextView) views.get("timeTextView");
        healthText = (TextView) views.get("healthTextView");


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                timeProgressBar.setProgress(timeProgressBar.getProgress() - 1);

                                if (timeProgressBar.getProgress() < 1) {
                                    timeProgressBar.setProgress(60);
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        myoDataFileReader = new MyoDataFileReader();
//        movingAverage = new MovingAverage();
//        setCountingProcess();
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        Log.d(TAG, "onConnectionStateChange: " + status + " -> " + newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            // GATT Connected
            // Searching GATT Service
            gatt.discoverServices();

        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // GATT Disconnected
            stopCallback();
            Log.d(TAG, "Bluetooth Disconnected");
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);

        Log.d(TAG, "onServicesDiscovered received: " + status);
        if (status == BluetoothGatt.GATT_SUCCESS) { // 0
            // Find GATT Service
            BluetoothGattService service_emg = gatt.getService(UUID.fromString(MYO_EMG_DATA_ID));
            if (service_emg == null) {
                Log.d(TAG, "No Myo EMG-Data Service !!");
            } else {
                Log.d(TAG, "Find Myo EMG-Data Service !!");
                // Getting CommandCharacteristic
                mCharacteristic_emg0 = service_emg.getCharacteristic(UUID.fromString(EMG_0_ID));
                if (mCharacteristic_emg0 == null) {
                    callback_msg = "Not Found EMG-Data Characteristic";
                } else {
                    // Setting the notification
                    boolean registered_0 = gatt.setCharacteristicNotification(mCharacteristic_emg0, true);
                    if (!registered_0) {
                        Log.d(TAG, "EMG-Data Notification FALSE !!");
                    } else {
                        Log.d(TAG, "EMG-Data Notification TRUE !!");
                        // Turn ON the Characteristic Notification
                        BluetoothGattDescriptor descriptor_0 = mCharacteristic_emg0.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                        if (descriptor_0 != null) {
                            descriptor_0.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                            writeGattDescriptor(descriptor_0);

                            Log.d(TAG, "Set descriptor");

                        } else {
                            Log.d(TAG, "No descriptor");
                        }
                    }
                }
            }

            BluetoothGattService service = gatt.getService(UUID.fromString(MYO_CONTROL_ID));
            if (service == null) {
                Log.d(TAG, "No Myo Control Service !!");
            } else {
                Log.d(TAG, "Find Myo Control Service !!");
                // Get the MyoInfoCharacteristic
                BluetoothGattCharacteristic characteristic =
                        service.getCharacteristic(UUID.fromString(MYO_INFO_ID));
                if (characteristic == null) {
                } else {
                    Log.d(TAG, "Find read Characteristic !!");
                    //put the characteristic into the read queue
                    readCharacteristicQueue.add(characteristic);
                    //if there is only 1 item in the queue, then read it.  If more than 1, we handle asynchronously in the callback above
                    //GIVE PRECEDENCE to descriptor writes.  They must all finish first.
                    if ((readCharacteristicQueue.size() == 1) && (descriptorWriteQueue.size() == 0)) {
                        mBluetoothGatt.readCharacteristic(characteristic);
                    }
/*                        if (gatt.readCharacteristic(characteristic)) {
                            Log.d(TAG, "Characteristic read success !!");
                        }
*/
                }

                // Get CommandCharacteristic
                mCharacteristic_command = service.getCharacteristic(UUID.fromString(COMMAND_ID));
                if (mCharacteristic_command == null) {
                } else {
                    Log.d(TAG, "Find command Characteristic !!");
                }
            }
        }
    }

    public void writeGattDescriptor(BluetoothGattDescriptor d) {
        //put the descriptor into the write queue
        descriptorWriteQueue.add(d);
        //if there is only 1 item in the queue, then write it.  If more than 1, we handle asynchronously in the callback above
        if (descriptorWriteQueue.size() == 1) {

            // I think that if this method is successful, onDescriptorWrite method is called
            mBluetoothGatt.writeDescriptor(d);
        }
    }


    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "Callback: Wrote GATT Descriptor successfully.");
        } else {
            Log.d(TAG, "Callback: Error writing GATT Descriptor: " + status);
        }
        descriptorWriteQueue.remove();  //pop the item that we just finishing writing
        //if there is more to write, do it!
        if (descriptorWriteQueue.size() > 0)
            mBluetoothGatt.writeDescriptor(descriptorWriteQueue.element());
        else if (readCharacteristicQueue.size() > 0)
            mBluetoothGatt.readCharacteristic(readCharacteristicQueue.element());
    }

    // reporting the result of a characteristic read operation.
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        readCharacteristicQueue.remove();

        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (UUID.fromString(FIRMWARE_ID).equals(characteristic.getUuid())) {

                // Myo Firmware Infomation
                final byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    ByteReader byteReader = new ByteReader();
                    byteReader.setByteData(data);

                    Log.d(TAG, String.format("This Version is %d.%d.%d - %d",
                            byteReader.getShort(), byteReader.getShort(),
                            byteReader.getShort(), byteReader.getShort()));

                }
                if (data == null) {
                    Log.d(TAG, "Characteristic String is " + characteristic.toString());
                }
            } else if (UUID.fromString(MYO_INFO_ID).equals(characteristic.getUuid())) {
                // Myo Device Information
                final byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    ByteReader byteReader = new ByteReader();
                    byteReader.setByteData(data);

                    callback_msg = String.format("Serial Number     : %02x:%02x:%02x:%02x:%02x:%02x",
                            byteReader.getByte(), byteReader.getByte(), byteReader.getByte(),
                            byteReader.getByte(), byteReader.getByte(), byteReader.getByte()) +
                            '\n' + String.format("Unlock            : %d", byteReader.getShort()) +
                            '\n' + String.format("Classifier builtin:%d active:%d (have:%d)",
                            byteReader.getByte(), byteReader.getByte(), byteReader.getByte()) +
                            '\n' + String.format("Stream Type       : %d", byteReader.getByte());
                }
            }
        } else {
            Log.d(TAG, "onCharacteristicRead error: " + status);
        }

        if (readCharacteristicQueue.size() > 0)
            mBluetoothGatt.readCharacteristic(readCharacteristicQueue.element());
    }

    public void viberation() {
        setMyoControlCommand(commandList.sendVibration3());
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "onCharacteristicWrite success");
        } else {
            Log.d(TAG, "onCharacteristicWrite error: " + status);
        }
    }

    // Callback triggered as a result of a remote characteristic notification.
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

        if (EMG_0_ID.equals(characteristic.getUuid().toString())) {

            if (nowMode != BREAK_MODE) {
                systemTime_ms = System.currentTimeMillis();
                byte[] emg_data = characteristic.getValue();

                ByteReader emg_br = new ByteReader();
                emg_br.setByteData(emg_data);

                for (int emgInputIndex = 0; emgInputIndex < 16; emgInputIndex++) {
                    emgDatas[emgInputIndex] = emg_br.getByte();
                }

                movingAverage.addAverageData(emgDatas);

                dataList1_a[0][0] = movingAverage.applyMovingAverage();
                dataList1_b[0][0] = movingAverage.applyMovingAverage();
                callback_msg = String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", emgDatas[0], emgDatas[1], emgDatas[2], emgDatas[3], emgDatas[4], emgDatas[5], emgDatas[6], emgDatas[7], emgDatas[8], emgDatas[8], emgDatas[10], emgDatas[11], emgDatas[12], emgDatas[13], emgDatas[14], emgDatas[15]);
            }

            // Test 모드
            if (nowMode == TEST_MODE) {
                countingProcess.onTest(movingAverage.applyMovingAverage());

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        lineGraph.removeAllLines();

                        // 折れ線グラフ
                        int number = 50;
                        int addNumber = 100;
                        Line line = new Line();

                        while (0 < number) {
                            number--;
                            addNumber--;

                            //１点目add
                            if (number != 0) {
                                for (int setDatalistIndex = 0; setDatalistIndex < 8; setDatalistIndex++) {
                                    dataList1_a[setDatalistIndex][number] = dataList1_a[setDatalistIndex][number - 1];
                                }
                            }
                            LinePoint linePoint = new LinePoint();
                            linePoint.setY(dataList1_a[nowGraphIndex][number]); //ランダムで生成した値をSet
                            linePoint.setX(addNumber); //x軸を１ずつずらしてSet

                            line.addPoint(linePoint);
                        }
                        line.setColor(Color.parseColor("#66ffff")); // 線の色をSet

                        line.setShowingPoints(false);
                        lineGraph.addLine(line);
                        lineGraph.setRangeY(-20, 200); // 表示するY軸の最低値・最高値 今回は0から1まで
                    }
                });

                if (systemTime_ms > last_send_never_sleep_time_ms + NEVER_SLEEP_SEND_TIME) {
                    // set Myo [Never Sleep Mode]
                    setMyoControlCommand(commandList.sendUnSleep());
                    last_send_never_sleep_time_ms = systemTime_ms;
                }

            } else if (nowMode == MAIN_MODE) {

                myoDataFileReader.saveRAW(callback_msg);
                countingProcess.judgeDumbbellCounting(movingAverage.applyMovingAverage());     // Dumbbell Counting

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        lineGraph.removeAllLines();

                        if (Math.abs(movingAverage.getDegree()) > 30) {
                            breakTime = System.currentTimeMillis() / 1000;
                            healthText.setText("운동 중");
                        } else {
                            if ((System.currentTimeMillis() / 1000) - breakTime > 2)
                                healthText.setText("휴식 중");
                        }

                        // 折れ線グラフ
                        int number = 50;
                        int addNumber = 100;
                        Line line = new Line();

                        while (0 < number) {
                            number--;
                            addNumber--;

                            //１点目add
                            if (number != 0) {
                                for (int setDatalistIndex = 0; setDatalistIndex < 8; setDatalistIndex++) {
                                    dataList1_a[setDatalistIndex][number] = dataList1_a[setDatalistIndex][number - 1];
                                }
                            }
                            LinePoint linePoint = new LinePoint();
                            linePoint.setY(dataList1_a[nowGraphIndex][number]); //ランダムで生成した値をSet
                            linePoint.setX(addNumber); //x軸を１ずつずらしてSet

                            line.addPoint(linePoint);
                        }
                        line.setColor(Color.parseColor("#66ffff")); // 線の色をSet

                        line.setShowingPoints(false);
                        lineGraph.addLine(line);
                        lineGraph.setRangeY(-20, 200); // 表示するY軸の最低値・最高値 今回は0から1まで
                    }
                });

                if (systemTime_ms > last_send_never_sleep_time_ms + NEVER_SLEEP_SEND_TIME) {
                    // set Myo [Never Sleep Mode]
                    setMyoControlCommand(commandList.sendUnSleep());
                    last_send_never_sleep_time_ms = systemTime_ms;
                }
            }
        }
    }

    public void setBluetoothGatt(BluetoothGatt gatt) {
        mBluetoothGatt = gatt;
    }

    public boolean setMyoControlCommand(byte[] command) {

        if (mCharacteristic_command != null) {
            mCharacteristic_command.setValue(command);
            int i_prop = mCharacteristic_command.getProperties();
            if (i_prop == BluetoothGattCharacteristic.PROPERTY_WRITE) {
                if (mBluetoothGatt.writeCharacteristic(mCharacteristic_command)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int switchNextPage() {
        return countingProcess.getStandard();
    }

    public void setStandard(int standard)
    {
        countingProcess.setStandard(standard);
    }

    public void stopCallback() {
        // Before the closing GATT, set Myo [Normal Sleep Mode].
        setMyoControlCommand(commandList.sendNormalSleep());
        descriptorWriteQueue = new LinkedList<BluetoothGattDescriptor>();
        readCharacteristicQueue = new LinkedList<BluetoothGattCharacteristic>();
        if (mCharacteristic_command != null) {
            mCharacteristic_command = null;
        }
        if (mCharacteristic_emg0 != null) {
            mCharacteristic_emg0 = null;
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt = null;
        }
    }

    public void setType(int type)
    {
        countingProcess.setType(type);
    }
}
