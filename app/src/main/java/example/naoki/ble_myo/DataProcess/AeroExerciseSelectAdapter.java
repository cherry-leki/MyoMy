package example.naoki.ble_myo.DataProcess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import example.naoki.ble_myo.R;

/**
 * Created by Leki on 2016-09-09.
 */
public class AeroExerciseSelectAdapter extends BaseAdapter {

    private ArrayList<AeroExerciseSelectItem> aeroExerciseSelectItemArrayList = new ArrayList<AeroExerciseSelectItem>();

    public AeroExerciseSelectAdapter() {

    }

    @Override
    public int getCount() { return aeroExerciseSelectItemArrayList.size(); }

    @Override
    public Object getItem(int position) { return aeroExerciseSelectItemArrayList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_exerciselist, parent, false);
        }

        TextView titleTextView = (TextView) convertView.findViewById(R.id.exerciseName);
        TextView descTextView = (TextView) convertView.findViewById(R.id.exerciseDesc);

        AeroExerciseSelectItem aeroExerciseSelectItem = aeroExerciseSelectItemArrayList.get(pos);

        titleTextView.setText(aeroExerciseSelectItem.getTitle());
        descTextView.setText(aeroExerciseSelectItem.getDesc());

        return convertView;
    }

    public void addItem(String title, String desc){
        AeroExerciseSelectItem item = new AeroExerciseSelectItem();

        item.setTitle(title);
        item.setDesc(desc);

        aeroExerciseSelectItemArrayList.add(item);
    }
}