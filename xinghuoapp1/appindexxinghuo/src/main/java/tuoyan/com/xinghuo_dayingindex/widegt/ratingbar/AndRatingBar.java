package tuoyan.com.xinghuo_dayingindex.widegt.ratingbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RatingBar;

import tuoyan.com.xinghuo_dayingindex.R;

/**
 * Custom RatingBar
 *
 * @author shiju.wang
 * @date 2020-01-09
 */

@SuppressLint("AppCompatCustomView")
public class AndRatingBar extends RatingBar implements RatingBar.OnRatingBarChangeListener {

    /**
     * color of rating star
     */
    private ColorStateList mStarColor;

    /**
     * color of secondary rating star
     */
    private ColorStateList mSubStarColor;

    /**
     * background color of all star
     */
    private ColorStateList mBgColor;

    /**
     * customize star drawable
     */
    private int mStarDrawable;

    /**
     * customize background drawable
     */
    private int mBgDrawable;

    /**
     * if keep the origin color of star drawable
     */
    private boolean mKeepOriginColor;

    /**
     * the scale factor of ratingbar that can change the spacing of star
     */
    private float scaleFactor;

    /**
     * additional the spacing of the star
     */
    private float starSpacing;

    /**
     * right to left
     */
    private boolean right2Left;

    private StarDrawable mDrawable;

    /**
     * event listener
     */
    private OnRatingChangeListener mOnRatingChangeListener;

    private float mTempRating;

    public AndRatingBar(Context context) {
        this(context, null);
    }

    public AndRatingBar(Context context, AttributeSet attrs) {
        // notice:can't use this(context, attrs, 0); because ratingbar has it's own defStyleAttr    com.android.internal.R.attr.ratingBarStyle
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AndRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AndRatingBar, defStyleAttr, 0);
        right2Left = typedArray.getBoolean(R.styleable.AndRatingBar_right2Left, false);

        if (typedArray.hasValue(R.styleable.AndRatingBar_starColor)) {
            if (right2Left) {
                mBgColor = typedArray.getColorStateList(
                        R.styleable.AndRatingBar_starColor);
            } else {
                mStarColor = typedArray.getColorStateList(
                        R.styleable.AndRatingBar_starColor);
            }
        }

        if (typedArray.hasValue(R.styleable.AndRatingBar_subStarColor)) {
            if (!right2Left) {
                mSubStarColor = typedArray.getColorStateList(
                        R.styleable.AndRatingBar_subStarColor);
            }
        }

        if (typedArray.hasValue(R.styleable.AndRatingBar_bgColor)) {
            if (right2Left) {
                mStarColor = typedArray.getColorStateList(
                        R.styleable.AndRatingBar_bgColor);
            } else {
                mBgColor = typedArray.getColorStateList(
                        R.styleable.AndRatingBar_bgColor);
            }
        }

        mKeepOriginColor = typedArray.getBoolean(R.styleable.AndRatingBar_keepOriginColor, false);
        scaleFactor = typedArray.getFloat(R.styleable.AndRatingBar_scaleFactor, 1);
        starSpacing = typedArray.getDimension(R.styleable.AndRatingBar_starSpacing, 0);

        // get customize drawable
        mStarDrawable = typedArray.getResourceId(R.styleable.AndRatingBar_starDrawable, R.drawable.ic_appraise_check);
        if (typedArray.hasValue(R.styleable.AndRatingBar_bgDrawable)) {
            mBgDrawable = typedArray.getResourceId(R.styleable.AndRatingBar_bgDrawable, R.drawable.ic_appraise_check);
        } else {
            mBgDrawable = mStarDrawable;
        }

        typedArray.recycle();

        RattingAttr starAttr = new RattingAttr(context, getNumStars(), mBgDrawable, mStarDrawable, mBgColor, mSubStarColor, mStarColor, mKeepOriginColor);
        mDrawable = new StarDrawable(starAttr);
        setProgressDrawable(mDrawable);
    }

    @Override
    public void setRating(float rating) {
        super.setRating(rating);
        if (right2Left) {
            super.setRating(getNumStars() - getRating());
        }
    }

    @Override
    public void setNumStars(int numStars) {
        super.setNumStars(numStars);
        if (mDrawable != null) {
            mDrawable.setStarCount(numStars);
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getMeasuredHeight();
        int width = Math.round(height * mDrawable.getTileRatio() * getNumStars() * scaleFactor) + (int) ((getNumStars() - 1) * starSpacing);
        setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0), height);
    }

    /**
     * A callback that notifies clients when the rating has been changed. This
     * includes changes that were initiated by the user through a touch gesture
     * or arrow key/trackball as well as changes that were initiated
     * programmatically.
     */
    public interface OnRatingChangeListener {

        /**
         * Notification that the rating has changed. Clients can use the
         * fromUser parameter to distinguish user-initiated changes from those
         * that occurred programmatically. This will not be called continuously
         * while the user is dragging, only when the user finalizes a rating by
         * lifting the touch.
         *
         * @param ratingBar The RatingBar whose rating has changed.
         * @param rating    The current rating. This will be in the range
         *                  0..numStars.
         * @param fromUser  True if the rating change was initiated by a user's
         *                  touch gesture or arrow key/horizontal trackbell movement.
         */
        void onRatingChanged(AndRatingBar ratingBar, float rating, boolean fromUser);
    }


    /**
     * Sets the listener to be called when the rating changes.
     *
     * @param listener The listener.
     */
    public void setOnRatingChangeListener(OnRatingChangeListener listener) {
        mOnRatingChangeListener = listener;
        setOnRatingBarChangeListener(this);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (mOnRatingChangeListener != null && rating != mTempRating) {
            if (right2Left) {
                mOnRatingChangeListener.onRatingChanged(this, getNumStars() - rating, fromUser);
            } else {
                mOnRatingChangeListener.onRatingChanged(this, rating, fromUser);
            }
        }
        mTempRating = rating;
    }

    /**
     * set the scale factor of the ratingbar
     *
     * @param scaleFactor
     */
    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
        requestLayout();
    }

    /**
     * set the spacing of the star
     *
     * @param starSpacing
     */
    public void setStarSpacing(float starSpacing) {
        this.starSpacing = starSpacing;
        requestLayout();
    }
}
