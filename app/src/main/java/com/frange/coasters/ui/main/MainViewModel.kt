package com.frange.coasters.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Park
import com.frange.coasters.domain.model.Company
import com.frange.coasters.domain.model.ParkInfo
import com.frange.coasters.domain.usecase.RequestAllParkInfoListUseCase
import com.frange.coasters.domain.usecase.RequestParkUseCase
import com.frange.coasters.domain.usecase.RequestParkInfoListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val requestCompanyListUseCase: RequestAllParkInfoListUseCase,
    private val requestParkUseCase: RequestParkUseCase,
) : ViewModel() {

    private val companyList = MutableLiveData<AppResult<List<Company>>>()
    private val parkInfoInfoList = MutableLiveData<AppResult<List<ParkInfo>>>()
    private val parkList = MutableLiveData<AppResult<Park>>()

    fun requestAllParkInfoList() {
        viewModelScope.launch {
            requestCompanyListUseCase.execute()
                .catch {
                    val exception = it
                    Log.v("Exception", "Exception", exception)
                }.collect {
                    parkInfoInfoList.postValue(it)
                }
        }
    }

    fun requestPark(id: Int, sortedByTime: Boolean) {
        viewModelScope.launch {
            requestParkUseCase.execute(RequestParkUseCase.Parameters(id, sortedByTime))
                .catch {
                    val exception = it
                    Log.v("Exception", "Exception", exception)
                }.collect {
                    parkList.postValue(it)
                }
        }
    }

    fun getCompanyList(): MutableLiveData<AppResult<List<Company>>> {
        return companyList
    }

    fun getParkList(): MutableLiveData<AppResult<List<ParkInfo>>> {
        return parkInfoInfoList
    }

    fun getPark(): MutableLiveData<AppResult<Park>> {
        return parkList
    }

}
