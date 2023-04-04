package com.example.storageapp.internalstorage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.storageapp.databinding.ActivityMainBinding
import com.example.storageapp.internalstorage.models.InternalStorageImageModel
import java.io.IOException

// https://www.youtube.com/watch?v=Gd1jbmZCauQ&list=PLzRDlxbkV34aTdb_HQWApl01b7xaXFj8f&index=3

/**
 * @author Jishnu P Dileep(Jd)
 * Created at 4 Apr 2023
 * */

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var imageList = arrayListOf<InternalStorageImageModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

    private fun loadImageFilesFromInternalStorage():List<InternalStorageImageModel>{
        val files = filesDir.listFiles()
        return files?.filter {
            it.canRead() && it.isFile && it.name.endsWith(".jpg")
        }?.map {
            val bytes = it.readBytes()
            val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            InternalStorageImageModel(it.name,bitmap)
        }?: listOf()
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

    private fun storeDataInInternalStorage(fileName:String,bitmap: Bitmap):Boolean {
       return try {
           openFileOutput("$fileName.jpeg", MODE_PRIVATE).use { stream->
               if(!bitmap.compress(Bitmap.CompressFormat.JPEG,96,stream)){
                   throw IOException("Unable to store bitmap")
               }
           }
           true
        }catch (e:IOException){
            e.stackTrace
            false
        }
    }

    /**
     *   function for delete the file in internal storage
     *   @param name - name of the file
     * */

    private fun deleteImageFromInternalStorage(name:String):Boolean{
        return try {
            deleteFile(name)
        }catch (e:Exception){
            false
        }
    }
}