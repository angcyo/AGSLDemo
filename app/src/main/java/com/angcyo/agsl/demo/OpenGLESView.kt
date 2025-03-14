package com.angcyo.agsl.demo

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.opengles.GL10

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @date 2025/03/13
 *
 * https://developer.android.com/develop/ui/views/graphics/opengl/about-opengl?hl=zh-cn
 *
 * https://developer.android.com/develop/ui/views/graphics/opengl/environment?hl=zh-cn
 *
 */
class OpenGLESView(context: Context, attr: AttributeSet?) : GLSurfaceView(context, attr) {

    val renderer = MyRenderer()

    init {
        setEGLContextFactory(ContextFactory())
        //setEGLContextClientVersion(glVersion.toInt())
        setRenderer(renderer)

        //Android Extension Pack (AEP)
        //https://developer.android.com/develop/ui/views/graphics/opengl/about-opengl?hl=zh-cn#aep
        val packageManager = context.packageManager
        val deviceSupportsAEP: Boolean =
            packageManager.hasSystemFeature(PackageManager.FEATURE_OPENGLES_EXTENSION_PACK)
        Log.i(TAG, "deviceSupportsAEP: $deviceSupportsAEP")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    private val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f
    private var previousX: Float = 0f
    private var previousY: Float = 0f

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {

                var dx: Float = x - previousX
                var dy: Float = y - previousY

                // reverse direction of rotation above the mid-line
                if (y > height / 2) {
                    dx *= -1
                }

                // reverse direction of rotation to left of the mid-line
                if (x < width / 2) {
                    dy *= -1
                }

                renderer.angle += (dx + dy) * TOUCH_SCALE_FACTOR
                requestRender()
            }
        }

        previousX = x
        previousY = y
        return true
    }
}

class OpenGLESView2(context: Context, attr: AttributeSet?) : GLSurfaceView(context, attr) {

    init {
        //setEGLContextFactory(ContextFactory())
        //setEGLContextClientVersion(glVersion.toInt())
        setRenderer(MyRenderer2())

        //Android Extension Pack (AEP)
        //https://developer.android.com/develop/ui/views/graphics/opengl/about-opengl?hl=zh-cn#aep
        val packageManager = context.packageManager
        val deviceSupportsAEP: Boolean =
            packageManager.hasSystemFeature(PackageManager.FEATURE_OPENGLES_EXTENSION_PACK)
        Log.i(TAG, "deviceSupportsAEP: $deviceSupportsAEP")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}

//--

class MyRenderer : GLSurfaceView.Renderer {

    @Volatile
    var angle: Float = 0f

