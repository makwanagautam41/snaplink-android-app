package com.example.snaplink.network

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.snaplink.models.CreatePostResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

object PostUploadManager {
    var isUploading = false
        private set

    var uploadStatusListener: ((Boolean) -> Unit)? = null
    var uploadSuccessListener: (() -> Unit)? = null

    private fun setUploadingState(uploading: Boolean) {
        if (isUploading != uploading) {
            isUploading = uploading
            // Call on main thread just in case
            Handler(Looper.getMainLooper()).post {
                uploadStatusListener?.invoke(uploading)
            }
        }
    }

    fun uploadPost(
        context: Context,
        selectedImageUris: List<Uri>,
        captionText: String
    ) {
        if (isUploading) return

        val appCtx = context.applicationContext
        setUploadingState(true)

        val captionBody = captionText.toRequestBody("text/plain".toMediaTypeOrNull())
        val imageParts = selectedImageUris.map { uriToMultipart(appCtx, it) }

        ApiClient.api.createPost(imageParts, captionBody)
            .enqueue(object : Callback<CreatePostResponse> {
                override fun onResponse(
                    call: Call<CreatePostResponse>,
                    response: Response<CreatePostResponse>
                ) {
                    setUploadingState(false)
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(appCtx, "Posted successfully 🎉", Toast.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).post {
                            uploadSuccessListener?.invoke()
                        }
                    } else {
                        Toast.makeText(
                            appCtx,
                            "Post failed: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<CreatePostResponse>, t: Throwable) {
                    setUploadingState(false)
                    Toast.makeText(
                        appCtx,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part {
        val resolver = context.contentResolver
        val type = resolver.getType(uri) ?: "image/jpeg"
        val extension = if (type.contains("video")) ".mp4" else ".jpg"
        
        val inputStream = resolver.openInputStream(uri)!!
        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}$extension")
        file.outputStream().use { inputStream.copyTo(it) }

        val reqFile = file.asRequestBody(type.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("media", file.name, reqFile)
    }
}
