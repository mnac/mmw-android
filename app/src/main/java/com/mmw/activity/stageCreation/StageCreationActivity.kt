package com.mmw.activity.stageCreation

import android.app.Activity
import android.arch.lifecycle.LifecycleRegistryOwner
import android.content.Intent
import android.databinding.DataBindingUtil
import android.location.Address
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.AdapterView
import android.widget.RatingBar
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.drew.imaging.ImageMetadataReader
import com.drew.lang.GeoLocation
import com.drew.metadata.exif.GpsDirectory
import com.mmw.AppConstant
import com.mmw.R
import com.mmw.activity.BaseActivity
import com.mmw.data.repository.TripRepository
import com.mmw.data.source.remote.Location.ReverseGeocoding
import com.mmw.databinding.ActivityStageCreationBinding
import com.mmw.helper.picture.PictureManager
import com.mmw.helper.view.setupSnackBar
import com.mmw.helper.view.setupSnackBarRes
import com.mmw.model.Stage
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class StageCreationActivity : BaseActivity(), LifecycleRegistryOwner, TransferListener,
        ReverseGeocoding.Listener, RatingBar.OnRatingBarChangeListener, AdapterView.OnItemSelectedListener {

    companion object {
        @JvmStatic val TRIP_INTENT_KEY = "Trip"
        @JvmStatic val STAGE_CREATION_RESULT_KEY = 20
        @JvmStatic val STAGE_ADDING_RESULT_KEY = 30
    }

    private var cameraUri: Uri? = null
    private var tempPictureFile: File? = null

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
        cameraUri = PictureManager.requestPicture(this)
    }

    private fun goToHomeActivity(stage: Stage) {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val content = this.findViewById<View>(android.R.id.content)
        cameraUri = PictureManager.onPermissionResult(this, content, requestCode, grantResults)
    }

    private fun readMetaData(file: File) {
        val metadata = ImageMetadataReader.readMetadata(file)

        // Date
        val date = metadata?.getFirstDirectoryOfType(GpsDirectory::class.java)!!.gpsDate
        val viewFormatDate = SimpleDateFormat("'le' d MMMM yyyy 'à' HH:mm", Locale.FRENCH)
        binding.viewModel?.formattedDate?.set(viewFormatDate.format(date))

        val formatSaveDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.FRENCH)
        binding.viewModel?.date = formatSaveDate.format(date)

        // position
        val gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory::class.java)
        val geoLocation: GeoLocation = gpsDirectory!!.geoLocation

        val pictureLocation = Location("picture")
        pictureLocation.latitude = geoLocation.latitude
        pictureLocation.longitude = geoLocation.longitude

        val reverseGeoCoding = ReverseGeocoding(this, this)
        reverseGeoCoding.getAddressFromLocation(pictureLocation)

        binding.viewModel?.latitude = pictureLocation.latitude
        binding.viewModel?.longitude = pictureLocation.longitude
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        tempPictureFile = PictureManager.onActivityResult(this, requestCode, resultCode, cameraUri, data)

        if (tempPictureFile != null) {

            binding.viewModel?.setPicture(tempPictureFile!!)
            readMetaData(tempPictureFile!!)

            PictureManager.uploadFile(this, tempPictureFile, AppConstant.S3_PROFILE_PICTURES_BUCKET, this)
        }
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

    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {

    }

    override fun onStateChanged(id: Int, state: TransferState?) {
        if (state == TransferState.COMPLETED && tempPictureFile != null) {
            binding.viewModel?.setPicture(tempPictureFile!!.name)
            tempPictureFile!!.delete()
        }
    }

    override fun onError(id: Int, ex: Exception?) {
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
