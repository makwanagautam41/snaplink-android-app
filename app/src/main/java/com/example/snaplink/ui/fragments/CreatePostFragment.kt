package com.example.snaplink.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snaplink.R
import com.example.snaplink.SelectedImageAdapter
import com.example.snaplink.models.CreatePostResponse
import com.example.snaplink.network.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CreatePostFragment : Fragment() {

    private lateinit var recyclerSelectedImages: RecyclerView
    private lateinit var btnSelectImages: Button
    private lateinit var etCaption: EditText
    private lateinit var btnPost: Button
    private lateinit var btnBack: ImageView

    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var imageAdapter: SelectedImageAdapter

    // Image picker launcher
    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                selectedImageUris.clear()
                selectedImageUris.addAll(uris)
                imageAdapter.notifyDataSetChanged()
            }
        }

    // Permission launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openImagePicker()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupImageAdapter()
        setupListeners()
    }

    private fun initViews(view: View) {
        recyclerSelectedImages = view.findViewById(R.id.recyclerSelectedImages)
        btnSelectImages = view.findViewById(R.id.btnSelectImages)
        etCaption = view.findViewById(R.id.etCaption)
        btnPost = view.findViewById(R.id.btnPost)
        btnBack = view.findViewById(R.id.btnBack)
    }

    private fun setupImageAdapter() {
        imageAdapter = SelectedImageAdapter(selectedImageUris) { position ->
            selectedImageUris.removeAt(position)
            imageAdapter.notifyItemRemoved(position)
        }
        recyclerSelectedImages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerSelectedImages.adapter = imageAdapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSelectImages.setOnClickListener {
            checkPermissionAndPickImages()
        }

        btnPost.setOnClickListener {
            uploadPost()
        }
    }

    private fun checkPermissionAndPickImages() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openImagePicker()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
            else -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openImagePicker()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun openImagePicker() {
        pickImagesLauncher.launch("image/*")
    }

    private fun uploadPost() {
        if (selectedImageUris.isEmpty()) {
            Toast.makeText(requireContext(), "Select at least one image", Toast.LENGTH_SHORT).show()
            return
        }

        btnPost.isEnabled = false
        btnPost.text = "Posting..."

        val captionText = etCaption.text.toString()
        val captionBody = captionText.toRequestBody("text/plain".toMediaTypeOrNull())

        val imageParts = selectedImageUris.map { uriToMultipart(it) }

        ApiClient.api.createPost(imageParts, captionBody)
            .enqueue(object : Callback<CreatePostResponse> {
                override fun onResponse(
                    call: Call<CreatePostResponse>,
                    response: Response<CreatePostResponse>
                ) {
                    if (!isAdded) return
                    btnPost.isEnabled = true
                    btnPost.text = "Post"

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            requireContext(),
                            "Posted successfully 🎉",
                            Toast.LENGTH_SHORT
                        ).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Post failed: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<CreatePostResponse>, t: Throwable) {
                    if (!isAdded) return
                    btnPost.isEnabled = true
                    btnPost.text = "Post"
                    Toast.makeText(
                        requireContext(),
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun uriToMultipart(uri: Uri): MultipartBody.Part {
        val inputStream = requireContext().contentResolver.openInputStream(uri)!!
        val file = File(requireContext().cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { inputStream.copyTo(it) }

        val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("images", file.name, reqFile)
    }
}
