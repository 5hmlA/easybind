package jzy.easybindpagelist.loadmorehelper;

import java.util.HashMap;
import java.util.List;

import jzy.easybindpagelist.statehelper.PageDiffState;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;

/**
 * @another 江祖赟
 * @date 2017/12/18.
 * view 中 第一次发起数据请求调用 {@link #toSubscribeData(HashMap)} <br>
 * viewModel 中实际请求网络接口需要实现 {@link #onSubscribeData(Object)}<br>
 * {@link #onSubscribeData(Object)}拿到数据成功之后 根据结果调用{@link #refreshedAllData(List)},{@link #addMoreData(List, boolean)}<br>
 */
public abstract class LoadMoreViewModel2 extends LoadMoreViewModel {


    private HashMap mMapParam;

    public final void toSubscribeData(HashMap mapParam) {
        putOrignParam(mapParam);
        mMapParam = mapParam;
        toGetData(mMapParam);
    }

    @Override
    public void onSubscribeData(Object orignParam){
        toGetData(mMapParam);
    }

    /**
     * 界面 第一次发起请求 调用 {@link #toSubscribeData(HashMap)}
     */
    @Override public final void subscribeData(Object orignParam) {
        super.subscribeData(orignParam);
    }


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
     */
    public abstract void toGetData(HashMap mapParam);
}
