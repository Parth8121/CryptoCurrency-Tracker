package com.example.cryptocurrencytracker.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.cryptocurrencytracker.R
import com.example.cryptocurrencytracker.databinding.FragmentDetailsBinding
import com.example.cryptocurrencytracker.model.CryptoCurrency
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.reflect.KProperty


class DetailsFragment : Fragment() {

    lateinit var binding: FragmentDetailsBinding
    private var item : DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailsBinding.inflate(layoutInflater)

        var data : CryptoCurrency = item.data!!

        setupDetails(data)

        loadChart(data)

        SetButtonOnCLick(data)

        addToWatchList(data)

        return binding.root
    }

    var watchList : ArrayList<String>? = null
    var watchListIsChecked = false

    private fun addToWatchList(data: CryptoCurrency) {
        readData()

        watchListIsChecked = if(watchList!!.contains(data.symbol)){
            binding.addWatchlistButton.setImageResource(R.drawable.ic_star_enabled_24dp)
            true
        }else{
            binding.addWatchlistButton.setImageResource(R.drawable.ic_star_disabled_24dp)
            false
        }

        binding.addWatchlistButton.setOnClickListener{
            watchListIsChecked =
                if (!watchListIsChecked){
                    if (!watchList!!.contains(data.symbol)){
                        watchList!!.add(data.symbol)
                    }
                    storeData()
                    binding.addWatchlistButton.setImageResource(R.drawable.ic_star_enabled_24dp)
                    true
                }
                else{

                    watchList!!.remove(data.symbol)
                    storeData()
                    binding.addWatchlistButton.setImageResource(R.drawable.ic_star_disabled_24dp)
                    false
                }
        }
    }

    private fun storeData(){
        val sharedPreferences = requireContext().getSharedPreferences("watchList", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(watchList)
        editor.putString("watchList",json)
        editor.apply()
    }


    private fun readData() {
        var sharedPreferences = requireContext().getSharedPreferences("watchList", Context.MODE_PRIVATE)
        var gson = Gson()
        var json = sharedPreferences.getString("watchList", ArrayList<String>().toString())
        var type = object : TypeToken<ArrayList<String>>(){}.type
        watchList = gson.fromJson(json, type)

    }

    private fun SetButtonOnCLick(item: CryptoCurrency) {

        val oneMonth = binding.button
        val oneWeek = binding.button1
        val oneDay = binding.button2
        val fourHour = binding.button3
        val onehour = binding.button4
        val fifteenMinute = binding.button5

        val clickListener = View.OnClickListener {
            when(it.id){
                fifteenMinute.id -> loadChartData(it, "15", item, oneDay, oneMonth, oneWeek, onehour, fourHour)
                onehour.id -> loadChartData(it, "1H", item, oneDay, oneMonth, oneWeek, fifteenMinute, fourHour)
                fourHour.id -> loadChartData(it, "4H", item, oneDay, oneMonth, oneWeek, onehour, fifteenMinute)
                oneDay.id -> loadChartData(it, "1D", item, fifteenMinute, oneMonth, oneWeek, onehour, fourHour)
                oneWeek.id -> loadChartData(it, "1W", item, oneDay, oneMonth, fifteenMinute, onehour, fourHour)
                oneMonth.id -> loadChartData(it, "1M", item, oneDay, fifteenMinute, oneWeek, onehour, fourHour)

            }
        }

        fifteenMinute.setOnClickListener(clickListener)
        oneDay.setOnClickListener(clickListener)
        onehour.setOnClickListener(clickListener)
        fourHour.setOnClickListener(clickListener)
        oneWeek.setOnClickListener(clickListener)
        oneMonth.setOnClickListener(clickListener)

    }

    private fun loadChartData(
        it: View?,
        s: String,
        item: CryptoCurrency,
        oneDay: AppCompatButton,
        oneMonth: AppCompatButton,
        oneWeek: AppCompatButton,
        onehour: AppCompatButton,
        fourHour: AppCompatButton
    ) {

        disableButton(onehour,fourHour,oneDay,oneWeek,oneMonth)
        it!!.setBackgroundResource(R.drawable.active_button )
        binding.detaillChartWebView.settings.javaScriptEnabled = true
        binding.detaillChartWebView.setLayerType(View.LAYER_TYPE_SOFTWARE,null)

        binding.detaillChartWebView.loadUrl(

            "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + item.symbol
                .toString() + "USD&interval=" + s + "&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=" +
                    "F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides=" +
                    "{}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
        )

    }

    private fun disableButton(onehour: AppCompatButton, fourHour: AppCompatButton, oneDay: AppCompatButton, oneWeek: AppCompatButton, oneMonth: AppCompatButton) {
        oneDay.background = null
        onehour.background = null
        oneMonth.background = null
        oneWeek.background = null
        fourHour.background = null
    }

    private fun loadChart(data: CryptoCurrency) {

        binding.detaillChartWebView.settings.javaScriptEnabled = true
        binding.detaillChartWebView.setLayerType(View.LAYER_TYPE_SOFTWARE,null)

        binding.detaillChartWebView.loadUrl(

            "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + data.symbol
                .toString() + "USD&interval=D&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=" +
                    "F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides=" +
                    "{}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
        )
    }

    private fun setupDetails(data: CryptoCurrency) {
        binding.detailSymbolTextView.text = data.symbol

        Glide.with(requireContext()).load(
            "https://s2.coinmarketcap.com/static/img/coins/64x64/" + data.id + ".png"
        ).thumbnail(Glide.with(requireContext()).load(R.drawable.spinner))
            .into(binding.detailImageView)

        binding.detailPriceTextView.text = "${String.format("$%.02f", data.quotes[0].price)}"

        if (data.quotes!![0].percentChange24h > 0) {

            binding.detailChangeImageView.setImageResource(R.drawable.ic_caret_up)
            binding.detailChangeTextView.setTextColor(requireContext().resources.getColor(R.color.green))
            binding.detailChangeTextView.text = "+ ${String.format("%.02f", data.quotes[0].percentChange24h)}%"
        } else {

            binding.detailChangeImageView.setImageResource(R.drawable.ic_caret_down)
            binding.detailChangeTextView.setTextColor(requireContext().resources.getColor(R.color.red))
            binding.detailChangeTextView.text = "${String.format("%.02f", data.quotes[0].percentChange24h)}%"
        }


    }

}

private operator fun Any.setValue(detailsFragment: DetailsFragment, property: KProperty<*>, detailsFragmentArgs: DetailsFragmentArgs) {
    return
}

