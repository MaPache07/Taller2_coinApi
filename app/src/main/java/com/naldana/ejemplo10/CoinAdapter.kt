package com.naldana.ejemplo10

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.naldana.ejemplo10.models.Coin
import kotlinx.android.synthetic.main.list_item_coin.view.*

class CoinAdapter(val coins:List<Coin>) : RecyclerView.Adapter<CoinAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CoinAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_coin, null, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = coins.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(coins[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(item: Coin) {
            var uri = Uri.parse(item.img)
            with(itemView){
                with(itemView){
                    Glide.with(itemView).load(uri).into(img_coin)
                }
            }
        }
    }
}