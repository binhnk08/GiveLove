package com.xlteam.givelove.external.utility.gesture;

import android.content.Context;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xlteam.givelove.external.utility.utils.Utility;

public class MultiTouchListener implements OnTouchListener {

    private static final int INVALID_POINTER_ID = -1;
    private static final int DIFF_IN_BOUND_AREA = 40;
    private final GestureDetector gestureListener;
    boolean isRotateEnabled = true;
    boolean isTranslateEnabled = true;
    boolean isScaleEnabled = true;
    float minimumScale = 0.2f;
    float maximumScale = 10.0f;
    private int activePointerId = INVALID_POINTER_ID;
    private static final int CLICK_THRESHOLD_DURATION = 300;
    private static final float CLICK_THRESHOLD_DISTANCE = 2f;
    private float prevX, prevY, prevRawX, prevRawY;
    private ScaleGestureDetector scaleGestureDetector;

    private int[] location = new int[2];
    private Rect outRect;
    private View deleteView;
    private ImageView photoEditImageView;
    private RelativeLayout parentView;
    private Context mContext;
    private boolean mIsInViewBounds;

    private OnMultiTouchListener onMultiTouchListener;
    private OnGestureControl onGestureControl;
    boolean isTextPinchZoomable;
    private OnPhotoEditorListener onPhotoEditorListener;

