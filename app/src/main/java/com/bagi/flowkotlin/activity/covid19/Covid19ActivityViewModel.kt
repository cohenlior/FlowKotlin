package com.bagi.flowkotlin.activity.covid19

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.bagi.flowkotlin.R
import com.bagi.flowkotlin.model.Card
import com.bagi.flowkotlin.model.CountryDetails
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class Covid19ActivityViewModel : ViewModel() {
    private val _countryDetailsMLD = MutableLiveData<MutableList<Card>>()
    private val _progressDataMLD = MutableLiveData<Boolean>()
    private val _updatedMLd = MutableLiveData<String>()

    fun getCountryDetailsMLD(): LiveData<MutableList<Card>> = _countryDetailsMLD

    fun getProgressDataMLD(): LiveData<Boolean> = _progressDataMLD
    fun getUpdatedMLd(): LiveData<String> = _updatedMLd

    init {
        getCountryData()
    }

    private fun getCountryData() {
        viewModelScope.launch {
            val countryDetails = Covid19ActivityModel.getCountryDetails()
                    .onStart { 
                        _progressDataMLD.value = true
                    _updatedMLd.value = "waiting..."}
                    .onCompletion { _progressDataMLD.value = false }
                    .catch { Log.e("${Covid19ActivityViewModel::class.java.name}: getCountryData()",
                        "Error fetch data") }
                    .singleOrNull()
            _countryDetailsMLD.value = getListFRomObject(countryDetails!!)
            _updatedMLd.value = convertLongToTime(countryDetails.updated)
        }
    }

    private fun convertLongToTime(time: Long?): String {
        time?.let {
            val sdf = SimpleDateFormat("dd.MM.yy HH:mm", Locale.US)
            return sdf.format(Date(time))
        }
        return ""
    }

    private fun getListFRomObject(countryDetails: CountryDetails): MutableList<Card> {
        val listDetails = mutableListOf<Card>()
        listDetails.apply {
            add(Card(R.string.cases, countryDetails.cases, R.color.red))
            add(Card(R.string.active, countryDetails.active,null))
            add(Card(R.string.critical, countryDetails.critical,null))
            add(Card(R.string.recovered, countryDetails.recovered,null))
            add(Card(R.string.tests, countryDetails.tests,null))
            add(Card(R.string.today_deaths, countryDetails.todayDeaths,null))
            add(Card(R.string.deaths, countryDetails.deaths, R.color.green))
        }
        return listDetails
    }
}