    private lateinit var triangle: Triangle

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        triangle = Triangle()
        //
        // Create a minimum supported OpenGL ES context, then check:
        gl?.glGetString(GL10.GL_VERSION).also {
            //OpenGL ES 3.2 v1.r50p0-00eac0.23a3e850fb3835a51f4357e034c00a3d
            Log.w(TAG, "Version: $it")
        }
        gl?.glGetString(GL10.GL_EXTENSIONS).also {
            //GL_EXT_debug_marker GL_ARM_rgba8 GL_ARM_mali_shader_binary GL_ARM_debug_assert GL_OES_depth24 GL_OES_depth_texture GL_OES_depth_texture_cube_map GL_OES_packed_depth_stencil GL_OES_rgb8_rgba8 GL_EXT_read_format_bgra GL_OES_compressed_paletted_texture GL_OES_compressed_ETC1_RGB8_texture GL_OES_standard_derivatives GL_OES_EGL_image GL_OES_EGL_image_external GL_OES_EGL_image_external_essl3 GL_OES_EGL_sync GL_OES_texture_npot GL_OES_vertex_half_float GL_OES_required_internalformat GL_OES_vertex_array_object GL_OES_mapbuffer GL_EXT_texture_format_BGRA8888 GL_EXT_texture_rg GL_EXT_texture_type_2_10_10_10_REV GL_OES_fbo_render_mipmap GL_OES_element_index_uint GL_EXT_shadow_samplers GL_OES_texture_compression_astc GL_KHR_texture_compression_astc_ldr GL_KHR_texture_compression_astc_hdr GL_KHR_texture_compression_astc_sliced_3d GL_EXT_texture_compression_astc_decode_mode GL_EXT_texture_compression_astc_decode_mode_rgb9e5 GL_KHR_debug GL_EXT_occlusion_query_boolean GL_EXT_disjoint_timer_query GL_EXT_blend_minmax GL_EXT_discard_framebuffer GL_OES_get_program_binary GL_OES_texture_3D GL_EXT_texture_storage GL_EXT_multisampled_render_to_texture GL_EXT_multisampled_render_to_texture2 GL_OES_surfaceless_context GL_OES_texture_stencil8 GL_EXT_shader_pixel_local_storage GL_ARM_shader_framebuffer_fetch GL_ARM_shader_framebuffer_fetch_depth_stencil GL_ARM_mali_program_binary GL_EXT_sRGB GL_EXT_sRGB_write_control GL_EXT_texture_sRGB_decode GL_EXT_texture_sRGB_R8 GL_EXT_texture_sRGB_RG8 GL_KHR_blend_equation_advanced GL_KHR_blend_equation_advanced_coherent GL_OES_texture_storage_multisample_2d_array GL_OES_shader_image_atomic GL_EXT_robustness GL_EXT_draw_buffers_indexed GL_OES_draw_buffers_indexed GL_EXT_texture_border_clamp GL_OES_texture_border_clamp GL_EXT_texture_cube_map_array GL_OES_texture_cube_map_array GL_OES_sample_variables GL_OES_sample_shading GL_OES_shader_multisample_interpolation GL_EXT_shader_io_blocks GL_OES_shader_io_blocks GL_EXT_tessellation_shader GL_OES_tessellation_shader GL_EXT_primitive_bounding_box GL_OES_primitive_bounding_box GL_EXT_geometry_shader GL_OES_geometry_shader GL_ANDROID_extension_pack_es31a GL_EXT_gpu_shader5 GL_OES_gpu_shader5 GL_EXT_texture_buffer GL_OES_texture_buffer GL_EXT_copy_image GL_OES_copy_image GL_EXT_shader_non_constant_global_initializers GL_EXT_color_buffer_half_float GL_EXT_unpack_subimage GL_EXT_color_buffer_float GL_EXT_float_blend GL_EXT_YUV_target GL_OVR_multiview GL_OVR_multiview2 GL_OVR_multiview_multisampled_render_to_texture GL_KHR_robustness GL_KHR_robust_buffer_access_behavior GL_EXT_draw_elements_base_vertex GL_OES_draw_elements_base_vertex GL_EXT_protected_textures GL_EXT_buffer_storage GL_EXT_external_buffer GL_EXT_EGL_image_array GL_EXT_clear_texture GL_ARM_shader_core_properties GL_EXT_texture_filter_anisotropic GL_OES_texture_float_linear GL_ARM_texture_unnormalized_coordinates GL_EXT_shader_framebuffer_fetch GL_EXT_clip_control GL_EXT_polygon_offset_clamp GL_EXT_EGL_image_storage
            Log.w(TAG, "extensions: $it")
        }
        // The version format is displayed as: "OpenGL ES <major>.<minor>"
        // followed by optional content provided by the implementation.


        // Set the background frame color
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f)
    }

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.w(TAG, "onSurfaceChanged: $width x $height")
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)

        //GLES20.glViewport(0, 0, width, height)
        //val ratio: Float = width.toFloat() / height.toFloat()
        // create a projection matrix from device screen geometry
        //Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    private val rotationMatrix = FloatArray(16)

    override fun onDrawFrame(gl: GL10?) {
        Log.w(TAG, "onDrawFrame: ${gl?.glGetString(GL10.GL_VERSION)}")

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        val scratch = FloatArray(16)
        // Create a rotation transformation for the triangle
        val time = SystemClock.uptimeMillis() % 4000L
        //val angle = 0.090f * time.toInt()
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        // Draw shape
        triangle.draw(scratch)
    }

    //--
}


