package com.pedro.sample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*


fun getArrayWithSteps(
  iMinValue: Int,
  iMaxValue: Int,
  iStep: Int
): Array<String?>? {
  val steps = 1 + (iMaxValue - iMinValue) / iStep
  val arrayValues = arrayOfNulls<String>(steps)
  for (i in 0 until steps) {
    val value = iMinValue + i*iStep
    arrayValues[i] = value.toString()
  }
  return arrayValues
}

class MainActivity : AppCompatActivity() {

  private val PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
    Manifest.permission.WRITE_EXTERNAL_STORAGE)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    setContentView(R.layout.activity_main)


    val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
    /*
    val defaultValue = resources.getInteger(R.integer.saved_high_score_default_key)
    val highScore = sharedPref.getInt(getString(R.string.saved_high_score_key), defaultValue)
    */

    val resolutionAdapter =
      ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item)
    val resolutions: List<String> = listOf(
      "320X240", "480X360", "640x480", "800X600", "1024X768",
      "1280X720", "1280X960", "1440X1080", "1600X1200", "1920X1080")
    resolutionAdapter.addAll(resolutions)
    sp_resolution?.adapter = resolutionAdapter
    val initial_resolution = sharedPref.getString("resolution", "800X600")
    sp_resolution?.setSelection(resolutions.indexOf(initial_resolution))

    val fpsAdapter =
      ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item)
    val frameRates: List<String> = listOf("5", "10", "15", "20", "25", "30", "60")
    fpsAdapter.addAll(frameRates)
    sp_fps?.adapter = fpsAdapter
    val initial_fps = sharedPref.getString("fps", "30")
    sp_fps?.setSelection(frameRates.indexOf(initial_fps))

    val codecAdapter =
      ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item)
    val codecs: List<String> = listOf("H264", "HEVC")
    codecAdapter.addAll(codecs)
    sp_codec?.adapter = codecAdapter
    val initial_codec = sharedPref.getString("codec", "H264")
    sp_codec?.setSelection(codecs.indexOf(initial_codec))

    val minValue = 100
    val maxValue = 5000
    val step = 100
    num_bitrate.minValue = 0;
    num_bitrate.maxValue = (maxValue - minValue) / step;
    num_bitrate.displayedValues = getArrayWithSteps(minValue, maxValue, step)
    val initial_bitrate = sharedPref.getInt("bitrate", 2000)
    num_bitrate?.setValue(initial_bitrate)

    b_camera_demo.setOnClickListener {
      if (!hasPermissions(this, *PERMISSIONS)) {
        ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
      } else {
        val intent = Intent(this, CameraDemoActivity::class.java)

        val resolution = sp_resolution.selectedItem.toString()
        intent.putExtra("resolution", resolution)

        val fps = sp_fps.selectedItem.toString()
        intent.putExtra("fps", fps.toInt())

        val codec = sp_codec.selectedItem.toString()
        intent.putExtra("codec", codec)

        val bitrate = num_bitrate.value
        intent.putExtra("bitrate", minValue+bitrate*step )

        with(sharedPref.edit()) {
          putString("resolution", resolution)
          putString("fps", fps)
          putString("codec", codec)
          putInt("bitrate", bitrate)
          commit()
        }

        startActivity(intent)
      }
    }
  }

  private fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
      for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(context,
              permission) != PackageManager.PERMISSION_GRANTED) {
          return false
        }
      }
    }
    return true
  }
}