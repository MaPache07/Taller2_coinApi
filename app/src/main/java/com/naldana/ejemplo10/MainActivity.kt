package com.naldana.ejemplo10

import android.content.ContentValues
import android.content.Intent
import android.database.DatabaseUtils
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
import com.google.gson.Gson
import com.naldana.ejemplo10.data.Database
import com.naldana.ejemplo10.data.DatabaseContract
import com.naldana.ejemplo10.models.Coin
import com.naldana.ejemplo10.models.CoinList
import com.naldana.ejemplo10.utilities.AppConstants
import com.naldana.ejemplo10.utilities.NetworkUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var twoPane =  false
    val coinLink:  String = "https://apicoins.herokuapp.com/coin"
    lateinit var coinList : CoinList
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
        var coinAdapter = CoinAdapter(coinList.coins, {coin: Coin -> clickedCoin(coin)})
         recyclerview.apply {
             adapter = coinAdapter
             layoutManager = viewManager
         }
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
            val db = dbHelper.writableDatabase
            var coinApi = NetworkUtil().buildUrl(coinLink)
            var listCoin = NetworkUtil().getResponseFromHttpUrl(coinApi)
            coinList = Gson().fromJson(listCoin, CoinList::class.java)
            if(DatabaseUtils.queryNumEntries(db, "coin").toInt() != coinList.coins.size){
                db.delete("coin", null, null)
                for(coin: Coin in coinList.coins){
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
                }
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            initRecycler()
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
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
