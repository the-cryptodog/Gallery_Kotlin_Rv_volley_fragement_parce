package com.example.pagerGallery

import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery.*


class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel : GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }
    //初始化menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }
    //初始化menu點擊事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.swipeIndicator -> {
                swipeRefreshLayout.isRefreshing = true
                Handler().postDelayed(Runnable { galleryViewModel.fetchData() },1000) //延遲執行函式

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //賦予menu
        setHasOptionsMenu(true)
        //給予ADAPTER變數
        val galleryAdapter = GalleryAdapter()

        //初始化recyclerView
        recyclerView.apply {
            adapter = galleryAdapter
            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        }

        galleryViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(GalleryViewModel::class.java)//綁定viewModel

        galleryViewModel.photoListLive.observe(this, Observer {
            galleryAdapter.submitList(it)
            swipeRefreshLayout.isRefreshing =false
        })//設定觀察

        galleryViewModel.photoListLive.value?:galleryViewModel.fetchData() //fragment開啟時的資料空檢查

        swipeRefreshLayout.setOnRefreshListener {
            galleryViewModel.fetchData()

        }
    }


}