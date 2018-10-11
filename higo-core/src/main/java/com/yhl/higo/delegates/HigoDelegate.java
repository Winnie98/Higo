package com.yhl.higo.delegates;

public abstract class HigoDelegate extends PermissionCheckerDelegate{

    @SuppressWarnings("unchecked")
    public <T extends HigoDelegate> T getParentDelegate() {
        return (T) getParentFragment();
    }
}
