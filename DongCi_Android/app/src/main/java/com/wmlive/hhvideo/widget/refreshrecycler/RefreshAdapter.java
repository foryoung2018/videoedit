package com.wmlive.hhvideo.widget.refreshrecycler;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.utils.KLog;

import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * 如果mDataContainer的size==1，有可能是空数据，需要使用{@link #isContent(int)}或者{@link #hasContent()}方法判断是否是list中的数据
 *
 * @param <VH>
 * @param <T>
 */
public abstract class RefreshAdapter<VH extends BaseRecyclerViewHolder, T> extends RecyclerView.Adapter
        implements BaseRecyclerViewHolder.ItemClickListener, BaseRecyclerViewHolder.ItemLongClickListener {
    private RefreshRecyclerView mRecycleView;
    protected OnRecyclerItemClickListener<T> mOnRecyclerViewItemClickListener;
    protected OnRecyclerItemLongClickListener<T> mOnRecyclerItemLongClickListener;

    public static final int TYPE_CONTENT = 1; // 内容类型
    public static final int TYPE_FOOTER = 2; // 底部加载更多
    public static final int TYPE_HEADER = 3; // 头部
    public static final int TYPE_EMPTY = 4; // 空白页
    private List<Integer> mViewTypes; // 子视图类型
    protected List<T> mDataContainer;  //数据容器
    private int mPageSize = 20;  //默认分页大小
    private boolean showError;//显示错误页面
    private int mEmptyResId = R.string.empty_data_msg;
    private boolean mIsShowImg = false;//是否展示空界面 图片
    private boolean mIsShowEmptyView = true;//是否展示空界面
    private boolean isFirstLoad = true;


    public RefreshAdapter(List<T> list, RefreshRecyclerView refreshView) {
        this(list, refreshView, 20);
    }

    /**
     * @param list        不能为空！！！
     * @param refreshView RefreshRecycleView
     * @param pageSize    分页大小，默认12
     */
    public RefreshAdapter(List<T> list, RefreshRecyclerView refreshView, int pageSize) {
        this.mRecycleView = refreshView;
        mDataContainer = list;
        mPageSize = pageSize;
        mViewTypes = new ArrayList<>();
    }

    public RefreshRecyclerView getRecycleView() {
        return mRecycleView;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CONTENT || mViewTypes.contains(viewType)) {
            VH vh = onCreateHolder(parent, viewType);
            vh.setItemClickListener(this);
            vh.setItemLongClickListener(this);
            return vh;
        } else if (viewType == TYPE_HEADER && hasHeader()) {
            mRecycleView.setFullSpan(mRecycleView.getHeader(), 1);
            return new BaseRecyclerViewHolder(mRecycleView.getHeader()) {
            };
        } else if (viewType == TYPE_FOOTER && hasFooter()) {
            mRecycleView.setFullSpan(mRecycleView.getFooter(), 1);
            return new BaseRecyclerViewHolder(mRecycleView.getFooter()) {
            };
        } else if (viewType == TYPE_EMPTY) {
            mRecycleView.setFullSpan(mRecycleView.getEmptyView(), -1);
            return new BaseRecyclerViewHolder(mRecycleView.getEmptyView()) {
            };
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemCount() > 0) {
            int viewType = this.getItemViewType(position);
            if (viewType == TYPE_CONTENT || mViewTypes.contains(viewType)) {
                int p = hasHeader() ? (holder.getLayoutPosition() - 1) : holder.getLayoutPosition();
                onBindHolder((VH) holder, p, mDataContainer.get(p));
            } else if (viewType == TYPE_EMPTY && mRecycleView.getEmptyView() != null) {
                if (showError) {
                    mRecycleView.showEmptyViewImg(mIsShowImg);
                    mRecycleView.showError();
                } else {
                    mRecycleView.setNoUserEmptyView(!isFirstLoad && mIsShowEmptyView);
                    mRecycleView.showEmptyViewImg(mIsShowImg);
                    if (mEmptyResId > 0) {
                        mRecycleView.showEmpty(mRecycleView.getContext().getResources().getString(mEmptyResId));
                    } else {
                        mRecycleView.showEmpty("");
                    }
                }
            }
        }
    }

    /**
     * 是否展示空界面
     *
     * @param isShowEmptyView
     */
    public void setShowEmptyView(boolean isShowEmptyView) {
        mIsShowEmptyView = isShowEmptyView;
    }

    /**
     * 空界面 文案
     *
     * @param emptyResId
     */
    public void setEmptyStr(int emptyResId) {
        mEmptyResId = emptyResId;
    }

    /**
     * 空界面 图片是否展示
     *
     * @param isShowImg
     */
    public void setShowImg(boolean isShowImg) {
        mIsShowImg = isShowImg;
    }

    /**
     * 没有特殊情况时子类请勿重写此方法！！！
     *
     * @return
     */
    @Override
    public int getItemCount() {
        int count = 0;
        if (mDataContainer != null && mDataContainer.size() > 0) {
            count = mDataContainer.size();
        }

        if (hasHeader()) {
            count += 1;
        }
        if (hasFooter()) {
            count += 1;
        }
        return count;
    }

    /**
     * 没有特殊情况时子类请勿重写此方法！！！
     *
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0 && hasHeader()) {
            return TYPE_HEADER;
        } else if ((position == (getItemCount() - 1)) && hasFooter()) {
            return TYPE_FOOTER;
        } else if ((mDataContainer == null) || (mDataContainer.size() == 1 && mDataContainer.get(0) == null)) {
            return TYPE_EMPTY;
        } else {
            return getItemType(hasHeader() ? (position - 1) : position);
        }
    }


    /**
     * 设置分页大小
     *
     * @param pageSize 默认12
     */
    public void setPageSize(int pageSize) {
        mPageSize = pageSize;
    }

    /**
     * 设置子视图类型, 如果有新的子视图类型, 直接往参数viewTypes中添加即可, 每个类型的值都要>3, 且不能重复
     *
     * @param viewTypes 子视图类型列表
     */
    public void setItemTypes(List<Integer> viewTypes) {
        if (viewTypes != null) {
            this.mViewTypes.addAll(viewTypes);
        }
    }

    /**
     * 自定义获取子视图类型的方法，如果有多种itemType，请重写此方法，在onBindHolder使用getItemType(int position)方法获得itemType
     * <p>
     * !!!!!!!!!!!!!!!!!!!!!请勿在子类中调用getItemViewType方法获取!!!
     */
    public int getItemType(int position) {
        return TYPE_CONTENT;
    }


    /**
     * 插入数据
     *
     * @param position
     * @param newDataList
     */
    public void insertData(int position, List<T> newDataList) {
        if (mDataContainer == null) {
            throw new NullPointerException("the mDataContainer not allowed null!!!");
        }
        isFirstLoad = false;
        if (mDataContainer.size() == 1 && mDataContainer.get(0) == null) {//之前显示为空或错误
            mDataContainer.clear();
            notifyDataSetChanged();
        }
        if (newDataList != null && newDataList.size() > 0) {
            KLog.i("=======插入前mDataContainer大小：" + mDataContainer.size());
            mDataContainer.addAll(position, newDataList);
            int refreshItemCount = mDataContainer.size() - (position + (hasHeader() ? 1 : 0));
            KLog.i("=======插入后mDataContainer大小：" + mDataContainer.size() + " ,插入位置：" + position + " ，需要刷新的个数：" + refreshItemCount);
            notifyDataSetChanged();
        }
        isNormalEmpty(newDataList != null && newDataList.size() >= mPageSize);
    }

    /**
     * 添加数据，自动控制是否还有更多，默认分页20条
     *
     * @param isRefresh   是否是刷新
     * @param newDataList 新数据
     */
    public void addData(boolean isRefresh, List<T> newDataList) {
        isFirstLoad = false;
        if (mDataContainer == null) {
            throw new NullPointerException("the mDataContainer not allowed null!!!");
        }
        if (mRecycleView.isRefreshing()) {
            mRecycleView.setRefreshing(false);
        }
        if (isRefresh) {
            mDataContainer.clear();
            notifyDataSetChanged();
        } else {
            if (mDataContainer.size() == 1 && mDataContainer.get(0) == null) {//之前显示为空或错误
                mDataContainer.clear();
                notifyDataSetChanged();
            }
        }
        if (newDataList != null && newDataList.size() > 0) {
            mDataContainer.addAll(newDataList);
            notifyItemsInserted(newDataList);
        }
        isNormalEmpty(newDataList != null && newDataList.size() >= mPageSize);
    }


    private boolean hasMore = true;

    /**
     * 添加数据，手动控制是否还有更多
     *
     * @param isRefresh   是否是刷新
     * @param newDataList 新数据
     * @param hasMore     手动控制是否还有更多
     */
    public void addData(boolean isRefresh, List<T> newDataList, boolean hasMore) {
        this.hasMore = hasMore;
        isFirstLoad = false;
        if (mDataContainer == null) {
            throw new NullPointerException("the mDataContainer not allowed null!!!");
        }
        if (mRecycleView.isRefreshing()) {
            mRecycleView.setRefreshing(false);
        }
        if (isRefresh) {
            mDataContainer.clear();
            notifyDataSetChanged();
        } else if (mDataContainer.size() == 1 && mDataContainer.get(0) == null) {
            mDataContainer.clear();
            notifyDataSetChanged();
        }
        if (newDataList != null && newDataList.size() > 0) {
            mDataContainer.addAll(newDataList);
            notifyItemsInserted(newDataList);
        }
        isNormalEmpty(hasMore);
    }

    public boolean hasMore() {
        return hasMore;
    }

    /**
     * 此方法在请求数据出错的情况下调用，只在列表中数据为空时候有效
     * 显示空白页或者错误页
     */
    public void showError(boolean isRefresh) {
        if (mDataContainer == null) {
            throw new NullPointerException("the mDataContainer not allowed null!!!");
        }
        if (mRecycleView.isRefreshing()) {
            mRecycleView.setRefreshing(false);
        }

        if (isRefresh) {
            mDataContainer.clear();
            notifyDataSetChanged();
        } else if (mDataContainer.size() == 1 && mDataContainer.get(0) == null) {
            mDataContainer.clear();
            notifyDataSetChanged();
        }
        isErrorEmpty();
    }

    /**
     * 只有在数据列表中所有的数据为空的时候才显示错误页或者空白页
     * 数据列表是否为空
     */
    private void isErrorEmpty() {
        mRecycleView.setFooterStatus(FooterStatusHandle.TYPE_PULL_LOAD_MORE);
        if (mDataContainer.size() == 0) {
            this.showError = true;
            mDataContainer.add(null);
            notifyDataSetChanged();
            mRecycleView.setFooterVisible(false);
        } else if (mRecycleView.isLoadMoreEnable()) {
            mRecycleView.setFooterVisible(true);
            mRecycleView.setFooterStatus(FooterStatusHandle.TYPE_ERROR);
        }
    }

    /**
     * 返回的数据正常的情况下，添加完数据后，如果mDataContainer是空，则显示数据为空
     * 如果mDataContainer不为空，则根据hasMore显示是否可加载更多
     */
    private void isNormalEmpty(boolean hasMore) {
        mRecycleView.setFooterStatus(hasMore ? FooterStatusHandle.TYPE_PULL_LOAD_MORE : FooterStatusHandle.TYPE_NO_MORE);
        if (mDataContainer.size() == 0) {
            this.showError = false;
            mDataContainer.add(null);
            notifyDataSetChanged();
            mRecycleView.setFooterVisible(false);
        } else if (mRecycleView.isLoadMoreEnable()) {
            mRecycleView.setFooterVisible(true);
        }
    }


    public T getItemData(int position) {
        if (position > -1 && mDataContainer != null && position < mDataContainer.size()) {
            return mDataContainer.get(position);
        }
        return null;
    }

    /**
     * 获取所有的数据
     *
     * @return
     */
    public List<T> getDataContainer() {
        return mDataContainer;
    }

    /**
     * 批量插入更新
     *
     * @param list
     */
    public void notifyItemsInserted(List list) {
        if (list == null || mDataContainer == null) {
            return;
        }
        int startPosition = mDataContainer.size() - list.size() + (hasHeader() ? 2 : 1);
        KLog.i("=======插入位置：" + startPosition);
        notifyItemRangeInserted(startPosition, list.size());
    }

    public void notifyItemsInserted(int insertPosition, List list) {
        if (list == null) {
            return;
        }
        KLog.i("=====插入位置：" + insertPosition + " ,大小：" + list.size());
        notifyItemRangeInserted(insertPosition + (hasHeader() ? 1 : 0), list.size());
    }

    /**
     * Item的点击事件
     *
     * @param onRecyclerViewItemClickListener
     */
    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener<T> onRecyclerViewItemClickListener) {
        mOnRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public void setOnRecyclerItemLongClickListener(OnRecyclerItemLongClickListener<T> onRecyclerItemLongClickListener) {
        mOnRecyclerItemLongClickListener = onRecyclerItemLongClickListener;
    }

    public abstract VH onCreateHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    public abstract void onBindHolder(VH holder, int position, T data);

    public boolean isFooter(int position) {
        return TYPE_FOOTER == getItemViewType(position);
    }

    public boolean isHeader(int position) {
        return TYPE_HEADER == getItemViewType(position);
    }

    /**
     * mDataContainer中是否有数据
     *
     * @return
     */
    public boolean hasContent() {
        if (mDataContainer == null) {
            return false;
        }
        return mDataContainer.size() > 0 && mDataContainer.get(0) != null;
    }

    /**
     * 是否是内容
     *
     * @param position
     * @return
     */
    public boolean isContent(int position) {
        return getItemViewType(position) == TYPE_CONTENT;
    }

    @Override
    public void onItemClick(View view, int position) {
        if (null != mOnRecyclerViewItemClickListener && position > -1) {
            int p = getDataPosition(position);
            if (p < getDataContainer().size() && p >= 0) {
                mOnRecyclerViewItemClickListener.onRecyclerItemClick(p, view, mDataContainer.get(p));
            }
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        if (null != mOnRecyclerItemLongClickListener && position > -1) {
            int p = getDataPosition(position);
            mOnRecyclerItemLongClickListener.onRecyclerItemLongClick(p, view, mDataContainer.get(p));
        }
    }

    /*header是否存在或者是否显示了*/
    public boolean hasHeader() {
        return (mRecycleView.getHeader() != null) && (mRecycleView.getHeader().getVisibility() == View.VISIBLE);
    }

    /*footer是否存在或者是否显示了*/
    public boolean hasFooter() {
        return mRecycleView.getFooter() != null && mRecycleView.getFooter().getVisibility() == View.VISIBLE;
    }

    public boolean isEmptyOrError(int position) {
        return TYPE_EMPTY == getItemViewType(position);
    }

    /*获取data的position*/
    public int getDataPosition(int layoutPosition) {
        return hasHeader() ? (layoutPosition - 1) : layoutPosition;
    }


}


