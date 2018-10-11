package com.yhl.higo.ec.main.personal.address;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.app.Higo;
import com.yhl.higo.ec.R;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.ui.recycler.MultipleRecyclerAdapter;
import com.yhl.higo.ui.recycler.MultipleViewHolder;
import com.yhl.higo.util.log.HigoLogger;

import java.util.List;

/**
 * Created by Administrator on 2018/5/26/026.
 */

public class SelectAddressAdapter extends MultipleRecyclerAdapter{


    protected SelectAddressAdapter(List<MultipleItemEntity> data) {
        super(data);
        addItemType(ItemType.ITEM_ADDRESS, R.layout.item_select_address);
    }

    @Override
    protected void convert(final MultipleViewHolder holder, MultipleItemEntity entity) {
        super.convert(holder, entity);
        switch (holder.getItemViewType()) {
                case ItemType.ITEM_ADDRESS:
                final String name = entity.getField(MultipleFields.NAME);
                final String phone = entity.getField(MultipleFields.PHONE);
                final String address = entity.getField(MultipleFields.ADDRESS);
//                final boolean isDefault = entity.getField(MultipleFields.TAG);
                final int id = entity.getField(MultipleFields.ID);

                final TextView nameText = holder.getView(R.id.txt_select_address_name);
                final TextView phoneText = holder.getView(R.id.txt_select_address_phone);
                final TextView addressText = holder.getView(R.id.txt_select_address_address);


                nameText.setText(name);
                phoneText.setText(phone);
                addressText.setText(address);
                break;
            default:
                break;
        }
    }

}
