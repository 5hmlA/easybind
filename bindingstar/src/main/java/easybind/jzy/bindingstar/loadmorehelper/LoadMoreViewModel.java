package easybind.jzy.bindingstar.loadmorehelper;

import java.util.List;

import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;

/**
 * @another 江祖赟
 * @date 2017/12/18.
 *
 * 布局中引用
 * <include
 * android:id="@+id/com_pagelist_layout"
 * layout="@layout/com_pagelist_layout"
 * android:layout_width="match_parent"
 * android:layout_height="0dp"
 * android:layout_weight="1"
 * bind:pageViewModel="@{recViewModel}"/>
 */
public abstract class LoadMoreViewModel extends BaseLoadmoreViewModel {


    /**
     * 注册 列表中使用到的 数据类型和布局文件 {@link OnItemBindClass#regist(Class, int, int)}(参数：类型，布局参数变量，布局)
     *
     * @param multipleItems
     */
    @Override
    protected abstract void registItemTypes(OnItemBindClass<Object> multipleItems);

    /**
     * 调用接口 获取数据
     * <p>
     * <B>下拉刷新</B><br>
     * 下拉刷新 会自动调用 {@link #down2RefreshData()},mCurrentPage会重置为初值---》回掉{@link #subscribeData(Object)}
     * </p>
     * <p>
     * <b> 上拉加载更多</b><br>
     * 参数 当前页码 {@link #mCurrentPage}直接使用 父类有自动增减
     * <br>上拉之后 {@link #mCurrentPage}自增，同时回掉{@link #subscribeData(Object)}
     * </p>
     * <B>拿到数据后需要子类处理</B>
     * <li>数据完整正常 <br>
     * 如果是第一页数据 调用{@link #refreshedAllData(List)}<br>
     * 如果 不是 第一页数据 调用{@link #addMoreData(List, boolean)}同时传入是否还有下一页数据<br>
     * </li>
     * <li>数据异常(空/网络异常) ，直接调用{@link #showPageStateError(int)},或者 {@link #showPageStateError(int, String)}
     * <br>要显示的错误状态{PAGE_STATE_EMPTY,PAGE_STATE_EMPTY}具体看{@link easybind.jzy.bindingstar.statehelper.PageDiffState}</li>
     *
     * @param orignParam
     */
    @Override
    public abstract void subscribeData(Object orignParam);

}
