package com.yhl.higo.ec.main.personal.feeedback;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.detail.feedback.FeedbackDetailDelegate;
import com.yhl.higo.ec.detail.promo.PromoDetailDelegate;
import com.yhl.higo.ec.main.index.IndexDataConverter;
import com.yhl.higo.ui.image.GlideApp;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.ui.recycler.MultipleRecyclerAdapter;
import com.yhl.higo.ui.recycler.MultipleViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2018/5/26/026.
 */

public class FeedbackAdapter extends MultipleRecyclerAdapter{

    private final HigoDelegate DELEGATE;
//    private int feedbackId;
    private FeedbackDataConverter  mDataConverter = new FeedbackDataConverter();

    @Override
    protected MultipleViewHolder createBaseViewHolder(View view) {
        return MultipleViewHolder.create(view);
    }

    public FeedbackAdapter(List<MultipleItemEntity> data, HigoDelegate delegate) {
        super(data);
        DELEGATE = delegate;
        addItemType(ItemType.FEEDBACK, R.layout.arrow_item_layout);
        addItemType(ItemType.FEEDBACK_NO, R.layout.item_no_publish);
    }

    @Override
    protected void convert(final MultipleViewHolder holder, MultipleItemEntity entity) {
        super.convert(holder, entity);


        final long orderNo;
        final int statusId;
        final String statusName;
        switch (holder.getItemViewType()) {
            case ItemType.FEEDBACK_NO:

                break;
            case ItemType.FEEDBACK:
                //先取出所有值
                final int feedbackId = entity.getField(MultipleFields.ID);
                orderNo = entity.getField(MultipleFields.ORDER_NO);
//                statusId = entity.getField(MultipleFields.STATUS_ID);
                statusName = entity.getField(MultipleFields.STATUS_NAME);

                //取出所有控件
                final AppCompatTextView  mOrderNo = holder.getView(R.id.tv_arrow_text);
                final AppCompatTextView  mStatusName = holder.getView(R.id.tv_arrow_value);


                //赋值
                mOrderNo.setText(String.valueOf(orderNo));
                mStatusName.setText(statusName);
                break;
            default:
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                final int feedbackId = mDataConverter.mFeedbackId.get(position);
                final FeedbackDetailDelegate  delegate  = FeedbackDetailDelegate.create(feedbackId);
                DELEGATE.getSupportDelegate().start(delegate);
            }
        });

                }
                }