private class MyRenderer2 : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //
        // Create a minimum supported OpenGL ES context, then check:
        gl?.glGetString(GL10.GL_VERSION).also {
            //OpenGL ES 3.2 v1.r50p0-00eac0.23a3e850fb3835a51f4357e034c00a3d
            Log.w(TAG, "Version: $it")
        }
        gl?.glGetString(GL10.GL_EXTENSIONS).also {
            //GL_EXT_debug_marker GL_ARM_rgba8 GL_ARM_mali_shader_binary GL_ARM_debug_assert GL_OES_depth24 GL_OES_depth_texture GL_OES_depth_texture_cube_map GL_OES_packed_depth_stencil GL_OES_rgb8_rgba8 GL_EXT_read_format_bgra GL_OES_compressed_paletted_texture GL_OES_compressed_ETC1_RGB8_texture GL_OES_standard_derivatives GL_OES_EGL_image GL_OES_EGL_image_external GL_OES_EGL_image_external_essl3 GL_OES_EGL_sync GL_OES_texture_npot GL_OES_vertex_half_float GL_OES_required_internalformat GL_OES_vertex_array_object GL_OES_mapbuffer GL_EXT_texture_format_BGRA8888 GL_EXT_texture_rg GL_EXT_texture_type_2_10_10_10_REV GL_OES_fbo_render_mipmap GL_OES_element_index_uint GL_EXT_shadow_samplers GL_OES_texture_compression_astc GL_KHR_texture_compression_astc_ldr GL_KHR_texture_compression_astc_hdr GL_KHR_texture_compression_astc_sliced_3d GL_EXT_texture_compression_astc_decode_mode GL_EXT_texture_compression_astc_decode_mode_rgb9e5 GL_KHR_debug GL_EXT_occlusion_query_boolean GL_EXT_disjoint_timer_query GL_EXT_blend_minmax GL_EXT_discard_framebuffer GL_OES_get_program_binary GL_OES_texture_3D GL_EXT_texture_storage GL_EXT_multisampled_render_to_texture GL_EXT_multisampled_render_to_texture2 GL_OES_surfaceless_context GL_OES_texture_stencil8 GL_EXT_shader_pixel_local_storage GL_ARM_shader_framebuffer_fetch GL_ARM_shader_framebuffer_fetch_depth_stencil GL_ARM_mali_program_binary GL_EXT_sRGB GL_EXT_sRGB_write_control GL_EXT_texture_sRGB_decode GL_EXT_texture_sRGB_R8 GL_EXT_texture_sRGB_RG8 GL_KHR_blend_equation_advanced GL_KHR_blend_equation_advanced_coherent GL_OES_texture_storage_multisample_2d_array GL_OES_shader_image_atomic GL_EXT_robustness GL_EXT_draw_buffers_indexed GL_OES_draw_buffers_indexed GL_EXT_texture_border_clamp GL_OES_texture_border_clamp GL_EXT_texture_cube_map_array GL_OES_texture_cube_map_array GL_OES_sample_variables GL_OES_sample_shading GL_OES_shader_multisample_interpolation GL_EXT_shader_io_blocks GL_OES_shader_io_blocks GL_EXT_tessellation_shader GL_OES_tessellation_shader GL_EXT_primitive_bounding_box GL_OES_primitive_bounding_box GL_EXT_geometry_shader GL_OES_geometry_shader GL_ANDROID_extension_pack_es31a GL_EXT_gpu_shader5 GL_OES_gpu_shader5 GL_EXT_texture_buffer GL_OES_texture_buffer GL_EXT_copy_image GL_OES_copy_image GL_EXT_shader_non_constant_global_initializers GL_EXT_color_buffer_half_float GL_EXT_unpack_subimage GL_EXT_color_buffer_float GL_EXT_float_blend GL_EXT_YUV_target GL_OVR_multiview GL_OVR_multiview2 GL_OVR_multiview_multisampled_render_to_texture GL_KHR_robustness GL_KHR_robust_buffer_access_behavior GL_EXT_draw_elements_base_vertex GL_OES_draw_elements_base_vertex GL_EXT_protected_textures GL_EXT_buffer_storage GL_EXT_external_buffer GL_EXT_EGL_image_array GL_EXT_clear_texture GL_ARM_shader_core_properties GL_EXT_texture_filter_anisotropic GL_OES_texture_float_linear GL_ARM_texture_unnormalized_coordinates GL_EXT_shader_framebuffer_fetch GL_EXT_clip_control GL_EXT_polygon_offset_clamp GL_EXT_EGL_image_storage
            Log.w(TAG, "extensions: $it")
        }
        // The version format is displayed as: "OpenGL ES <major>.<minor>"
        // followed by optional content provided by the implementation.


        // Set the background frame color
        GLES20.glClearColor(.0f, 1.0f, 0.0f, 1.0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.w(TAG, "onSurfaceChanged: $width x $height")
        GLES20.glViewport(0, 0, width, height)

        //GLES20.glViewport(0, 0, width, height)
        //val ratio: Float = width.toFloat() / height.toFloat()
        // create a projection matrix from device screen geometry
        //Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.w(TAG, "onDrawFrame: ${gl?.glGetString(GL10.GL_VERSION)}")

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }
}

