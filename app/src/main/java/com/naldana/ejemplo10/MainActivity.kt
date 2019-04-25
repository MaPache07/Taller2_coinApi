package com.naldana.ejemplo10

import android.content.ContentValues
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.naldana.ejemplo10.data.Database
import com.naldana.ejemplo10.data.DatabaseContract
import com.naldana.ejemplo10.models.Coin
import com.naldana.ejemplo10.utilities.AppConstants
import com.naldana.ejemplo10.utilities.NetworkUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var twoPane =  false
    val coinLink:  String = "https://apicoins.herokuapp.com/coin"
    var cList: ArrayList<Coin> = ArrayList()
    var cListMap: MutableMap<String, Coin> = HashMap()
    var dbHelper = Database(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FetchCoinTask().execute()
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        if (fragment_content != null ){
            twoPane =  true
        }
    }

    fun initRecycler(){
        var viewManager = LinearLayoutManager(this)
        if(this.resources.configuration.orientation == 2 || this.resources.configuration.orientation == 4){
            viewManager = LinearLayoutManager(this)
        }
        else{
            viewManager = GridLayoutManager(this, 2)
        }
        var coinAdapter = CoinAdapter(cList, {coin: Coin -> clickedCoin(coin)})
         recyclerview.apply {
             adapter = coinAdapter
             layoutManager = viewManager
         }
    }

    fun inToBase(){
        val db = dbHelper.writableDatabase
        for(coin: Coin in cList){
            val values = ContentValues().apply {
                put(DatabaseContract.CoinEntry.COLUMN_NAME, coin.name)
                put(DatabaseContract.CoinEntry.COLUMN_COUNTRY, coin.country)
                put(DatabaseContract.CoinEntry.COLUMN_VALUE, coin.value)
                put(DatabaseContract.CoinEntry.COLUMN_VALUE_US, coin.values_us)
                put(DatabaseContract.CoinEntry.COLUMN_YEAR, coin.year)
                put(DatabaseContract.CoinEntry.COLUMN_VALUE, coin.value)
                put(DatabaseContract.CoinEntry.COLUMN_ISAVAILABLE, coin.isAvailable)
                put(DatabaseContract.CoinEntry.COLUMN_IMG, coin.img)
            }

            val newRowId = db?.insert(DatabaseContract.CoinEntry.TABLE_NAME, null, values)

            if (newRowId == -1L) {
                Snackbar.make(it, getString(R.string.alert_person_not_saved), Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(it, getString(R.string.alert_person_saved_success) + newRowId, Snackbar.LENGTH_SHORT)
                    .show()
                mAdapter.setPersonas(readPersonas())
            }
        }
        initRecycler()
    }

    fun clickedCoin(coin: Coin){
        var bundle = Bundle()
        bundle.putString(AppConstants.NAME_KEY, coin.name)
        bundle.putString(AppConstants.COUNTRY_KEY, coin.country)
        bundle.putString(AppConstants.VALUE_KEY, coin.value.toString())
        bundle.putString(AppConstants.VALUE_US_KEY, coin.values_us.toString())
        bundle.putString(AppConstants.YEAR_KEY, coin.year.toString())
        bundle.putString(AppConstants.IS_AVAILABLE_KEY, coin.isAvailable.toString())
        bundle.putString(AppConstants.IMG_KEY, coin.img)
        var  mIntent = Intent(this, CoinActivity::class.java)
        mIntent.putExtras(bundle)
        startActivity(mIntent)
    }

    inner class FetchCoinTask : AsyncTask<String,Void,String>(){
        override fun doInBackground(vararg params: String?): String? {
            var coinApi = NetworkUtil().buildUrl(coinLink)
            var coinParser = JsonParser()
            var coinJson = NetworkUtil().getResponseFromHttpUrl(coinApi)
            coinJson = "[" + coinJson + "]"
            var theCoin: Coin
            var listCoinArray = coinParser.parse(coinJson).asJsonArray
            for(listElement: JsonElement in listCoinArray){
                var coinObject = listElement.asJsonObject
                var coinArray = coinObject.getAsJsonArray("coins")
                for(coinElement: JsonElement in coinArray){
                    theCoin = Coin(coinElement.asJsonObject.get("_id").asString,
                        coinElement.asJsonObject.get("name").asString,
                        coinElement.asJsonObject.get("country").asString,
                        coinElement.asJsonObject.get("value").asInt,
                        coinElement.asJsonObject.get("value_us").asDouble,
                        coinElement.asJsonObject.get("year").asInt,
                        coinElement.asJsonObject.get("isAvailable").asBoolean,
                        coinElement.asJsonObject.get("img").asString)
                    cList.add(theCoin)
                    cListMap.put(coinElement.asJsonObject.get("_id").asString, theCoin)
                }
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            inToBase()
        }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {

            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
