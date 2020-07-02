package com.example.gallery

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

class GalleryViewModel(application: Application) : AndroidViewModel(application){
    /*封裝viewModel的數據*/
     private val _photoListLive = MutableLiveData<List<PhotoItem>>()   //內部數據，可更改(mutable)
     val photoListLive: LiveData<List<PhotoItem>> get() = _photoListLive //對外暴露的數據，只供讀取

     fun fetchData(){
         val stringRequest = StringRequest(
             /*四個參數分別為: 網路方法,網址,成功響應監聽器,失敗響應監聽器*/
             Request.Method.GET,
             getUrl(),
             Response.Listener {
                 _photoListLive.value =Gson().fromJson(it,Pixabay::class.java).hits.toList()
                 /*用Gson解析回傳的字串後建立Pixabay實例(此處取得hits內的array後轉化成List)，同時賦值給MutableLiveData*/
             },
             Response.ErrorListener {
                Log.e("error",it.toString())
             }
         )
         VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest);
     }
     private fun getUrl():String  {
        return "https://pixabay.com/api/?key=17285261-0fd819a271895461fea665d25&q=${keyWords.random()}&per_page=100"
     }
     private val keyWords = arrayOf("cat","dog","car","beauty","phone","computer","flower","animal")


}