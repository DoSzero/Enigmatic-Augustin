package com.rubygames.assass.logic.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rubygames.assass.R
import com.rubygames.assass.logic.model.Card
import kotlinx.android.synthetic.main.card_layout.view.*

import java.util.*

class DeckAdapter(

    private val items: ArrayList<Card>,
    private val context: Context,

    private val listener: (Int) -> Unit) : RecyclerView.Adapter<DeckAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Card, pos: Int, listener: (Int) -> Unit) = with(itemView) {
            setOnClickListener {
                listener(pos)
            }

            if(!item.isVisible) {
                card_image.setBackgroundColor(Color.parseColor("#ECDFBD"))
                card_image.setImageResource(R.drawable.ic_help)
            } else {
                card_image.setImageResource(item.getImage())
            }

            if(item.isMatched) {
                card_image.visibility = View.INVISIBLE
                card_image.isEnabled = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_layout,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position],position,listener)

    override fun getItemCount(): Int {
        return items.size
    }
}