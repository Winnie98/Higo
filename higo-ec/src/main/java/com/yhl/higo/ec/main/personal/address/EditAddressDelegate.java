package com.yhl.higo.ec.main.personal.address;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.main.personal.address.pickerView.JsonBean;
import com.yhl.higo.ec.main.personal.address.pickerView.JsonFileReader;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.log.HigoLogger;

import org.json.JSONArray;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/30/030.
 */

public class EditAddressDelegate extends HigoDelegate {

    private int mShippingId = -1;
    private String mName = null;
    private String mPhone = null;
    private String mProvince = null;
    private String mCity = null;
    private String mDistrict = null;
    private String mAddress = null;
    private String mZip = null;

    private boolean isReSelected = false;//判断是否重新选择了城市

//    private OptionsPickerView mCityPikerView;

//    private List<ProvinceModel> mProvinces;
//    private ArrayList<ArrayList<String>> mCities = new ArrayList<>();
//    private ArrayList<ArrayList<ArrayList<String>>> mDistricts = new ArrayList<>();

    private ArrayList<JsonBean> mProvinces = new ArrayList<>();
    private ArrayList<ArrayList<String>> mCities = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> mDistricts = new ArrayList<>();

    Bundle mBundle = new Bundle();


    @BindView(R2.id.tv_create_address)
    TextView mTvAddress = null;
    @BindView(R2.id.edt_create_receiver)
    EditText mEdtReceiver;
    @BindView(R2.id.edt_create_phone)
    EditText mEdtPhone;
    @BindView(R2.id.edt_create_address_details)
    EditText mEdtAddressDetails;
    @BindView(R2.id.edt_create_zipcode)
    EditText mEdtZip;

    @OnClick(R2.id.tv_create_address)
    void onClickCityPick() {
//        Toast.makeText(getContext(),"你点击了按钮",Toast.LENGTH_LONG).show();
//        mCityPikerView.show();
        showPickerView();
    }

    @OnClick(R2.id.icon_address_return)
    void onClickReturn() {
        getSupportDelegate().pop();
    }

    @OnClick(R2.id.btn_create_save)
    void onClickSave() {
        editAddress();
    }

    public static EditAddressDelegate create(int shippingId, String name, String phone, String province, String city, String district, String address, String zip) {
        final Bundle args = new Bundle();
        args.putInt("shippingId", shippingId);
        args.putString("name", name);
        args.putString("phone", phone);
        args.putString("province", province);
        args.putString("city", city);
        args.putString("district", district);
        args.putString("address", address);
        args.putString("zip", zip);

        HigoLogger.d("ADDRESS_INFO", shippingId + name + phone + province + city + district + address + zip);
        final EditAddressDelegate delegate = new EditAddressDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_create_address;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        initJsonData();

        final Bundle args = getArguments();
        if (args != null) {
            mShippingId = args.getInt("shippingId");
            mName = args.getString("name");
            mPhone = args.getString("phone");
            mProvince = args.getString("province");
            mCity = args.getString("city");
            mDistrict = args.getString("district");
            mAddress = args.getString("address");
            mZip = args.getString("zip");

            HigoLogger.d("EDIT_ADDRESS_INFO_GET_INFO",
                    mShippingId + mName + mPhone + mProvince + mCity + mDistrict + mAddress + mZip);

            mEdtReceiver.setText(mName);
            mEdtPhone.setText(mPhone);
            mTvAddress.setText(mProvince + mCity + mDistrict);
            mEdtAddressDetails.setText(mAddress);
            mEdtZip.setText(mZip);

        }
//        init();
    }

    private void showPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(getContext(), new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String text = mProvinces.get(options1).getPickerViewText() +
                        mCities.get(options1).get(options2) +
                        mDistricts.get(options1).get(options2).get(options3);
                mBundle.putString("province", String.valueOf(mProvinces.get(options1).getName()));
                mBundle.putString("city", String.valueOf(mCities.get(options1).get(options2)));
                mBundle.putString("district", String.valueOf(mDistricts.get(options1).get(options2).get(options3)));
                mTvAddress.setText(text);
            }
        }).setTitleText("选择城市")
                .setDividerColor(Color.GRAY)
                .setTextColorCenter(Color.GRAY)
                .setContentTextSize(16)
                .setOutSideCancelable(false)
                .build();

        isReSelected = true;

          /*pvOptions.setPicker(mProvinces);//一级选择器
        pvOptions.setPicker(mProvinces, mCities);//二级选择器*/
        pvOptions.setPicker(mProvinces, mCities, mDistricts);//三级选择器
        pvOptions.show();
    }


    private void initJsonData() {   //解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        //  获取json数据
        String JsonData = JsonFileReader.getJson(getContext(), "province_data.json");
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体


        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        mProvinces = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            mCities.add(CityList);

            /**
             * 添加地区数据
             */
            mDistricts.add(Province_AreaList);
        }
    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }


    public void editAddress(){
        String province;
        String city;
        String district;

        final String receiver = mEdtReceiver.getText().toString();
        String phone = mEdtPhone.getText().toString();
//        String province = mBundle.getString("province");
//        String city = mBundle.getString("city");
//        String district = mBundle.getString("district");
        String address = mEdtAddressDetails.getText().toString();
        String zip = mEdtZip.getText().toString();
        if (isReSelected){
            province = mBundle.getString("province");
            city = mBundle.getString("city");
            district = mBundle.getString("district");
        }else {
            province = mProvince;
            city = mCity;
            district = mDistrict;
        }

//        Toast.makeText(getContext(),receiver+"\n"+phone+"\n"+province+"\n"+city+"\n"+district+"\n"+address+"\n"+zip,Toast.LENGTH_LONG).show();

        //访问服务器
        RestClient.builder()
                .url("user/address/update_address.do")
                .params("id",mShippingId)
                .params("name",receiver)
                .params("phone",phone)
                .params("province",province)
                .params("city",city)
                .params("district",district)
                .params("address",address)
                .params("zip",zip)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("EDIT_ADDRESS",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                getSupportDelegate().pop();
                                break;
                            default:
                                final String msg = JSON.parseObject(response).getString("msg");
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .build()
                .post();

    }


}
