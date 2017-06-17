package me.nontan.spajam_sweets_kun.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public final class PopupView extends RelativeLayout {
    public interface PopupViewDismissListener {
        void onDismissed();
    }

    private View mTouchHandleView;
    private View mTrianglerView;
    private View mContentView;
    private int mMinOutMargin;
    private int mMaxHeight;
    private int mMeasuredHeight;
    private int mContentViewX;
    private int mContentViewY;

    private boolean mDismissOnTouchOutside;
    private PopupViewDismissListener mListener;

    public static final int UNRESTRICTED = -1;

    public enum AnchorGravity {
        TOP, BOTTOM, CENTER, AUTO
    }

    public PopupView(Context context) {
        super(context);
        init();
    }

    public PopupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setListener(PopupViewDismissListener listener) {
        mListener = listener;
    }

    // TODO: DrawActivityで同じインスタンスを使いまわしてるから毎回これを呼び出さないといけないのが辛いので後でどうにかする
    // reset()とか用意して毎回インスタンスの状態をdefaultに戻すとかかなぁ…
    public void setDismissOnTouchOutside(boolean dismissOnTouchOutside) {
        mDismissOnTouchOutside = dismissOnTouchOutside;
    }

    private void init() {
        mTouchHandleView = new View(getContext());
        mTouchHandleView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mTouchHandleView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDismissOnTouchOutside) {
                    dismiss();
                }
            }
        });
        mTouchHandleView.setBackgroundColor(Color.BLACK);
        mTouchHandleView.setAlpha(0.1f);
        mTouchHandleView.setVisibility(GONE);
        addView(mTouchHandleView);
        mTrianglerView = new View(getContext());
        int trianglerViewSize = 10; // getResources().getDimensionPixelSize(10);
        mTrianglerView.setLayoutParams(new ViewGroup.LayoutParams(trianglerViewSize, trianglerViewSize));
        mTrianglerView.setRotation(45);
        mTrianglerView.setBackgroundColor(Color.WHITE);
        addView(mTrianglerView);
        // ぬるぽ回避
        mContentView = new View(getContext());
        // TODO: dimenで指定せずPopupViewで管理したい
        mMinOutMargin = 10; // getResources().getDimensionPixelSize(10);
        setBackgroundColor(0);
        setVisibility(View.INVISIBLE);
    }

    private void showAnimation() {
        mTouchHandleView.setVisibility(VISIBLE);
        setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(150);
        startAnimation(alphaAnimation);
    }

    public int getContentWidth() {
        int maxWidth = 800;
        return getWidth() - mMinOutMargin * 2 > maxWidth ? maxWidth : getWidth() - mMinOutMargin * 2;
    }

    private void updateContentViewLayout(int height) {
        LayoutParams layoutParams = new LayoutParams(
                getContentWidth(),
                height
        );
        layoutParams.addRule(CENTER_IN_PARENT, TRUE);
        mContentView.setLayoutParams(layoutParams);
    }

    public boolean isShowing() {
        return getVisibility() == View.VISIBLE;
    }

    public void dismiss() {
        if (mListener != null) {
            mListener.onDismissed();
        }

        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTouchHandleView.setVisibility(GONE);
                setVisibility(View.INVISIBLE);
                removeView(mContentView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        startAnimation(alphaAnimation);
    }

    public void setContentView(View contentView) {
        removeView(mContentView);
        mContentView = contentView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mContentView.setClipToOutline(true);
        }
        addView(mContentView);
    }

    public void setMaxHeight(int maxHeight) {
        mMaxHeight = maxHeight;
    }

    public void setTrianglerViewColor(int color) {
        mTrianglerView.setBackgroundColor(color);
    }

    public void show(final Rect boundingRect, final AnchorGravity anchorGravity, final int height, final int offset) {
        showAnimation();
        mTrianglerView.setVisibility(anchorGravity == AnchorGravity.CENTER ?
                View.INVISIBLE : View.VISIBLE);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                updateContentViewLayout(height);
                if (anchorGravity == AnchorGravity.CENTER) {
                    return;
                }
                System.out.printf("%d, %d\n", mContentView.getWidth(), mContentView.getHeight());
                mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        int centerX = boundingRect.centerX();
                        int top = boundingRect.top;
                        int anchorViewsHeight = boundingRect.height();

                        AnchorGravity gravity = anchorGravity;
                        if (anchorGravity == AnchorGravity.AUTO) {
                            gravity = top > getHeight() / 2 ? AnchorGravity.TOP : AnchorGravity.BOTTOM;
                        }

                        int x = centerX - mContentView.getWidth() / 2;
                        if (x < mMinOutMargin) {
                            x = mMinOutMargin;
                        } else if (x > getWidth() - mContentView.getWidth() - mMinOutMargin) {
                            x = getWidth() - mContentView.getWidth() - mMinOutMargin;
                        }
                        mContentViewX = x;
                        mContentView.setX(x);
                        mTrianglerView.setX(centerX - mTrianglerView.getWidth() / 2);

                        if (gravity == AnchorGravity.TOP) {
                            int y = top - anchorViewsHeight / 2 - mContentView.getHeight() - offset;
                            mContentViewY = y;
                            mContentView.setY(y);
                            mTrianglerView.setY(y + mContentView.getHeight() - mTrianglerView.getHeight() / 2);
                        } else if (gravity == AnchorGravity.BOTTOM) {
                            int y = top + anchorViewsHeight / 2 + offset;
                            mContentViewY = y;
                            mContentView.setY(y);
                            mTrianglerView.setY(y - mTrianglerView.getHeight() / 2);
                        }
                    }
                });
            }
        });
    }

    private List<View> singleToList(View v) {
        ArrayList<View> vs = new ArrayList<View>();
        vs.add(v);
        return vs;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mMaxHeight == UNRESTRICTED) {
            return;
        }

        int width = getContentWidth();
        int contentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int contentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);

        mContentView.measure(contentWidthMeasureSpec, contentHeightMeasureSpec);
        mMeasuredHeight = mContentView.getMeasuredHeight();
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mMaxHeight == UNRESTRICTED) {
            return;
        }

        int width = getContentWidth();
        int contentViewHeight = Math.min(mMeasuredHeight, mMaxHeight);
        mContentView.layout(mContentViewX, mContentViewY, mContentViewX + width, mContentViewY + contentViewHeight);

        mContentView.setX(mContentViewX);
        mContentView.setY(mContentViewY);
        mContentView.setBottom(mContentViewY + contentViewHeight);
    }
}
