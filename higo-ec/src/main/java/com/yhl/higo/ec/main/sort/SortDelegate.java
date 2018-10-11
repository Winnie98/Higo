package com.yhl.higo.ec.main.sort;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.yhl.higo.delegates.bottom.BottomItemDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.main.sort.content.ContentDelegate;
import com.yhl.higo.ec.main.sort.list.VerticalListDelegate;

/**
 * Created by Administrator on 2018/5/12/012.
 */

public class SortDelegate extends BottomItemDelegate {
    @Override
    public Object setLayout() {
        return R.layout.delegate_sort;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        final VerticalListDelegate listDelegate = new VerticalListDelegate();
//        loadRootFragment(R.id.vertical_list_container,listDelegate);
        //设置右侧第一个分类显示，默认显示分类一
//        replaceLoadRootFragment(R.id.sort_content_container, ContentDelegate.newInstance(1),false);
        getSupportDelegate().loadRootFragment(R.id.vertical_list_container, listDelegate);
        //设置右侧第一个分类显示，默认显示分类一
//        getSupportDelegate().loadRootFragment(R.id.sort_content_container, ContentDelegate.newInstance(1));
        getSupportDelegate().loadRootFragment(R.id.sort_content_container, ContentDelegate.newInstance(10001));
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
    }

//    @Override
//    public void onSupportVisible() {
//        super.onSupportVisible();
//        final VerticalListDelegate listDelegate = new VerticalListDelegate();
//        getSupportDelegate().loadRootFragment(R.id.vertical_list_container, listDelegate);
//        getSupportDelegate().loadRootFragment(R.id.sort_content_container, ContentDelegate.newInstance(10001));
//    }
}
