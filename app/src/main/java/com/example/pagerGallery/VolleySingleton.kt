package com.example.pagerGallery

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleySingleton private constructor(context :Context){

    //靜態的field
    companion object{
        private var INSTANCE : VolleySingleton?=null

        /*Kotlin提供一種叫做evlis的語法，可以簡單地處理變數為空的情形。
        val title: String? = "Human Resource"
        val titleLength: Int = title?.length ?: 0
        使用「?:」語法，可以檢查左邊的值，如果不是null，就用它來賦予變數值，否則就採用右邊的值。*/

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this){
            VolleySingleton(context).also { this.INSTANCE=it }
        }
    }
    /*lazy 的使用
lazy也是kotlin中常用的一種延遲載入方式，使用方法如下：
val a2:String by lazy{
    println("開始初始化")
    // 初始化的值
    "sss"
}
使用時，在型別後面加by lazy{}即可，{}中的最後一行程式碼，需要返回初始化的結果，上述程式碼中，"sss"即為最後初始化的值。下面是lazy的一些注意點：
lazy只能對常量val使用，不能修飾變數var
lazy的載入時機為第一次呼叫常量的時候，且只會載入一次（畢竟是個常量，只能賦值一次）*/

    val requestQueue : RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext) } //引用applicationContext(生命週期跟App同步)且全局唯一
}