package com.example.gallery

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.gallery_cell.view.*

class GalleryAdapter : ListAdapter<PhotoItem, MyViewholder>(DIFFCALLBACK){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        val holder = MyViewholder(LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell,parent,false));
        holder.itemView.setOnClickListener{
            Bundle().apply {
                putParcelable("PHOTO",getItem(holder.adapterPosition))
                holder.itemView.findNavController().navigate(R.id.action_galleryFragment_to_photoFragment,this)
            }

        }
        return holder
    }

    override fun onBindViewHolder(holder:MyViewholder, position: Int) {
        holder.itemView.shimmerGalleryCell.apply {
            setShimmerColor(0x55FFFFF)
            setShimmerAngle(0)
            startShimmerAnimation()
        }
        Glide.with(holder.itemView)
            .load(getItem(position).previewUrl)
            .placeholder(R.drawable.ic_baseline_photo_24)
            .listener(object: RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false.also { holder.itemView.shimmerGalleryCell?.stopShimmerAnimation() }
                }

            })
            .into(holder.itemView.imageView)
    }

    object DIFFCALLBACK : DiffUtil.ItemCallback<PhotoItem>(){
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

    }

}

class MyViewholder(itemView: View) : RecyclerView.ViewHolder(itemView){

}