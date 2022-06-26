package com.azzunyan.note.db

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.azzunyan.note.EditActivity
import com.azzunyan.note.R

class MyAdapter( listMain:ArrayList<ListItem>, contextM: Context) : RecyclerView.Adapter<MyAdapter.MyHolder>() {
    private var listArray = listMain
    private var context = contextM


    class MyHolder(itemView: View, contextV:Context) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle:TextView = itemView.findViewById(R.id.tvTitle)
        private val tvTime:TextView = itemView.findViewById(R.id.tvTime)
        private val context = contextV

        fun setData(item:ListItem){

            tvTitle.text = item.title
            tvTime.text = item.time
            itemView.setOnClickListener {

                val intent = Intent(context,EditActivity::class.java).apply {

                    putExtra(MyIntentConstants.I_TITLE_KEY, item.title)
                    putExtra(MyIntentConstants.I_DESC_KEY, item.desc)
                    putExtra(MyIntentConstants.I_URI_KEY, item.uri)
                    putExtra(MyIntentConstants.I_ID_KEY, item.id)

                }
                context.startActivity(intent)

            }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyHolder(inflater.inflate(R.layout.rc_item, parent, false), context)
    }

    override fun getItemCount(): Int {

        return listArray.size

    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        holder.setData(listArray[position])
    }

    fun updateAdapter(listItems:List<ListItem>){

        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()

    }

    fun removeItem(pos:Int, dbManager: MyDbManager){

        dbManager.removeItemFromDb(listArray[pos].id.toString())
        listArray.removeAt(pos)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(pos)


    }
}