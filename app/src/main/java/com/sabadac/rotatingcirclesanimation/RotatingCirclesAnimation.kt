package com.sabadac.rotatingcirclesanimation

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

class RotatingCirclesAnimation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val radius = 19
    private val distance = 9
    private val numberOfCircles = 5
    private val canvasSide = numberOfCircles * dpToPx(radius * 2 + distance, context)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val transparentPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val bitmap = Bitmap.createBitmap(canvasSide.toInt(), canvasSide.toInt(), Bitmap.Config.ARGB_8888)
    private val bitmapCanvas = Canvas(bitmap)

    private var currentPosition = -4 * dpToPx(radius, context) - 2 * dpToPx(distance, context)
    private var currentPositionFirstX = -2 * dpToPx(radius, context) - dpToPx(distance, context)
    private var currentPositionFirstY = 0f
    private var currentPositionAngle = 0f

    init {
        leftToRightAnimation()
    }

    private fun workInProgress() {
        val currentPositionXProperty = "currentPositionX"
        val currentPositionYProperty = "currentPositionY"
        val currentPositionAngleProperty = "currentPositionAngle"
        val currentPositionXPropertyHolder = PropertyValuesHolder.ofFloat(
            currentPositionXProperty,
            -2 * dpToPx(radius, context) - dpToPx(distance, context),
            0f
        )
        val currentPositionYPropertyHolder = PropertyValuesHolder.ofFloat(
            currentPositionYProperty,
            0f,
            -2 * dpToPx(radius, context) - dpToPx(distance, context)
        ) // - 2 * dpToPx(radius, context) - dpToPx(distance, context)
        val currentPositionAnglePropertyHolder = PropertyValuesHolder.ofFloat(currentPositionAngleProperty, 0f, -45f)
        val valueAnimator = ValueAnimator()
        valueAnimator.setValues(
            currentPositionXPropertyHolder,
            currentPositionYPropertyHolder,
            currentPositionAnglePropertyHolder
        )
        valueAnimator.duration = 3000
//        valueAnimator.repeatMode = ValueAnimator.REVERSE
//        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.startDelay = 2000
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                currentPositionFirstX = animation?.getAnimatedValue(currentPositionXProperty) as Float
                currentPositionFirstY = animation?.getAnimatedValue(currentPositionYProperty) as Float
                currentPositionAngle = animation?.getAnimatedValue(currentPositionAngleProperty) as Float
                invalidate()
            }
        })
        valueAnimator.start()
    }

    private fun leftToRightAnimation() {
        val currentPositionProperty = "currentPosition"
        val currentPositionPropertyHolder = PropertyValuesHolder.ofFloat(
            currentPositionProperty,
            -4 * dpToPx(radius, context) - 2 * dpToPx(distance, context),
            4 * dpToPx(radius, context) + 2 * dpToPx(distance, context)
        )
        val valueAnimator = ValueAnimator()
        valueAnimator.setValues(currentPositionPropertyHolder)
        valueAnimator.duration = 3000
        valueAnimator.repeatMode = ValueAnimator.REVERSE
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.startDelay = 2000
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                currentPosition = animation?.getAnimatedValue(currentPositionProperty) as Float
                invalidate()
            }
        })
        valueAnimator.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        bitmapCanvas.save()
        bitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        bitmapCanvas.drawColor(Color.WHITE)

        paint.color = ContextCompat.getColor(context, R.color.firstCircleColor)
        paint.alpha = 255
        bitmapCanvas.drawCircle(bitmap.width / 2f + currentPosition, bitmap.height / 2f, dpToPx(radius, context), paint)
        bitmapCanvas.restore()

        bitmapCanvas.save()
        paint.color = ContextCompat.getColor(context, R.color.secondCircleColor)
        paint.alpha = 155
        bitmapCanvas.rotate(currentPositionAngle)
        bitmapCanvas.drawCircle(
            canvasSide / 2f + currentPositionFirstX,
            canvasSide / 2f + currentPositionFirstY,
            dpToPx(radius, context),
            paint
        )
        bitmapCanvas.restore()

        bitmapCanvas.save()
        paint.color = ContextCompat.getColor(context, R.color.thirdCircleColor)
        paint.alpha = 155
        bitmapCanvas.drawCircle(canvasSide / 2f, canvasSide / 2f, dpToPx(radius, context), paint)
        bitmapCanvas.restore()

        bitmapCanvas.save()
        paint.color = ContextCompat.getColor(context, R.color.fourthCircleColor)
        paint.alpha = 155
        bitmapCanvas.drawCircle(
            canvasSide / 2f + 2 * dpToPx(radius, context) + dpToPx(distance, context),
            canvasSide / 2f,
            dpToPx(radius, context),
            paint
        )
        bitmapCanvas.restore()

        bitmapCanvas.save()
        paint.color = ContextCompat.getColor(context, R.color.fifthCircleColor)
        paint.alpha = 155
        bitmapCanvas.drawCircle(
            canvasSide / 2f + 4 * dpToPx(radius, context) + 2 * dpToPx(distance, context),
            canvasSide / 2f,
            dpToPx(radius, context),
            paint
        )
        bitmapCanvas.restore()

        paint.color = Color.TRANSPARENT
        canvas?.drawBitmap(bitmap, (width - bitmap.width) / 2f, (height - bitmap.height) / 2f, transparentPaint)
    }

    private fun dpToPx(dp: Int, context: Context): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics)

}