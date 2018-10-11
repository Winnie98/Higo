package com.yhl.higo.ec.main.market;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yhl.higo.app.Higo;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.sign.SignInCodeDelegate;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
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
 * Created by Administrator on 2018/6/24/024.
 */

public class EditProductDelegate extends HigoDelegate {

    @BindView(R2.id.spinner_product_edit)
    Spinner mProductCategory = null;
    @BindView(R2.id.edt_product_edit_name)
    EditText mProductName = null;
    @BindView(R2.id.txt_product_edit_count)
    TextView mProductCount = null;
    @BindView(R2.id.edt_product_edit_price)
    EditText mProductPrice = null;

    @BindView(R2.id.recycler_product_edit)
    RecyclerView mRecyclerView = null;
    @BindView(R2.id.spinner_product_edit_country)
    Spinner mProductCountry = null;

    private int mProductId = -1;
    private int mCategoryId = -1;
    private int mCountryId = -1;
    private int mTypeId;

    private int mCurrentCategoryId = 0;
    private int mCurrentCountryId = 0;

    private Integer[] mCategorySpinnerID = new Integer[]{};
    private String[] mCategorySpinnerData = new String[]{};
    private Integer[] mCountrySpinnerID = new Integer[]{};
    private String[] mCountrySpinnerData = new String[]{};
    private ProductImageAdapter mAdapter  =  null;

    protected String mSubImages;
    private String mDetail;