    public MultiTouchListener(@Nullable View deleteView, RelativeLayout parentView,
                              ImageView photoEditImageView,
                              OnPhotoEditorListener onPhotoEditorListener, Context context) {
        isTextPinchZoomable = true;
        scaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener(this));
        gestureListener = new GestureDetector(context, new GestureListener());
        this.deleteView = deleteView;
        this.parentView = parentView;
        this.photoEditImageView = photoEditImageView;
        this.onPhotoEditorListener = onPhotoEditorListener;
        if (deleteView != null) {
            outRect = new Rect(deleteView.getLeft(), deleteView.getTop(),
                    deleteView.getRight(), deleteView.getBottom());
        } else {
            outRect = new Rect(0, 0, 0, 0);
        }
        mContext = context;
    }

    private float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            degrees -= 360.0f;
        } else if (degrees < -180.0f) {
            degrees += 360.0f;
        }

        return degrees;
    }

    void move(View view, TransformInfo info) {
        computeRenderOffset(view, info.pivotX, info.pivotY);
        adjustTranslation(view, info.deltaX, info.deltaY);

        float scale = view.getScaleX() * info.deltaScale;
        scale = Math.max(info.minimumScale, Math.min(info.maximumScale, scale));
        view.setScaleX(scale);
        view.setScaleY(scale);

        float rotation = adjustAngle(view.getRotation() + info.deltaAngle);
        view.setRotation(rotation);
    }

    private void adjustTranslation(View view, float deltaX, float deltaY) {
        float[] deltaVector = {deltaX, deltaY};
        view.getMatrix().mapVectors(deltaVector);
        view.setTranslationX(view.getTranslationX() + deltaVector[0]);
        view.setTranslationY(view.getTranslationY() + deltaVector[1]);
    }

    private void computeRenderOffset(View view, float pivotX, float pivotY) {
        if (view.getPivotX() == pivotX && view.getPivotY() == pivotY) {
            return;
        }

        float[] prevPoint = {0.0f, 0.0f};
        view.getMatrix().mapPoints(prevPoint);

        view.setPivotX(pivotX);
        view.setPivotY(pivotY);

        float[] currPoint = {0.0f, 0.0f};
        view.getMatrix().mapPoints(currPoint);

        float offsetX = currPoint[0] - prevPoint[0];
        float offsetY = currPoint[1] - prevPoint[1];

        view.setTranslationX(view.getTranslationX() - offsetX);
        view.setTranslationY(view.getTranslationY() - offsetY);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        scaleGestureDetector.onTouchEvent(view, event);
        gestureListener.onTouchEvent(event);

        if (!isTranslateEnabled) {
            return true;
        }

        int action = event.getAction();

        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (action & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                prevX = event.getX();
                prevY = event.getY();
                prevRawX = event.getRawX();
                prevRawY = event.getRawY();
                activePointerId = event.getPointerId(0);
                view.bringToFront();
                firePhotoEditorSDKListener(view, EventType.ON_DOWN);
                mIsInViewBounds = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerIndexMove = event.findPointerIndex(activePointerId);
                if (pointerIndexMove != -1) {
                    float currX = event.getX(pointerIndexMove);
                    float currY = event.getY(pointerIndexMove);
                    if (!scaleGestureDetector.isInProgress()) {
                        adjustTranslation(view, currX - prevX, currY - prevY);
                    }
                    boolean checkViewInBound = isViewInBounds(deleteView, x, y, DIFF_IN_BOUND_AREA);
                    long duration = event.getEventTime() - event.getDownTime();
                    if (!(duration < CLICK_THRESHOLD_DURATION && isSingleTapEvent(prevRawX, x, prevRawY, y))) {
                        firePhotoEditorSDKListener(view, EventType.ON_MOVE);
                        if (!mIsInViewBounds) {
                            if (deleteView != null && checkViewInBound) {
                                mIsInViewBounds = true;
                                Utility.vibratorNotify(mContext, 50);
                                deleteView.setScaleX(1.2f);
                                deleteView.setScaleY(1.2f);
                            } else {
                                mIsInViewBounds = false;
                                deleteView.setScaleX(1f);
                                deleteView.setScaleY(1f);
                            }
                        } else {
                            if (deleteView != null && !checkViewInBound) {
                                mIsInViewBounds = false;
                                deleteView.setScaleX(1f);
                                deleteView.setScaleY(1f);
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                activePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_UP:
                activePointerId = INVALID_POINTER_ID;
                if (deleteView != null && isViewInBounds(deleteView, x, y, DIFF_IN_BOUND_AREA)) {
                    if (onMultiTouchListener != null)
                        onMultiTouchListener.onRemoveViewListener(view);
                } else if (!isViewInBounds(photoEditImageView, x, y)) {
                    view.animate().translationY(0).translationY(0);
                }
                mIsInViewBounds = false;
                deleteView.setScaleX(1f);
                deleteView.setScaleY(1f);
                long duration = event.getEventTime() - event.getDownTime();
                if (!(duration < CLICK_THRESHOLD_DURATION && isSingleTapEvent(prevRawX, x, prevRawY, y))) {
                    firePhotoEditorSDKListener(view, EventType.ON_UP);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                int pointerIndexPointerUp = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                int pointerId = event.getPointerId(pointerIndexPointerUp);
                if (pointerId == activePointerId) {
                    int newPointerIndex = pointerIndexPointerUp == 0 ? 1 : 0;
                    prevX = event.getX(newPointerIndex);
                    prevY = event.getY(newPointerIndex);
                    activePointerId = event.getPointerId(newPointerIndex);
                }
                break;
        }
        return true;
    }

    enum EventType {
        ON_DOWN,
        ON_MOVE,
        ON_UP
    };

    private boolean isSingleTapEvent(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return (CLICK_THRESHOLD_DISTANCE > differenceX) || (CLICK_THRESHOLD_DISTANCE > differenceY);
    }

    private void firePhotoEditorSDKListener(View view, EventType eventType) {
        if (view instanceof TextView) {
            if (onMultiTouchListener != null) {
                notifyWhenEventChangeListener(view, eventType);
            } else {
                notifyWhenEventChangeListener(view, eventType);
            }
        } else {
            notifyWhenEventChangeListener(view, eventType);
        }
    }

    private void notifyWhenEventChangeListener(View view, EventType eventType) {
        if (onPhotoEditorListener != null) {
            switch (eventType) {
                case ON_DOWN:
                    onPhotoEditorListener.onEventDownChangeListener(view);
                    break;
                case ON_MOVE:
                    onPhotoEditorListener.onEventMoveChangeListener(view);
                    break;
                case ON_UP:
                    onPhotoEditorListener.onEventUpChangeListener(view);
                    break;
            }
        }
    }

    private boolean isViewInBounds(View view, int x, int y) {
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    private boolean isViewInBounds(View view, int x, int y, int diff) {
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return checkInAreaBounds(outRect, x, y, Utility.getDp(mContext, diff));
    }

    private boolean checkInAreaBounds(Rect outRect, int x, int y, int diff) {
        return outRect.left < outRect.right && outRect.top < outRect.bottom  // check for empty first
                && (x >= outRect.left - diff) && (x <= outRect.right + diff)
                && (y >= outRect.top - diff) && (y <= outRect.bottom + diff);
    }

    private void setOnMultiTouchListener(OnMultiTouchListener onMultiTouchListener) {
        this.onMultiTouchListener = onMultiTouchListener;
    }

    public void setOnGestureControl(OnGestureControl onGestureControl) {
        this.onGestureControl = onGestureControl;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (onGestureControl != null) {
                onGestureControl.onClick();
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (onGestureControl != null) {
                onGestureControl.onDown();
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            if (onGestureControl != null) {
                onGestureControl.onLongClick();
            }
        }
    }
}