package com.lab83.sligamer.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView

/**
 * Created by Justin Freres on 4/10/2018.
 * Compass Lab 8.3 *
 * Plugin Support with kotlin_version = '1.2.41'
 */
class MainActivity : AppCompatActivity(), SensorEventListener {

    // COMPASS IMAGE ON SCREEN
    private lateinit var compassImage: ImageView

    // RECORD THE COMPASS ANGLE IN DEGREES
    private var currentDegree: Float = 0.0F

    // SENSOR MANAGER AND THE SENSORS THAT WILL BE MONITORED
    private lateinit var sensorManager: SensorManager
    private lateinit var sensorAccelerometer: Sensor
    private lateinit var sensorMagnetometer: Sensor

    private var accelerometer: FloatArray = FloatArray(9)
    private var geomagnetic: FloatArray= FloatArray(9)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TASK 1: REF THE UI ELEMENTS
        compassImage = findViewById(R.id.compassimageView)

        // TASK 2: INIT SENSORS
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    }

    override fun onResume() {
        super.onResume()
        // REG LISTENERS
        // SENSOR DELAY IS THE ONLY ONE WORKS HERE
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, sensorMagnetometer,  SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        // UNREG LISTENERS
        sensorManager.unregisterListener(this)
    }

    /**
     * Called when the accuracy of the registered sensor has changed.  Unlike
     * onSensorChanged(), this is only called when this accuracy value changes.
     *
     *
     * See the SENSOR_STATUS_* constants in
     * [SensorManager][android.hardware.SensorManager] for details.
     *
     * @param accuracy The new accuracy of this sensor, one of
     * `SensorManager.SENSOR_STATUS_*`
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    /**
     * Called when there is a new sensor event.  Note that "on changed"
     * is somewhat of a misnomer, as this will also be called if we have a
     * new reading from a sensor with the exact same sensor values (but a
     * newer timestamp).
     *
     *
     * See [SensorManager][android.hardware.SensorManager]
     * for details on possible sensor types.
     *
     * See also [SensorEvent][android.hardware.SensorEvent].
     *
     *
     * **NOTE:** The application doesn't own the
     * [event][android.hardware.SensorEvent]
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the [SensorEvent][android.hardware.SensorEvent].
     */
    override fun onSensorChanged(event: SensorEvent?) {
        // ROTATION ANIMATION IS SET FOR 1000 MILLISECONDS
        val DELAY: Long = 1000

        // COLLECT DATA FROM AN ACCELEROMETER EVENT
        if(event!!.sensor.type == Sensor.TYPE_ACCELEROMETER)
            accelerometer = event.values

        if(event!!.sensor.type == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = event.values

        // CHECK IF BOTH SENORS CAUSED EVENT
        when {
            accelerometer != null && geomagnetic != null -> {
                val r = FloatArray(9)
                val i = FloatArray(9)
                val foundRotation: Boolean = SensorManager.getRotationMatrix(r, i, accelerometer, geomagnetic)

                // ROTATION HAS OCCURRED
                when {
                    foundRotation -> {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(r, orientation)

                        // COMPUTE TEH X-AXIS ROTATION ANGLE
                        val degree: Float = Math.toDegrees(orientation[0].toDouble()).toFloat()

                        // CREATE A ROTATION ANIMATION
                        val animation: RotateAnimation = RotateAnimation(currentDegree, -degree,
                                Animation.RELATIVE_TO_SELF,0.5F,
                                Animation.RELATIVE_TO_SELF, 0.5F)

                        // SET ANIMATION DURATION
                        animation.duration = DELAY

                        // BEGIN THE ANIMATION
                        compassImage.animation = animation
                        currentDegree = -degree
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item!!.itemId
        if(id == R.string.action_settings){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
