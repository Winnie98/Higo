package com.yhl.higo.ec.main;

import com.yhl.higo.delegates.bottom.BaseBottomDelegate;
import com.yhl.higo.delegates.bottom.BottomItemDelegate;
import com.yhl.higo.delegates.bottom.BottomTabBean;
import com.yhl.higo.delegates.bottom.ItemBuilder;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.main.index.IndexDelegate;
import com.yhl.higo.ec.main.market.MarketDelegate;
import com.yhl.higo.ec.main.personal.PersonalDelegate;
import com.yhl.higo.ec.main.sort.SortDelegate;

import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2018/5/12/012.
 */

public class EcBottomDelegate extends BaseBottomDelegate {

    @Override
    public LinkedHashMap<BottomTabBean, BottomItemDelegate> setItems(ItemBuilder builder) {
        final LinkedHashMap<BottomTabBean,BottomItemDelegate> items = new LinkedHashMap<>();
        items.put(new BottomTabBean("{fa-home}","主页"),new IndexDelegate());
        items.put(new BottomTabBean("{fa-sort}","分类"),new SortDelegate());
        items.put(new BottomTabBean("{fa-compass}","集市"),new MarketDelegate());
//        items.put(new BottomTabBean("{fa-shopping-cart}","购物车"),new ShopCartDelegate());
        items.put(new BottomTabBean("{fa-user}","我的"),new PersonalDelegate());
        return builder.addItems(items).build();
    }


    @Override
    public int setIndexDelegate() {
        return 0;
    }

    @Override
    public int setClickedColor() {
//        return Color.parseColor("#ffff8800");
        return getResources().getColor(R.color.app_main);
    }
}
