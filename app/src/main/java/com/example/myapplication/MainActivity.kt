package com.example.myapplication

import android.Manifest.permission
import android.content.pm.PackageManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.core.ArCoreApk.InstallStatus
import com.google.ar.core.Camera
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Point
import com.google.ar.core.Session
import com.google.ar.core.Track
import com.google.ar.core.TrackingState
import com.google.ar.core.examples.java.common.helpers.DisplayRotationHelper
import com.google.ar.core.examples.java.common.helpers.FullScreenHelper
import com.google.ar.core.examples.java.common.helpers.SnackbarHelper
import com.google.ar.core.examples.java.common.helpers.TapHelper
import com.google.ar.core.examples.java.common.helpers.TrackingStateHelper
import com.google.ar.core.examples.java.common.rendering.BackgroundRenderer
import com.google.ar.core.examples.java.common.rendering.ObjectRenderer
import com.google.ar.core.examples.java.common.rendering.PlaneRenderer
import com.google.ar.core.examples.java.common.rendering.PointCloudRenderer
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.PlaybackFailedException
import com.google.ar.core.exceptions.RecordingFailedException
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.util.Locale
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * This is a simple example that shows how to create an augmented reality (AR) app that demonstrates
 * recording and playback of the AR session:
 *
 *
 * - During recording, ARCore captures device camera and IMU sensor to an MP4 video file.
 *  * During plaback, ARCore replays the recorded session.
 *  * The app visualizes detected planes.
 *  * The user can tap on a detected plane to place a 3D model. These taps are simultaneously
 * recorded in a separate MP4 data track, so that the taps can be replayed during playback.
 *
 */
class MainActivity : AppCompatActivity(), GLSurfaceView.Renderer {

    private var session: Session? = null
    private val messageSnackbarHelper = SnackbarHelper()
    private var displayRotationHelper: DisplayRotationHelper? = null
    private val trackingStateHelper = TrackingStateHelper(this)
    private var tapHelper: TapHelper? = null
    private lateinit var surfaceView: GLSurfaceView

    // The Renderers are created here, and initialized when the GL surface is created.
    private val backgroundRenderer = BackgroundRenderer()
    private val virtualObject = ObjectRenderer()
    private val virtualObjectShadow = ObjectRenderer()
    private val planeRenderer = PlaneRenderer()
    private val pointCloudRenderer = PointCloudRenderer()

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private val anchorMatrix = FloatArray(16)

    // Anchors created from taps used for object placing with a given color.
    private class ColoredAnchor(val anchor: Anchor, val color: FloatArray)

    private val anchors = ArrayList<ColoredAnchor>()
    private val anchorsToBeRecorded = ArrayList<ColoredAnchor>()
    private var installRequested = false

