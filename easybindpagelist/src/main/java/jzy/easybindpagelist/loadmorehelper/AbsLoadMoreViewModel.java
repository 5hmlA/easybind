package jzy.easybindpagelist.loadmorehelper;

import android.databinding.Observable;

import java.util.List;

import jzy.easybindpagelist.statehelper.PageDiffState;
import me.tatarka.bindingcollectionadapter2.itembindings.ExtrasBindViewModel;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;

/**
 * @another 江祖赟
 * @date 2017/12/18.
 * view 中 第一次发起数据请求调用 {@link #subscribeData(Object)}<br>
 * viewModel 中实际请求网络接口需要实现 {@link #onSubscribeData(Object)}<br>
 * {@link #onSubscribeData(Object)}拿到数据成功之后 根据结果调用{@link #refreshedAllData(List)},{@link #addMoreData(List, boolean)}<br>
 *   <b>接口返回的数据列表的javaBean需要更新对应的item界面必须继承{@link Observable},建议最好继承自{@link ExtrasBindViewModel} ViewModel需要继承{@link LoadMoreViewModel}</b>
 *   <b>接口返回的数据列表的javaBean也可以不需要更细UI不需要继承任何父类，那么只能展示列表数据无法动态更新, ViewModel需要继承{@link LoadMoreObjectViewModel}</b>
 */
public abstract class AbsLoadMoreViewModel<ID> extends BaseLoadmoreViewModel<ID> {

    /**
     * 注册 列表中使用到的 数据类型和布局文件 {@link OnItemBindClass#regist(Class, int, int)}(参数：类型，布局参数变量，布局)
     *
     * @param multipleItems
     */
    @Override
    protected abstract void registItemTypes(OnItemBindClass<ID> multipleItems);

    /**
     * 调用接口 获取数据
     * <p>
     * <B>下拉刷新</B><br>
     * 下拉刷新 会自动调用 {@link #down2RefreshData()},mCurrentPage会重置为初值---》回掉{@link #onSubscribeData(Object)}
     * </p>
     * <p>
     * <b> 上拉加载更多</b><br>
     * 参数 当前页码 {@link #mCurrentPage}直接使用 父类有自动增减
     * <br>上拉之后 {@link #mCurrentPage}自增，同时回掉{@link #onSubscribeData(Object)}
     * </p>
     * <B>拿到数据后需要子类处理</B>
     * <li>数据完整正常 <br>
     * 如果是第一页数据 调用{@link #refreshedAllData(List)}<br>
     * 如果 不是 第一页数据 调用{@link #addMoreData(List, boolean)}同时传入是否还有下一页数据<br>
     * </li>
     * <li>数据异常(空/网络异常) ，直接调用{@link #showPageStateError(int)},或者 {@link #showPageStateError(int, String)}
     * <br>要显示的错误状态{PAGE_STATE_EMPTY,PAGE_STATE_EMPTY}具体看{@link PageDiffState}</li>
     *
     * @param orignParam
     */
    @Override
    public abstract void onSubscribeData(Object orignParam);

}
