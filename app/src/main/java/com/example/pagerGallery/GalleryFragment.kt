package com.example.pagerGallery

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery.*


class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel

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
        inflater.inflate(R.menu.menu, menu)
    }

    //初始化menu點擊事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.swipeIndicator -> {
                swipeRefreshLayout.isRefreshing = true
                Handler().postDelayed(Runnable { galleryViewModel.resetQuery() }, 1000) //延遲執行函式

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //賦予menu
        setHasOptionsMenu(true)
        //創建ViewModel
        galleryViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(GalleryViewModel::class.java)//綁定viewModel

        //給予ADAPTER變數
        val galleryAdapter = GalleryAdapter(galleryViewModel)

        //初始化recyclerView
        recyclerView.apply {
            adapter = galleryAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        galleryViewModel.photoListLive.observe(this, Observer {
            if(galleryViewModel.needToScrollToTop){
                recyclerView.scrollToPosition(0)
                galleryViewModel.needToScrollToTop =false
            }
            galleryAdapter.submitList(it)
            swipeRefreshLayout.isRefreshing = false
        })//設定觀察.

        galleryViewModel.dataStatusLive.observe(this, Observer {
            galleryAdapter.footerViewStatus = it //將狀態傳給ADAPTER
            galleryAdapter.notifyItemChanged(galleryAdapter.itemCount-1)
            if(it == DATA_STATUS_NETWORK_ERROR) swipeRefreshLayout.isRefreshing = false
        })



        swipeRefreshLayout.setOnRefreshListener {
            galleryViewModel.resetQuery() //內建swipe更新時(頂部下刷更新)，透過viewModel的resetQuery()重新獲取數據

        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d("myTag", "onScrolled : " + dy)

                if (dy < 0) return //畫面如向上跑，則return不進行更新，反之向下刷則進入底部判斷

                val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                val intArray = IntArray(2)
                layoutManager.findLastVisibleItemPositions(intArray) //回穿一個int陣列，位置0跟位置1代表最底下的是cell是第幾個(序號)
                if (intArray[0] == galleryAdapter.itemCount - 1) {
                    /*判斷回傳來的序號是否為整筆資料數的最後一個數* 因為我們已經拓展了整個cell成一整列寬，所以findLastVisibleItemPositions
                    所返回的intArray[1]跟[0]的cell序號都可以用來判斷(都是最後一個cell)*/
                    galleryViewModel.fetchData() //加追數據而非重新獲取數據
                }

            }
        })
    }
}