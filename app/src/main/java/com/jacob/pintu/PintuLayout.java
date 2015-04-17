package com.jacob.pintu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jacob-wj on 2015/4/17.
 */
public class PintuLayout extends RelativeLayout implements View.OnClickListener {

    /**
     *游戏的背景图片
     */
    private Bitmap mBitmap;
    /**
     * 图片切割后的子图片
     */
    private List<ImagePieces> mImagePieceList;
    /**
     * 对应每个宫格的视图
     */
    private ImageView[] mImageViews;
    /**
     * 布局的尺寸
     */
    private int mLayoutSize;
    /**
     *初始的行列
     */
    private int mColumn = 3;
    /**
     * 间隔
     */
    private int mMargin;
    /**
     * 默认间隔
     */
    private int mPadding;
    /**
     * 是否已经初始化
     */
    private boolean hasInit = false;
    /**
     * 每个宫格的大小
     */
    private int mItemWidth;
    /**
     * 交换图片时第一个点击的view
     */
    private ImageView mFirst;
    /**
     *交换图片时第2个点击的view
     */
    private ImageView mSecond;
    /**
     * 动画层
     */
    private RelativeLayout mRelativeAnim;
    /**
     * 是否还在执行动画
     */
    private boolean isAniming;
    /**
     * 游戏是否成功
     */
    private boolean isGameSuccess;
    /**
     * 默认的关卡
     */
    private int mLevel = 1;
    /**
     * 游戏信息的回调
     */
    private OnGameListener mGameListener;

    public static final int MSG_GAME_SUCCESS = 100;
    public static final int MSG_GAME_START= 101;
    public static final int MSG_GAME_OVER = 102;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_GAME_START:
                    if (mGameListener != null){
                        mGameListener.gameStart(mLevel);
                    }
                    break;
                case MSG_GAME_SUCCESS:
                    if (mGameListener != null){
                        mGameListener.gameSuccess(mLevel);
                    }
                    break;
                case MSG_GAME_OVER:

