package com.yhl.higo.ui.date;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Administrator on 2018/5/25/025.
 */

public class DateDialogUtil {

    public interface IDateListener {

        void onDateChange(String date);
    }

    private IDateListener mDateListener = null;

    public void setDateListener(IDateListener listener){
        this.mDateListener = listener;
    }

    public void showDialog(Context context){
        final LinearLayout linearLayout = new LinearLayout(context);
        final DatePicker picker = new DatePicker(context);
        final LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

        picker.setLayoutParams(layoutParams);

        picker.init(1990, 1, 1, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                final Calendar calendar = Calendar.getInstance();
                calendar.set(year,monthOfYear,dayOfMonth);
                final SimpleDateFormat format = new SimpleDateFormat("yyyy年mm月dd日", Locale.getDefault());
                final String data = format.format(calendar.getTime());
                if (mDateListener != null){
                    mDateListener.onDateChange(data);
                }
            }
        });
        linearLayout.addView(picker);

        new AlertDialog.Builder(context)
                .setTitle("选择日期")
                .setView(linearLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

}
