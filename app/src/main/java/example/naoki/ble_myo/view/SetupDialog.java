package example.naoki.ble_myo.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;

import example.naoki.ble_myo.Activity.MainActivity;
import example.naoki.ble_myo.Interface.GetSetupData;
import example.naoki.ble_myo.R;

/**
 * Created by Sabarada on 2016-08-17.
 */
public class SetupDialog {

    AlertDialog dialog;

    CheckBox counterCheckBox;
    CheckBox timeCheckBox;
    EditText counterEditText;
    EditText setEditText;
    NumberPicker hourNumberPicker;
    NumberPicker minuteNumberPicker;
    NumberPicker secondNumberPicker;
    GetSetupData getSetupData;

    public void setSetupData(GetSetupData getSetupData)
    {
        this.getSetupData = getSetupData;
    }

    public SetupDialog(Context context)
    {
        LayoutInflater inflater = ((MainActivity)context).getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_setup, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        counterCheckBox = (CheckBox)dialogView.findViewById(R.id.counterCheckbox);
        timeCheckBox = (CheckBox)dialogView.findViewById(R.id.timeCheckbox);
        counterEditText = (EditText)dialogView.findViewById(R.id.counterEditText);
        setEditText = (EditText)dialogView.findViewById(R.id.setEditText);
        hourNumberPicker = (NumberPicker)dialogView.findViewById(R.id.hour);
        minuteNumberPicker = (NumberPicker)dialogView.findViewById(R.id.minute);
        secondNumberPicker = (NumberPicker)dialogView.findViewById(R.id.second);

        hourNumberPicker.setMinValue(0);
        hourNumberPicker.setMaxValue(99);
        hourNumberPicker.setWrapSelectorWheel(false);

        minuteNumberPicker.setMinValue(0);
        minuteNumberPicker.setMaxValue(59);
        minuteNumberPicker.setWrapSelectorWheel(false);

        secondNumberPicker.setMinValue(0);
        secondNumberPicker.setMaxValue(59);
        secondNumberPicker.setWrapSelectorWheel(false);

        builder.setView(dialogView);
        builder.setTitle("Setup");
        builder.setPositiveButton("Ok", positiveButtonClick);
        builder.setNegativeButton("Cancel", negativeButtonClick);

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
    }



    private DialogInterface.OnClickListener positiveButtonClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            if(counterCheckBox.isChecked()) getSetupData.getCountData(Integer.parseInt(counterEditText.getText().toString()), Integer.parseInt(setEditText.getText().toString()));
            if(timeCheckBox.isChecked()) getSetupData.getTimeData(hourNumberPicker.getValue(), minuteNumberPicker.getValue(), secondNumberPicker.getValue());
        }
    };

    private DialogInterface.OnClickListener negativeButtonClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    public void showDialog()
    {
        dialog.show();
    }
}
