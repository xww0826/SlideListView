package listview.example.x.slidelistview;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * @Created by xww.
 * @Creation time 2018/8/22.
 */

public class RefreshLayout extends FrameLayout implements ListView.OnScrollListener {

    private View mHeaderView;
    private View mContentView;
    private View mFooterView;
    private ListView mListView;

    private TextView tvRefreshText;
    private ProgressBar mRefreshProgress;
    private ImageView ivRefreshIcon;
    private ProgressBar mLoadingProgress;
    private TextView tvLoadingText;
    private ImageView ivLoadingIcon;

    private int mHeaderWidth;
    private int mHeaderHeight;
    private int mFooterWidth;
    private int mFooterHeight;
    private int mContentWidth;
    private int mContentHeight;

    /**
     * 刷新的高度是Header的2/3
     */
    private int mRefreshHeight;
    private int mLoadingHeight;

    private Scroller mScroller;

    private boolean isTop;
    private boolean isBottom;

    private float startX;
    private float startY;

    private boolean isIntercept;

    private Handler handler;

    /**
     * 保存上拉开始的坐标
     */
    private float upX;
    private float upY;
    /**
     * 保存下拉开始的坐标
     */
    private float downX;
    private float downY;

    private int firstDownTag;

    public RefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        handler = new Handler();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHeaderView = getChildAt(0);
        mContentView = getChildAt(1);
        mFooterView = getChildAt(2);

