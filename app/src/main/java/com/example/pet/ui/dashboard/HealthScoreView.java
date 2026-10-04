package com.example.pet.ui.dashboard;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.pet.R;

/**
 * Semicircular health-score gauge, drawn entirely with Canvas/Paint (no external
 * charting library). Shows a percentage in the center and a short status label
 * below it. Bind it from a ViewModel/Activity — nothing here is hardcoded:
 *
 *   healthScoreView.setScore(86);
 *   healthScoreView.setStatusText("Good");
 *
 * Both setters animate the arc smoothly from its current value to the new one.
 */
public class HealthScoreView extends View {

    private static final int DEFAULT_MAX_SCORE = 100;
    private static final int ANIM_DURATION_MS = 700;

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint percentTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint statusTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcBounds = new RectF();

    private int targetScore = 0;
    private int maxScore = DEFAULT_MAX_SCORE;
    private float displayedScore = 0f; // animates towards targetScore
    private String statusText = "";
    private float strokeWidthPx;

    @ColorInt private int gaugeStartColor;
    @ColorInt private int gaugeEndColor;
    @ColorInt private int trackColor;

    @Nullable private ValueAnimator animator;

    public HealthScoreView(Context context) {
        this(context, null);
    }

    public HealthScoreView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HealthScoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        gaugeStartColor = ContextCompat.getColor(context, R.color.ps_gauge_start);
        gaugeEndColor = ContextCompat.getColor(context, R.color.ps_gauge_end);
        trackColor = ContextCompat.getColor(context, R.color.ps_text_primary); // fallback, overridden below
        try {
            trackColor = ContextCompat.getColor(context, R.color.ps_gauge_track);
        } catch (Exception ignored) {
            // Falls back silently if ps_gauge_track hasn't been merged into the host project yet.
        }

        strokeWidthPx = getResources().getDimension(R.dimen.ps_gauge_stroke_width);
        statusText = getResources().getString(R.string.ps_health_score_status_good);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.HealthScoreView, 0, 0);
            try {
                targetScore = a.getInt(R.styleable.HealthScoreView_score, 0);
                maxScore = a.getInt(R.styleable.HealthScoreView_maxScore, DEFAULT_MAX_SCORE);
                String attrStatus = a.getString(R.styleable.HealthScoreView_statusText);
                if (attrStatus != null) {
                    statusText = attrStatus;
                }
                strokeWidthPx = a.getDimension(R.styleable.HealthScoreView_gaugeStrokeWidth, strokeWidthPx);
            } finally {
                a.recycle();
            }
        }
        displayedScore = targetScore;

        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeCap(Paint.Cap.ROUND);
        trackPaint.setStrokeWidth(strokeWidthPx);
        trackPaint.setColor(trackColor);

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(strokeWidthPx);

        percentTextPaint.setColor(ContextCompat.getColor(context, R.color.ps_text_primary));
        percentTextPaint.setTextAlign(Paint.Align.CENTER);
        percentTextPaint.setFakeBoldText(true);

        statusTextPaint.setColor(ContextCompat.getColor(context, R.color.ps_text_secondary));
        statusTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    /** Sets the score (0..maxScore) and animates the arc to it. Never hardcode this — always call it. */
    public void setScore(int score) {
        setScore(score, null);
    }

    /** Same as {@link #setScore(int)} but also updates the status label shown under the percentage. */
    public void setScore(int score, @Nullable String newStatusText) {
        int clamped = Math.max(0, Math.min(score, maxScore));
        if (newStatusText != null) {
            this.statusText = newStatusText;
        }
        animateTo(clamped);
        this.targetScore = clamped;
    }

    public void setStatusText(String text) {
        this.statusText = text;
        invalidate();
    }

    public int getScore() {
        return targetScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = Math.max(1, maxScore);
        invalidate();
    }

    private void animateTo(int newScore) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        animator = ValueAnimator.ofFloat(displayedScore, newScore);
        animator.setDuration(ANIM_DURATION_MS);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            displayedScore = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // The gauge is a semicircle: usable height is roughly half the width,
        // plus room below the arc for the percentage and status text.
        int desiredHeight = (int) (width * 0.62f);
        int height = resolveSize(desiredHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float inset = strokeWidthPx / 2f + getPaddingLeft();
        float diameter = Math.min(w - getPaddingLeft() - getPaddingRight(),
                (h - getPaddingBottom()) * 2f);
        float left = (w - diameter) / 2f + strokeWidthPx / 2f;
        float top = getPaddingTop() + strokeWidthPx / 2f;
        float right = left + diameter - strokeWidthPx;
        float bottom = top + diameter - strokeWidthPx;
        arcBounds.set(left, top, right, bottom);

        // Sweep gradient centered on the arc so it reads green -> lavender across the sweep,
        // matching the "subtle green-to-purple" requirement without a static image.
        SweepGradient gradient = new SweepGradient(
                arcBounds.centerX(), arcBounds.centerY(),
                new int[]{gaugeStartColor, gaugeEndColor, gaugeEndColor},
                new float[]{0f, 0.5f, 1f});
        progressPaint.setShader(gradient);

        percentTextPaint.setTextSize(diameter * 0.20f);
        statusTextPaint.setTextSize(diameter * 0.075f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Background track: full semicircle from 180deg to 360deg (opens downward, like a smile).
        canvas.drawArc(arcBounds, 180f, 180f, false, trackPaint);

        // Progress arc: proportion of the semicircle filled by the current animated score.
        float fraction = maxScore == 0 ? 0 : (displayedScore / maxScore);
        float sweep = 180f * fraction;
        canvas.drawArc(arcBounds, 180f, sweep, false, progressPaint);

        float centerX = arcBounds.centerX();
        float arcBottomY = arcBounds.bottom;

        int roundedScore = Math.round(displayedScore);
        String percentLabel = roundedScore + "%";
        float percentBaselineY = arcBottomY - (arcBounds.height() * 0.06f);
        canvas.drawText(percentLabel, centerX, percentBaselineY, percentTextPaint);

        if (statusText != null && !statusText.isEmpty()) {
            float statusBaselineY = percentBaselineY + statusTextPaint.getTextSize() + dp(6);
            canvas.drawText(statusText, centerX, statusBaselineY, statusTextPaint);
        }
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}