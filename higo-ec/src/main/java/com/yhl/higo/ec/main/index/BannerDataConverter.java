package com.yhl.higo.ec.main.index;

import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.yhl.higo.ec.R;
import com.yhl.higo.ui.banner.HolderCreator;
import com.yhl.higo.ui.recycler.DataConverter;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/15/015.
 */

public final class BannerDataConverter extends DataConverter {

    @Override
    public ArrayList<MultipleItemEntity> convert() {

        ArrayList<MultipleItemEntity> dataList = new ArrayList<>();

        final int status = JSON.parseObject(getJsonData()).getInteger("status");
        switch (status){
            case 0:
                final String array = JSON.parseObject(getJsonData()).getJSONObject("data").getString("images");
                final List<String> images = new ArrayList<>();
                String[] strArray = array.split(",");
                final int size = strArray.length;
                for (int i = 0; i < size; i++) {
                    images.add("http://img.higo.party/"+strArray[i]);
                }

                final MultipleItemEntity entity = MultipleItemEntity.builder()
                        .setItemType(ItemType.BANNER)
                        .setField(MultipleFields.BANNERS, images)
                        .build();

                dataList.add(entity);
//                mIndexBanner
//                        .setPages(new HolderCreator(), images)
//                        .setPageIndicator(new int[]{R.drawable.dot_normal, R.drawable.dot_focus})
//                        .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
//                        .setPageTransformer(new DefaultTransformer())
//                        .startTurning(3000)
//                        .setCanLoop(true);
                break;
            default:
                break;
        }
        return dataList;

    }
}
