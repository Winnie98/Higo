package com.yhl.higo.ec.main.personal.wishlist;


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
import java.util.List;

/**
 * Created by Administrator on 2018/5/22/022.
 */

public class WishListDataConverter extends DataConverter {


    @Override
    public ArrayList<MultipleItemEntity> convert() {

        ArrayList<MultipleItemEntity> dataList = new ArrayList<>();

        final int pageNum  = JSON.parseObject(getJsonData()).getJSONObject("data").getInteger("pageNum");
        final JSONArray array = JSON.parseObject(getJsonData()).getJSONObject("data").getJSONArray("list");
        final int size = array.size();
        for (int i = 0; i < size ; i++){

            final JSONObject data = array.getJSONObject(i);
            final int id = data.getInteger("id");
            final int productId = data.getInteger("productId");
            final String imgHost = data.getString("imageHost");
            final String mainImage = data.getString("mainImage");
            final String productName = data.getString("name");
            final Double price = data.getDouble("price");
            final String categoryName = data.getString("categoryName");
            final String username = data.getString("username");
            final int quantity = data.getInteger("quantity");
            final String productType = data.getString("typeName");
            final String createTime = data.getString("createTime");
            final String countryFlag = data.getString("countryFlag");
            final String countryName = data.getString("countryName");




            final MultipleItemEntity entity = MultipleItemEntity.builder()
                    .setItemType(ItemType.WISH_LIST)
                    .setField(MultipleFields.ID,id)
                    .setField(MultipleFields.PRODUCT_ID,productId)
                    .setField(MultipleFields.IMAGE_URL,imgHost+mainImage)
                    .setField(MultipleFields.TITLE,productName)
                    .setField(MultipleFields.PRICE,price)
                    .setField(MultipleFields.CATEGORY,categoryName)
                    .setField(MultipleFields.NUMBER,quantity)
                    .setField(MultipleFields.USER_NAME,username)
                    .setField(MultipleFields.PRODUCT_TYPE,productType)
                    .setField(MultipleFields.COUNTRY_IMG,imgHost+countryFlag)
                    .setField(MultipleFields.COUNTRY_NAME,countryName)
                    .setField(MultipleFields.CREATE_TIME,createTime)
                    .build();
            dataList.add(entity);
        }

        return dataList;
    }
}
