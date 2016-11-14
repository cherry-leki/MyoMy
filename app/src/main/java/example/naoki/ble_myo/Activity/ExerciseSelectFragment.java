package example.naoki.ble_myo.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.HashMap;

import example.naoki.ble_myo.DataProcess.AeroExerciseSelectAdapter;
import example.naoki.ble_myo.DataProcess.AnaeroExerciseSelectAdapter;
import example.naoki.ble_myo.DataProcess.ExerciseSelectItem;
import example.naoki.ble_myo.R;

/**
 * Created by Leki on 2016-09-02.
 */
public class ExerciseSelectFragment extends Fragment {

    public Button anaerobicButton, aerobicButton;
    public ListView anaeroexerciseList, aeroexerciseList;

    private AnaeroExerciseSelectAdapter anaeroExerciseSelectAdapter;
    private AeroExerciseSelectAdapter aeroExerciseSelectAdapter;

    public SwitchScreen switchScreen;

    private static final int AERO = 0;
    private static final int ANAERO = 1;

    private HashMap<Integer, String> aeroExercise = new HashMap<>();
    private HashMap<Integer, String> anaeroExercise = new HashMap<>();

    public interface SwitchScreen
    {
        void switchNextScreen();
        void selectExercise(String name, int type);
    }

    public void setSwitchScreen(SwitchScreen switchScreen)
    {
        this.switchScreen = switchScreen;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aeroExerciseSelectAdapter = new AeroExerciseSelectAdapter();
        anaeroExerciseSelectAdapter = new AnaeroExerciseSelectAdapter();

        aeroExercise.put(0, "줄넘기");
        aeroExercise.put(1, "팔벌려뛰기");

        anaeroExercise.put(0, "아령운동");
        anaeroExercise.put(1, "팔굽혀펴기");
        anaeroExercise.put(2, "윗몸일으키기");
        anaeroExercise.put(3, "철봉");
        anaeroExercise.put(4, "스쿼트");


        // 유산소
        aeroExerciseSelectAdapter.addItem(aeroExercise.get(0), "유산소운동");                            // 0
        aeroExerciseSelectAdapter.addItem(aeroExercise.get(1), "유산소운동");                        // 1

                                                                                                    // 무산소
        anaeroExerciseSelectAdapter.addItem(anaeroExercise.get(0), "무산소운동");                       // 0
        anaeroExerciseSelectAdapter.addItem(anaeroExercise.get(1), "무산소운동");
        anaeroExerciseSelectAdapter.addItem(anaeroExercise.get(2), "무산소운동");
        anaeroExerciseSelectAdapter.addItem(anaeroExercise.get(3), "무산소운동");
        anaeroExerciseSelectAdapter.addItem(anaeroExercise.get(4), "무산소운동");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_exerciseselect, null);
        anaerobicButton = (Button)view.findViewById(R.id.anaerobicbutton);
        aerobicButton = (Button)view.findViewById(R.id.aerobicbutton);
        anaeroexerciseList = (ListView)view.findViewById(R.id.anaeroexerciseList);
        aeroexerciseList = (ListView)view.findViewById(R.id.aeroexerciseList);

        aeroexerciseList.setAdapter(aeroExerciseSelectAdapter);
        anaeroexerciseList.setAdapter(anaeroExerciseSelectAdapter);

        anaeroexerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                AnaeroExerciseSelectAdapter.AnaeroExerciseSelectItem item = (AnaeroExerciseSelectAdapter.AnaeroExerciseSelectItem)parent.getItemAtPosition(position);
                switchScreen.selectExercise(item.getDesc(), ANAERO);
                switchScreen.switchNextScreen();
            }
        });

        aeroexerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                AeroExerciseSelectAdapter.AeroExerciseSelectItem item = (AeroExerciseSelectAdapter.AeroExerciseSelectItem)parent.getItemAtPosition(position);
                switchScreen.selectExercise(item.getDesc(), AERO);
                switchScreen.switchNextScreen();
            }
        });

        anaerobicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anaerobicButton.setBackground(getResources().getDrawable(R.drawable.introbuttons_touch));
                anaerobicButton.setTextColor(Color.WHITE);
                aerobicButton.setBackground(getResources().getDrawable(R.drawable.introbuttons_notouch));
                aerobicButton.setTextColor(Color.BLACK);

                anaeroexerciseList.setVisibility(View.VISIBLE);
                aeroexerciseList.setVisibility(View.INVISIBLE);
            }
        });

        aerobicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anaerobicButton.setBackground(getResources().getDrawable(R.drawable.introbuttons_notouch));
                anaerobicButton.setTextColor(Color.BLACK);
                aerobicButton.setBackground(getResources().getDrawable(R.drawable.introbuttons_touch));
                aerobicButton.setTextColor(Color.WHITE);

                aeroexerciseList.setVisibility(View.VISIBLE);
                anaeroexerciseList.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }
}
