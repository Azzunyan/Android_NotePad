package com.azzunyan.note

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.azzunyan.note.db.MyDbManager
import com.azzunyan.note.db.MyIntentConstants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    var id = 0
    var isEditState = false
    val imageRequestCode = 10
    var tempImageUri = "empty"
    val myDbManager = MyDbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_activity)
        getMyIntents()
        Log.d("MyLog","Time : ${getCurrentTime()}")
    }
    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == imageRequestCode){

            findViewById<ImageView>(R.id.imMainImage).setImageURI(data?.data)
            tempImageUri = data?.data.toString()
            contentResolver.takePersistableUriPermission(data?.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)

        }

    }

    fun onClickAddImage(view: View) {

        findViewById<ConstraintLayout>(R.id.mainImageLayout).visibility = View.VISIBLE
        findViewById<FloatingActionButton>(R.id.fbAddImage).visibility = View.GONE

    }

    fun onClickDeleteImage(view: View) {

        findViewById<ConstraintLayout>(R.id.mainImageLayout).visibility = View.GONE
        findViewById<FloatingActionButton>(R.id.fbAddImage).visibility = View.VISIBLE
        tempImageUri = "empty"
    }

    fun onClickChooseImage(view: View) {

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, imageRequestCode)

    }
    fun onClickSave(view: View) {

        val myTitle = findViewById<EditText>(R.id.edTitle).text.toString()
        val myDesc = findViewById<EditText>(R.id.edDesc).text.toString()

        if(myTitle != "" && myDesc != ""){

            CoroutineScope(Dispatchers.Main).launch {

                if(isEditState){
                    myDbManager.updateItem(myTitle, myDesc, tempImageUri, id, getCurrentTime())
                } else {
                    myDbManager.insertToDb(myTitle, myDesc, tempImageUri, getCurrentTime())
                }

                finish()
            }

        }



    }

    fun onEditEnable(view:View){
        findViewById<EditText>(R.id.edTitle).isEnabled = true
        findViewById<EditText>(R.id.edDesc).isEnabled = true
        findViewById<FloatingActionButton>(R.id.fbEdit).visibility = View.GONE
        findViewById<FloatingActionButton>(R.id.fbAddImage).visibility = View.VISIBLE
        if(tempImageUri == "empty")return
        findViewById<ImageButton>(R.id.imButtonEditImage).visibility = View.VISIBLE
        findViewById<ImageButton>(R.id.imButtonDeleteImage).visibility = View.VISIBLE

    }

    fun getMyIntents() {
        findViewById<FloatingActionButton>(R.id.fbEdit).visibility = View.GONE
        val i = intent

        if (i != null) {


            if (i.getStringExtra(MyIntentConstants.I_TITLE_KEY) != null) {

                findViewById<FloatingActionButton>(R.id.fbAddImage).visibility = View.GONE
                findViewById<EditText>(R.id.edTitle).setText(i.getStringExtra(MyIntentConstants.I_TITLE_KEY))
                isEditState = true
                findViewById<EditText>(R.id.edTitle).isEnabled = false
                findViewById<EditText>(R.id.edDesc).isEnabled = false
                findViewById<FloatingActionButton>(R.id.fbEdit).visibility = View.VISIBLE
                findViewById<EditText>(R.id.edDesc).setText(i.getStringExtra(MyIntentConstants.I_DESC_KEY))
                id = i.getIntExtra(MyIntentConstants.I_ID_KEY, 0)
                if (i.getStringExtra(MyIntentConstants.I_URI_KEY) != "empty") {

                    findViewById<ConstraintLayout>(R.id.mainImageLayout).visibility = View.VISIBLE
                    tempImageUri = i.getStringExtra(MyIntentConstants.I_URI_KEY)!!
                    findViewById<ImageView>(R.id.imMainImage).setImageURI(Uri.parse(tempImageUri))
                    findViewById<FloatingActionButton>(R.id.imButtonDeleteImage).visibility = View.GONE
                    findViewById<ImageView>(R.id.imButtonEditImage).visibility = View.GONE

                }

            }

        }

    }

    private fun getCurrentTime():String{
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm",Locale.getDefault())
        return formatter.format(time)
    }
}