        mListView = (ListView) mContentView;
        tvRefreshText = mHeaderView.findViewById(R.id.tv_refresh_state);
        mRefreshProgress = mHeaderView.findViewById(R.id.refresh_progress);
        ivRefreshIcon = mHeaderView.findViewById(R.id.iv_refreshing);
        mLoadingProgress = mFooterView.findViewById(R.id.load_progress);
        tvLoadingText = mFooterView.findViewById(R.id.tv_load_state);
        ivLoadingIcon = mFooterView.findViewById(R.id.ivLoadingIcon);
        mListView.setOnScrollListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mContentWidth = getMeasuredWidth();
        mContentHeight = getMeasuredHeight();
        mHeaderWidth = mHeaderView.getMeasuredWidth();
        mHeaderHeight = mHeaderView.getMeasuredHeight();
        mFooterWidth = mFooterView.getMeasuredWidth();
        mFooterHeight = mFooterView.getMeasuredHeight();
        /**
         * 设置滑动多高才刷新的距离
         */
        mRefreshHeight = mHeaderHeight * 2 / 3;
        mLoadingHeight = mLoadingProgress.getHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mHeaderView.layout(0, -mHeaderHeight, mHeaderWidth, 0);
        mContentView.layout(0, 0, mContentWidth, mContentHeight);
        mFooterView.layout(0, mContentHeight, mFooterWidth, mContentHeight + mFooterHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = x;
                startY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTop) {
                    if (firstDownTag == 0) {
                        /**
                         * 如果是第一次的话，因为事件传递原因
                         * onInterceptTouchEvent()执行了 ACTION_DOWN事件
                         * 标记了startY的值（这个值也许非常大，是根据手指按下的y坐标来定的）
                         * 关键是onTouchEvent的ACTION_DOWN无法得到执行，所以 scrollTo(0, disY);将直接移动到startY的位置
                         * 效果就是导致第一次向下拉，瞬间移动了非常多
                         */
                        firstDownTag++;
                    } else {
                        final float dy = y - startY;
                        int disY = (int) (getScrollY() - dy);
                        if (-disY <= 0) {
                            disY = 0;
                        }

                        if (-disY < mHeaderHeight) {
                            scrollTo(0, disY);
                            mRefreshProgress.setVisibility(INVISIBLE);
                            if (-disY < mRefreshHeight) {
                                tvRefreshText.setText("准备起飞");
                                startRefreshIcon();
                            } else {
                                tvRefreshText.setText("加速中");
                                stopRefreshIcon();
                            }
                        }
                    }
                } else if (isBottom) {/** 在ListView底部，继续上拉 **/
                    final float dy = y - startY;
                    int disY = (int) (getScrollY() - dy);
                    if (disY < 0) {
                        disY = 0;
                        ivLoadingIcon.setVisibility(VISIBLE);
                        mLoadingProgress.setVisibility(INVISIBLE);
                    } else if (disY >= mLoadingHeight) {
                        disY = mLoadingHeight + 5;
                    }
                    scrollTo(getScrollX(), disY);

//                    if (dy < 0) {
//                        startLoadingIcon();
//                    } else {
//                        stopLoadingIcon();
//                    }
                }
                startX = x;
                startY = y;
                break;
            case MotionEvent.ACTION_UP:
                isIntercept = false;
                if (isTop) {
                    if (-getScrollY() > mRefreshHeight) {
                        startRefreshing();
                    } else {
                        stopRefreshing();
                    }
                } else if (isBottom) {
                    if (getScrollY() > mLoadingHeight) {
                        startLoading();
                    } else {
                        stopLoading();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isIntercept = false;
                upX = downX = x;
                upY = downY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTop) {
                    /** 下拉刷新拦截 **/
                    if (upY - y < 0) {
                        isIntercept = true;
                    } else if (y - upY < 0) {
                        isIntercept = false;
                    }
                } else if (isBottom) {
                    /** 上拉加载拦截 **/
                    if (y - downY < 0) {
                        isIntercept = true;
                    } else if (y - downY > 0) {
                        isIntercept = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                downX = upY = 0;
                downX = upX = 0;
                break;
        }
        return isIntercept;
    }

    private void stopRefreshing() {
        mScroller.startScroll(getScrollX(), getScrollY(), 0, -getScrollY());
        invalidate();
        isIntercept = false;
    }

    private void startRefreshing() {
        mScroller.startScroll(getScrollX(), getScrollY(), 0, -mRefreshHeight - getScrollY());
        tvRefreshText.setText("起飞咯~");
        mRefreshProgress.setVisibility(VISIBLE);
        startIconAnimation();
        invalidate();
        isIntercept = false;
        /**
         * 模拟刷新完成，延迟关闭
         */
        handler.postDelayed(() -> stopRefreshing(), 2000);
    }

    private void startLoading() {
        mScroller.startScroll(getScrollX(), getScrollY(), 0, mFooterHeight - getScrollY());
        ivLoadingIcon.setVisibility(INVISIBLE);
        mLoadingProgress.setVisibility(VISIBLE);
        invalidate();
        isIntercept = false;
        handler.postDelayed(() -> stopLoading(), 1500);
    }

    private void stopLoading() {
        mScroller.startScroll(getScrollX(), getScrollY(), 0, -getScrollY());
        invalidate();
        isIntercept = false;
    }

    private void startIconAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0, 0,
                getScaleY(), -mRefreshHeight);
        animation.setFillAfter(false);
        animation.setDuration(2000);
        ivRefreshIcon.startAnimation(animation);
    }

    private void startRefreshIcon() {
        ivRefreshIcon.setPivotX(ivRefreshIcon.getWidth() / 2);
        ivRefreshIcon.setPivotY(ivRefreshIcon.getHeight() / 2);
        ivRefreshIcon.setRotation(180);
    }

    private void stopRefreshIcon() {
        ivRefreshIcon.setPivotX(ivRefreshIcon.getWidth() / 2);
        ivRefreshIcon.setPivotY(ivRefreshIcon.getHeight() / 2);
        ivRefreshIcon.setRotation(360);
    }

    private void startLoadingIcon() {
        ivLoadingIcon.setPivotX(ivLoadingIcon.getWidth() / 2);
        ivLoadingIcon.setPivotY(ivLoadingIcon.getHeight() / 2);
        ivLoadingIcon.setRotation(0);
    }

    private void stopLoadingIcon() {
        ivLoadingIcon.setPivotX(ivLoadingIcon.getWidth() / 2);
        ivLoadingIcon.setPivotY(ivLoadingIcon.getHeight() / 2);
        ivLoadingIcon.setRotation(180);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isTop = firstVisibleItem == 0;
        isBottom = firstVisibleItem + visibleItemCount == totalItemCount;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}
