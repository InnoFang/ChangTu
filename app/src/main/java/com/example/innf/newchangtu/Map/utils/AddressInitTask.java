package com.example.innf.newchangtu.Map.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;

import cn.qqtheme.framework.picker.AddressPicker;

/**
 * 获取地址数据并显示地址选择器
 */
public class AddressInitTask extends AsyncTask<String, Void, ArrayList<AddressPicker.Province>> {
    private Activity activity;
    private ProgressDialog dialog;
    private String selectedProvince = "", selectedCity = "", selectedCounty = "";
    private boolean hideCounty = false;
    private StringBuilder address = new StringBuilder();
    private TextView mTextView;
    private Button mButton;
    /**
     * 初始化为不显示区县的模式
     *
     * @param activity
     * @param hideCounty is hide County
     */
    public AddressInitTask(Activity activity, boolean hideCounty) {
        this.activity = activity;
        this.hideCounty = hideCounty;
        dialog = ProgressDialog.show(activity, null, "正在初始化数据...", true, true);
    }

    public AddressInitTask(Activity activity, TextView textView) {
        this.activity = activity;
        this.mTextView = textView;
        dialog = ProgressDialog.show(activity, null, "正在初始化数据...", true, true);
    }
    public AddressInitTask(Activity activity, Button button) {
        this.activity = activity;
        this.mButton = button;
        dialog = ProgressDialog.show(activity, null, "正在初始化数据...", true, true);
    }

    @Override
    protected ArrayList<AddressPicker.Province> doInBackground(String... params) {
        if (params != null) {
            switch (params.length) {
                case 1:
                    selectedProvince = params[0];
                    break;
                case 2:
                    selectedProvince = params[0];
                    selectedCity = params[1];
                    break;
                case 3:
                    selectedProvince = params[0];
                    selectedCity = params[1];
                    selectedCounty = params[2];
                    break;
                default:
                    break;
            }
        }
        ArrayList<AddressPicker.Province> data = new ArrayList<AddressPicker.Province>();
        try {
            String json = AssetsUtils.readText(activity, "city.json");
            data.addAll(JSON.parseArray(json, AddressPicker.Province.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(ArrayList<AddressPicker.Province> result) {
        dialog.dismiss();
        if (result.size() > 0) {
            AddressPicker picker = new AddressPicker(activity, result);
            picker.setHideCounty(hideCounty);
            picker.setSelectedItem(selectedProvince, selectedCity, selectedCounty);
            picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
                @Override
                public void onAddressPicked(AddressPicker.Province province, AddressPicker.City city, AddressPicker.County county) {
                    if (null != mTextView){
                        mTextView.setText(getAddress(province, city, county));
                    }else if (null != mButton){
                        mButton.setText(getAddress(province, city, county));
                    }
                }
            });
            picker.show();
        } else {
            Toast.makeText(activity, "数据初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

    public String getAddress(AddressPicker.Province province, AddressPicker.City city, AddressPicker.County county){
        address.append(RegexValidateUtil.getChineseInString(province + ""));
        address.append(RegexValidateUtil.getChineseInString(city + ""));
        if (county != null){
            address.append(RegexValidateUtil.getChineseInString(county + ""));
        }
        Toast.makeText(activity, address.toString(), Toast.LENGTH_SHORT).show();
        return address.toString();
    }

}
