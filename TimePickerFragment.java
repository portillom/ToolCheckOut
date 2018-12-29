package com.michaelportillo.android.toolcheckout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by USER on 12/8/18.
 */

public class TimePickerFragment extends DialogFragment {

    public static final String EXTRA_TIME = "com.michaelportillo.android.toolcheckout.date";

    private static final String ARG_TIME = "time";

    private TimePicker mTimePicker;
    private Date mDate;

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        mDate = (Date) getArguments().getSerializable(ARG_TIME);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);
        mTimePicker.setIs24HourView(false);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            mTimePicker.setHour(hours);
            mTimePicker.setMinute(minutes);
        }else{
            Toast.makeText(getActivity(),"Requries API 23 or higher", Toast.LENGTH_LONG).show();
            return null;
        }


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final Calendar cal = Calendar.getInstance();
                                cal.setTime(mDate);
                                 int year = cal.get(Calendar.YEAR);
                                 int month = cal.get(Calendar.MONTH);
                                 int day = cal.get(Calendar.DAY_OF_MONTH);
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                    int hours = mTimePicker.getHour();
                                    int minutes = mTimePicker.getMinute();
                                    mDate = new GregorianCalendar(year, month, day, hours, minutes).getTime();
                                    sendResult(Activity.RESULT_OK, mDate);
                                }
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, Date date){
        if(getTargetFragment() == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
