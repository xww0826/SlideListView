package listview.example.x.slidelistview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import static android.os.Build.VERSION_CODES.M;

/**
 * @Created by xww.
 * @Creation time 2018/8/23.
 */

public class DrawerLayout extends FrameLayout {

    private View mDrawerView;
    private View mContentView;

    private int mDrawerWidth;
    private int mDrawerHeight;

    private Scroller mScroller;

    private float startX;
    private float startY;
    private float moveX;
    private float moveY;

    private boolean isDrawerOpen;
    private boolean isMoveLeft;
    private boolean isVerticalScroll;

    public DrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDrawerView = getChildAt(0);
        mContentView = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mDrawerWidth = mDrawerView.getMeasuredWidth();
        mDrawerHeight = mDrawerView.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mDrawerView.layout(-mDrawerWidth, 0, 0, mDrawerHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveX = startX = x;
                moveY = startY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                final float dx = x - startX;
                final float dy = y - startY;

                int disX = (int) (getScrollX() - dx);
                if (-disX >= mDrawerWidth) {
                    disX = -mDrawerWidth;
                }
                scrollTo(disX, getScrollY());

                if (x - startX < 0) {
                    isMoveLeft = true;
                } else if (x - startX > 0) {
                    isMoveLeft = false;
                }

                startX = x;
                startY = y;
                break;
            case MotionEvent.ACTION_UP:
                /**
                 * 抽屉未打开
                 */
                if (!isDrawerOpen) {
                    if (-getScrollX() > mDrawerWidth / 3) {
                        openDrawer();
                    } else {
                        closeDrawer();
                    }
                } else {
                    /**
                     * 抽屉是开着
                     */
                    if (isMoveLeft) { /** 产生左移行为，关闭抽屉 **/
                        if (-getScrollX() > 15) { //至少滑动一点距离
                            closeDrawer();
                        }
                    } else {
                        /** 产生右移行为，恢复打开时状态 **/
                        openDrawer();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercept = false;
        final float x = event.getX();
        final float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = x;
                startY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                final float dx = x - startX;
                final float dy = y - startX;
                /**
                 * 判断抽屉未打开时
                 */
                if (!isDrawerOpen) {
                    /**
                     * 抽屉右滑
                     */
                    if (x - startX > 0 && dx > 0) { // 右滑
                        intercept = true;
                    }
                } else {
                    /**
                     * 抽屉打开，并非拦截所有事件，也许抽屉里还有滚动，这里要做出判断。
                     * 全拦截，则抽屉子菜单选项滚动不了
                     */
                    final int disX = (int) Math.abs(x - startX);
                    final int disY = (int) Math.abs(y - startY);
                    if (disX > disY && disX > 15) {
                        intercept = true;
                    }
                    //如果抽屉里有滚动列表，则不拦截它
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return intercept;
    }

    private void openDrawer() {
        isDrawerOpen = true;
        mScroller.startScroll(getScrollX(), getScrollY(), -getScrollX() - mDrawerWidth, 0);
        invalidate();
    }

    private void closeDrawer() {
        isDrawerOpen = false;
        mScroller.startScroll(getScrollX(), getScrollY(), -getScrollX(), 0);
        invalidate();
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
