package com.example.pagerGallery

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.gallery_cell.view.*
import kotlinx.android.synthetic.main.gallery_footer.view.*

class GalleryAdapter(val galleryViewModel: GalleryViewModel) : ListAdapter<PhotoItem, MyViewholder>(DIFFCALLBACK) {

    companion object{
        const val NORMAL_VIEW_TYPE = 0;
        const val FOOTER_VIEW_TYPE = 1;
    }

    var footerViewStatus = DATA_STATUS_CAN_LOAD_MORE; //接收Fragment傳進來的狀態

    override fun getItemCount(): Int {
        return super.getItemCount() +1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == itemCount -1) FOOTER_VIEW_TYPE else NORMAL_VIEW_TYPE //回傳一個類型，並於onCreateViewHolder做判斷
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        val holder : MyViewholder
        if(viewType == NORMAL_VIEW_TYPE){
            holder =MyViewholder(LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell, parent, false))
            holder.itemView.setOnClickListener {
                Bundle().apply {
                    putParcelableArrayList("PhotoList", ArrayList(currentList))
                    putInt("PhotoPosition", holder.bindingAdapterPosition)
                    holder.itemView.findNavController().navigate(
                        R.id.action_galleryFragment_to_pagerPhotoFragment,
                        this
                    )//別忘了導航攜帶Bundle
                }
            }
        }else{
            holder = MyViewholder(LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_footer, parent, false).also { it
                    (it.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
                    //拓展了整個viewholder的寬度
                    it.setOnClickListener { itemView ->
                        itemView.progressBar.visibility = View.VISIBLE
                        itemView.textView.text = "正在重新加載"
                        galleryViewModel.fetchData()
                    }
                }
            )
        }
        return holder
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        if(position == itemCount -1){
            with(holder.itemView){
                when(footerViewStatus){
                    DATA_STATUS_CAN_LOAD_MORE ->{
                        progressBar.visibility = View.VISIBLE
                        textView.text = "正在載入"
                        this.isClickable = false
                    }
                    DATA_STATUS_NO_MORE ->{ /*INVISIBLE时，界面保留了view控件所占有的空間；用GONE則不保留*/
                        progressBar.visibility = View.GONE
                        textView.text = "全部載入完畢"
                        this.isClickable = false
                    }
                    DATA_STATUS_NETWORK_ERROR ->{
                        progressBar.visibility = View.GONE
                        textView.text = "網路故障，點擊以重試"
                        this.isClickable = true
                    }
                }
            }
            return //跳過下面的處理
        }
        //擷取單元數據導入元件
        val photoItem: PhotoItem = getItem(position)
        with(holder.itemView) {
            shimmerGalleryCell.apply {
                setShimmerColor(0x55FFFFF)
                setShimmerAngle(0)
                startShimmerAnimation()
            }
            tv_user.text = photoItem.photoUser
            tv_favorites.text = photoItem.photoFavorites.toString()
            tv_likes.text = photoItem.photoLikes.toString()
            imageView.layoutParams.height = photoItem.photoHeight
        }
        //加載圖片
        Glide.with(holder.itemView)
            .load(getItem(position).previewUrl)
            .placeholder(R.drawable.photoplacerholder)
            .listener(object : RequestListener<Drawable> {
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
}

object DIFFCALLBACK : DiffUtil.ItemCallback<PhotoItem>() {
    override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
        return oldItem.photoId == newItem.photoId
    }

}



class MyViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}