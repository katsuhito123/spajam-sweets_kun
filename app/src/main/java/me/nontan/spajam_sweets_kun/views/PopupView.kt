package me.nontan.spajam_sweets_kun.views

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.RelativeLayout

private val UNRESTRICTED = -1;

class PopupView : RelativeLayout {
    private lateinit var touchHandleView: View
    private lateinit var triangularView: View
    private lateinit var contentView: View
    private var minOutMargin: Int = 0
    private var maxHeight: Int = 0
    private var measuredContentViewHeight: Int = 0
    private var contentViewX: Int = 0
    private var contentViewY: Int = 0

    enum class AnchorGravity {
        TOP, BOTTOM, CENTER, AUTO
    }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init()
    }

    private fun init() {
        touchHandleView = View(context)
        touchHandleView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )
        touchHandleView.setOnClickListener {
            dismiss()
        }
        touchHandleView.setBackgroundColor(Color.BLACK)
        touchHandleView.alpha = 0.1f
        touchHandleView.visibility = View.GONE
        addView(touchHandleView)
        triangularView = View(context)
        val trianglerViewSize = 10
        triangularView.layoutParams = ViewGroup.LayoutParams(trianglerViewSize, trianglerViewSize)
        triangularView.rotation = 45f
        triangularView.setBackgroundColor(Color.WHITE)
        addView(triangularView)
        contentView = View(context)
        minOutMargin = 10
        setBackgroundColor(0)
        visibility = View.INVISIBLE
    }

    private fun showAnimation() {
        touchHandleView.visibility = View.VISIBLE
        visibility = View.VISIBLE
        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.duration = 150
        startAnimation(alphaAnimation)
    }

    val contentWidth: Int
        get() {
            val maxWidth = 800
            return if (width - minOutMargin * 2 > maxWidth) maxWidth else width - minOutMargin * 2
        }

    private fun updateContentViewLayout(height: Int) {
        val layoutParams = RelativeLayout.LayoutParams(
                contentWidth,
                height
        )
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        contentView.layoutParams = layoutParams
    }

    val isShowing: Boolean
        get() = visibility == View.VISIBLE

    fun dismiss() {
        val alphaAnimation = AlphaAnimation(1f, 0f)
        alphaAnimation.duration = 300
        alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                touchHandleView.visibility = View.GONE
                visibility = View.INVISIBLE
                removeView(contentView)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        startAnimation(alphaAnimation)
    }

    fun setContentView(contentView: View) {
        removeView(this.contentView)
        this.contentView = contentView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.contentView.clipToOutline = true
        }
        addView(this.contentView)
    }

    fun setMaxHeight(maxHeight: Int) {
        this.maxHeight = maxHeight
    }

    fun show(boundingRect: Rect, anchorGravity: AnchorGravity, height: Int, offset: Int) {
        showAnimation()
        triangularView.visibility = if (anchorGravity == AnchorGravity.CENTER)
            View.INVISIBLE
        else
            View.VISIBLE
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                updateContentViewLayout(height)
                if (anchorGravity == AnchorGravity.CENTER) {
                    return
                }
                contentView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                        val centerX = boundingRect.centerX()
                        val top = boundingRect.top
                        val anchorViewsHeight = boundingRect.height()

                        var gravity = anchorGravity
                        if (anchorGravity == AnchorGravity.AUTO) {
                            gravity = if (top > getHeight() / 2) AnchorGravity.TOP else AnchorGravity.BOTTOM
                        }

                        var x = centerX - contentView.width / 2
                        if (x < minOutMargin) {
                            x = minOutMargin
                        } else if (x > width - contentView.width - minOutMargin) {
                            x = width - contentView.width - minOutMargin
                        }
                        contentViewX = x
                        contentView.x = x.toFloat()
                        triangularView.x = (centerX - triangularView.width / 2).toFloat()

                        if (gravity == AnchorGravity.TOP) {
                            val y = top - anchorViewsHeight / 2 - contentView.height - offset
                            contentViewY = y
                            contentView.y = y.toFloat()
                            triangularView.y = (y + contentView.height - triangularView.height / 2).toFloat()
                        } else if (gravity == AnchorGravity.BOTTOM) {
                            val y = top + anchorViewsHeight / 2 + offset
                            contentViewY = y
                            contentView.y = y.toFloat()
                            triangularView.y = (y - triangularView.height / 2).toFloat()
                        }
                    }
                })
            }
        })
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (maxHeight == UNRESTRICTED) {
            return
        }

        val width = contentWidth
        val contentWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val contentHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST)

        contentView.measure(contentWidthMeasureSpec, contentHeightMeasureSpec)
        measuredContentViewHeight = contentView.measuredHeight
    }

    public override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        if (maxHeight == UNRESTRICTED) {
            return
        }

        val width = contentWidth
        val contentViewHeight = Math.min(measuredContentViewHeight, maxHeight)
        contentView.layout(contentViewX, contentViewY, contentViewX + width, contentViewY + contentViewHeight)

        contentView.x = contentViewX.toFloat()
        contentView.y = contentViewY.toFloat()
        contentView.bottom = contentViewY + contentViewHeight
    }
}
