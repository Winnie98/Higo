package com.yhl.higo.ec.main.index;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.main.market.ProductImageAdapter;
import com.yhl.higo.ec.sign.SignInCodeDelegate;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.callback.CallBackManager;
import com.yhl.higo.util.callback.CallbackType;
import com.yhl.higo.util.callback.IGlobalCallback;
import com.yhl.higo.util.log.HigoLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/20/020.
 */

public class PromoPublishDelegate extends HigoDelegate {

    @BindView(R2.id.edt_publish_promo_name)
    EditText mPromoName = null;
    @BindView(R2.id.edt_publish_promo_source)
    EditText mPromoSource = null;
    @BindView(R2.id.edt_publish_promo_detail)
    EditText mPromoDetail = null;

    @BindView(R2.id.edt_publish_promo_price)
    EditText mPromoPrice = null;

//    @BindView(R2.id.recycler_publish_promo)
//    RecyclerView mRecyclerView = null;

    @BindView(R2.id.edt_add_img)
    EditText mEdit;

//    @BindView(R2.id.custom_auto_photo_layout)
//    AutoPhotoLayout mAutoPhotoLayout = null;

    private Integer[] mCategorySpinnerID = new Integer[]{};
    private String[] mCategorySpinnerData = new String[]{};

    private Integer[] mCountrySpinnerID = new Integer[]{};
    private String[] mCountrySpinnerData = new String[]{};
    private int mCategoryId;
    private int mCountryId;
    private List<String> imgArray = new ArrayList<>();
    private ProductImageAdapter mAdapter  =  null;

//    protected String mSubImages;


    @OnClick(R2.id.icon_publish_promo_back)
    void onClickPublishBack(){
        getSupportDelegate().pop();
    }

    @OnClick(R2.id.btn_add_promo_img)
    void onClickAddImg(){
        //开始照相机或选择图片
        CallBackManager.getInstance()
                .addCallback(CallbackType.ON_CROP, new IGlobalCallback<Uri>() {
                    @Override
                    public void executeCallback(Uri args) {
                        HigoLogger.d("ON_CROP", args);

                        //利用SpannableString类，实现复杂的文本
                        SpannableString spanStr=new SpannableString("imag");
//                        SpannableString spanStr=new SpannableString("img");
                        //获取Drawable文件夹下的face.png图片
//                        Drawable drawable=MainActivity.this.getResources().getDrawable(R.drawable.face);

                        //将图片原样大小显示在界面上
//                        drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                        //将图片沿着输入框底线输入
//                        ImageSpan span=new ImageSpan(drawable,ImageSpan.ALIGN_BASELINE);
                        ImageSpan span=new ImageSpan(getContext(),args);

                        span.getDrawable().setBounds(0,0,120,120);
                        //将图片以扩展的方式添加到已有的输入内容中，形成新的输入内容
                        spanStr.setSpan(span,0,4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        //获取光标的位置
                        int cursor=mEdit.getSelectionStart();
                        mEdit.getText().insert(cursor,spanStr);

                        HigoLogger.d("FILE",args.getPath());

                        RestClient.builder()
                                .url("common/file/upload.do")
//                                .file(args.getPath())
                                .file(new File(args.getPath()))
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        HigoLogger.d("UPLOAD_AVATAR",response);
                                        final int status = JSON.parseObject(response).getInteger("status");
                                        final String msg = JSON.parseObject(response).getString("msg");
                                        switch (status){
                                            case 0:
                                                Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                                final String uri = JSON.parseObject(response).getJSONObject("data").getString("uri");
                                                final String url = JSON.parseObject(response).getJSONObject("data").getString("url");

                                                imgArray.add(uri);
                                                break;
                                            case 1:
                                                Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                                getParentDelegate().getSupportDelegate().start( new SignInDelegate());
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                })
                                .build()
                                .upload();

                    }
                });
        startCameraWithCheck();
    }


