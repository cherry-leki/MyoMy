package example.naoki.ble_myo.Activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echo.holographlibrary.LineGraph;

import example.naoki.ble_myo.DataProcess.NonSwipeableViewPager;
import example.naoki.ble_myo.MyoGattCallback;
import example.naoki.ble_myo.R;

/**
 * Created by Sabarada on 2016-09-21.
 */
@SuppressLint("ValidFragment")
public class ReadyFragment extends Fragment {

    private Context context;
    private Handler handler;
    private Fragment fragment;
    private NonSwipeableViewPager viewPager;

    private ReadyListener readyListener;
    private TextView readyText;

    public static final int EXERCISE_SELECT = 0;
    public static final int FIND_MYO = 1;
    public static final int EXERCISE_TEST = 2;

    public interface ReadyListener
    {
        void setGraph(LineGraph lineGraph);
        void fragmentChange();
        void send(BluetoothDevice device, String deviceName);
    }

    private ExerciseSelectFragment exerciseSelectFragment;
    private ExerciseTestFragment exerciseTestFragment;
    private FindMyoFragment findMyoFragment;

    public void setFragmentChange(ReadyListener readyListener)
    {
        this.readyListener = readyListener;
    }

    public ReadyFragment(Context context)
    {
        this.context = context;
        this.handler = ((MainActivity)context).mHandler;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = (NonSwipeableViewPager)view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));

        readyText = (TextView)view.findViewById(R.id.readyText);

        exerciseSelectFragment = new ExerciseSelectFragment();
        exerciseSelectFragment.setSwitchScreen(new ExerciseSelectFragment.SwitchScreen() {
            @Override
            public void switchNextScreen() {
                viewPager.setCurrentItem(FIND_MYO);
                readyText.setText(getString(R.string.text_find_myo));
            }
            @Override
            public void selectExercise(String name, int type) {

            }
        });

        exerciseTestFragment = new ExerciseTestFragment(this.handler);
        exerciseTestFragment.setSwitchScreen(new ExerciseTestFragment.ButtonInterface() {
            @Override
            public void switchPrevScreen() {
                viewPager.setCurrentItem(FIND_MYO);
                readyText.setText(getString(R.string.text_find_myo));
            }

            @Override
            public void startButtonClick(LineGraph lineGraph) {
                readyListener.setGraph(lineGraph);
            }

            @Override
            public void nextActivity() {
                readyListener.fragmentChange();
//                closeBLEGatt();
            }
        });

        findMyoFragment = new FindMyoFragment(this.handler, (MainActivity)context);
        findMyoFragment.setSwitchScreen(new FindMyoFragment.SwitchScreen() {
            @Override
            public void switchNextScreen() {
                viewPager.setCurrentItem(EXERCISE_TEST);
                readyText.setText(getString(R.string.text_exercise_test));
            }

            @Override
            public void switchPrevScreen() {
                viewPager.setCurrentItem(EXERCISE_SELECT);
                readyText.setText(getString(R.string.text_exercise_select));
            }

            @Override
            public void selectDevice(BluetoothDevice devices, String deviceName) {
                readyListener.send(devices, deviceName);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_ready, container, false);
        return view;
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position < EXERCISE_SELECT || EXERCISE_TEST + 1 <= position) return null;

            switch (position)
            {
                case  EXERCISE_SELECT:
                    fragment = exerciseSelectFragment;
                    break;
                case FIND_MYO:
                    fragment = findMyoFragment;
                    break;
                case EXERCISE_TEST:
                    fragment = exerciseTestFragment;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
