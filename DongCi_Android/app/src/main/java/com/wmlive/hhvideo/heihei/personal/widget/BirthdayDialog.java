package com.wmlive.hhvideo.heihei.personal.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.personal.widget.wheel.AbstractWheelTextAdapter;
import com.wmlive.hhvideo.heihei.personal.widget.wheel.OnWheelScrollListener;
import com.wmlive.hhvideo.heihei.personal.widget.wheel.WheelView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import cn.wmlive.hhvideo.R;


public class BirthdayDialog extends Dialog implements OnClickListener {

    private Context mContext;
    private WheelView first, second, third;
    private ArrayList<String> provinces = new ArrayList<String>(), months = new ArrayList<String>(),
            days = new ArrayList<String>();
    private List<ArrayList<String>> citys = new ArrayList<ArrayList<String>>();
    private List<List<ArrayList<String>>> area = new ArrayList<List<ArrayList<String>>>();
    private CountryAdapter proAdapter, cityAdapter, areAdapter;
    private int nowYear, nowMonth, nowDay, minyear, minmonth;


    private Date currentDate;

    public BirthdayDialog(Context context, Date currentDate) {
        super(context, R.style.BaseDialogTheme);
        this.mContext = context;
        this.currentDate = currentDate;
        if (this.currentDate == null) {
            this.currentDate = new Date();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_city_choose);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Calendar calendar = Calendar.getInstance();
        nowYear = calendar.get(Calendar.YEAR);
        nowMonth = calendar.get(Calendar.MONTH) + 1;
        nowDay = calendar.get(calendar.DAY_OF_MONTH);
        first = (WheelView) findViewById(R.id.city_choose_1);
        second = (WheelView) findViewById(R.id.city_choose_2);
        third = (WheelView) findViewById(R.id.city_choose_3);

        proAdapter = new CountryAdapter(mContext);
        cityAdapter = new CountryAdapter(mContext);
        areAdapter = new CountryAdapter(mContext);
        first.setVisibleItems(5);
        first.setWheelBackground(R.color.transparent);
        first.setWheelForeground(R.drawable.chen2_city_bg);
        first.setViewAdapter(proAdapter);
        first.setShadowColor(0x00000000, 0x00000000, 0x00000000);
        second.setVisibleItems(5);
        second.setViewAdapter(cityAdapter);
        second.setWheelBackground(R.color.transparent);
        second.setWheelForeground(R.drawable.chen2_city_bg);
        second.setShadowColor(0x00000000, 0x00000000, 0x00000000);
        third.setVisibleItems(5);
        third.setViewAdapter(areAdapter);
        third.setWheelBackground(R.color.transparent);
        third.setWheelForeground(R.drawable.chen2_city_bg);
        third.setShadowColor(0x00000000, 0x00000000, 0x00000000);
        findViewById(R.id.city_choose_cancle).setOnClickListener(this);
        findViewById(R.id.city_choose_ok).setOnClickListener(this);
        first.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                if (first.getCurrentItem() == provinces.size() - 1) {// 今年
                    cityAdapter.updata(months.subList(0, nowMonth));
                } else {
                    cityAdapter.updata(months);
                }
                third.setCurrentItem(0);
            }
        });
        second.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                if (first.getCurrentItem() == provinces.size() - 1 && second.getCurrentItem() == nowMonth - 1) {// 今年今月
                    areAdapter.updata(days.subList(0, nowDay));
                } else {
                    areAdapter.updata(days.subList(0, getMaxDay()));
                }
                third.setCurrentItem(0);
            }
        });
        getData();
    }

//    public static String getConstellation(Integer month, Integer day) {
//        String str = HostApplication.getInstance().getString(R.string.user_constellation);
//        Integer[] arr = {20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};
//        Integer num = month * 2 - (day < arr[month - 1] ? 2 : 0);
//        return str.substring(num, num + 2);
//    }

    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {

        private List<String> list = new ArrayList<String>();

        public void updata(List<String> list) {
            this.list = list;
            notifyDataInvalidatedEvent();
        }

        /**
         * Constructor
         */
        protected CountryAdapter(Context context) {
            super(context, R.layout.wheel_text_item, R.id.text_item_content);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            TextView text = (TextView) view.findViewById(R.id.text_item_content);
            text.setLines(1);
            text.setEllipsize(TruncateAt.END);
            view.findViewById(R.id.text_item_right).setVisibility(View.GONE);
            view.findViewById(R.id.text_item_bottom).setVisibility(View.GONE);
            if (list.size() > 0) {
                text.setText(list.get(index));
            }
            return view;
        }

        @Override
        public int getItemsCount() {
            if (list.size() == 0) {
                return 1;
            }
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            if (list.size() == 0) {
                return "";
            }
            return list.get(index);
        }
    }

    private int cityTag = -1, areaTag = -1;

    private void getData() {

        GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
        calendar.setTime(currentDate);
        int currentYear = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int yearIndex = 0;
        int monthIndex = 0;
        int dayIndex = 0;

        for (int i = 1900; i <= nowYear; i++) {
            provinces.add(i + mContext.getString(R.string.user_year));
            if (i == currentYear) {
                yearIndex = i - 1900;
            }
        }
        for (int i = 1; i <= 12; i++) {
            months.add(i + mContext.getString(R.string.user_month));
            if (i == month) {
                monthIndex = i - 1;
            }
        }
        for (int i = 1; i <= 31; i++) {
            days.add(i + mContext.getString(R.string.user_day));

            if (i == day) {
                dayIndex = i - 1;
            }

        }
        proAdapter.updata(provinces);
        first.setCurrentItem(yearIndex);

        if (first.getCurrentItem() == provinces.size() - 1) {// 今年
            cityAdapter.updata(months.subList(0, nowMonth));
        } else {
            cityAdapter.updata(months);
        }

        second.setCurrentItem(monthIndex);

        if (first.getCurrentItem() == provinces.size() - 1 && second.getCurrentItem() == nowMonth - 1) {// 今年今月
            areAdapter.updata(days.subList(0, nowDay));
        } else {
            areAdapter.updata(days.subList(0, getMaxDay()));
        }
        third.setCurrentItem(dayIndex);
    }

    private int getInt(int posi, int tag) {
        if (tag == 0) {
            return Integer.parseInt(provinces.get(posi).replace(mContext.getString(R.string.user_year), ""));
        } else if (tag == 1) {
            return Integer.parseInt(months.get(posi).replace(mContext.getString(R.string.user_month), ""));
        } else {
            return Integer.parseInt(days.get(posi).replace(mContext.getString(R.string.user_day), ""));
        }
    }

    private int getMaxDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, first.getCurrentItem() + 1920);
        calendar.set(Calendar.MONTH, second.getCurrentItem());
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        int day = calendar.get(Calendar.DATE);
        return day;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_choose_cancle:
                this.mOnDatePickListener = null;
                dismiss();
                break;
            case R.id.city_choose_ok:
                if (this.mOnDatePickListener != null) {
                    int year = getInt(first.getCurrentItem(), 0);
                    int month = getInt(second.getCurrentItem(), 1);
                    int day = getInt(third.getCurrentItem(), 2);
                    this.mOnDatePickListener.onDatePick(year, month, day);
                    this.mOnDatePickListener = null;
                }
                dismiss();
                break;
        }

    }

    private OnDatePickListener mOnDatePickListener;

    public void setOnDatePickListener(OnDatePickListener mOnDateListener) {
        this.mOnDatePickListener = mOnDateListener;
    }

    public static interface OnDatePickListener {
        public void onDatePick(int year, int month, int day);
    }

}
