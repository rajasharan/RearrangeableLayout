package com.rajasharan.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by raja on 6/19/15.
 */
public class RearrangeableLayout extends ViewGroup {
    private static final String TAG = "RearrangeableLayout";

    private PointF mStartTouch;
    private View mSelectedChild;
    private float mSelectionZoom;
    private Paint mSelectionPaint;
    private Paint mOutlinePaint;

    public RearrangeableLayout(Context context) {
        this(context, null);
    }

    public RearrangeableLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RearrangeableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mStartTouch = null;
        mSelectedChild = null;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RearrangeableLayout);
        float strokeWidth = a.getDimension(R.styleable.RearrangeableLayout_outlineWidth, 2.0f);
        int color = a.getColor(R.styleable.RearrangeableLayout_outlineColor, Color.GRAY);
        float alpha = a.getFloat(R.styleable.RearrangeableLayout_selectionAlpha, 0.5f);
        mSelectionZoom = a.getFloat(R.styleable.RearrangeableLayout_selectionZoom, 1.2f);
        a.recycle();

        float filter[] = new float[] {
                1f, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, alpha, 0f
        };
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(new ColorMatrix(filter));

        mOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutlinePaint.setStyle(Paint.Style.STROKE);
        mOutlinePaint.setStrokeWidth(strokeWidth);
        mOutlinePaint.setColor(color);
        mOutlinePaint.setColorFilter(colorFilter);

        mSelectionPaint = new Paint();
        mSelectionPaint.setColorFilter(colorFilter);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p) && p instanceof LayoutParams;
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        width = Math.max(width, getMinimumWidth());
        height = Math.max(height, getMinimumHeight());

        //Log.d(TAG, String.format("onMeasure: (%d, %d)", width, height));
        //measureChildren(widthMeasureSpec, heightMeasureSpec);

        for(int i=0; i<getChildCount(); i++) {
            View view = getChildAt(i);
            LayoutParams mp = (LayoutParams) view.getLayoutParams();
            view.measure(MeasureSpec.makeMeasureSpec(width -mp.leftMargin -mp.rightMargin, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(height -mp.topMargin -mp.bottomMargin, MeasureSpec.AT_MOST));

            //int w = view.getMeasuredWidth();
            //int h = view.getMeasuredHeight();
            //Log.d(TAG, String.format("View #%d: (%d, %d)", i, w, h));
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mSelectedChild == null) {
            doInitialLayout(l, t, r, b, getChildCount());
        }
        else {
            layoutSelectedChild();
        }
    }

    private void doInitialLayout(int l, int t, int r, int b, int count) {
        int currentLeft = l;
        int currentTop = t;
        int prevChildBottom = -1;
        for (int i=0; i<count; i++) {
            View view = getChildAt(i);
            LayoutParams mp = (LayoutParams) view.getLayoutParams();
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            int left, top, right, bottom;

            if (view.getVisibility() != View.GONE && !mp.moved) {
                if (currentTop+height > b || l+width > r) {
                    Toast.makeText(getContext(), "Couldn't fit a child View, skipping it", Toast.LENGTH_SHORT)
                            .show();
                    Log.d(TAG, "Couldn't fit a child View, skipping it");
                    continue;
                }
                if (currentLeft+width > r) {
                    left = l + mp.leftMargin;
                    currentTop = prevChildBottom;
                } else {
                    left = currentLeft + mp.topMargin;
                }
                top = currentTop + mp.topMargin;
                right = left + width;
                bottom = top + height;
                //Log.d(TAG, String.format("Layout #%d: (%d, %d, %d, %d)", i, left, top, right, bottom));
                mp.left = left;
                mp.top = top;
                view.layout(left, top, right, bottom);

                currentLeft = right + mp.rightMargin;
                prevChildBottom = bottom + mp.bottomMargin;
            }
            else if (mp.moved && view != mSelectedChild) {
                int x1 = Math.round(mp.left);
                int y1 = Math.round(mp.top);
                int x2 = Math.round(mp.left) + width;
                int y2 = Math.round(mp.top) + height;
                view.layout(x1, y1, x2, y2);
            }
        }
    }

    private void layoutSelectedChild() {
        LayoutParams lp = (LayoutParams) mSelectedChild.getLayoutParams();
        int l = Math.round(lp.left);
        int t = Math.round(lp.top);
        int r = l + mSelectedChild.getMeasuredWidth();
        int b = t + mSelectedChild.getMeasuredHeight();

        lp.moved = true;
        mSelectedChild.layout(l, t, r, b);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mSelectedChild != null) {
            mSelectedChild.setVisibility(View.GONE);
        }
        super.dispatchDraw(canvas);

        if (mSelectedChild != null) {
            Rect rect = new Rect();
            mSelectedChild.getHitRect(rect);

            int restorePoint = canvas.save();
            canvas.scale(mSelectionZoom, mSelectionZoom, rect.centerX(), rect.centerY());
            canvas.drawRect(rect, mOutlinePaint);

            mSelectedChild.setDrawingCacheEnabled(true);
            Bitmap child = mSelectedChild.getDrawingCache();
            if (child != null) {
                LayoutParams lp = (LayoutParams) mSelectedChild.getLayoutParams();
                canvas.drawBitmap(child, lp.left, lp.top, mSelectionPaint);
            } else {
                Log.d(TAG, "drawingCache not found! Maybe because of hardware acceleration");
                mSelectedChild.draw(canvas);
            }
            canvas.restoreToCount(restorePoint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartTouch = null;
                mSelectedChild = findChildViewInsideTouch(Math.round(x), Math.round(y));
                if (mSelectedChild != null) {
                    bringChildToFront(mSelectedChild);
                    LayoutParams lp = (LayoutParams) mSelectedChild.getLayoutParams();
                    lp.initial = new PointF(lp.left, lp.top);
                    mStartTouch = new PointF(x, y);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mSelectedChild != null && mStartTouch != null) {
                    LayoutParams lp = (LayoutParams) mSelectedChild.getLayoutParams();
                    float dx = x - mStartTouch.x;
                    float dy = y - mStartTouch.y;

                    lp.left = lp.initial.x + dx;
                    if (lp.left < 0.0f) {
                        lp.left = 0.0f;
                    }

                    lp.top = lp.initial.y + dy;
                    if (lp.top < 0.0f) {
                        lp.top = 0.0f;
                    }

                    layoutSelectedChild();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            default:
                if (mSelectedChild != null) {
                    mSelectedChild.setVisibility(View.VISIBLE);
                    mSelectedChild = null;
                    invalidate();
                }
                break;
        }
        return true;
    }

    /**
     * Search by hightest index to lowest so that the
     * most recently touched child is found first
     *
     * @return selectedChild
     */
    private View findChildViewInsideTouch(int x, int y) {
        for(int i=getChildCount()-1; i>=0; i--) {
            View view = getChildAt(i);
            Rect rect = new Rect();
            view.getHitRect(rect);
            if (rect.contains(x, y)) {
                return view;
            }
        }
        return null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public static class LayoutParams extends MarginLayoutParams {
        float left;
        float top;
        PointF initial;
        boolean moved;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            left = -1.0f;
            top = -1.0f;
            initial = new PointF(0.0f, 0.0f);
            moved = false;
        }
    }
}
