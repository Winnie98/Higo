package com.yhl.higo.delegates.bottom;

/**
 * Created by Administrator on 2018/5/11/011.
 */

public final class BottomTabBean {
    private final CharSequence ICON;
    private final CharSequence TITLE;

    public BottomTabBean(CharSequence icon, CharSequence title) {
        this.ICON = icon;
        this.TITLE = title;
    }

    public CharSequence getIcon() {
        return ICON;
    }

    public CharSequence getTitle() {
        return TITLE;
    }
}
