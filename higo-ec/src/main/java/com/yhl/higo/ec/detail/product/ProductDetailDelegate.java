package com.yhl.higo.ec.detail.product;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.joanzapata.iconify.widget.IconTextView;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.main.market.EditProductDelegate;
import com.yhl.higo.ec.main.personal.order.OrderSubmitDelegate;
import com.yhl.higo.ec.sign.SignInCodeDelegate;
import com.yhl.higo.ec.sign.SignInDelegate;
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

/**
 * Created by Administrator on 2018/5/18/018.
 */

public class ProductDetailDelegate extends HigoDelegate implements
        View.OnClickListener{


    private boolean mIsSelectedLike = false;
    private boolean mRequireIsSelectedLike = false;
//    private int mBottomLayout = -1;//0代表自己发布的商品，1代表代购，2代表求购

    private int mProductId = -1;
    private AlertDialog mDialog;
//    private Double mPrice;
    private Spinner mSpinner;
    private TextView mDefineNum;
    private TextView mProductPrice;
    private EditText mDetail;
    private int mProductType;

    private ImageView mProductImage;
    private TextView mProductTitle;

    Bundle  mBundle = new Bundle();


    @BindView(R2.id.product_detail_toolbar)
    Toolbar mToolbar = null;
    @BindView(R2.id.product_detail_banner)
    ConvenientBanner<String> mBanner = null;
    @BindView(R2.id.txt_product_detail)
    TextView mProductDetail = null;

    @OnClick(R2.id.icon_product_back)
    void onClickBack(){
        getSupportDelegate().pop();
    }

    //底部
    @BindView(R2.id.btn_close_product)
    Button mCloseButton = null;
    @BindView(R2.id.btn_edit_product)
    Button mEditButton = null;
    @BindView(R2.id.icon_product_favor)
    IconTextView mSelectIcon = null;
    @BindView(R2.id.icon_product_favor_require)
    IconTextView mSelectIconRequire = null;
    @BindView(R2.id.btn_purchase_product)
    Button mPurchaseButton = null;
    @BindView(R2.id.layout_bottom_purchase)
    LinearLayout mPurchaseLayout = null;
    @BindView(R2.id.layout_bottom_require_purchase)
    LinearLayout mRequirePurchaseLayout = null;
    @BindView(R2.id.layout_bottom_self)
    LinearLayout mSelfLayout = null;
    //心愿单
    @OnClick(R2.id.rl_product_favor_purchase)
    void onClickSelectFavorPurchase(){
        addOrRemoveWishList();
    }

    @OnClick(R2.id.rl_product_favor_require_purchase)
    void onClickSelectFavorRequire(){
        addOrRemoveWishListRequire();
    }


    //帮助代购
    @OnClick(R2.id.btn_help_purchase_product)
    void onClickHelpPurchase(){
        //创建求购订单
        RestClient.builder()
                .url("user/order/create_buy_order.do")
                .params("productId",mProductId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("CREATE_HELP_PURCHASE_ORDER",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                Toast.makeText(getContext(),"帮助代购成功",Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                final String need_login = JSON.parseObject(response).getString("msg");
                                Toast.makeText(getContext(),need_login,Toast.LENGTH_LONG).show();
                                getSupportDelegate().start(new SignInCodeDelegate());
                                break;
                            default:
                                final String msg = JSON.parseObject(response).getString("msg");
                                Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                })
                .build()
                .post();
    }
    //立即购买
    @OnClick(R2.id.btn_purchase_product)
    void onClickPurchase(){
//        Toast.makeText(getContext(),"你点击了立即购买",Toast.LENGTH_LONG).show();
        RestClient.builder()
                .url("user/user/get_current_user_info.do")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("PURCHASE-PRODUCT",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                beginPurchase();
                                break;
                            case 3:
                                final String need_login = JSON.parseObject(response).getString("msg");
                                Toast.makeText(getContext(),need_login,Toast.LENGTH_SHORT).show();
                                getSupportDelegate().start(new SignInCodeDelegate());
                                break;
                            default:
                                break;
                        }
                    }
                })
                .build()
                .get();
    }
    //商品下架
    @OnClick(R2.id.btn_close_product)
    void onClickClose(){
        RestClient.builder()
                .url("user/product/close_product.do")
                .params("productId",mProductId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("CLOSE_PRODUCT",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        final String msg = JSON.parseObject(response).getString("msg");
                        switch (status){
                            case 0:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();

                                mCloseButton.setText("商品已下架");
                                mCloseButton.setBackgroundColor(Color.GRAY);
                                getSupportDelegate().pop();
                                break;
                            case 1:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                getSupportDelegate().pop();
                                break;
                            case 3:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                getSupportDelegate().start(new SignInCodeDelegate());
                                break;
                            default:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                getSupportDelegate().pop();
                                break;
                        }

                    }
                })
                .build()
                .post();
    }
    //编辑商品
    @OnClick(R2.id.btn_edit_product)
    void onClickEdit(){
        getSupportDelegate().start(EditProductDelegate.create(mProductId,mProductDetail.getText().toString()));
    }


    public void beginPurchase(){
        mDialog = new AlertDialog.Builder(getContext()).create();
        mDialog.show();
        final Window window = mDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_purchase_product);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置属性
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(params);

            //添加事件
            mProductImage = window.findViewById(R.id.img_purchase_product);
            mProductTitle = window.findViewById(R.id.txt_purchase_product_title);
            mProductPrice = window.findViewById(R.id.txt_purchase_product_price);
            mDefineNum = window.findViewById(R.id.txt_purchase_product_count);

            GlideApp.with(getContext())
                    .load(mBundle.getString("imageHost")+mBundle.getString("mainImage"))
                    .into(mProductImage);

            mProductTitle.setText(mBundle.getString("name"));
            mProductPrice.setText(String.valueOf(mBundle.getDouble("price")));


            window.findViewById(R.id.img_reduce_purchase_product_num).setOnClickListener(this);
            window.findViewById(R.id.img_increase_purchase_product_num).setOnClickListener(this);
            window.findViewById(R.id.btn_dialog_purchase_product).setOnClickListener(this);

        }
    }

    public static ProductDetailDelegate create(int productId){
        final Bundle args = new Bundle();
        args.putInt("productId", productId);
        final ProductDetailDelegate delegate = new ProductDetailDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            mProductId = args.getInt("productId");
        }
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_product_detail;
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
                .url("user/product/get_normal_product_detail.do")
                .params("productId", mProductId)
                .loader(getContext())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("PRODUCT_INFO_DETAILS",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final JSONObject data = JSON.parseObject(response).getJSONObject("data");
                                final String imageHost = data.getString("imageHost");
                                final String mainImage = data.getString("mainImage");
                                final String name = data.getString("name");
                                final int quantity = data.getInteger("quantity");
                                final double price = data.getDouble("price");
                                final double tax = data.getDouble("tax");

                                final String detail = data.getString("detail");
                                mProductDetail.setMovementMethod(LinkMovementMethod.getInstance());
                                RichText.fromHtml(detail).into(mProductDetail);

                                mBundle.putString("imageHost",imageHost);
                                mBundle.putString("mainImage",mainImage);
                                mBundle.putString("name",name);
                                mBundle.putInt("quantity",quantity);
                                mBundle.putDouble("price",price);
                                mBundle.putDouble("tax",tax);
                                initBanner(data);
                                initProductInfo(data);
                                initBottomLayout(data);
                                break;
                            case 1:
                                final String msg = JSON.parseObject(response).getString("msg");
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                getSupportDelegate().pop();
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

    private void initProductInfo(JSONObject data) {
        final String productData = data.toJSONString();
        getSupportDelegate().
                loadRootFragment(R.id.frame_product_info, ProductInfoDelegate.create(productData));
    }

    //底部布局
    private void initBottomLayout(JSONObject data) {
        mPurchaseLayout.setVisibility(View.INVISIBLE);
        mRequirePurchaseLayout.setVisibility(View.INVISIBLE);
        mSelfLayout.setVisibility(View.INVISIBLE);

        final int product_type = data.getInteger("typeId");
        final String publishUser = data.getString("username");

        RestClient.builder()
                .url("user/user/get_current_user_info.do")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("USER_INFO",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final String currentUser = JSON.parseObject(response).getJSONObject("data").getString("username");
                                if (currentUser.equals(publishUser)){
                                    mSelfLayout.setVisibility(View.VISIBLE);

                                    initLikeStatus(0);

                                }else {
                                    if (product_type == 1){
                                        mPurchaseLayout.setVisibility(View.VISIBLE);
                                        initLikeStatus(1);
                                    }else if (product_type == 2){
                                        mRequirePurchaseLayout.setVisibility(View.VISIBLE);
                                        initLikeStatus(2);
                                    }
                                }
                                break;
                            default:
                                if (product_type == 1){
                                    mPurchaseLayout.setVisibility(View.VISIBLE);
                                }else if (product_type == 2){
                                    mRequirePurchaseLayout.setVisibility(View.VISIBLE);
                                }
                                break;
                        }
                    }
                })
                .build()
                .get();




    }

    //访问服务器获取心选中状态
    private void initLikeStatus(final int bottomLayout){
        RestClient.builder()
                .url("user/wishlist/check_wishlist.do")
                .params("productId",mProductId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("WISH_LIST_IS_SELECTED",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        final String msg = JSON.parseObject(response).getString("msg");
                        switch (status){
                            case 0:
                                if (bottomLayout == 1){
                                    mIsSelectedLike = true;
                                    mSelectIcon.setText("{fa-heart}");
                                    mSelectIcon.setTextColor(ContextCompat.getColor(getContext(), R.color.app_main));
                                }else if (bottomLayout == 2){
                                    mRequireIsSelectedLike = true;
                                    mSelectIconRequire.setText("{fa-heart}");
                                    mSelectIconRequire.setTextColor(ContextCompat.getColor(getContext(), R.color.app_main));
                                }

                                break;
                            default:
                                break;
                        }
                    }
                })
                .build()
                .post();
    }

    //添加或移出心愿单
    private void addOrRemoveWishList(){
        if (mIsSelectedLike) {
            //访问服务器,从心愿单中移出或添加到心愿单
            RestClient.builder()
                    .url("user/wishlist/add_or_remove_wishlist.do")
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            HigoLogger.d("WISH_LIST",response);
                            final int status = JSON.parseObject(response).getInteger("status");
                            final String msg = JSON.parseObject(response).getString("msg");
                            switch (status){
                                case 0:
                                    mSelectIcon.setText("{fa-heart-o}");
                                    mSelectIcon.setTextColor(Color.GRAY);
                                    mIsSelectedLike = false;
                                    break;
                                case 3:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    getSupportDelegate().start(new SignInCodeDelegate());
                                default:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    })
                    .build()
                    .post();
        } else if (!mIsSelectedLike){
            //访问服务器,添加到心愿单
            RestClient.builder()
                    .url("user/wishlist/add_or_remove_wishlist.do")
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            HigoLogger.d("WISH_LIST",response);
                            final int status = JSON.parseObject(response).getInteger("status");
                            final String msg = JSON.parseObject(response).getString("msg");
                            switch (status){
                                case 0:
                                    mSelectIcon.setText("{fa-heart}");
                                    mSelectIcon.setTextColor
                                            (ContextCompat.getColor(getContext(), R.color.app_main));
                                    mIsSelectedLike = true;
                                    break;
                                case 3:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    getSupportDelegate().start(new SignInCodeDelegate());
                                default:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    })
                    .build()
                    .post();

        }
    }

    //添加或移出心愿单
    private void addOrRemoveWishListRequire(){
        if (mRequireIsSelectedLike) {
            //访问服务器,从心愿单中移出或添加到心愿单
            RestClient.builder()
                    .url("user/wishlist/add_or_remove_wishlist.do")
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            HigoLogger.d("WISH_LIST",response);
                            final int status = JSON.parseObject(response).getInteger("status");
                            final String msg = JSON.parseObject(response).getString("msg");
                            switch (status){
                                case 0:
                                    mSelectIconRequire.setText("{fa-heart-o}");
                                    mSelectIconRequire.setTextColor(Color.GRAY);
                                    mRequireIsSelectedLike = false;
                                    break;
                                case 3:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    getSupportDelegate().start(new SignInCodeDelegate());
                                default:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    })
                    .build()
                    .post();
        } else if (!mRequireIsSelectedLike){
            //访问服务器,添加到心愿单
            RestClient.builder()
                    .url("user/wishlist/add_or_remove_wishlist.do")
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            HigoLogger.d("WISH_LIST",response);
                            final int status = JSON.parseObject(response).getInteger("status");
                            final String msg = JSON.parseObject(response).getString("msg");
                            switch (status){
                                case 0:
                                    mSelectIconRequire.setText("{fa-heart}");
                                    mSelectIconRequire.setTextColor
                                            (ContextCompat.getColor(getContext(), R.color.app_main));
                                    mRequireIsSelectedLike = true;
                                    break;
                                case 3:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    getSupportDelegate().start(new SignInCodeDelegate());
                                default:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    })
                    .build()
                    .post();

        }
    }

    //弹出的dialog的点击事件
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if  (id ==R.id.img_reduce_purchase_product_num){
            //减少购买商品数量
            int currentCount = Integer.parseInt(mDefineNum.getText().toString());
            if (currentCount>1){
                currentCount--;
                mDefineNum.setText(String.valueOf(currentCount));
            }else {
                Toast.makeText(getContext(),"该宝贝不能减少了哟~",Toast.LENGTH_SHORT).show();
            }
        }else if (id ==R.id.img_increase_purchase_product_num){
            //增加购买商品数量
            int currentCount = Integer.parseInt(mDefineNum.getText().toString());
            currentCount++;
            mDefineNum.setText(String.valueOf(currentCount));
        }else if (id == R.id.btn_dialog_purchase_product){
            final String imgUrl = mBundle.getString("imageHost")+mBundle.getString("mainImage");
            final String title = mBundle.getString("name");
            final double price = mBundle.getDouble("price");
            final int quantity = Integer.parseInt(mDefineNum.getText().toString());
            final double tax = mBundle.getDouble("tax");

            getSupportDelegate().start(OrderSubmitDelegate.create(mProductId,imgUrl, title,price,quantity,tax));

            mDialog.cancel();
        }

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        initData();
    }
}
