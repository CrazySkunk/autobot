package com.example.autobot1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.autobot1.R
import com.example.autobot1.databinding.MechItemBinding
import com.example.autobot1.models.User
import com.squareup.picasso.Picasso

class MechanicAdapter(private val mechanics:List<User>) : RecyclerView.Adapter<MechanicAdapter.MechanicViewHolder>() {

    private lateinit var listener:OnItemClick

    interface OnItemClick{
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MechanicViewHolder {
        return MechanicViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.mech_item,
                parent,
                false),
            listener
        )
    }

    fun setOnItemClickListener(listener: OnItemClick){
        this.listener=listener
    }

    override fun onBindViewHolder(holder: MechanicViewHolder, position: Int) {
       holder.bind(mechanics[position])
    }

    override fun getItemCount()=mechanics.size

    class MechanicViewHolder(itemView: View, listener: OnItemClick) :
        RecyclerView.ViewHolder(itemView) {
        val binding = MechItemBinding.bind(itemView)
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    listener.onItemClick(position)
                }
            }
        }
        fun bind(user: User){
            binding.textView.text=user.name
            Picasso.get().load(user.imageUrl).into(binding.imageView3)
        }
    }
}