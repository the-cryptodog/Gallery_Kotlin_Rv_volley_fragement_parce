package com.example.pagerGallery

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlin.math.ceil

const val DATA_STATUS_CAN_LOAD_MORE = 0
const val DATA_STATUS_NO_MORE =1
const val DATA_STATUS_NETWORK_ERROR =2


class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    /*封裝viewModel的數據*/

    private val _dataStatusLive = MutableLiveData<Int>()//對內的狀態數據
    val dataStatusLive : LiveData<Int> get() = _dataStatusLive//對內的狀態數據
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()   //內部數據，可更改(mutable)
    val photoListLive: LiveData<List<PhotoItem>> get() = _photoListLive //對外暴露的數據，只供讀取
    var needToScrollToTop = true

    private var perPage = 50
    private val keyWords =
        arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal")
    private var currentPage = 1
    private var totalPage = 1
    private var currentKey = ""
    private var isNewQuery = true
    private var isLoading = false

    init {
        resetQuery()
    }

    fun resetQuery() {
        currentPage = 1
        totalPage = 1
        currentKey = keyWords.random()
        isNewQuery = true
        needToScrollToTop = true
        fetchData()
    }

    fun fetchData() {
        if (isLoading) return
        if (currentPage > totalPage) {
            _dataStatusLive.value = DATA_STATUS_NO_MORE
            return
        }
        isLoading = true
        val stringRequest = StringRequest(
            /*四個參數分別為: 網路方法,網址,成功響應監聽器,失敗響應監聽器*/
            Request.Method.GET,
            getUrl(),
            Response.Listener {
                with(Gson().fromJson(it, Pixabay::class.java)) {
                    totalPage = ceil(totalHits.toDouble() / perPage).toInt()
                    if (isNewQuery) {
                        _photoListLive.value = hits.toList()
                    }else{
                        _photoListLive.value = arrayListOf(_photoListLive.value!!,hits.toList()).flatten()
                    }
                }
                _dataStatusLive.value = DATA_STATUS_CAN_LOAD_MORE
                isLoading = false
                isNewQuery = false
                currentPage++
                /*用Gson解析回傳的字串後建立Pixabay實例(此處取得hits內的array後轉化成List)，同時賦值給MutableLiveData*/
            },
            Response.ErrorListener {
                Log.e("error", it.toString())
                isLoading = false
                _dataStatusLive.value = DATA_STATUS_NETWORK_ERROR
            }
        )
        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest);
    }

    private fun getUrl(): String {
        return "https://pixabay.com/api/?key=17285261-0fd819a271895461fea665d25&q=" +
                "${currentKey}&per_page=${perPage}&page=${currentPage}"
    }

}