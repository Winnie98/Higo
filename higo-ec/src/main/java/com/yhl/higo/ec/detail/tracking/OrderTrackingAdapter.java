package com.yhl.higo.ec.detail.tracking;

import android.widget.TextView;

import com.yhl.higo.ec.R;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.ui.recycler.MultipleRecyclerAdapter;
import com.yhl.higo.ui.recycler.MultipleViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2018/9/6/006.
 */

public class OrderTrackingAdapter extends MultipleRecyclerAdapter {

    protected OrderTrackingAdapter(List<MultipleItemEntity> data) {
        super(data);
        addItemType(ItemType.TRACKING, R.layout.item_tracking);
    }

    @Override
    protected void convert(MultipleViewHolder holder, MultipleItemEntity entity) {
        super.convert(holder, entity);
        switch (holder.getItemViewType()){
            case ItemType.TRACKING:
                final String acceptTime = entity.getField(MultipleFields.ACCEPT_TIME);
                final String acceptStation = entity.getField(MultipleFields.ACCEPT_STATION);

                final TextView mAcceptTime = holder.getView(R.id.txt_tracking_acceptTime);
                final TextView mAcceptStation = holder.getView(R.id.txt_tracking_acceptSpace);

                mAcceptTime.setText(acceptTime);
                mAcceptStation.setText(acceptStation);

                break;
            default:
                break;
        }
    }
}