    private val REQUIRED_PERMISSIONS_FOR_ANDROID_S_AND_BELOW = arrayOf(
        permission.CAMERA, permission.WRITE_EXTERNAL_STORAGE
    )
    private val REQUIRED_PERMISSIONS_FOR_ANDROID_T_AND_ABOVE = arrayOf(
        permission.CAMERA, permission.READ_MEDIA_VIDEO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        surfaceView = findViewById(R.id.surfaceview)
        displayRotationHelper = DisplayRotationHelper( /*context=*/this)

        // Set up touch listener.
        tapHelper = TapHelper( /*context=*/this)
        surfaceView.setOnTouchListener(tapHelper)

        // Set up renderer.
        surfaceView.setPreserveEGLContextOnPause(true)
        surfaceView.setEGLContextClientVersion(2)
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0) // Alpha used for plane blending.
        surfaceView.setRenderer(this)
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY)
        surfaceView.setWillNotDraw(false)
        installRequested = false

    }

    override fun onResume() {
        super.onResume()
        if (session == null) {
            var exception: Exception? = null
            var message: String? = null
            try {
                when (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    InstallStatus.INSTALL_REQUESTED -> {
                        installRequested = true
                        return
                    }

                    InstallStatus.INSTALLED -> {}
                }

                // If we did not yet obtain runtime permission on Android M and above, now is a good time to
                // ask the user for it.
                if (requestPermissions(permissionsForTargetSDK)) {
                    return
                }

                // Create the session.
                session = Session( /* context= */this)

            } catch (e: UnavailableArcoreNotInstalledException) {
                message = "Please install Google Play Services for AR (ARCore)"
                exception = e
            } catch (e: UnavailableUserDeclinedInstallationException) {
                message = "Please install Google Play Services for AR (ARCore)"
                exception = e
            } catch (e: UnavailableApkTooOldException) {
                message = "Please update Google Play Services for AR (ARCore)"
                exception = e
            } catch (e: UnavailableSdkTooOldException) {
                message = "Please update this app"
                exception = e
            } catch (e: UnavailableDeviceNotCompatibleException) {
                message = "This device does not support AR"
                exception = e
            } catch (e: Exception) {
                message = "Failed to create AR session"
                exception = e
            }
            if (message != null) {
                messageSnackbarHelper.showError(this, "$message $exception")
                return
            }
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            // Playback will now start if an MP4 dataset has been set.
            session!!.resume()
        } catch (e: CameraNotAvailableException) {
            messageSnackbarHelper.showError(this, "Camera not available. Try restarting the app.")
            session = null
            return
        }

        surfaceView!!.onResume()
        displayRotationHelper!!.onResume()
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)

        // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
        try {
            // Create the texture and pass it to ARCore session to be filled during update().
            backgroundRenderer.createOnGlThread( /*context=*/this)
            planeRenderer.createOnGlThread( /*context=*/this, "models/trigrid.png")
            pointCloudRenderer.createOnGlThread( /*context=*/this)
            virtualObject.createOnGlThread( /*context=*/this,
                "models/map_quality_bar.obj",
                "models/map_quality_bar.png"
            )
            virtualObject.setMaterialProperties(0.0f, 2.0f, 0.5f, 6.0f)
            virtualObjectShadow.createOnGlThread( /*context=*/
                this, "models/andy_shadow.obj", "models/andy_shadow.png"
            )
            virtualObjectShadow.setBlendMode(ObjectRenderer.BlendMode.Shadow)
            virtualObjectShadow.setMaterialProperties(1.0f, 0.0f, 0.0f, 1.0f)
        } catch (e: IOException) {
            Log.e("Test", "Failed to read an asset file", e)
        }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        displayRotationHelper!!.onSurfaceChanged(width, height)
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        // Clear screen to tell driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Do not render anything or call session methods until session is created.
        if (session == null) {
            return
        }

        // Notify ARCore session that the view size changed so that the projection matrix and
        // the video background can be properly adjusted.
        displayRotationHelper!!.updateSessionIfNeeded(session)
        try {
            session!!.setCameraTextureName(backgroundRenderer.textureId)

            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            val frame = session!!.update()
            val camera = frame.camera

            // Handle one tap per frame.
            val anchor = handleTap(frame, camera)
            if (anchor != null) {
                // If we created an anchor, then try to record it.
                anchorsToBeRecorded.add(anchor)
            }

            // If frame is ready, render camera preview image to the GL surface.
            backgroundRenderer.draw(frame)

            // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
            trackingStateHelper.updateKeepScreenOnFlag(camera.trackingState)

            // If not tracking, don't draw 3D objects, show tracking failure reason instead.
            if (camera.trackingState == TrackingState.PAUSED) {
                messageSnackbarHelper.showMessage(
                    this, TrackingStateHelper.getTrackingFailureReasonString(camera)
                )
                return
            }

            // Get projection matrix.
            val projmtx = FloatArray(16)
            camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f)

            // Get camera matrix and draw.
            val viewmtx = FloatArray(16)
            camera.getViewMatrix(viewmtx, 0)

            // Compute lighting from average intensity of the image.
            // The first three components are color scaling factors.
            // The last one is the average pixel intensity in gamma space.
            val colorCorrectionRgba = FloatArray(4)
            frame.lightEstimate.getColorCorrection(colorCorrectionRgba, 0)
            frame.acquirePointCloud().use { pointCloud ->
                pointCloudRenderer.update(pointCloud)
                pointCloudRenderer.draw(viewmtx, projmtx)
            }

            // No tracking failure at this point. If we detected any planes, then hide the
            // message UI. If not planes detected, show searching planes message.
            if (hasTrackingPlane()) {
                messageSnackbarHelper.hide(this)
            } else {
                messageSnackbarHelper.showMessage(this, "Searching for surfaces...")
            }

            // Visualize detected planes.
            planeRenderer.drawPlanes(
                session!!.getAllTrackables(Plane::class.java), camera.displayOrientedPose, projmtx
            )

            // Visualize anchors created by tapping.
            val scaleFactor = 1.0f
            for (coloredAnchor in anchors) {
                if (coloredAnchor.anchor.trackingState != TrackingState.TRACKING) {
                    continue
                }
                // Get the current pose of an Anchor in world space. The Anchor pose is updated
                // during calls to session.update() as ARCore refines its estimate of the world.
                coloredAnchor.anchor.pose.toMatrix(anchorMatrix, 0)

                // Update and draw the model and its shadow.
                virtualObject.updateModelMatrix(anchorMatrix, scaleFactor)
                virtualObjectShadow.updateModelMatrix(anchorMatrix, scaleFactor)
                virtualObject.draw(viewmtx, projmtx, colorCorrectionRgba, coloredAnchor.color)
                virtualObjectShadow.draw(viewmtx, projmtx, colorCorrectionRgba, coloredAnchor.color)
            }
        } catch (t: Throwable) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e("Test", "Exception on the OpenGL thread", t)
        }
    }

    /** Try to create an anchor if the user has tapped the screen.  */
    private fun handleTap(frame: Frame, camera: Camera): ColoredAnchor? {
        // Handle only one tap per frame, as taps are usually low frequency compared to frame rate.
        val tap = tapHelper!!.poll()
        if (tap != null && camera.trackingState == TrackingState.TRACKING) {
            for (hit in frame.hitTest(tap)) {
                // Check if any plane was hit, and if it was hit inside the plane polygon.
                val trackable = hit.trackable
                // Creates an anchor if a plane or an oriented point was hit.
                if ((trackable is Plane
                            && trackable.isPoseInPolygon(hit.hitPose) && PlaneRenderer.calculateDistanceToPlane(
                        hit.hitPose,
                        camera.pose
                    ) > 0)
                    || (trackable is Point
                            && trackable.orientationMode
                            == Point.OrientationMode.ESTIMATED_SURFACE_NORMAL)
                ) {
                    // Hits are sorted by depth. Consider only closest hit on a plane or oriented point.
                    // Cap the number of objects created. This avoids overloading both the
                    // rendering system and ARCore.
                    if (anchors.size >= 20) {
                        anchors[0].anchor.detach()
                        anchors.removeAt(0)
                    }

                    // Assign a color to the object for rendering based on the trackable type
                    // this anchor attached to.
                    val objColor: FloatArray
                    objColor = if (trackable is Point) {
                        floatArrayOf(66.0f, 133.0f, 244.0f, 255.0f) // Blue.
                    } else if (trackable is Plane) {
                        floatArrayOf(139.0f, 195.0f, 74.0f, 255.0f) // Green.
                    } else {
                        floatArrayOf(0f, 0f, 0f, 0f)
                    }
                    val anchor = ColoredAnchor(hit.createAnchor(), objColor)
                    // Adding an Anchor tells ARCore that it should track this position in
                    // space. This anchor is created on the Plane to place the 3D model
                    // in the correct position relative both to the world and to the plane.
                    anchors.add(anchor)
                    return anchor
                }
            }
        }
        return null
    }



    /** Checks if we detected at least one plane.  */
    private fun hasTrackingPlane(): Boolean {
        for (plane in session!!.getAllTrackables(
            Plane::class.java
        )) {
            if (plane.trackingState == TrackingState.TRACKING) {
                return true
            }
        }
        return false
    }

    /**
     * Requests any not (yet) granted required permissions needed for recording and playback.
     *
     *
     * Returns false if all permissions are already granted. Otherwise, requests missing
     * permissions and returns true.
     */
    private fun requestPermissions(permissions: Array<String>): Boolean {
        val permissionsNotGranted: MutableList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNotGranted.add(permission)
            }
        }
        if (permissionsNotGranted.isEmpty()) {
            return false
        }
        ActivityCompat.requestPermissions(
            this, permissionsNotGranted.toTypedArray(),0
        )
        return true
    }



    /** Helper function to log error message and show it on the screen.  */
    private fun logAndShowErrorMessage(errorMessage: String) {
        messageSnackbarHelper.showError(this, errorMessage)
    }



    private val permissionsForTargetSDK: Array<String>
        private get() {
            val targetSdkVersion = this.applicationInfo.targetSdkVersion
            val buildSdkVersion = Build.VERSION.SDK_INT
            return if (targetSdkVersion >= VERSION_CODES.TIRAMISU && buildSdkVersion >= VERSION_CODES.TIRAMISU)
                REQUIRED_PERMISSIONS_FOR_ANDROID_T_AND_ABOVE else REQUIRED_PERMISSIONS_FOR_ANDROID_S_AND_BELOW
        }


}