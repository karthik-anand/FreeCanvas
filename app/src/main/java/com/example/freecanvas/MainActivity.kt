package com.example.freecanvas

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.brush_size_menu.*
import kotlinx.android.synthetic.main.color_menu.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //drawing_view.setBrushSize(30.toFloat())
        btn_brush_size.setOnClickListener{
            chooseBrushSize()
        }
        btn_colour_choice.setOnClickListener{
            chooseColour()
        }
        btn_choose_image.setOnClickListener{
            if(isReadStorageAllowed()){
                val pickPhoto = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto,GALLERY)
            }else{
                requestStoragePermission()
            }
        }
        btn_undo.setOnClickListener{
            drawing_view.onUndo()
        }
        btn_save.setOnClickListener{
            val toSave:Bitmap=getBitMapFromView(Fl_container)
            val uri:Uri=saveImageToInternalStorage(toSave)
            Toast.makeText(this," saved at $uri",Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK){
            if(requestCode== GALLERY){
                try{
                    if(data!!.data!=null){
                        bg_image.visibility= View.VISIBLE
                        bg_image.setImageURI(data.data)
                    }else{
                        Toast.makeText(
                            this,
                            "Image not selected",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }catch(e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE).toString())){
            Toast.makeText(this,"Need permission to add background",Toast.LENGTH_LONG).show()
        }
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERM_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PERM_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"permission granted",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isReadStorageAllowed(): Boolean{
        val result=ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if(result==PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun getBitMapFromView(view:View):Bitmap{
        val resMap = Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
        val canvas=Canvas(resMap)
        val bgDrawable=view.background
        if(bgDrawable!=null){
            bgDrawable.draw(canvas)
        }else{
            canvas.drawColor(Color.TRANSPARENT)
        }
        view.draw(canvas)
        return resMap
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap):Uri{
        val wrapper=ContextWrapper(applicationContext)
        var file_path = wrapper.getDir("images",Context.MODE_PRIVATE)
        val random=(0..100).random()
        val baseDir: String = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        file_path= File(file_path,"$baseDir+$random.jpg")
        try {
            val stream:OutputStream=FileOutputStream(file_path)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }
        return Uri.parse(file_path.absolutePath)
    }

    companion object{
        private const val STORAGE_PERM_CODE=1
        private const val GALLERY=2
    }

    private fun chooseBrushSize(){
        val brushDialog=Dialog(this)
        brushDialog.setContentView(R.layout.brush_size_menu)
        brushDialog.setTitle("Brush Size:")
        val smallBtn=brushDialog.ib_small_brush
        smallBtn.setOnClickListener{
            drawing_view.setBrushSize(10.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
        val mediumBtn=brushDialog.ib_medium_brush
        mediumBtn.setOnClickListener{
            drawing_view.setBrushSize(20.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
        val largeBtn=brushDialog.ib_large_brush
        largeBtn.setOnClickListener{
            drawing_view.setBrushSize(30.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
    }

    private fun chooseColour(){
        val colourDialog=Dialog(this)
        colourDialog.setContentView(R.layout.color_menu)
        colourDialog.setTitle("Choose colour")
        val red=colourDialog.ib_red
        red.setOnClickListener{
            drawing_view.setColour(Color.RED)
            colourDialog.dismiss()
        }
        colourDialog.show()
        val black=colourDialog.ib_black
        black.setOnClickListener{
            drawing_view.setColour(Color.BLACK)
            colourDialog.dismiss()
        }
        colourDialog.show()
        val yellow=colourDialog.ib_yellow
        yellow.setOnClickListener{
            drawing_view.setColour(Color.YELLOW)
            colourDialog.dismiss()
        }
        colourDialog.show()
        val green=colourDialog.ib_green
        green.setOnClickListener{
            drawing_view.setColour(Color.GREEN)
            colourDialog.dismiss()
        }
        colourDialog.show()
        val lollipop=colourDialog.ib_navy
        lollipop.setOnClickListener{
            drawing_view.setColour(Color.CYAN)
            colourDialog.dismiss()
        }
        colourDialog.show()
        val blue=colourDialog.ib_blue
        blue.setOnClickListener{
            drawing_view.setColour(Color.BLUE)
            colourDialog.dismiss()
        }
        colourDialog.show()
        val skin=colourDialog.ib_skin
        skin.setOnClickListener{
            drawing_view.setColour(Color.LTGRAY)
            colourDialog.dismiss()
        }
        colourDialog.show()
        val sea=colourDialog.ib_navy
        sea.setOnClickListener{
            drawing_view.setColour(Color.TRANSPARENT)
            colourDialog.dismiss()
        }
        colourDialog.show()
    }
}