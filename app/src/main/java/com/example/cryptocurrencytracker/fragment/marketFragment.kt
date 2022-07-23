package com.example.cryptocurrencytracker.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.cryptocurrencytracker.adapter.MarketAdapter
import com.example.cryptocurrencytracker.apis.ApiInterface
import com.example.cryptocurrencytracker.apis.ApiUtilities
import com.example.cryptocurrencytracker.databinding.FragmentMarketBinding
import com.example.cryptocurrencytracker.model.CryptoCurrency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class marketFragment : Fragment() {

    lateinit var binding: FragmentMarketBinding

    private lateinit var list: List<CryptoCurrency>
    private lateinit var adapter: MarketAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentMarketBinding.inflate(layoutInflater)

        list = listOf()
        adapter = MarketAdapter(requireContext(), list, "market")
        binding.currencyRecyclerView.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO){
            val res = ApiUtilities.getInstance().create(ApiInterface::class.java).getMarketData()

            if(res.body() != null){
                withContext(Dispatchers.Main){
                    list = res.body()!!.data.cryptoCurrencyList

                    adapter.updateData(list)
                    binding.spinKitView.visibility = GONE
                }
            }
        }

        searchCoin()

        return binding.root
    }

    lateinit var seachText : String

    private fun searchCoin() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                seachText = p0.toString().toLowerCase()

                updateRecyclerView()
            }

            private fun updateRecyclerView() {
                var data = ArrayList<CryptoCurrency>()

                for(item in list){
                    val coinName = item.name.lowercase(Locale.getDefault())
                    val coinSymbol = item.symbol.lowercase(Locale.getDefault())

                    if(coinName.contains(seachText) || coinSymbol.contains(seachText)){
                        data.add(item)
                    }
                }

                adapter.updateData(data)
            }
        })
    }

}