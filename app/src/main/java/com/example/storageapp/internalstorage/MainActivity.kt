package com.example.storageapp.internalstorage

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.storageapp.databinding.ActivityMainBinding
import com.example.storageapp.hide
import com.example.storageapp.internalstorage.adapters.ImageAdapter
import com.example.storageapp.internalstorage.models.InternalStorageImageModel
import com.example.storageapp.internalstorage.utils.SpacingItemDecorator
import com.example.storageapp.show
import java.io.IOException
import java.util.UUID

//https://www.youtube.com/watch?v=Gd1jbmZCauQ&list=PLzRDlxbkV34aTdb_HQWApl01b7xaXFj8f&index=3

/**
 * @author Jishnu P Dileep(Jd)
 * Created at 4 Apr 2023
 * */

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ImageAdapter
    private var isEnableSelectionMode = false
    private var selectedItemCount = 0

    private val photoContract =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            val isStored = storeDataInInternalStorage(UUID.randomUUID().toString(), bitmap = it!!)
            if (isStored) {
                Toast.makeText(this, "Stored Successfully", Toast.LENGTH_LONG).show()
                updateImageList()
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRecyclerview()
        setOnClickListeners()
    }

    private fun setRecyclerview() {
        adapter =
            ImageAdapter( onLongClickPressed = { item,position ->
            if(!isEnableSelectionMode){
                binding.apply {
                    layoutOptions.hide()
                    layoutControls.show()
                    TransitionManager.beginDelayedTransition(root)
                    isEnableSelectionMode = true
                    item.isImageSelected = true
                    adapter.notifyItemChanged(position)
                    selectedItemCount++
                    binding.textSelectedItem.text = if(selectedItemCount==1) "$selectedItemCount item selected" else "$selectedItemCount items selected"
                }
            }
        }, onItemSelected = {item, position ->
                if(isEnableSelectionMode){
                    if(item.isImageSelected){
                        item.isImageSelected = false
                        adapter.notifyItemChanged(position)
                        selectedItemCount--
                        binding.textSelectedItem.text = if(selectedItemCount==1) "$selectedItemCount item selected" else "$selectedItemCount items selected"
                    }else{
                        item.isImageSelected = true
                        adapter.notifyItemChanged(position)
                        selectedItemCount++
                        binding.textSelectedItem.text = if(selectedItemCount==1) "$selectedItemCount item selected" else "$selectedItemCount items selected"
                    }
                }

        })
        binding.apply {
            adapter.setHasStableIds(true)
            recyclerviewImages.adapter = adapter
            recyclerviewImages.itemAnimator = null
            val x = (resources.displayMetrics.density * 2).toInt()
            recyclerviewImages.addItemDecoration(SpacingItemDecorator(x))
            val list = loadImageFilesFromInternalStorage()
            Log.e("listSize", "${list.size}")
            adapter.asyncListDiffer.submitList(list)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setOnClickListeners() {



       binding.imgDelete.setOnClickListener {
           if(adapter.asyncListDiffer.currentList.isNotEmpty()){
               adapter.asyncListDiffer.currentList.map {
                   if(it.isImageSelected)
                       deleteImageFromInternalStorage(it.name)
               }
               binding.layoutOptions.show()
               binding.layoutControls.hide()
               TransitionManager.beginDelayedTransition(binding.layoutToolBar)
               updateImageList()
               isEnableSelectionMode = false
               selectedItemCount = 0
           }
       }

        binding.imageCamera.setOnClickListener {
            photoContract.launch(null)
        }

        binding.imageCancelSelection.setOnClickListener {
            isEnableSelectionMode = false
            binding.apply {

                val list = adapter.asyncListDiffer.currentList
                list.map { it.isImageSelected = false }
                adapter.notifyDataSetChanged()

                layoutOptions.show()
                layoutControls.hide()
                TransitionManager.beginDelayedTransition(binding.layoutToolBar)
                selectedItemCount = 0
                binding.textSelectedItem.text = ""
            }


        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.apply {
                    if(isEnableSelectionMode){
                        val list = adapter.asyncListDiffer.currentList
                        list.map { it.isImageSelected = false }
                        adapter.notifyDataSetChanged()

                        layoutOptions.show()
                        layoutControls.hide()
                        textSelectedItem.text = ""
                        TransitionManager.beginDelayedTransition(binding.layoutToolBar)
                        isEnableSelectionMode = false
                        selectedItemCount = 0

                    }else{
                        finish()
                        overridePendingTransition(0,0)
                    }
                }
            }
        })
    }

    private fun updateImageList() {
        adapter.asyncListDiffer.submitList(loadImageFilesFromInternalStorage())
    }

    /**
     *  function to load only images from internal storage
     *  @return list of image files from internal storage
     *  readBytes - the entire content of this file as a byte array
     *  decodeByteArray - Decode an immutable bitmap from the specified byte array
     *  BitmapFactory - Creates Bitmap objects from various sources, including files, streams, and byte-arrays.
     *  @see 'https://developer.android.com/reference/android/graphics/BitmapFactory'
     *
     *  Environment returns user data directory. And getFilesDir returns application data directory.
     *  Returns the absolute path to the directory on the filesystem where files created with openFileOutput(String, int) are stored.
     *  @see 'https://stackoverflow.com/questions/21230629/getfilesdir-vs-environment-getdatadirectory#:~:text=public%20File%20getFilesDir%20(),String%2C%20int)%20are%20stored.'
     * */

    private fun loadImageFilesFromInternalStorage(): List<InternalStorageImageModel> {
        val files = filesDir.listFiles()
        return files?.filter {
            it.canRead() && it.isFile && (it.name.endsWith(".jpg") || it.name.endsWith(".jpeg") || it.name.endsWith(
                ".png"
            ))
        }?.map {
            val bytes = it.readBytes()
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            InternalStorageImageModel(it.name, bitmap)
        } ?: listOf()
    }

    /**
     *  function to store an image in internal storage
     *
     *  @param fileName - name of the file to be stored
     *  @param bitmap - image bitmap object to store image
     *
     *  openFileOutput is specifically used for file writing into internal storage and disallow writing to external storage
     *  Open a private file associated with this Context's application package for writing. Creates the file if it doesn't already exist.
     *  No additional permissions are required for the calling app to read or write the returned file.
     *
     *  MODE_PRIVATE - File creation mode: the default mode, where the created file can only be accessed by the calling application
     *
     *  use block - Executes the given block function on this resource and then closes it down correctly whether an exception
     *  is thrown or not.
     *
     * */

    private fun storeDataInInternalStorage(fileName: String, bitmap: Bitmap): Boolean {
        return try {
            openFileOutput("$fileName.jpeg", MODE_PRIVATE).use { stream ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 96, stream)) {
                    throw IOException("Unable to store bitmap")
                }
            }
            true
        } catch (e: IOException) {
            e.stackTrace
            false
        }
    }

    /**
     *   function for delete the file in internal storage
     *   @param name - name of the file
     * */

    private fun deleteImageFromInternalStorage(name: String): Boolean {
        return try {
            deleteFile(name)
        } catch (e: Exception) {
            false
        }
    }
}