package com.mmw.activity.stageCreation

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.LifecycleRegistryOwner
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.databinding.DataBindingUtil
import android.location.Address
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.RatingBar
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.drew.imaging.ImageMetadataReader
import com.drew.lang.GeoLocation
import com.drew.metadata.exif.GpsDirectory
import com.mmw.AppConstant
import com.mmw.R
import com.mmw.activity.BaseActivity
import com.mmw.data.repository.TripRepository
import com.mmw.data.source.remote.Location.ReverseGeocoding
import com.mmw.databinding.ActivityStageCreationBinding
import com.mmw.helper.view.setupSnackBar
import com.mmw.helper.view.setupSnackBarRes
import com.mmw.model.Stage
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*

class StageCreationActivity : BaseActivity(), LifecycleRegistryOwner,
        ReverseGeocoding.Listener, RatingBar.OnRatingBarChangeListener, AdapterView.OnItemSelectedListener {

    companion object {
        @JvmStatic val TRIP_INTENT_KEY = "Trip"
        @JvmStatic val STAGE_CREATION_RESULT_KEY = 20
        @JvmStatic val STAGE_ADDING_RESULT_KEY = 30
    }

    private val PICTURE_PERMISSION_REQUEST_CODE = 100
    private val PICTURE_REQUEST_CODE = 200

    private var cameraUri: Uri? = null

    private lateinit var binding: ActivityStageCreationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_stage_creation)
        binding.viewModel = StageCreationViewModel(application, TripRepository.instance)
        binding.activity = this

        binding.viewModel?.trip = intent.getParcelableExtra(TRIP_INTENT_KEY)

        binding.viewModel?.createdStage?.observe(this, android.arch.lifecycle.Observer {
            if (it != null) goToHomeActivity(it)
        })

        val content = this.findViewById<View>(android.R.id.content)
        content.setupSnackBar(this, binding.viewModel?.snackBarMessage, Snackbar.LENGTH_LONG)
        content.setupSnackBarRes(this, binding.viewModel?.snackBarMessageRes, Snackbar.LENGTH_LONG)
    }

    fun onClickPicture(view: View) {
        requestPicture()
    }

    private fun goToHomeActivity(stage: Stage) {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun requestPicture() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

    private fun getImageContentIntent(): Intent {
        // create chooser with available picker application
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        return intent
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


            if (file != null && file.exists()) {

                binding.viewModel?.setPicture(file)

                Log.i("Transfert path", file.path)

                val metadata = ImageMetadataReader.readMetadata(file)

                val date = metadata?.getFirstDirectoryOfType(GpsDirectory::class.java)!!.gpsDate
                val viewFormatDate = SimpleDateFormat("'le' d MMMM yyyy 'à' HH:mm", Locale.FRENCH)
                binding.viewModel?.formattedDate?.set(viewFormatDate.format(date))

                val formatSaveDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.FRENCH)
                binding.viewModel?.date = formatSaveDate.format(date)

                val gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory::class.java)

                val geoLocation: GeoLocation = gpsDirectory!!.geoLocation

                val pictureLocation = Location("picture")
                pictureLocation.latitude = geoLocation.latitude
                pictureLocation.longitude = geoLocation.longitude

                val reverseGeoCoding = ReverseGeocoding(this, this)
                reverseGeoCoding.getAddressFromLocation(pictureLocation)

                binding.viewModel?.latitude = pictureLocation.latitude
                binding.viewModel?.longitude = pictureLocation.longitude

                val credentialsProvider = CognitoCachingCredentialsProvider(
                        applicationContext,
                        AppConstant.S3_POOL_ID,
                        AppConstant.S3_REGION
                )

                val s3 = AmazonS3Client(credentialsProvider)
                val transferUtility = TransferUtility(s3, applicationContext)

                val outputDir = applicationContext.cacheDir // context being the Activity pointer
                val outputFile = File.createTempFile(
                        UUID.randomUUID().toString(), ".jpg", outputDir)

                copyFile(file, outputFile)

                val observer = transferUtility.upload(
                        AppConstant.S3_BUCKET_NAME,
                        AppConstant.S3_TRIP_PICTURES_BUCKET + outputFile.name,
                        outputFile,
                        CannedAccessControlList.PublicRead)

                observer.setTransferListener(object : TransferListener {
                    override fun onStateChanged(id: Int, state: TransferState) {
                        Log.d("Transfert state: ", state.name)
                        if (state == TransferState.COMPLETED) {
                            outputFile.delete()
                            binding.viewModel?.setPicture(outputFile.name)
                        }
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

    @Throws(IOException::class)
    private fun copyFile(sourceFile: File, destFile: File) {
        if (!destFile.exists()) {
            destFile.createNewFile()
        }

        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            source = RandomAccessFile(sourceFile, "rw").channel
            destination = RandomAccessFile(destFile, "rw").channel

            val position: Long = 0
            val count = source!!.size()

            source.transferTo(position, count, destination)
        } finally {
            if (source != null) {
                source.close()
            }
            if (destination != null) {
                destination.close()
            }
        }
    }

    private fun createPrivatePictureFile(): File {
        return File.createTempFile(generatePictureFileName(), ".jpg",
                this.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
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

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val types = applicationContext.resources.getStringArray(R.array.stageType)
        binding.viewModel?.type = types[p2]
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        binding.viewModel?.type = null
    }

    override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
        binding.viewModel?.rate = Math.round(p1)
    }

    override fun onAddresses(addresses: List<Address>) {
        binding.viewModel?.address?.set(addresses[0].getAddressLine(0))
    }

    override fun onNoAddressFound(e: Throwable) {

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        binding.viewModel?.saveState()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.viewModel?.restoreState()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewModel?.dispose()
    }

}
