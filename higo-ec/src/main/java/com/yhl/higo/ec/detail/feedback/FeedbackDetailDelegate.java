package com.yhl.higo.ec.detail.feedback;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.detail.order.OrderDetailDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.log.HigoLogger;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/9/5/005.
 */

public class FeedbackDetailDelegate extends HigoDelegate {
    private int mFeedbackId = -1;
    private long orderNo;
    private int userId;

    @BindView(R2.id.txt_feedback_detail_status)
    TextView mFeedbackStatus = null;
    @BindView(R2.id.txt_feedback_detail_orderNo)
    TextView mOrderNo = null;
    @BindView(R2.id.txt_feedback_detail_content)
    TextView mFeedbackDetail = null;
    @BindView(R2.id.txt_feedback_detail_feedbackTime)
    TextView mFeedTime = null;
    @BindView(R2.id.txt_feedback_detail_solveTime)
    TextView mSolveTime = null;
    @BindView(R2.id.txt_feedback_detail_feedbackUser)
    TextView mFeedbackUser = null;
    @BindView(R2.id.ll_feedback_detail_solveTime)
    LinearLayout  mSolveTimeLayout;


    public static FeedbackDetailDelegate create(int feedbackId){
        final Bundle args = new Bundle();
        args.putInt("feedbackId", feedbackId);
        final FeedbackDetailDelegate delegate = new FeedbackDetailDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @OnClick(R2.id.icon_feedback_detail)
    void onClickBack(){
        getSupportDelegate().pop();
    }

    @OnClick(R2.id.rl_feedback_order)
    void onClickOrder(){
        final OrderDetailDelegate delegate  = OrderDetailDelegate.create(orderNo,userId);
        getSupportDelegate().start(delegate);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            mFeedbackId = args.getInt("feedbackId");
        }
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_feedback_detail;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        RestClient.builder()
                .url("user/feedback/get_feedback_detail.do")
                .params("feedbackId",mFeedbackId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("FEEDBACK_DETAIL",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                userId = JSON.parseObject(response).getJSONObject("data").getInteger("userId");
                                final String username = JSON.parseObject(response).getJSONObject("data").getString("username");
                                orderNo = JSON.parseObject(response).getJSONObject("data").getLong("orderNo");
                                final String detail = JSON.parseObject(response).getJSONObject("data").getString("detail");
                                final int statusId = JSON.parseObject(response).getJSONObject("data").getInteger("statusId");//0已关闭，1待解决，2已解决
                                final String statusName = JSON.parseObject(response).getJSONObject("data").getString("statusName");
                                final String createTime = JSON.parseObject(response).getJSONObject("data").getString("createTime");
                                final String updateTime = JSON.parseObject(response).getJSONObject("data").getString("updateTime");

                                if (statusId !=1){
                                    mSolveTimeLayout.setVisibility(View.VISIBLE);
                                }
                                mFeedbackStatus.setText(statusName);
                                mOrderNo.setText(String.valueOf(orderNo));
                                mFeedbackDetail.setText(detail);
                                mFeedbackUser.setText(username);
                                mFeedTime.setText(createTime);
                                mSolveTime.setText(updateTime);
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
