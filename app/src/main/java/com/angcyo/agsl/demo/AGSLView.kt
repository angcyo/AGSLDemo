package com.angcyo.agsl.demo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RuntimeShader
import android.util.AttributeSet
import android.view.View

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @date 2025/03/12
 *
 * https://developer.android.com/develop/ui/views/graphics/agsl/using-agsl?hl=zh-cn
 *
 * https://developer.android.com/develop/ui/views/graphics/agsl/agsl-vs-glsl?hl=zh-cn#precision_and_types
 *
 * # 精确率和类型
 * 支持与 GLSL 兼容的精度修饰符，但 AGSL 引入了 half 和 short 类型，它们也表示中等精度。
 *
 * 矢量类型可声明为命名的 <base type><columns>。您可以使用 float2（而非 vec2）和 bool4（而非 bvec4）。
 * 矩阵类型可声明为命名 <base type><columns>x<rows>，因此 float3x3，而非 mat3。
 * AGSL 还允许使用 GLSL 样式的声明 的 mat 和 vec，并且这些类型映射到其浮点 等效项。
 */
class AGSLView(context: Context, attr: AttributeSet?) : View(context, attr) {

    private val COLOR_SHADER_SRC =
        """half4 main(float2 fragCoord) {
      return half4(0,0,1,1); //R G B A
   }"""

    private val COLOR_SHADER_SRC2 =
        """layout(color) uniform half4 iColor;
        half4 main(float2 fragCoord) {
      return iColor;
    }"""

    private val COLOR_SHADER_SRC3 =
        """uniform float2 iResolution;
        half4 main(float2 fragCoord) {
      //float2 scaled = fragCoord/iResolution.xy; //x y z w
      //float2 scaled = fragCoord/float2(iResolution[0], iResolution[1]);
      //return half4(scaled, 0, 1); 
      float scaled = fragCoord.x / iResolution.x;
      //return half4(fragCoord.x/100, 0, 0, 1); //R G B A
      return half4(scaled, 0, 0, 1); //R G B A
   }"""

    val fixedColorShader = RuntimeShader(COLOR_SHADER_SRC3)
    val paint = Paint()

    init {
        //fixedColorShader.setColorUniform("iColor", Color.LTGRAY)
        paint.style = Paint.Style.STROKE
        paint.shader = fixedColorShader
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //canvas.drawColor(Color.RED)

        //fixedColorShader.setFloatUniform("iResolution", width.toFloat() / 2 , height.toFloat() / 2)
        fixedColorShader.setFloatUniform("iResolution", width.toFloat(), height.toFloat())
        //canvas.drawPaint(paint) // fill the Canvas with the shader
        canvas.drawRect(Rect(100, 100, width / 2, 400), paint)
    }
}