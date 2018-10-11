package com.yhl.higo.ui.refresh;

/**
 * Created by Administrator on 2018/5/16/016.
 */

public final class PaginBean {
    //当前是第几页
    private int mPageIndex = 0;
    //总数据条数
    private int mTotal = 0;
    //一页显示几条数据
    private int mPageSize = 0;
    //当前已经显示了几条数据
    private int mCurrentCount = 0;
    //加载延迟
    private int mDelayed = 0;

    //当前页面之后是否还有页
    private boolean mIsHasNextPage = false;

    public boolean getIsHasNextPage() {
        return mIsHasNextPage;
    }

    public PaginBean setIsHasNextPage(boolean hasNextPage) {
        this.mIsHasNextPage = hasNextPage;
        return this;
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    public PaginBean setPageIndex(int mPageIndex) {
        this.mPageIndex = mPageIndex;
        return this;
    }

    public int getTotal() {
        return mTotal;
    }

    public PaginBean setTotal(int mTotal) {
        this.mTotal = mTotal;
        return this;
    }

    public int getPageSize() {
        return mPageSize;
    }

    public PaginBean setPageSize(int mPageSize) {
        this.mPageSize = mPageSize;
        return this;
    }

    public int getCurrentCount() {
        return mCurrentCount;
    }

    public PaginBean setCurrentCount(int mCurrentCount) {
        this.mCurrentCount = mCurrentCount;
        return this;
    }

    public int getDelayed() {
        return mDelayed;
    }

    public PaginBean setDelayed(int mDelayed) {
        this.mDelayed = mDelayed;
        return this;
    }

    public PaginBean addIndex(){
        mPageIndex++;
        return this;
    }

    public PaginBean addPageSize(){
        mPageSize += mPageSize;
        return this;
    }
}