    @OnClick(R2.id.txt_publish_promo)
    void onClickPublishPromo(){

        final String subImages = listToString(imgArray);
        HigoLogger.d("mSubImages_Publish",subImages);

        RestClient.builder()
                .url("user/promo/add_promo.do")
                .params("categoryId",mCategoryId)
                .params("countryId",mCountryId)
                .params("name",mPromoName.getText().toString())
                .params("subImages",subImages)
//                .params("subImages",mSubImages)
                .params("detail",mPromoDetail.getText().toString())
                .params("source",mPromoSource.getText().toString())
                .params("price",mPromoPrice.getText().toString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("PUBLISH_PROMO",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final String data = JSON.parseObject(response).getString("data");
                                Toast.makeText(getContext(),data,Toast.LENGTH_SHORT).show();
                                getSupportDelegate().pop();
                                break;
                            case 3:
                                final String need_login = JSON.parseObject(response).getString("msg");
                                Toast.makeText(getContext(),need_login,Toast.LENGTH_SHORT).show();
                                getSupportDelegate().start(new SignInCodeDelegate());
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

    //把list转换为一个用逗号分隔的字符串
    public static String listToString(List list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    sb.append(list.get(i) + ",");
                } else {
                    sb.append(list.get(i));
                }
            }
        }
        return sb.toString();
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_promo_publish;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull final View rootView) {

        RestClient.builder()
                .url("user/category/get_enabled_category_list.do")
                .loader(getContext())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("CATEGORY_LIST",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final JSONArray dataArray = JSON.parseObject(response).getJSONArray("data");

                                final List<Integer> categoryId = new ArrayList<>();
                                final List<String> categoryName = new ArrayList<>();
                                final int size = dataArray.size();
                                for (int i = 0; i < size; i++) {
                                    final JSONObject data = dataArray.getJSONObject(i);
                                    final int id = data.getInteger("id");
                                    final String name = data.getString("name");
                                    categoryId.add(id);
                                    categoryName.add(name);
                                }
                                mCategorySpinnerID = categoryId.toArray(new Integer[categoryId.size()]);
                                mCategorySpinnerData = categoryName.toArray(new String[categoryName.size()]);

                                //创建类别下拉框的数据源
                                Spinner mCategorySpinner = ButterKnife.findById(rootView, R.id.spinner_publish_promo_category);
                                //创建适配器(下拉框的数据源是来自适配器)
                                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,mCategorySpinnerData);
                                //将适配器中的数据显示在Spinner上
                                mCategorySpinner.setAdapter(categoryAdapter);
                                mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        mCategoryId = mCategorySpinnerID[position];
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                                break;
                            default:
                                break;

                        }
                    }
                })
                .build()
                .get();

        //获取国家列表
        RestClient.builder()
                .url("user/country/get_enabled_country_list.do")
                .loader(getContext())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("COUNTRY_LIST",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final JSONArray dataArray = JSON.parseObject(response).getJSONArray("data");

                                final List<Integer> categoryId = new ArrayList<>();
                                final List<String> categoryName = new ArrayList<>();

                                final int size = dataArray.size();
                                for (int i = 0; i < size; i++) {
                                    final JSONObject data = dataArray.getJSONObject(i);
                                    final int id = data.getInteger("id");
                                    final String name = data.getString("name");
                                    categoryId.add(id);
                                    categoryName.add(name);
                                }
                                mCountrySpinnerID = categoryId.toArray(new Integer[categoryId.size()]);
                                mCountrySpinnerData = categoryName.toArray(new String[categoryName.size()]);

                                //创建国家下拉框的数据源
                                for(String name : mCountrySpinnerData) {
                                    HigoLogger.d("NAME",name);
                                }
                                Spinner mCountrySpinner = ButterKnife.findById(rootView, R.id.spinner_publish_promo_country);
                                //创建适配器(下拉框的数据源是来自适配器)
                                ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,mCountrySpinnerData);
                                //将适配器中的数据显示在Spinner上
                                mCountrySpinner.setAdapter(countryAdapter);
                                mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        mCountryId = mCountrySpinnerID[position];
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });


                                break;
                            default:
                                break;
                        }
                    }
                })
                .build()
                .get();


    }
}
