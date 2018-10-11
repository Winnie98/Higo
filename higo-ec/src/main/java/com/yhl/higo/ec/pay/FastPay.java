package com.yhl.higo.ec.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.main.personal.order.OrderConfirmDelegate;
import com.yhl.higo.ec.sign.SignHandler;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.CountDownTimerUtils;
import com.yhl.higo.util.log.HigoLogger;

import org.w3c.dom.Text;

import static com.blankj.utilcode.util.Utils.getContext;

/**
 * Created by Administrator on 2018/5/24/024.
 */

public class FastPay implements View.OnClickListener{

//    private TextInputEditText mPhone;
    private TextInputEditText mCode;
    private TextView mGetCode;
    private OrderConfirmDelegate delegate = new OrderConfirmDelegate();

    //设置支付回调监听
    private IAlPayResultListener mIAlPayResultListener = null;
    private Activity mActivity = null;

    private AlertDialog mDialog = null;
    private long mOrderId = -1;
    private double mOrderAmount = -1;

    private final HigoDelegate DELEGATE;

    private FastPay(HigoDelegate delegate){
        this.mActivity = delegate.getProxyActivity();
        this.mDialog =  new AlertDialog.Builder(delegate.getContext()).create();

        this.DELEGATE = delegate;
    }

    public static FastPay create(HigoDelegate delegate){
        return new FastPay(delegate);
    }

    public void beginPayDialog(){
        mDialog.show();
        final Window window = mDialog.getWindow();
        if (window != null){
            window.setContentView(R.layout.dialog_pay_panel);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置属性
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(params);

            //添加事件
            window.findViewById(R.id.btn_dialog_pay_alpay).setOnClickListener(this);
            window.findViewById(R.id.btn_dialog_pay_cancel).setOnClickListener(this);
            window.findViewById(R.id.btn_dialog_pay_money).setOnClickListener(this);
        }
    }

    public void getPayCode(){
        CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(mGetCode, 60000, 1000);
        mCountDownTimerUtils.start();

        RestClient.builder()
                .url("user/code/send_code.do")
                .params("phone", SignHandler.getPhone() )
                .params("type",IdentifyItemType.IDENTIFY_BALANCE_PAY)
                .params("amount",mOrderAmount)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("SEND_CODE",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        final String msg  = JSON.parseObject(response).getString("msg");
                        switch (status){
                            case 0:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .build()
                .post();
    }

    public void beginPayCode(){
        mDialog.show();
        final Window window = mDialog.getWindow();
        if (window != null){
            window.setContentView(R.layout.dialog_pay_code);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置属性
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(params);

//            mPhone = window.findViewById(R.id.edit_pay_code_phone);
            mCode = window.findViewById(R.id.edit_pay_code);
            mGetCode = window.findViewById(R.id.txt_get_pay_code);

            window.findViewById(R.id.txt_get_pay_code).setOnClickListener(this);
            window.findViewById(R.id.btn_pay_code).setOnClickListener(this);


        }
    }

    public FastPay  setPayResultListener(IAlPayResultListener listener){
        this.mIAlPayResultListener = listener;
        return this;
    }

    public FastPay  setOrderId(long orderId){
        this.mOrderId = orderId;
        return this;
    }

    public FastPay  setOrderAmount(double orderAmount){
        this.mOrderAmount = orderAmount;
        return this;
    }

    public final void alPay(long orderId){
//        final String singUrl = "你的服务端支付地址" + orderId;
        //获取签名字符串
        RestClient.builder()
                .url("user/order/alipay_trade_app_pay.do")
                .params("orderNo",orderId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final String paySign = JSON.parseObject(response).getString("data");
                                HigoLogger.d("PAY_SIGN",paySign);

                                //必须是异步的调用客户端支付接口
                                final PayAsyncTask payAsyncTask = new PayAsyncTask(mActivity,mIAlPayResultListener);
                                payAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,paySign);
                                break;
                            case 3:
                                final String need_login = JSON.parseObject(response).getString("msg");
                                Toast.makeText(getContext(),need_login,Toast.LENGTH_SHORT).show();
                                DELEGATE.getSupportDelegate().start(IdentifyDelegate.create(IdentifyItemType.IDENTIFY_SIGN_IN));
//                                DELEGATE.getSupportDelegate().start(new SignInDelegate());
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

    public final void balancePay(long orderId){
        RestClient.builder()
                .url("user/order/pay_order_by_balance.do")
                .params("orderNo",orderId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final String data = JSON.parseObject(response).getString("data");
                                Toast.makeText(getContext(),data,Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                final String need_login = JSON.parseObject(response).getString("msg");
                                Toast.makeText(getContext(),need_login,Toast.LENGTH_SHORT).show();
                                DELEGATE.getSupportDelegate().start(IdentifyDelegate.create(IdentifyItemType.IDENTIFY_SIGN_IN));
//                                DELEGATE.getSupportDelegate().start(new SignInDelegate());
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

    public void checkCode(){
        RestClient.builder()
                .url("user/code/check_code.do")
                .params("phone",SignHandler.getPhone())
                .params("code",mCode.getText().toString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("CHECK_CODE",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        final String msg  = JSON.parseObject(response).getString("msg");
                        switch (status){
                            case 0:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                balancePay(mOrderId);
                            default:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                break;
                        }

                    }
                })
                .build()
                .post();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id ==R.id.btn_dialog_pay_alpay){
            //用支付宝进行支付
            alPay(mOrderId);
            mDialog.cancel();
        }else if (id == R.id.btn_dialog_pay_money){
            //用余额进行支付

//            balancePay(mOrderId);

            mDialog.cancel();
            beginPayCode();

        }else if (id == R.id.btn_dialog_pay_cancel){
            //取消支付
            mDialog.cancel();
        }else if (id == R.id.txt_get_pay_code){
            //获取余额支付验证码
            getPayCode();

        }else if (id == R.id.btn_pay_code){
            //使用余额支付
            checkCode();
            mDialog.cancel();
//            balancePay(mOrderId);
        }
    }
}