    @OnClick(R2.id.icon_edit_product_back)
    void onClickBack(){
        getSupportDelegate().pop();
    }
    @OnClick(R2.id.img_reduce_product_edit_num)
    void onClickReduce(){
        int currentCount = Integer.parseInt(mProductCount.getText().toString());
        if (currentCount>1){
            currentCount--;
            mProductCount.setText(String.valueOf(currentCount));
        }else {
            Toast.makeText(getContext(),"该宝贝不能减少了哟~",Toast.LENGTH_SHORT).show();
        }
    }
    @OnClick(R2.id.img_increase_product_edit_num)
    void onClickIncrease(){
        int currentCount = Integer.parseInt(mProductCount.getText().toString());
        currentCount++;
        mProductCount.setText(String.valueOf(currentCount));
    }
    @OnClick(R2.id.btn_add_product_edit_img)
    void onClickAddImg(){
        //开始照相机或选择图片
        CallBackManager.getInstance()
                .addCallback(CallbackType.ON_CROP, new IGlobalCallback<Uri>() {
                    @Override
                    public void executeCallback(Uri args) {
                        HigoLogger.d("ON_CROP", args);

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
//                                                final List<MultipleItemEntity> imgData =
//                                                        new ImageCameraDataConverter().setJsonData(response).convert();
                                                if (mSubImages.isEmpty()){
                                                    mSubImages = uri;
                                                }else {
                                                    mSubImages = mSubImages+ ","+uri;
                                                }
                                                final List<MultipleItemEntity> imgData =
                                                        new ImageDataConverter().convert(mSubImages);

//                                                ProductImageAdapter adapter = new ProductImageAdapter(imgData,EditProductDelegate.this);
                                                mAdapter = new ProductImageAdapter(imgData,EditProductDelegate.this);
//                                                mAdapter.notifyItemRangeInserted(mAdapter.getData().size()+1,1);
                                                mRecyclerView.setAdapter(mAdapter);
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
    @OnClick(R2.id.txt_product_edit)
    void onClickConfirm(){
        HigoLogger.d("CURRENT_IMAGE",mSubImages);
        RestClient.builder()
                .url("user/product/update_product_info.do")
                .params("id",mProductId)
                .params("categoryId",mCategoryId)
                .params("countryId",mCountryId)
                .params("name",mProductName.getText().toString())
                .params("subImages",mSubImages)
                .params("quantity",Integer.parseInt(mProductCount.getText().toString()))
                .params("price",mProductPrice.getText().toString())
                .params("productType",mTypeId)
                .params("status",1)
                .params("detail",mDetail)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("UPDATE_PRODUCT",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        final String msg = JSON.parseObject(response).getString("msg");
                        switch (status){
                            case 0:
                                Toast.makeText(Higo.getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                                getSupportDelegate().pop();
                                break;
                            case 3:
                                Toast.makeText(Higo.getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                                getSupportDelegate().start(new SignInCodeDelegate());
                                break;
                            default:
                                Toast.makeText(Higo.getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .build()
                .post();
        getSupportDelegate().pop();
    }


    public static EditProductDelegate create(int productId,String detail){
        final Bundle args = new Bundle();
        args.putInt("productId", productId);
        args.putString("detail",detail);
        final EditProductDelegate delegate = new EditProductDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            mProductId = args.getInt("productId");
            mDetail = args.getString("detail");
        }
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_product_edit;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {

        final GridLayoutManager manager = new GridLayoutManager(getContext(),3);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.addOnItemTouchListener(ImageItemClickListener.create(this));
        RestClient.builder()
                .url("user/product/get_normal_product_detail.do")
                .params("productId",mProductId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("PRODUCT_DETAIL",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                initProductData(response);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .build()
                .get();

    }

    private void initProductData(String response){

        final JSONObject data = JSON.parseObject(response).getJSONObject("data");
        final int categoryId = data.getInteger("categoryId");
        mCurrentCategoryId = data.getInteger("categoryId");
        mCurrentCountryId = data.getInteger("countryId");
        final String categoryName = data.getString("categoryName");
        final String productName = data.getString("name");
        final int quantity = data.getInteger("quantity");
        final Double price = data.getDouble("price");
        final String imageHost = data.getString("imageHost");
        final String subImages = data.getString("subImages");
        final String detail = data.getString("detail");
        final int id = data.getInteger("id");

        final String username = data.getString("username");
        final String productType = data.getString("typeName");
        final String createTime = data.getString("createTime");
        mTypeId = data.getInteger("typeId");

        mSubImages = subImages;

        final List<MultipleItemEntity> imgData =
                new ImageDataConverter().convert(mSubImages);

        mAdapter = new ProductImageAdapter(imgData,this);
        mRecyclerView.setAdapter(mAdapter);


        mProductName.setText(productName);
        mProductCount.setText(String.valueOf(quantity));
        mProductPrice.setText(String.valueOf(price));

        initCategorySpanner();
        initCountrySpanner();

    }

    private void initCategorySpanner(){
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


//                                //创建类别下拉框的数据源
//                                for(String name : mCategorySpinnerData) {
//                                    HigoLogger.d("NAME",name);
//                                }
                                //创建适配器(下拉框的数据源是来自适配器)
                                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,mCategorySpinnerData);
                                //将适配器中的数据显示在Spinner上
                                mProductCategory.setAdapter(categoryAdapter);

                                for (int j=0 ; j<mCategorySpinnerID.length ;j++){
                                    if (mCurrentCategoryId  == mCategorySpinnerID[j]){
                                        mProductCategory.setSelection(j);
                                        mCategoryId =  mCategorySpinnerID[j];
                                    }
                                }

                                mProductCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
    }

    private void initCountrySpanner(){
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

                                final List<Integer> countryId = new ArrayList<>();
                                final List<String> countryName = new ArrayList<>();
                                final int size = dataArray.size();
                                for (int i = 0; i < size; i++) {
                                    final JSONObject data = dataArray.getJSONObject(i);
                                    final int id = data.getInteger("id");
                                    final String name = data.getString("name");
                                    countryId.add(id);
                                    countryName.add(name);
                                }
                                mCountrySpinnerID = countryId.toArray(new Integer[countryId.size()]);
                                mCountrySpinnerData = countryName.toArray(new String[countryName.size()]);

                                //创建适配器(下拉框的数据源是来自适配器)
                                ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,mCountrySpinnerData);
                                //将适配器中的数据显示在Spinner上
                                mProductCountry.setAdapter(countryAdapter);

                                for (int j=0 ; j<mCountrySpinnerID.length ;j++){
                                    if (mCurrentCountryId  == mCountrySpinnerID[j]){
                                        mProductCountry.setSelection(j);
                                        mCountryId  = mCountrySpinnerID[j];
                                    }
                                }

                                mProductCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

}
