package example.naoki.ble_myo.Activity;

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

import example.naoki.ble_myo.DataProcess.AeroExerciseSelectAdapter;
import example.naoki.ble_myo.DataProcess.AnaeroExerciseSelectAdapter;
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

        aeroExerciseSelectAdapter.addItem("줄넘기", "유산소운동");

        anaeroExerciseSelectAdapter.addItem("아령운동", "무산소운동");
        anaeroExerciseSelectAdapter.addItem("팔굽혀펴기", "무산소운동");
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

                Class item = parent.getItemAtPosition(position).getClass();
                Field titleField = null;
                Field  descField = null;

                try {
                    titleField = item.getDeclaredField("titleStr");
                    descField = item.getDeclaredField("descStr");
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }

                String titleStr = titleField.toString();
                String descStr = descField.toString();

                System.out.println(titleStr);

                switchScreen.selectExercise(descStr, ANAERO);
                switchScreen.switchNextScreen();
            }
        });

        aeroexerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Class item = parent.getItemAtPosition(position).getClass();
                Field titleField = null;
                Field  descField = null;

                try {
                    titleField = item.getDeclaredField("titleStr");
                    descField = item.getDeclaredField("descStr");
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }

                String titleStr = titleField.toGenericString();
                String descStr = descField.toGenericString();

                System.out.println(titleStr);

                switchScreen.selectExercise(descStr, AERO);
                switchScreen.switchNextScreen();
            }
        });

        anaerobicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anaerobicButton.setBackground(getResources().getDrawable(R.drawable.introbuttons_touch));
                aerobicButton.setBackground(getResources().getDrawable(R.drawable.introbuttons_notouch));

                anaeroexerciseList.setVisibility(View.VISIBLE);
                aeroexerciseList.setVisibility(View.INVISIBLE);
            }
        });

        aerobicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anaerobicButton.setBackground(getResources().getDrawable(R.drawable.introbuttons_notouch));
                aerobicButton.setBackground(getResources().getDrawable(R.drawable.introbuttons_touch));

                aeroexerciseList.setVisibility(View.VISIBLE);
                anaeroexerciseList.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }
}
