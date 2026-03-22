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

    // Media picker launcher (Images and Videos)
    private val pickMediaLauncher =
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
                openMediaPicker()
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
            checkPermissionAndPickMedia()
        }

        btnPost.setOnClickListener {
            uploadPost()
        }
    }

    private fun checkPermissionAndPickMedia() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                val hasImages = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                val hasVideos = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
                
                if (hasImages && hasVideos) {
                    openMediaPicker()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    // Note: In a real app, you should request both.
                }
            }
            else -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openMediaPicker()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun openMediaPicker() {
        // Allow both images and videos
        pickMediaLauncher.launch("*/*")
    }

    private fun uploadPost() {
        if (selectedImageUris.isEmpty()) {
            Toast.makeText(requireContext(), "Select at least one image or video", Toast.LENGTH_SHORT).show()
            return
        }

        btnPost.isEnabled = false
        btnPost.text = "Posting..."

        val captionText = etCaption.text.toString()
        
        // Let the manager handle the upload in background
        com.example.snaplink.network.PostUploadManager.uploadPost(
            requireContext(), 
            selectedImageUris.toList(), 
            captionText
        )
        
        Toast.makeText(requireContext(), "Posting in background...", Toast.LENGTH_SHORT).show()
        (activity as? com.example.snaplink.ui.activities.MainActivity)?.navigateToFragment(com.example.snaplink.ui.fragments.HomeFragment())
    }
}
