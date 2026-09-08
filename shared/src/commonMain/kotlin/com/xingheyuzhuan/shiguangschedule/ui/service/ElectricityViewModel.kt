package com.xingheyuzhuan.shiguangschedule.ui.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xingheyuzhuan.shiguangschedule.data.api.electricity.ElectricityLocation
import com.xingheyuzhuan.shiguangschedule.data.repository.ElectricityConfig
import com.xingheyuzhuan.shiguangschedule.data.repository.ElectricityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.max
import org.koin.core.annotation.KoinViewModel

data class ElectricityUiState(val isConfigured:Boolean=false,val isLoading:Boolean=false,val isLoadingLocations:Boolean=false,val studentId:String="",val password:String="",val campuses:List<ElectricityLocation> = emptyList(),val buildings:List<ElectricityLocation> = emptyList(),val rooms:List<ElectricityLocation> = emptyList(),val selectedCampus:ElectricityLocation?=null,val selectedBuilding:ElectricityLocation?=null,val selectedRoom:ElectricityLocation?=null,val balance:Double?=null,val lastUpdated:Long?=null,val history:List<Pair<Long,Double>> = emptyList(),val errorMessage:String?=null,val editing:Boolean=false)

@KoinViewModel
class ElectricityViewModel(private val repo: ElectricityRepository): ViewModel() {
 private suspend fun <T> attempt(block: suspend () -> T): Result<T> = runCatching { block() }
 private val _state=MutableStateFlow(ElectricityUiState()); val state:StateFlow<ElectricityUiState> = _state.asStateFlow(); private var config:ElectricityConfig?=null
 init { viewModelScope.launch { val c=repo.configFlow.first(); if(c!=null){ config=c; _state.value=_state.value.copy(isConfigured=true,studentId=c.studentId,password=c.password,selectedCampus=c.campus,selectedBuilding=c.building,selectedRoom=c.room); refresh(c) } } }
 fun updateStudentId(v:String){_state.value=_state.value.copy(studentId=v)}; fun updatePassword(v:String){_state.value=_state.value.copy(password=v)}
 fun loadCampuses(){viewModelScope.launch { attempt { _state.value=_state.value.copy(isLoadingLocations=true); repo.campuses(config?.token ?: repo.login(_state.value.studentId,_state.value.password)) }.onSuccess{_state.value=_state.value.copy(campuses=it,isLoadingLocations=false)}.onFailure{_state.value=_state.value.copy(isLoadingLocations=false,errorMessage=it.message)}}}
 fun selectCampus(v:ElectricityLocation){_state.value=_state.value.copy(selectedCampus=v,selectedBuilding=null,selectedRoom=null,buildings=emptyList(),rooms=emptyList()); viewModelScope.launch { attempt { repo.buildings(config?.token ?: repo.login(_state.value.studentId,_state.value.password),v.value)}.onSuccess{_state.value=_state.value.copy(buildings=it)}.onFailure{_state.value=_state.value.copy(errorMessage=it.message)}}}
 fun selectBuilding(v:ElectricityLocation){_state.value=_state.value.copy(selectedBuilding=v,selectedRoom=null,rooms=emptyList()); viewModelScope.launch { attempt { repo.rooms(config?.token ?: repo.login(_state.value.studentId,_state.value.password),_state.value.selectedCampus!!.value,v.value)}.onSuccess{_state.value=_state.value.copy(rooms=it)}.onFailure{_state.value=_state.value.copy(errorMessage=it.message)}}}
 fun selectRoom(v:ElectricityLocation){_state.value=_state.value.copy(selectedRoom=v)}
 fun saveAndQuery(){viewModelScope.launch { val s=_state.value; val c0=ElectricityConfig(s.studentId.trim(),s.password,s.selectedCampus!!,s.selectedBuilding!!,s.selectedRoom!!); attempt { _state.value=s.copy(isLoading=true); val t=repo.login(c0.studentId,c0.password); val c=c0.copy(token=t); repo.save(c); config=c; val r=repo.query(c,t); c to r }.onSuccess { (c,r)-> _state.value=_state.value.copy(isConfigured=true,isLoading=false,balance=r.first,lastUpdated=kotlin.time.Clock.System.now().toEpochMilliseconds(),editing=false,errorMessage=null) ; observeHistory(c)}.onFailure{_state.value=_state.value.copy(isLoading=false,errorMessage=it.message)}}}
 fun refresh(c:ElectricityConfig?=config){c ?: return; viewModelScope.launch { attempt {
  _state.value=_state.value.copy(isLoading=true)
  runCatching { repo.query(c,c.token) }.getOrElse {
   // Saved tokens may expire; re-authenticate once before surfacing the error.
   val token = repo.login(c.studentId,c.password)
   val renewed = c.copy(token=token)
   repo.save(renewed)
   config = renewed
   repo.query(renewed,token)
  }
 }.onSuccess{_state.value=_state.value.copy(isLoading=false,balance=it.first,lastUpdated=kotlin.time.Clock.System.now().toEpochMilliseconds(),errorMessage=null); observeHistory(config ?: c)}.onFailure{_state.value=_state.value.copy(isLoading=false,errorMessage=it.message ?: "电量查询失败")}}}
 private fun observeHistory(c:ElectricityConfig){viewModelScope.launch { repo.history(c).collect { h -> _state.value=_state.value.copy(history=h.map{it.recordedAt to it.balance}) } }}
 fun editConfiguration(){_state.value=_state.value.copy(editing=true)}; fun dismissError(){_state.value=_state.value.copy(errorMessage=null)}; fun clearConfiguration(){viewModelScope.launch { repo.clear(config); config=null; _state.value=ElectricityUiState() }}
}
