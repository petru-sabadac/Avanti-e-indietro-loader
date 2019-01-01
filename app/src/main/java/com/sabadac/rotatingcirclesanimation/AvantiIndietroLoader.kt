package com.sabadac.rotatingcirclesanimation

import android.animation.*
import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator

class AvantiIndietroLoader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val radius = 20
    private val distance = 15
    private val numberOfCircles = 6
    private val canvasSide = numberOfCircles * dpToPx(radius * 2 + distance, context)
    private val shadowAlpha = 50
    private val shadowX = 6f
    private val shadowY = 20f
    private val shadowRadius = 9.5f
    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val bitmap = Bitmap.createBitmap(canvasSide.toInt(), canvasSide.toInt(), Bitmap.Config.ARGB_8888)
    private val bitmapCanvas = Canvas(bitmap)

    private val points = Array(5) { PointF() }
    private val initialPoints = Array(5) { PointF() }
    private val colors = IntArray(5)
    private val initialColors = IntArray(5)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val duration = 300L
    private val delay = 300L

    init {
        initColorsAndPoints()
        resetColorsAndPoints()
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(
            moveTo(1, false, delay),
            moveTo(2),
            moveTo(3),
            moveTo(4),
            moveTo(3, true, delay),
            moveTo(2, true),
            moveTo(1, true),
            moveTo(0, true)
        )
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                animatorSet.start()
            }
        })
        animatorSet.start()
    }

    private fun moveTo(to: Int, isBackwards: Boolean = false, delay: Long = 0L): AnimatorSet {
        val initialIndex = if (isBackwards) to + 1 else to - 1
        val initialPathIndex = if (isBackwards) to + 1 else to

        val path = Path()
        val rectF = RectF()
        rectF.left = initialPoints[initialPathIndex].x - 2 * dpToPx(radius, context) - dpToPx(distance, context)
        rectF.right = initialPoints[initialPathIndex].x
        rectF.top = initialPoints[0].y - 2 * dpToPx(radius, context) - dpToPx(distance, context)
        rectF.bottom = initialPoints[0].y + 2 * dpToPx(radius, context) + dpToPx(distance, context)
        val sweepAngle = if (isBackwards) 180f else -180f
        path.addArc(rectF, 0f, sweepAngle)

        val movingColorAnimator =
            ValueAnimator.ofObject(ArgbEvaluator(), initialColors[initialIndex], initialColors[to])
        val rotatingColor = ArgbEvaluator()
        movingColorAnimator.interpolator = LinearInterpolator()
        movingColorAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                val fraction = animation!!.animatedFraction
                colors[0] = animation?.animatedValue as Int
                points[0].x = initialPoints[initialIndex].x + fraction *
                        (initialPoints[to].x - initialPoints[initialIndex].x)
                colors[initialPathIndex] =
                        rotatingColor.evaluate(fraction, initialColors[to], initialColors[initialIndex]) as Int

                invalidate()
            }
        })

        val rotatingCircleAnimator = ValueAnimator.ofFloat(0f, 1f)
        rotatingCircleAnimator.interpolator = AccelerateDecelerateInterpolator()
        rotatingCircleAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                val fraction = if (isBackwards) 1 - animation!!.animatedFraction else animation!!.animatedFraction
                val point = FloatArray(2)
                val pathMeasure = PathMeasure(path, false)
                pathMeasure.getPosTan(pathMeasure.length * fraction, point, null)
                points[initialPathIndex].x = point[0]
                points[initialPathIndex].y = point[1]
                invalidate()
            }
        })

        val animatorSet = AnimatorSet()
        animatorSet.startDelay = delay
        animatorSet.duration = duration
        animatorSet.playTogether(movingColorAnimator, rotatingCircleAnimator)

        return animatorSet

    }

    private fun initColorsAndPoints() {
        initialColors[0] = ContextCompat.getColor(context, R.color.firstCircleColor)
        initialPoints[0].x = canvasSide / 2f - 4 * dpToPx(radius, context) - 2 * dpToPx(distance, context)
        initialPoints[0].y = canvasSide / 2f

        initialColors[1] = ContextCompat.getColor(context, R.color.secondCircleColor)
        initialPoints[1].x = canvasSide / 2f + -2 * dpToPx(radius, context) - dpToPx(distance, context)
        initialPoints[1].y = canvasSide / 2f

        initialColors[2] = ContextCompat.getColor(context, R.color.thirdCircleColor)
        initialPoints[2].x = canvasSide / 2f
        initialPoints[2].y = canvasSide / 2f

        initialColors[3] = ContextCompat.getColor(context, R.color.fourthCircleColor)
        initialPoints[3].x = canvasSide / 2f + 2 * dpToPx(radius, context) + dpToPx(distance, context)
        initialPoints[3].y = canvasSide / 2f

        initialColors[4] = ContextCompat.getColor(context, R.color.fifthCircleColor)
        initialPoints[4].x = canvasSide / 2f + 4 * dpToPx(radius, context) + 2 * dpToPx(distance, context)
        initialPoints[4].y = canvasSide / 2f
    }

    private fun resetColorsAndPoints() {
        initialPoints.forEachIndexed { index, pointF ->
            colors[index] = initialColors[index]
            points[index].set(initialPoints[index])
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        bitmapCanvas.save()
        bitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        bitmapCanvas.drawColor(Color.WHITE)
        bitmapCanvas.restore()

        points.forEachIndexed { index, pointF ->
            bitmapCanvas.save()
            paint.color = colors[index]
            paint.setShadowLayer(
                shadowRadius,
                shadowX,
                shadowY,
                Color.argb(
                    shadowAlpha,
                    Color.red(paint.color),
                    Color.green(paint.color),
                    Color.blue(paint.color)
                )
            )
            bitmapCanvas.drawCircle(
                pointF.x,
                pointF.y,
                dpToPx(radius, context),
                paint
            )
            bitmapCanvas.restore()
        }

        canvas?.drawBitmap(bitmap, (width - bitmap.width) / 2f, (height - bitmap.height) / 2f, bitmapPaint)

    }

    private fun dpToPx(dp: Int, context: Context): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics)

}