                    break;
            }
        }
    };

    public PintuLayout(Context context) {
        this(context, null);
    }

    public PintuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PintuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMargin = dpToPx(3);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pintu);
        mPadding = min(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mLayoutSize = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(mLayoutSize, mLayoutSize);

        if (!hasInit) {
            //对图片进行初始化和乱序操作
            initBitmaps();

            //初始化所有的宫格，每个宫格是一个ImageView
            initImageItems();

            hasInit = true;
        }
    }

    /**
     * 下一关卡
     */
    public void nextLevel(){
        mLevel++;
        mColumn++;
        isGameSuccess = false;
        removeAllViews();
        mRelativeAnim.removeAllViews();
        mRelativeAnim = null;
        mFirst = mSecond = null;
        isAniming = false;

        initBitmaps();
        initImageItems();
    }

    /**
     * 对图片进行初始化和乱序操作
     */
    private void initBitmaps() {
        mHandler.sendEmptyMessage(MSG_GAME_START);

        mImageViews = new ImageView[mColumn * mColumn];
        mImagePieceList = PintuBitmapUtils.getImagePiecesList(mBitmap, mColumn);
        mItemWidth = (mLayoutSize - mPadding * 2 - (mColumn - 1) * mMargin) / mColumn;

        Collections.sort(mImagePieceList, new Comparator<ImagePieces>() {
            @Override
            public int compare(ImagePieces lhs, ImagePieces rhs) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
    }

    /**
     * 初始化所有的宫格，每个宫格是一个ImageView
     */
    private void initImageItems() {

        int pieceSize = mImagePieceList.size();
        for (int i = 0; i < pieceSize; i++) {
            ImagePieces imagePieces = mImagePieceList.get(i);
            ImageView imageView = new ImageView(getContext());
            imageView.setId(i + 1);
            imageView.setTag(i + "_" + imagePieces.getIndex());
            imageView.setImageBitmap(imagePieces.getBitmap());
            imageView.setOnClickListener(this);

            RelativeLayout.LayoutParams layoutParams = new LayoutParams(mItemWidth, mItemWidth);
            //除了最上面一排之外其他都设置上边距
            if (i >= mColumn) {
                layoutParams.topMargin = mMargin;
            }

            //除了最左边一列之外其他都设置左边距
            if (i % mColumn != 0) {
                layoutParams.leftMargin = mMargin;
            }


            if (i % mColumn != 0) {
                layoutParams.addRule(RelativeLayout.RIGHT_OF, mImageViews[i - 1].getId());
            }

            if (i >= mColumn) {
                layoutParams.addRule(RelativeLayout.BELOW, mImageViews[i - mColumn].getId());
            }
            imageView.setLayoutParams(layoutParams);
            mImageViews[i] = imageView;

            addView(imageView);
        }
    }


    @Override
    public void onClick(View v) {
        if (isAniming) return;

        if (mFirst == v) {
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }

        if (mFirst == null) {
            mFirst = (ImageView) v;
            mFirst.setColorFilter(Color.parseColor("#5FFF0000"));
            return;
        }

        mSecond = (ImageView) v;

        changeBitmap();

    }

    /**
     * 对选中的两个view进行图片的交换
     */
    private void changeBitmap() {
        mFirst.setColorFilter(null);

        final Bitmap firstBitmap = getBitmapFromView(mFirst);
        final Bitmap secondBitmap = getBitmapFromView(mSecond);

        setupAnimLayout();

        ImageView imageViewFirst = createNewImageView(firstBitmap, mFirst);
        mRelativeAnim.addView(imageViewFirst);

        ImageView imageViewSecond = createNewImageView(secondBitmap, mSecond);
        mRelativeAnim.addView(imageViewSecond);

        TranslateAnimation animFirst = new TranslateAnimation(0, mSecond.getLeft() - mFirst.getLeft(),
                0, mSecond.getTop() - mFirst.getTop());
        animFirst.setDuration(300);
        animFirst.setFillAfter(true);
        imageViewFirst.setAnimation(animFirst);

        TranslateAnimation animSecond = new TranslateAnimation(0, -mSecond.getLeft() + mFirst.getLeft(),
                0, -mSecond.getTop() + mFirst.getTop());
        animSecond.setDuration(300);
        animSecond.setFillAfter(true);
        imageViewSecond.setAnimation(animSecond);

        animSecond.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFirst.setVisibility(INVISIBLE);
                mSecond.setVisibility(INVISIBLE);
                isAniming = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();

                mFirst.setImageBitmap(secondBitmap);
                mSecond.setImageBitmap(firstBitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(VISIBLE);
                mSecond.setVisibility(VISIBLE);

                mRelativeAnim.removeAllViews();

                mFirst = mSecond = null;
                isAniming = false;

                isGameSuccess = checkIfSuccess();
                if (isGameSuccess){
                    mHandler.sendEmptyMessage(MSG_GAME_SUCCESS);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    /**
     * 检查是否复原成功
     */
    private boolean checkIfSuccess() {
        int size  = mImagePieceList.size();
        boolean isSuccess = true;
        for (int i = 0; i < size; i++) {
            String tag = (String) mImageViews[i].getTag();
            int  index = Integer.parseInt(tag.split("_")[1]);
            if (index != i){
                isSuccess  = false;
                break;
            }
        }
        return  isSuccess;
    }

    private ImageView createNewImageView(Bitmap firstBitmap, ImageView imageView) {
        ImageView imageViewFirst = new ImageView(getContext());
        imageViewFirst.setImageBitmap(firstBitmap);
        LayoutParams layoutParams = new LayoutParams(mItemWidth, mItemWidth);
        layoutParams.topMargin = imageView.getTop() - mPadding;
        layoutParams.leftMargin = imageView.getLeft() - mPadding;
        imageViewFirst.setLayoutParams(layoutParams);
        return imageViewFirst;
    }

    private void setupAnimLayout() {
        if (mRelativeAnim == null) {
            mRelativeAnim = new RelativeLayout(getContext());
            addView(mRelativeAnim);
        }
    }


    private Bitmap getBitmapFromView(ImageView imageView) {
        String tag = (String) imageView.getTag();
        int firstIndex = Integer.parseInt(tag.split("_")[0]);
        Bitmap bitmap = mImagePieceList.get(firstIndex).getBitmap();
        return bitmap;
    }


    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


    private int min(int... paddings) {
        int min = paddings[0];
        for (int i = 0; i < paddings.length; i++) {
            if (paddings[i] < min) {
                min = paddings[i];
            }
        }
        return min;
    }



    public interface OnGameListener{
        void gameStart(int level);

        void gameSuccess(int level);
    }

    public void setOnGameListener(OnGameListener gameListener){
        this.mGameListener = gameListener;
    }

}
