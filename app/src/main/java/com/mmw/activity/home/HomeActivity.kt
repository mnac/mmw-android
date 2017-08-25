package com.mmw.activity.home

import android.Manifest.permission
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.mmw.AppConstant
import com.mmw.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity() {

    private val PICTURE_PERMISSION_REQUEST_CODE = 100
    private val PICTURE_REQUEST_CODE = 200

    private var cameraUri: Uri? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                //message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                //message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                //message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val button = findViewById<Button>(R.id.pickPicture)
        button.setOnClickListener({ requestPicture() })

        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val currentUri = cameraUri
            var file: File? = null

            if (data?.data != null) {
                val path = getLocalPath(data.data)
                Log.i("Transfert Local path", path)
                file = File(path)
            } else if (currentUri != null) {
                file = File(currentUri.path)
            }

            if (file != null) {
                Log.i("Transfert path", file.path)

                /*val metadata = ImageMetadataReader.readMetadata(file)

            val gpsDirectory = metadata?.getFirstDirectoryOfType(GpsDirectory::class.java)

            val geoLocation: GeoLocation = gpsDirectory!!.geoLocation

            val pictureLocation: Location = Location("picture")
            pictureLocation.latitude = geoLocation.latitude
            pictureLocation.longitude = geoLocation.longitude*/

                val credentialsProvider = CognitoCachingCredentialsProvider(
                        applicationContext,
                        AppConstant.S3_POOL_ID,
                        AppConstant.S3_REGION
                )

                val s3 = AmazonS3Client(credentialsProvider)
                val transferUtility = TransferUtility(s3, applicationContext)

                val observer = transferUtility.upload(
                        AppConstant.S3_BUCKET_NAME,
                        AppConstant.S3_TRIP_PICTURE_ROOT + file.name,
                        file,
                        CannedAccessControlList.PublicRead)

                observer.setTransferListener(object : TransferListener {
                    override fun onStateChanged(id: Int, state: TransferState) {
                        Log.d("Transfert state: ", state.name)
                    }

                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                        Log.d("Transfert progress: ", bytesCurrent.toString())
                    }

                    override fun onError(id: Int, ex: Exception) {
                        Log.d("Transfert error: ", ex.message)
                    }
                })
            }
        }
    }

    private fun requestPicture() {
        val permission = checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    Array(1, { android.Manifest.permission.WRITE_EXTERNAL_STORAGE }),
                    PICTURE_PERMISSION_REQUEST_CODE)
        } else {
            createPicturePickerIntent()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PICTURE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createPicturePickerIntent()
                } else {
                    val content = this.findViewById<View>(android.R.id.content)
                    Snackbar.make(content, getString(R.string.pickPicturePermissionRefused),
                            Snackbar.LENGTH_LONG)
                }
                return
            }
        }
    }

    private fun createPicturePickerIntent() {
        val intents = ArrayList<Intent>()

        try {
            cameraUri = Uri.fromFile(createPrivatePictureFile())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Add camera applications
        intents.addAll(getCameraIntents(cameraUri))

        // create chooser with by default image content application
        val chooserIntent = Intent.createChooser(
                getImageContentIntent(), getString(R.string.pickPictureIntent))

        // Add other applications
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())

        startActivityForResult(chooserIntent, PICTURE_REQUEST_CODE)
    }

    private fun createPrivatePictureFile(): File {
        return File.createTempFile(generatePictureFileName(), ".jpg", this.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
    }

    private fun generatePictureFileName(): String {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.FRANCE).format(Date())
        return "JPEG_" + timeStamp + "_"
    }

    private fun getCameraIntents(pictureUri: Uri?): ArrayList<Intent> {
        val intents = ArrayList<Intent>()
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (pictureUri != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri)
        }

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            val listCam = this.packageManager.queryIntentActivities(takePictureIntent, 0)

            for (res in listCam) {
                val packageName = res.activityInfo.packageName
                val intent = Intent(takePictureIntent)
                intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
                intent.`package` = packageName
                intents.add(intent)
            }
        }
        return intents
    }

    private fun getImageContentIntent(): Intent {
        // create chooser with available picker application
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        return intent
    }

    private fun getLocalPath(uri: Uri): String? {
        var cursor: Cursor? = null
        try {
            val projection = arrayOf(MediaStore.MediaColumns.DATA)
            cursor = contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                return cursor.getString(column_index)
            }
        } catch (e: Exception) {
            return null
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
        return null
    }
}
