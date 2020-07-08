package com.example.pagerGallery

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.contentValuesOf
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import kotlinx.android.synthetic.main.fragment_pager_photo.*
import kotlinx.android.synthetic.main.pager_photo_view.view.*
import java.util.jar.Manifest


const val REQUEST_WRITE_EXTERNAL_STORAGE = 1

class PagerPhotoFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pager_photo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val photoList: ArrayList<PhotoItem>? =
            arguments?.getParcelableArrayList<PhotoItem>("PhotoList")
        PagerPhotoListAdapter().apply {
            viewPager2.adapter = this
            submitList(photoList)
        }

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photoTag.text = getString(R.string.photo_tag, position + 1, photoList?.size)
            }
        })

        viewPager2.setCurrentItem(arguments?.getInt("PhotoPosition") ?: 0, false)//初始化 直接跳到選中的圖片

        //共享區間權限在API_29之後默認OK，因此要檢查版本是否小於29
        btn_download.setOnClickListener {
            if (Build.VERSION.SDK_INT < 29 &&
                //ContextCompat是什麼?
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_EXTERNAL_STORAGE)
                //這裡可以自訂requestCode在第二個參數，此處利用常數REQUEST_WRITE_EXTERNAL_STORAGE
            } else {
                savePhoto()
            }
        }
    }
    //回應權限請求後的的回調函數
    override fun onRequestPermissionsResult(
        requestCode: Int, //權限請求碼(requestCode)
        permissions: Array<out String>,//權限類型陣列
        grantResults: IntArray //授權碼佇列
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //依照權限請求碼_來分類處理
        when(requestCode){
            REQUEST_WRITE_EXTERNAL_STORAGE->{
                //授權碼不為空且，第一項為PERMISSION_GRANTED時，直接執行
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    savePhoto()
                }else{
                    Toast.makeText(requireContext(),"儲存失敗",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun savePhoto() {
        val holder =
            (viewPager2[0] as RecyclerView).findViewHolderForAdapterPosition(viewPager2.currentItem) as PagerPhotoViewHolder
        val bitmap : Bitmap = holder.itemView.pager_photo.drawable.toBitmap()
        val saveUri : Uri? = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ,ContentValues())?: kotlin.run { //判空
            Toast.makeText(requireContext(),"儲存失敗",Toast.LENGTH_SHORT).show()
            return//不需要執行下去
        }



    }


}