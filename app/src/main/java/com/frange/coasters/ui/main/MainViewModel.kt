package com.frange.coasters.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Coaster
import com.frange.coasters.domain.model.Company
import com.frange.coasters.domain.model.Park
import com.frange.coasters.domain.usecase.RequestCoasterUseCase
import com.frange.coasters.domain.usecase.RequestCompanyListUseCase
import com.frange.coasters.domain.usecase.RequestParkListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val requestCompanyListUseCase: RequestCompanyListUseCase,
    private val requestCoasterUseCase: RequestCoasterUseCase,
    private val requestParkListUseCase: RequestParkListUseCase
) : ViewModel() {

    private val companyList = MutableLiveData<AppResult<List<Company>>>()
    private val parkList = MutableLiveData<AppResult<List<Park>>>()
    private val coaster = MutableLiveData<AppResult<Coaster>>()

    fun requestCompanyList() {
        viewModelScope.launch {
            requestCompanyListUseCase.execute()
                .catch {
                    val exception = it
                    Log.v("Exception", "Exception", exception)
                }.collect {
                    companyList.postValue(it)
                }
        }
    }

    fun requestParkList(id: Int) {
        viewModelScope.launch {
            requestParkListUseCase.execute(RequestParkListUseCase.Parameters(id))
                .catch {
                    val exception = it
                    Log.v("Exception", "Exception", exception)
                }.collect {
                    parkList.postValue(it)
                }
        }
    }

    fun requestCoaster(id: Int, sortedByTime: Boolean) {
        viewModelScope.launch {
            requestCoasterUseCase.execute(RequestCoasterUseCase.Parameters(id, sortedByTime))
                .catch {
                    val exception = it
                    Log.v("Exception", "Exception", exception)
                }.collect {
                    coaster.postValue(it)
                }
        }
    }

    fun getCompanyList(): MutableLiveData<AppResult<List<Company>>> {
        return companyList
    }

    fun getParkList(): MutableLiveData<AppResult<List<Park>>> {
        return parkList
    }

    fun getCoaster(): MutableLiveData<AppResult<Coaster>> {
        return coaster
    }

}
