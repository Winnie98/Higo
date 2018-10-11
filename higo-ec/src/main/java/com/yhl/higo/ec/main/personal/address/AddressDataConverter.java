package com.yhl.higo.ec.main.personal.address;

import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yhl.higo.app.Higo;
import com.yhl.higo.ui.recycler.DataConverter;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.util.log.HigoLogger;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/5/26/026.
 */

public class AddressDataConverter extends DataConverter {
    @Override
    public ArrayList<MultipleItemEntity> convert() {

        final JSONArray array = JSON.parseObject(getJsonData()).getJSONArray("data");
        final int size = array.size();
        for (int i = 0; i < size ; i++){

            final JSONObject data = array.getJSONObject(i);
            final int id = data.getInteger("id");
            final String name = data.getString("name");
            final String phone = data.getString("phone");
            final String address =
                    data.getString("province")
                            +data.getString("city")
                            +data.getString("district")
                            +data.getString("address");

//            final boolean isDefault = data.getBoolean("default");
            HigoLogger.d("ADDERSS",address);
            HigoLogger.d("ADDERSS",id);

            final MultipleItemEntity entity = MultipleItemEntity.builder()
                    .setItemType(ItemType.ITEM_ADDRESS)
                    .setField(MultipleFields.ID,id)
                    .setField(MultipleFields.NAME,name)
//                    .setField(MultipleFields.TAG,isDefault)
                    .setField(MultipleFields.PHONE,phone)
                    .setField(MultipleFields.ADDRESS,address)
                    .build();
            ENTITIES.add(entity);
        }

        return ENTITIES;
    }
}
