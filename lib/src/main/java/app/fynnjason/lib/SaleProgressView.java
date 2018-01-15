package app.fynnjason.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;

/**
 * Created by FynnJason.
 * Des：仿淘宝 淘抢购进度条
 */

public class SaleProgressView extends View {

    //商品总数
    private int totalCount;
    //当前销售数量
    private int currentCount;
    //动画进度
    private int progressCount;
    //销售比例
    private float scale;
    //边框颜色
    private int sideColor;
    //文字颜色
    private int textColor;
    //边框粗细
    private float sideWidth;
    //边框的矩形
    private Paint sidePaint;
    //边框包裹的矩形
    private RectF bgRectF;
    private float radius;
    private int width;
    private int height;
    private PorterDuffXfermode mPorterDuffXfermode;
    private Paint srcPaint;
    private Bitmap fgSrc;
    private Bitmap bgSrc;

    private String nearOverText;
    private String overText;
    private float textSize;

    private Paint textPaint;
    private float nearOverTextWidth;
    private float overTextWidth;
    private float baseLineY;
    private Bitmap bgBitmap;
    private boolean isNeedAnim;

    public SaleProgressView(Context context) {
        super(context, null);
    }

    public SaleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SaleProgressView);
        sideColor = ta.getColor(R.styleable.SaleProgressView_sideColor, 0xffff3c32);
        textColor = ta.getColor(R.styleable.SaleProgressView_textColor, 0xffff3c32);
        sideWidth = ta.getDimension(R.styleable.SaleProgressView_sideWidth, SundryUtils.dp2px(2, context));
        overText = ta.getString(R.styleable.SaleProgressView_overText);
        nearOverText = ta.getString(R.styleable.SaleProgressView_nearOverText);
        textSize = ta.getDimension(R.styleable.SaleProgressView_textSize, SundryUtils.sp2px(16, context));
        isNeedAnim = ta.getBoolean(R.styleable.SaleProgressView_isNeedAnim, true);
        ta.recycle();
    }

    private void initPaint() {
        sidePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sidePaint.setStyle(Paint.Style.STROKE);
        sidePaint.setStrokeWidth(sideWidth);
        sidePaint.setColor(sideColor);

        srcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);

        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        if (nearOverText != null) {
            nearOverTextWidth = textPaint.measureText(nearOverText);
        }
        if (overText != null) {
            overTextWidth = textPaint.measureText(overText);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取view宽高
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        //圆角半径为高的一半
        radius = height / 2.0f;
        //留出一定的间隔，避免边框被切掉一部分
        if (bgRectF == null) {
            bgRectF = new RectF(sideWidth, sideWidth, width - sideWidth, height - sideWidth);

        }

        if (baseLineY == 0.0f) {
            Paint.FontMetricsInt fm = textPaint.getFontMetricsInt();
            baseLineY = height / 2 - (fm.descent / 2 + fm.ascent / 2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isNeedAnim) {
            progressCount = currentCount;
        }

        if (totalCount == 0) {
            scale = 0.0f;
        } else {
            scale = Float.parseFloat(new DecimalFormat("0.00").format((float) progressCount / (float) totalCount));
        }

        drawSide(canvas);
        drawBg(canvas);
        drawFg(canvas);
        drawText(canvas);

        //这里是为了演示动画方便，实际开发中进度只会增加
        if (progressCount != currentCount) {
            if (progressCount < currentCount) {
                progressCount++;
            } else {
                progressCount--;
            }
            postInvalidate();
        }
    }

    //绘制背景边框
    private void drawSide(Canvas canvas) {
        canvas.drawRoundRect(bgRectF, radius, radius, sidePaint);
    }

    //绘制背景
    private void drawBg(Canvas canvas) {
        if (bgBitmap == null) {
            bgBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        Canvas bgCanvas = new Canvas(bgBitmap);
        if (bgSrc == null) {
            bgSrc = BitmapFactory.decodeResource(getResources(), R.mipmap.bg);
        }
        bgCanvas.drawRoundRect(bgRectF, radius, radius, srcPaint);

        srcPaint.setXfermode(mPorterDuffXfermode);
        bgCanvas.drawBitmap(bgSrc, null, bgRectF, srcPaint);

        canvas.drawBitmap(bgBitmap, 0, 0, null);
        srcPaint.setXfermode(null);
    }

    //绘制进度条
    private void drawFg(Canvas canvas) {
        if (scale == 0.0f) {
            return;
        }
        Bitmap fgBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas fgCanvas = new Canvas(fgBitmap);
        if (fgSrc == null) {
            fgSrc = BitmapFactory.decodeResource(getResources(), R.mipmap.fg);
        }
        fgCanvas.drawRoundRect(
                new RectF(sideWidth, sideWidth, (width - sideWidth) * scale, height - sideWidth),
                radius, radius, srcPaint);

        srcPaint.setXfermode(mPorterDuffXfermode);
        fgCanvas.drawBitmap(fgSrc, null, bgRectF, srcPaint);

        canvas.drawBitmap(fgBitmap, 0, 0, null);
        srcPaint.setXfermode(null);
    }

    //绘制文字信息
    private void drawText(Canvas canvas) {
        String scaleText = new DecimalFormat("#%").format(scale);
        String saleText = String.format("已抢%s件", progressCount);

        float scaleTextWidth = textPaint.measureText(scaleText);

        Bitmap textBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas textCanvas = new Canvas(textBitmap);
        textPaint.setColor(textColor);

        if (scale < 0.8f) {
            textCanvas.drawText(saleText, SundryUtils.dp2px(10, getContext()), baseLineY, textPaint);
            textCanvas.drawText(scaleText, width - scaleTextWidth - SundryUtils.dp2px(10, getContext()), baseLineY, textPaint);
        } else if (scale < 1.0f) {
            if (nearOverText == null || nearOverText.equals("")) {
                textCanvas.drawText(saleText, SundryUtils.dp2px(10, getContext()), baseLineY, textPaint);
            } else {
                textCanvas.drawText(nearOverText, width / 2 - nearOverTextWidth / 2, baseLineY, textPaint);
            }
            textCanvas.drawText(scaleText, width - scaleTextWidth - SundryUtils.dp2px(10, getContext()), baseLineY, textPaint);
        } else {
            if (overText == null || overText.equals("")) {
                textCanvas.drawText(saleText, SundryUtils.dp2px(10, getContext()), baseLineY, textPaint);
            } else {
                textCanvas.drawText(overText, width / 2 - overTextWidth / 2, baseLineY, textPaint);
            }
        }

        textPaint.setXfermode(mPorterDuffXfermode);
        textPaint.setColor(Color.WHITE);
        textCanvas.drawRoundRect(
                new RectF(sideWidth, sideWidth, (width - sideWidth) * scale, height - sideWidth),
                radius, radius, textPaint);
        canvas.drawBitmap(textBitmap, 0, 0, null);
        textPaint.setXfermode(null);
    }

    public void setTotalAndCurrentCount(int totalCount, int currentCount) {
        this.totalCount = totalCount;
        if (currentCount > totalCount) {
            currentCount = totalCount;
        }
        this.currentCount = currentCount;
        postInvalidate();
    }
}
