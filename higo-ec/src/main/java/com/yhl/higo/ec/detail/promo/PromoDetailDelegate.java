package com.yhl.higo.ec.detail.promo;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.comment.PromoCommentDelegate;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.sign.SignInCodeDelegate;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.ec.source.SourceDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.banner.HolderCreator;
import com.yhl.higo.ui.image.GlideApp;
import com.yhl.higo.util.log.HigoLogger;
import com.zzhoujay.richtext.RichText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
//import me.wcy.htmltext.HtmlImageLoader;
//import me.wcy.htmltext.HtmlText;
//import me.wcy.htmltext.OnTagClickListener;

/**
 * Created by Administrator on 2018/5/18/018.
 */

public class PromoDetailDelegate extends HigoDelegate implements
//        AppBarLayout.OnOffsetChangedListener,
        View.OnClickListener {

    AlertDialog mDialog;
    private Double mPrice;
    private Spinner mSpinner;
    private TextView mDefineNum;
    private EditText mProductPrice;
    private EditText mDetail;
    private int mProductType;
    private int mPromoId = -1;
    private String mSource = null;

    @BindView(R2.id.promo_detail_toolbar)
    Toolbar mToolbar = null;
    @BindView(R2.id.promo_detail_banner)
    ConvenientBanner<String> mBanner = null;

//    @BindView(R2.id.collapsing_toolbar_detail_promo)
//    CollapsingToolbarLayout mCollapsingToolbarLayout = null;
//    @BindView(R2.id.app_bar_detail_promo)
//    AppBarLayout mAppBar = null;

    @BindView(R2.id.txt_promo_detail)
    TextView mPromoDetail = null;

    @OnClick(R2.id.icon_promo_back)
    void onClickBack(){
        getSupportDelegate().pop();
    }

    //底部
    //促销信息评论
    @OnClick(R2.id.rl_comment_promo)
    void onClickComment(){
        final PromoCommentDelegate delegate  = PromoCommentDelegate.create(mPromoId);
        getSupportDelegate().start(delegate);
    }
    //促销信息来源
    @OnClick(R2.id.rl_link_promo)
    void onClickLink(){
        getSupportDelegate().start(SourceDelegate.create(mSource));
    }
    //发布到集市
    @OnClick(R2.id.rl_publish_to_market)
    void onClickPublishToMarket(){
        beginPublish();

    }

    public void beginPublish(){
        mDialog = new AlertDialog.Builder(getContext()).create();
        mDialog.show();
        final Window window = mDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_publish_to_market);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置属性
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(params);

            //添加事件
            mSpinner = window.findViewById(R.id.spinner_choose_type);
            mDefineNum = window.findViewById(R.id.tv_publish_to_market_count);
            mProductPrice = window.findViewById(R.id.edt_publish_to_market_product_price);
            mDetail = window.findViewById(R.id.publish_to_market_product_desc);

            //创建下拉框的数据源
            String[] mSpinnerData = new String[]{"代购", "求购"};
            //创建适配器(下拉框的数据源是来自适配器)
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,mSpinnerData);
            //将适配器中的数据显示在Spinner上
            mSpinner.setAdapter(adapter);

            mProductPrice.setText(String.valueOf(mPrice));

            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mProductType = position+1;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            //添加事件
            window.findViewById(R.id.img_reduce_product_num).setOnClickListener(this);
            window.findViewById(R.id.img_increase_product_num).setOnClickListener(this);
            window.findViewById(R.id.btn_dialog_publish_to_market).setOnClickListener(this);
        }
    }

    public static PromoDetailDelegate create(int promoId){
        final Bundle args = new Bundle();
        args.putInt("promoId", promoId);
        final PromoDetailDelegate delegate = new PromoDetailDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            mPromoId = args.getInt("promoId");
        }
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_promo_detail;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initData();
    }

    private void initData() {
        RestClient.builder()
                .url("user/promo/get_passed_promo_detail.do")
                .params("promoId", mPromoId)
                .loader(getContext())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("PROMO_INFO_DETAILS",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final JSONObject data = JSON.parseObject(response).getJSONObject("data");
                                mPrice = data.getDouble("price");
//                                mPromoDetail.setText(data.getString("detail"));
                                mSource = data.getString("source");
                                final String detail = data.getString("detail");

//                                mPromoDetail.setText(Html.fromHtml(detail));
                                mPromoDetail.setMovementMethod(LinkMovementMethod.getInstance());
                                RichText.fromHtml(detail).into(mPromoDetail);

                                initBanner(data);
                                initPromoInfo(data);
                                break;
                            case 1:
                                break;
                            default:
                                break;
                        }
                    }
                })
                .build()
                .get();
    }


    private void initBanner(JSONObject data) {
        final String array = data.getString("subImages");
        final List<String> images = new ArrayList<>();
        String[] strArray = array.split(",");
        final int size = strArray.length;
        for (int i = 0; i < size; i++) {
            images.add("http://img.higo.party/"+strArray[i]);
        }
        mBanner
                .setPages(new HolderCreator(), images)
                .setPageIndicator(new int[]{R.drawable.dot_normal, R.drawable.dot_focus})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setPageTransformer(new DefaultTransformer())
                .startTurning(3000)
                .setCanLoop(true);
    }

    private void initPromoInfo(JSONObject data) {
        final String promoData = data.toJSONString();
        getSupportDelegate().
                loadRootFragment(R.id.frame_promo_info, PromoInfoDelegate.create(promoData));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id ==R.id.img_reduce_product_num){
            int currentCount = Integer.parseInt(mDefineNum.getText().toString());
            if (currentCount>1){
                currentCount--;
                mDefineNum.setText(String.valueOf(currentCount));
            }else {
                Toast.makeText(getContext(),"该宝贝不能减少了哟~",Toast.LENGTH_SHORT).show();
            }
        }else if (id ==R.id.img_increase_product_num){
            int currentCount = Integer.parseInt(mDefineNum.getText().toString());
            currentCount++;
            mDefineNum.setText(String.valueOf(currentCount));
        }else if (id == R.id.btn_dialog_publish_to_market){
            Toast.makeText(getContext(),"你点击了确定按钮",Toast.LENGTH_SHORT).show();
            RestClient.builder()
                    .url("user/product/add_product.do")
                    .params("promoId",mPromoId)
                    .params("productType",mProductType)
                    .params("name",mDetail.getText().toString())
                    .params("quantity",Integer.parseInt(mDefineNum.getText().toString()))
                    .params("price",mProductPrice.getText().toString())
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            HigoLogger.d("PUBLISH_PRODUCT",response);
                            final int status = JSON.parseObject(response).getInteger("status");
                            final String msg = JSON.parseObject(response).getString("msg");
                            switch (status){
                                case 0:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    break;
                                case 2:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    getSupportDelegate().start(new SignInCodeDelegate());
                                    break;
                                default:
                                    break;
                            }
                        }
                    })
                    .build()
                    .post();
            mDialog.cancel();
        }

    }
}