/**
 * https://developer.android.com/develop/ui/views/graphics/opengl/about-opengl?hl=zh-cn#version-check
 * */
private const val EGL_CONTEXT_CLIENT_VERSION = 0x3098
private const val glVersion = 3.0
private const val TAG = "angcyo"

/**
 * [android.opengl.GLSurfaceView.DefaultContextFactory]
 * */
private class ContextFactory : GLSurfaceView.EGLContextFactory {

    override fun createContext(
        egl: EGL10?,
        display: EGLDisplay?,
        eglConfig: EGLConfig?
    ): EGLContext? {
        Log.w(TAG, "creating OpenGL ES $glVersion context")
        return egl?.eglCreateContext(
            display,
            eglConfig,
            EGL10.EGL_NO_CONTEXT,
            intArrayOf(EGL_CONTEXT_CLIENT_VERSION, glVersion.toInt(), EGL10.EGL_NONE)
        ) // returns null if 3.0 is not supported
    }

    override fun destroyContext(
        egl: EGL10?,
        display: EGLDisplay?,
        context: EGLContext?
    ) {
        Log.w(TAG, "destroy OpenGL ES $glVersion context")
        egl?.eglDestroyContext(display, context)
    }
}

//--

// https://developer.android.com/develop/ui/views/graphics/opengl/shapes?hl=zh-cn#triangle
// number of coordinates per vertex in this array
const val COORDS_PER_VERTEX = 3
var triangleCoords = floatArrayOf(     // in counterclockwise order:
    0.0f, 0.622008459f, 0.0f,      // top
    -0.5f, -0.311004243f, 0.0f,    // bottom left
    0.5f, -0.311004243f, 0.0f      // bottom right
)

class Triangle {

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private var vertexBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(triangleCoords)
                // set the buffer to read the first coordinate
                position(0)
            }
        }

    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +
                // the matrix must be included as a modifier of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"

    // Use to access and set the view transformation
    private var vPMatrixHandle: Int = 0

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private var mProgram: Int

    // https://developer.android.com/develop/ui/views/graphics/opengl/draw?hl=zh-cn#draw
    init {

        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram().also {

            // add the vertex shader to program
            GLES20.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES20.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES20.glLinkProgram(it)
        }
    }

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    fun draw(mvpMatrix: FloatArray) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            // get handle to fragment shader's vColor member
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                // Set color for drawing the triangle
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }

            // get handle to shape's transformation matrix
            vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")

            // Pass the projection and view transformation to the shader
            GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

            // Draw the triangle
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
            //GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount)

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(it)
        }
    }

    fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        return GLES20.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}

//--

// https://developer.android.com/develop/ui/views/graphics/opengl/shapes?hl=zh-cn#square
// number of coordinates per vertex in this array
var squareCoords = floatArrayOf(
    -0.5f, 0.5f, 0.0f,      // top left
    -0.5f, -0.5f, 0.0f,      // bottom left
    0.5f, -0.5f, 0.0f,      // bottom right
    0.5f, 0.5f, 0.0f       // top right
)

class Square2 {

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

    // initialize vertex byte buffer for shape coordinates
    private val vertexBuffer: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(squareCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }
        }

    // initialize byte buffer for the draw list
    private val drawListBuffer: ShortBuffer =
        // (# of coordinate values * 2 bytes per short)
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }
}