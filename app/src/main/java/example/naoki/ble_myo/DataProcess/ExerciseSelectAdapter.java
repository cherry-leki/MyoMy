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
 * Created by Leki on 2016-08-10.
 */
public class ExerciseSelectAdapter extends BaseAdapter {

    private ArrayList<ExerciseSelectItem> exerciseSelectItemArrayList = new ArrayList<ExerciseSelectItem>();

    public ExerciseSelectAdapter() {

    }
    @Override
    public int getCount() {
        return exerciseSelectItemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return exerciseSelectItemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

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

        ExerciseSelectItem exerciseSelectItem = exerciseSelectItemArrayList.get(pos);

        titleTextView.setText(exerciseSelectItem.getTitle());
        descTextView.setText(exerciseSelectItem.getDesc());

        return convertView;
    }

    public void addItem(String title, String desc){
        ExerciseSelectItem item = new ExerciseSelectItem();

        item.setTitle(title);
        item.setDesc(desc);

        exerciseSelectItemArrayList.add(item);
    }
}
