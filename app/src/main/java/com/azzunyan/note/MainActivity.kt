package com.azzunyan.note

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azzunyan.note.db.MyAdapter
import com.azzunyan.note.db.MyDbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val myDbManager = MyDbManager(this)
    val myAdapter = MyAdapter(ArrayList(), this)
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initSearchView()

    }
    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
        fillAdapter("")

    }

    fun onClickNew(view: View) {

        val i = Intent(this,EditActivity::class.java)
        startActivity(i)
    }

    fun init(){

        findViewById<RecyclerView>(R.id.rcView).layoutManager = LinearLayoutManager(this)
        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(findViewById(R.id.rcView))
        findViewById<RecyclerView>(R.id.rcView).adapter = myAdapter

    }

    private fun initSearchView(){
        findViewById<SearchView>(R.id.searchView).setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                fillAdapter(text!!)
                return true
            }
        })
    }

    private fun fillAdapter(text: String){

        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch{

            val list = myDbManager.readDbData(text)
            myAdapter.updateAdapter(list)
            if(list.size > 0){

                findViewById<TextView>(R.id.tvNoElements).visibility = View.GONE
            } else {

                findViewById<TextView>(R.id.tvNoElements).visibility = View.VISIBLE
            }
        }


    }
    private fun getSwapMg(): ItemTouchHelper{
        return ItemTouchHelper(object:ItemTouchHelper.
        SimpleCallback(0,ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myAdapter.removeItem(viewHolder.adapterPosition, myDbManager)

            }
        })
    }


}