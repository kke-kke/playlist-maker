package com.example.playlistmaker.ui.library.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.CreatePlaylistBinding
import com.example.playlistmaker.domain.library.models.Playlist
import com.example.playlistmaker.ui.library.viewModel.PlaylistsViewModel
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

open class CreatePlaylistFragment : Fragment() {
    lateinit var createPlaylistBinding: CreatePlaylistBinding
    private val requester = PermissionRequester.instance()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var isCoverSelected = false
    var savedCoverUri: Uri? = null
    private val playlistsViewModel by viewModel<PlaylistsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        createPlaylistBinding = CreatePlaylistBinding.inflate(inflater, container, false)
        return createPlaylistBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createPlaylistBinding.playlistName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                createPlaylistBinding.createPlaylistButton.isEnabled = !s.isNullOrEmpty()
            }
        })

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                createPlaylistBinding.playlistCoverImageContainer.background = null
                createPlaylistBinding.playlistCoverImage.setImageURI(uri)
                savedCoverUri = saveImageToPrivateStorage(uri)
                createPlaylistBinding.playlistCoverImage.tag = savedCoverUri.toString()
                isCoverSelected = true
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        initClickListeners()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    private fun initClickListeners() {
        createPlaylistBinding.createPlaylistToolbar.setOnClickListener {
            if (isPlaylistModified()) {
                showExitConfirmationDialog()
            } else {
                findNavController().navigateUp()
            }
        }

        createPlaylistBinding.playlistCoverImageContainer.setOnClickListener {
            requestStoragePermission()
        }

        createPlaylistBinding.createPlaylistButton.setOnClickListener {
            val name = createPlaylistBinding.playlistName.text.toString()
            val description = createPlaylistBinding.playlistDescription.text.toString() ?: ""
            val cover = savedCoverUri?.toString() ?: ""

            playlistsViewModel.insertPlaylist(Playlist(0, name, description, cover, emptyList(), 0))
            Toast.makeText(requireContext(), "Плейлист $name создан", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
        }
    }

    private fun isPlaylistModified(): Boolean {
        val nameNotEmpty = !createPlaylistBinding.playlistName.text.isNullOrEmpty()
        val descriptionNotEmpty = !createPlaylistBinding.playlistDescription.text.isNullOrEmpty()
        return nameNotEmpty || descriptionNotEmpty || isCoverSelected
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setPositiveButton("Завершить") { _, _ ->
                findNavController().navigateUp()
            }
            .setNegativeButton("Отмена", null)
            .show()
            .apply {
                getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(context.getColor(R.color.main_background_color))
                getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(context.getColor(R.color.main_background_color))
            }

        }

    fun requestStoragePermission() {
        lifecycleScope.launch {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            requester.request(permission).collect { result ->
                when (result) {
                    is PermissionResult.Granted -> {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    is PermissionResult.Denied.NeedsRationale -> {
                        showRationaleDialog()
                    }
                    is PermissionResult.Denied.DeniedPermanently -> {
                        showSettingsRedirectDialog()
                    }
                    is PermissionResult.Cancelled -> {
                        Toast.makeText(requireContext(), "Запрос разрешения отменен", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Доступ к галерее")
            .setMessage("Для выбора обложки плейлиста необходимо разрешение на доступ к галерее.")
            .setPositiveButton("OK") { _, _ ->
                requestStoragePermission()
            }
            .setNegativeButton("Отмена", null)
            .show()
            .apply {
                getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(context.getColor(R.color.main_background_color))
                getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(context.getColor(R.color.main_background_color))
            }
    }

    private fun showSettingsRedirectDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Доступ к галерее")
            .setMessage("Разрешение отклонено. Пожалуйста, предоставьте доступ в настройках приложения.")
            .setPositiveButton("Настройки") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", requireContext().packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("Отмена", null)
            .show()
            .apply {
                getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(context.getColor(R.color.main_background_color))
                getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(context.getColor(R.color.main_background_color))
            }
    }

    private fun saveImageToPrivateStorage(uri: Uri): Uri {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        val file = File(filePath, "first_cover.jpg")

        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        inputStream?.close()
        outputStream.close()

        return Uri.fromFile(file)
    }

}