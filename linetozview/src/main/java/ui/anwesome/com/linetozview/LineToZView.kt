package ui.anwesome.com.linetozview

/**
 * Created by anweshmishra on 19/04/18.
 */

import android.content.*
import android.graphics.*
import android.view.View
import android.view.MotionEvent

class LineToZView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State (var prevScale : Float = 0f, var dir : Float = 0f, var j : Int = 0) {

        val scales : Array<Float> = arrayOf(0f, 0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += 0.1f * dir
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    prevScale = scales[j]
                    dir = 0f
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {
        fun animate(updatecb : () -> Unit) {
            if (animated) {
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class LineToZ (var i : Int, private val state : State = State()) {
        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val size : Float = Math.min(w, h)/8
            paint.color = Color.parseColor("#4CAF50")
            paint.strokeWidth = Math.min(w, h) / 55
            paint.strokeCap = Paint.Cap.ROUND
            if (state.scales.size == 3) {
                canvas.save()
                canvas.translate(w/2, h/2)
                for (i in 0..1) {
                    canvas.save()
                    canvas.translate(0f, (size) * Math.sqrt(2.0).toFloat() * (1 - 2 * i))
                    for (j in 0..1) {
                        canvas.save()
                        canvas.rotate(-45f * j * state.scales[2])
                        canvas.drawLine(0f, 0f, size * state.scales[0], 0f, paint)
                        canvas.restore()
                    }
                    canvas.restore()
                }
                canvas.restore()
            }
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }

    data class Renderer (var view : LineToZView) {

        private val lineToZ : LineToZ = LineToZ(0)

        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            lineToZ.draw(canvas, paint)
            animator.animate {
                lineToZ.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            lineToZ.startUpdating {
                animator.start()
            }
        }
    }
}