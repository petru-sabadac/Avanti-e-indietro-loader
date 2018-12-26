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
import kotlin.reflect.KMutableProperty0

class RotatingCirclesAnimation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val duration = 1200L
    private val delay = 300L
    private val radius = 19
    private val distance = 9
    private val numberOfCircles = 5
    private val canvasSide = numberOfCircles * dpToPx(radius * 2 + distance, context)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val transparentPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val bitmap = Bitmap.createBitmap(canvasSide.toInt(), canvasSide.toInt(), Bitmap.Config.ARGB_8888)
    private val bitmapCanvas = Canvas(bitmap)
    private var currentPosition = -4 * dpToPx(radius, context) - 2 * dpToPx(distance, context)

    private var firstRotatincCircleAngle = 0f
    private var firstRotatingCircleY = 0f

    private var secondRotatincCircleAngle = 0f
    private var secondRotatingCircleY = 0f

    private var thirdRotatincCircleAngle = 0f
    private var thirdRotatingCircleY = 0f

    private var fourthRotatincCircleAngle = 0f
    private var fourthRotatingCircleY = 0f

    init {

        val leftToRightAnimation = leftToRightAnimation(-1)
        val rightToLeftAnimation = leftToRightAnimation(1)
        val animatorSet = AnimatorSet()
        val firstRotatingCircle = rotatingCircleAnimator(-1, ::firstRotatincCircleAngle, ::firstRotatingCircleY)
        firstRotatingCircle.startDelay = delay
        val secondRotatingCircle = rotatingCircleAnimator(-1, ::secondRotatincCircleAngle, ::secondRotatingCircleY)
        val thirdRotatingCircle = rotatingCircleAnimator(-1, ::thirdRotatincCircleAngle, ::thirdRotatingCircleY)
        val fourthRotatingCircle = rotatingCircleAnimator(-1, ::fourthRotatincCircleAngle, ::fourthRotatingCircleY)
        fourthRotatingCircle.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                rightToLeftAnimation.start()
            }
        })
        val fourthRotatingCircleReverse =
            rotatingCircleAnimator(1, ::fourthRotatincCircleAngle, ::fourthRotatingCircleY)
        fourthRotatingCircleReverse.startDelay = delay
        val thirdRotatingCircleReverse = rotatingCircleAnimator(1, ::thirdRotatincCircleAngle, ::thirdRotatingCircleY)
        val secondRotatingCircleReverse =
            rotatingCircleAnimator(1, ::secondRotatincCircleAngle, ::secondRotatingCircleY)
        val firstRotatingCircleReverse = rotatingCircleAnimator(1, ::firstRotatincCircleAngle, ::firstRotatingCircleY)
        firstRotatingCircleReverse.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                animatorSet.start()
                leftToRightAnimation.start()
            }
        })

        animatorSet.playSequentially(
            firstRotatingCircle,
            secondRotatingCircle,
            thirdRotatingCircle,
            fourthRotatingCircle,
            fourthRotatingCircleReverse,
            thirdRotatingCircleReverse,
            secondRotatingCircleReverse,
            firstRotatingCircleReverse
        )

        animatorSet.start()
        leftToRightAnimation.start()

    }

    fun rotatingCircleAnimator(
        factor: Int,
        angleProperty0: KMutableProperty0<Float>,
        yProperty0: KMutableProperty0<Float>
    ): ValueAnimator {
        val angleProperty = "angle"
        val yProperty = "y"
        val anglePropertyHolder =
            PropertyValuesHolder.ofFloat(angleProperty, if (factor == 1) 180f else 0f, if (factor == 1) 0f else -180f)
        val yPropertyHolder = PropertyValuesHolder.ofFloat(yProperty, 0f, factor * dpToPx(radius, context), 0f)
        val valueAnimator = ValueAnimator()
        valueAnimator.setValues(anglePropertyHolder, yPropertyHolder)
        valueAnimator.duration = duration / 4
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
            }
        })
        valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                angleProperty0.set(animation?.getAnimatedValue(angleProperty) as Float)
                yProperty0.set(animation?.getAnimatedValue(yProperty) as Float)
                invalidate()
            }
        })
        return valueAnimator

    }

    private fun leftToRightAnimation(factor: Int): ValueAnimator {
        val currentPositionProperty = "currentPosition"
        val currentPositionPropertyHolder = PropertyValuesHolder.ofFloat(
            currentPositionProperty,
            factor * (4 * dpToPx(radius, context) + 2 * dpToPx(distance, context)),
            -factor * (4 * dpToPx(radius, context) + 2 * dpToPx(distance, context))
        )
        val valueAnimator = ValueAnimator()
        valueAnimator.setValues(currentPositionPropertyHolder)
        valueAnimator.duration = duration
        valueAnimator.startDelay = delay
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                currentPosition = animation?.getAnimatedValue(currentPositionProperty) as Float
                invalidate()
            }
        })
        return valueAnimator
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        bitmapCanvas.save()
        bitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        bitmapCanvas.drawColor(Color.WHITE)

        paint.color = ContextCompat.getColor(context, R.color.firstCircleColor)
        paint.alpha = 255
        bitmapCanvas.drawCircle(
            bitmap.width / 2f + currentPosition,
            bitmap.height / 2f,
            dpToPx(radius, context),
            paint
        )
        bitmapCanvas.restore()

        bitmapCanvas.save()
        paint.color = ContextCompat.getColor(context, R.color.secondCircleColor)
        paint.alpha = 255
        bitmapCanvas.rotate(
            firstRotatincCircleAngle,
            (canvasSide + dpToPx(distance, context)) / 2f - 4 * dpToPx(radius, context),
            canvasSide / 2f + firstRotatingCircleY
        )
        bitmapCanvas.drawCircle(
            canvasSide / 2f + -2 * dpToPx(radius, context) - dpToPx(distance, context),
            canvasSide / 2f + 0f,
            dpToPx(radius, context),
            paint
        )
        bitmapCanvas.restore()

        bitmapCanvas.save()
        paint.color = ContextCompat.getColor(context, R.color.thirdCircleColor)
        paint.alpha = 255
        bitmapCanvas.rotate(
            secondRotatincCircleAngle,
            (canvasSide - dpToPx(distance, context)) / 2f - dpToPx(radius, context),
            canvasSide / 2f + secondRotatingCircleY
        )
        bitmapCanvas.drawCircle(canvasSide / 2f, canvasSide / 2f, dpToPx(radius, context), paint)
        bitmapCanvas.restore()

        bitmapCanvas.save()
        paint.color = ContextCompat.getColor(context, R.color.fourthCircleColor)
        paint.alpha = 255
        bitmapCanvas.rotate(
            thirdRotatincCircleAngle,
            (canvasSide + dpToPx(distance, context)) / 2f + dpToPx(radius, context),
            canvasSide / 2f + thirdRotatingCircleY
        )
        bitmapCanvas.drawCircle(
            canvasSide / 2f + 2 * dpToPx(radius, context) + dpToPx(distance, context),
            canvasSide / 2f,
            dpToPx(radius, context),
            paint
        )
        bitmapCanvas.restore()

        bitmapCanvas.save()
        paint.color = ContextCompat.getColor(context, R.color.fifthCircleColor)
        paint.alpha = 255
        bitmapCanvas.rotate(
            fourthRotatincCircleAngle,
            (canvasSide - dpToPx(distance, context)) / 2f + 4 * dpToPx(radius, context),
            canvasSide / 2f + fourthRotatingCircleY
        )
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