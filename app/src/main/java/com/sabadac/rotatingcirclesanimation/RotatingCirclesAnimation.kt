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
import kotlin.reflect.KProperty0

class RotatingCirclesAnimation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val duration = 1200L
    private val delay = 300L
    private val radius = 20
    private val distance = 10
    private val numberOfCircles = 5
    private val canvasSide = numberOfCircles * dpToPx(radius * 2 + distance, context)
    private val shadowAlpha = 50
    private val shadowX = 6f
    private val shadowY = 20f
    private val shadowRadius = 9.5f
    private val leftToRightMovingCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val firstRotatingCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val secondRotatingCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thirdRotatingCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fourthRotatingCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

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
        leftToRightMovingCirclePaint.color = ContextCompat.getColor(context, R.color.firstCircleColor)
        leftToRightMovingCirclePaint.setShadowLayer(
            shadowRadius,
            shadowX,
            shadowY,
            Color.argb(
                shadowAlpha,
                Color.red(leftToRightMovingCirclePaint.color),
                Color.green(leftToRightMovingCirclePaint.color),
                Color.blue(leftToRightMovingCirclePaint.color)
            )
        )

        firstRotatingCirclePaint.color = ContextCompat.getColor(context, R.color.secondCircleColor)
        secondRotatingCirclePaint.color = ContextCompat.getColor(context, R.color.thirdCircleColor)
        thirdRotatingCirclePaint.color = ContextCompat.getColor(context, R.color.fourthCircleColor)
        fourthRotatingCirclePaint.color = ContextCompat.getColor(context, R.color.fifthCircleColor)

        playAnimations()
    }

    private fun playAnimations() {
        val leftToRightAnimation = leftToRightAnimation(-1)
        val rightToLeftAnimation = leftToRightAnimation(1)
        val animatorSet = AnimatorSet()
        val firstRotatingCircle =
            rotatingCircleAnimator(-1, ::firstRotatincCircleAngle, ::firstRotatingCircleY, ::firstRotatingCirclePaint)
        firstRotatingCircle.startDelay = delay
        val secondRotatingCircle = rotatingCircleAnimator(
            -1,
            ::secondRotatincCircleAngle,
            ::secondRotatingCircleY,
            ::secondRotatingCirclePaint
        )
        val thirdRotatingCircle =
            rotatingCircleAnimator(-1, ::thirdRotatincCircleAngle, ::thirdRotatingCircleY, ::thirdRotatingCirclePaint)
        val fourthRotatingCircle = rotatingCircleAnimator(
            -1,
            ::fourthRotatincCircleAngle,
            ::fourthRotatingCircleY,
            ::fourthRotatingCirclePaint
        )
        fourthRotatingCircle.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                rightToLeftAnimation.start()
            }
        })
        val fourthRotatingCircleReverse =
            rotatingCircleAnimator(1, ::fourthRotatincCircleAngle, ::fourthRotatingCircleY, ::fourthRotatingCirclePaint)
        fourthRotatingCircleReverse.startDelay = delay
        val thirdRotatingCircleReverse =
            rotatingCircleAnimator(1, ::thirdRotatincCircleAngle, ::thirdRotatingCircleY, ::thirdRotatingCirclePaint)
        val secondRotatingCircleReverse =
            rotatingCircleAnimator(1, ::secondRotatincCircleAngle, ::secondRotatingCircleY, ::secondRotatingCirclePaint)
        val firstRotatingCircleReverse =
            rotatingCircleAnimator(1, ::firstRotatincCircleAngle, ::firstRotatingCircleY, ::firstRotatingCirclePaint)
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

    private fun rotatingCircleAnimator(
        factor: Int,
        angleProperty0: KMutableProperty0<Float>,
        yProperty0: KMutableProperty0<Float>,
        paint: KProperty0<Paint>
    ): ValueAnimator {
        val angleProperty = "angle"
        val yProperty = "y"
        val anglePropertyHolder =
            PropertyValuesHolder.ofFloat(angleProperty, if (factor == 1) 180f else 0f, if (factor == 1) 0f else -180f)
        val yPropertyHolder =
            PropertyValuesHolder.ofFloat(yProperty, 0f, factor * dpToPx(radius + distance, context), 0f)
        val valueAnimator = ValueAnimator()
        valueAnimator.setValues(anglePropertyHolder, yPropertyHolder)
        valueAnimator.duration = duration / 4
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                val color = paint.get().color
                paint.get().setShadowLayer(
                    shadowRadius,
                    factor * shadowX,
                    factor * shadowY,
                    Color.argb(shadowAlpha, Color.red(color), Color.green(color), Color.blue(color))
                )
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

        bitmapCanvas.drawCircle(
            bitmap.width / 2f + currentPosition,
            bitmap.height / 2f,
            dpToPx(radius, context),
            leftToRightMovingCirclePaint
        )
        bitmapCanvas.restore()

        bitmapCanvas.save()
        bitmapCanvas.rotate(
            firstRotatincCircleAngle,
            (canvasSide + dpToPx(distance, context)) / 2f - 4 * dpToPx(radius, context),
            canvasSide / 2f + firstRotatingCircleY
        )
        bitmapCanvas.drawCircle(
            canvasSide / 2f + -2 * dpToPx(radius, context) - dpToPx(distance, context),
            canvasSide / 2f + 0f,
            dpToPx(radius, context),
            firstRotatingCirclePaint
        )
        bitmapCanvas.restore()

        bitmapCanvas.save()
        bitmapCanvas.rotate(
            secondRotatincCircleAngle,
            (canvasSide - dpToPx(distance, context)) / 2f - dpToPx(radius, context),
            canvasSide / 2f + secondRotatingCircleY
        )
        bitmapCanvas.drawCircle(canvasSide / 2f, canvasSide / 2f, dpToPx(radius, context), secondRotatingCirclePaint)
        bitmapCanvas.restore()

        bitmapCanvas.save()
        bitmapCanvas.rotate(
            thirdRotatincCircleAngle,
            (canvasSide + dpToPx(distance, context)) / 2f + dpToPx(radius, context),
            canvasSide / 2f + thirdRotatingCircleY
        )
        bitmapCanvas.drawCircle(
            canvasSide / 2f + 2 * dpToPx(radius, context) + dpToPx(distance, context),
            canvasSide / 2f,
            dpToPx(radius, context),
            thirdRotatingCirclePaint
        )
        bitmapCanvas.restore()

        bitmapCanvas.save()
        bitmapCanvas.rotate(
            fourthRotatincCircleAngle,
            (canvasSide - dpToPx(distance, context)) / 2f + 4 * dpToPx(radius, context),
            canvasSide / 2f + fourthRotatingCircleY
        )
        bitmapCanvas.drawCircle(
            canvasSide / 2f + 4 * dpToPx(radius, context) + 2 * dpToPx(distance, context),
            canvasSide / 2f,
            dpToPx(radius, context),
            fourthRotatingCirclePaint
        )
        bitmapCanvas.restore()

        canvas?.drawBitmap(bitmap, (width - bitmap.width) / 2f, (height - bitmap.height) / 2f, paint)
    }

    private fun dpToPx(dp: Int, context: Context): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics)

}