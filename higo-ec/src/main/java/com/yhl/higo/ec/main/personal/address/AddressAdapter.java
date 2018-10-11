package com.yhl.higo.ec.main.personal.address;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;
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

public class AddressAdapter extends MultipleRecyclerAdapter{

    private final AddressDelegate DELEGATE;


    protected AddressAdapter(List<MultipleItemEntity> data, AddressDelegate delegate) {
        super(data);
        DELEGATE = delegate;
        addItemType(ItemType.ITEM_ADDRESS, R.layout.item_address);
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

                final AppCompatTextView nameText = holder.getView(R.id.tv_address_name);
                final AppCompatTextView phoneText = holder.getView(R.id.tv_address_phone);
                final AppCompatTextView addressText = holder.getView(R.id.tv_address_address);

                final LinearLayout editAddress = holder.getView(R.id.ll_update_address);
                final LinearLayout deleteAddress = holder.getView(R.id.ll_delete_address);

                editAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RestClient.builder()
                                .url("user/address/get_address_detail.do")
                                .params("addressId",id)
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        HigoLogger.d("ADDERSS_INFO",response);
                                        final int status = JSON.parseObject(response).getInteger("status");
                                        switch (status){
                                            case 0:
                                                final String receiverName = JSON.parseObject(response).getJSONObject("data").getString("name");
                                                final String receiverPhone = JSON.parseObject(response).getJSONObject("data").getString("phone");
                                                final String receiverProvince = JSON.parseObject(response).getJSONObject("data").getString("province");
                                                final String receiverCity = JSON.parseObject(response).getJSONObject("data").getString("city");
                                                final String receiverDistrict = JSON.parseObject(response).getJSONObject("data").getString("district");
                                                final String receiverAddress = JSON.parseObject(response).getJSONObject("data").getString("address");
                                                final String receiverZip = JSON.parseObject(response).getJSONObject("data").getString("zip");


                                                final EditAddressDelegate delegate  =
                                                        EditAddressDelegate.create(id,receiverName,receiverPhone,receiverProvince,receiverCity,receiverDistrict,receiverAddress,receiverZip);
                                                DELEGATE.getSupportDelegate().start(delegate);
                                                break;
                                            default:
                                                final String msg = JSON.parseObject(response).getString("msg");
                                                Toast.makeText(Higo.getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                                break;
                                        }
                                    }
                                })
                                .build()
                                .get();
                    }
                });

                deleteAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RestClient.builder()
                                .url("user/address/delete_address.do")
                                .params("addressId", id)
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        HigoLogger.d("DELETE_ADDRESS",response);
                                        final int status = JSON.parseObject(response).getInteger("status");
                                        final String msg = JSON.parseObject(response).getString("msg");
                                        switch (status){
                                            case 0:
                                                remove(holder.getLayoutPosition());
                                                break;
                                            default:
                                                Toast.makeText(Higo.getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                                break;
                                        }
                                    }
                                })
                                .build()
                                .post();
                    }
                });

                nameText.setText(name);
                phoneText.setText(phone);
                addressText.setText(address);
                break;
            default:
                break;
        }
    }

}
