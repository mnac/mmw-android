package com.mmw.helper.picture

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.mmw.AppConstant
import com.mmw.R
import com.mmw.activity.BaseActivity
import java.io.*
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mathias on 05/09/2017.
 *
 */
class PictureManager {

    companion object {
        val PICTURE_REQUEST_CODE = 100
        val PICTURE_PERMISSION_REQUEST_CODE = 200

        /**
         * Request picture handler (with permission check)
         */
        fun requestPicture(context: BaseActivity): Uri? {
            val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, Array(1, { android.Manifest.permission.WRITE_EXTERNAL_STORAGE }),
                        PICTURE_PERMISSION_REQUEST_CODE)
            } else {
                return requestPicturePicker(context)
            }
            return null
        }


        fun onActivityResult(context: BaseActivity, requestCode: Int, resultCode: Int, cameraUri: Uri?,  data: Intent?): File? {
            if (requestCode == PICTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                var file: File? = null

                val outputDir = context.applicationContext.cacheDir // context being the Activity pointer
                val outputFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg", outputDir)

                if (data?.data != null) {
                    val path = getLocalPath(context, data.data)
                    if (path != null && !path.isEmpty()) {
                        Log.i("Transfert Local path", path)
                        file = File(path)
                    } else {
                        getContentResolverFile(context, data.data, outputFile)
                    }
                } else if (cameraUri != null) {
                    file = File(cameraUri.path)
                }

                if (file != null && file.exists()) {
                    copyFile(file, outputFile)
                }

                return outputFile
            }
            return null
        }

        /**
         * Get picture from content resolver uri
         */
        private fun getContentResolverFile(context: BaseActivity, uri: Uri, outputFile: File) {
            var out: OutputStream? = null
            val inputStream = context.contentResolver.openInputStream(uri)

            try {
                out = FileOutputStream(outputFile)
                inputStream.copyTo(out, 1024)
            } catch (e: Exception) {
                Log.e("Read content resolver", e.message)
            } finally {
                try {
                    if (out != null) out.close()
                    inputStream.close()
                } catch (e: IOException) {
                    Log.e("Read content resolver", e.message)
                }
            }
        }

        /**
         * Upload file
         */
        fun uploadFile(context: BaseActivity, file: File?, bucket: String, transferListener: TransferListener) {
            if (file != null) {
                val applicationContext = context.applicationContext

                val credentialsProvider = CognitoCachingCredentialsProvider(
                        applicationContext,
                        AppConstant.S3_POOL_ID,
                        AppConstant.S3_REGION)

                val s3 = AmazonS3Client(credentialsProvider)
                val transferUtility = TransferUtility(s3, applicationContext)

                val observer = transferUtility.upload(
                        AppConstant.S3_BUCKET_NAME,
                        bucket + file.name,
                        file,
                        CannedAccessControlList.PublicRead)

                observer.setTransferListener(transferListener)
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

        /**
         * Handling permission result on get picture
         */
        fun onPermissionResult(context: BaseActivity, view:View, requestCode: Int, grantResults: IntArray): Uri? {
            when (requestCode) {
                PICTURE_PERMISSION_REQUEST_CODE -> {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        return requestPicture(context)
                    } else {
                        Snackbar.make(view, context.getString(R.string.pickPicturePermissionRefused), Snackbar.LENGTH_LONG)
                    }
                }
            }
            return null
        }

        /**
        * Launch picture picker dialog with apps handling pictures and photo
         */
        private fun requestPicturePicker(context: BaseActivity): Uri? {
            val intents = ArrayList<Intent>()

            var cameraUri: Uri? = null
            try {
               cameraUri = Uri.fromFile(createPrivatePictureFile(context))
                // Add camera applications
                intents.addAll(getCameraIntents(context, cameraUri))

                // create chooser with by default image content application
                val chooserIntent = Intent.createChooser(
                        getImageContentIntent(), context.getString(R.string.pickPictureIntent))

                // Add other applications
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())

                context.startActivityForResult(chooserIntent, PICTURE_REQUEST_CODE)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                return cameraUri
            }
        }

        /**
         * Get intents for for applications handling camera
         */
        private fun getCameraIntents(context: BaseActivity, pictureUri: Uri?): ArrayList<Intent> {
            val intents = ArrayList<Intent>()
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (pictureUri != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri)
            }

            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(context.packageManager) != null) {
                val listCam = context.packageManager.queryIntentActivities(takePictureIntent, 0)

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

        /**
         * Get intents for applications handling user pictures
         */
        private fun getImageContentIntent(): Intent {
            // create chooser with available picker application
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            return intent
        }

        /**
         * Generate a private file for picture
         */
        private fun createPrivatePictureFile(context: BaseActivity): File {
            return File.createTempFile(generatePictureFileName(), ".jpg",
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        }

        /**
         * Generate custom picture file name
         */
        private fun generatePictureFileName(): String {
            val timeStamp = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.FRANCE).format(Date())
            return "JPEG_" + timeStamp + "_"
        }


        private fun getLocalPath(context: BaseActivity, uri: Uri): String? {
            var cursor: Cursor? = null
            try {
                val projection = arrayOf(MediaStore.MediaColumns.DATA)
                cursor = context.contentResolver.query(uri, projection, null, null, null)
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
}