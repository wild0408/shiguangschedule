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

data class ElectricityUiState(val isConfigured:Boolean=false,val isLoggedIn:Boolean=false,val isLoading:Boolean=false,val isLoadingLocations:Boolean=false,val studentId:String="",val password:String="",val campuses:List<ElectricityLocation> = emptyList(),val buildings:List<ElectricityLocation> = emptyList(),val rooms:List<ElectricityLocation> = emptyList(),val selectedCampus:ElectricityLocation?=null,val selectedBuilding:ElectricityLocation?=null,val selectedRoom:ElectricityLocation?=null,val balance:Double?=null,val lastUpdated:Long?=null,val history:List<Pair<Long,Double>> = emptyList(),val errorMessage:String?=null,val editing:Boolean=false)

@KoinViewModel
class ElectricityViewModel(private val repo: ElectricityRepository): ViewModel() {
 private suspend fun <T> attempt(block: suspend () -> T): Result<T> = runCatching { block() }
 private val _state=MutableStateFlow(ElectricityUiState()); val state:StateFlow<ElectricityUiState> = _state.asStateFlow(); private var config:ElectricityConfig?=null; private var sessionToken: String = ""
 init { viewModelScope.launch { val c=repo.configFlow.first(); val campuses=repo.storedCampuses(); if(c!=null){ config=c; sessionToken=c.token; val buildings=repo.storedBuildings(c.campus.value); val rooms=repo.storedRooms("${c.campus.value}|${c.building.value}"); _state.value=_state.value.copy(isConfigured=true,isLoggedIn=true,studentId=c.studentId,password=c.password,campuses=campuses,buildings=buildings,rooms=rooms,selectedCampus=c.campus,selectedBuilding=c.building,selectedRoom=c.room); refresh(c) } else if(campuses.isNotEmpty()) _state.value=_state.value.copy(campuses=campuses) } }
 fun updateStudentId(v:String){_state.value=_state.value.copy(studentId=v)}; fun updatePassword(v:String){_state.value=_state.value.copy(password=v)}
 fun loginAndLoadCampuses(){viewModelScope.launch { if (_state.value.isLoadingLocations) return@launch; attempt { _state.value=_state.value.copy(isLoadingLocations=true); sessionToken=repo.login(_state.value.studentId.trim(),_state.value.password); val saved=repo.storedCampuses(); if(saved.isNotEmpty()) saved else repo.campuses(sessionToken) }.onSuccess{ if(it.isNotEmpty()) repo.saveLocations(0,it); _state.value=_state.value.copy(isLoggedIn=true,campuses=it,isLoadingLocations=false,errorMessage=null)}.onFailure{sessionToken=""; _state.value=_state.value.copy(isLoadingLocations=false,errorMessage=it.message ?: "登录失败")}}}
 fun loadCampuses(){ if (_state.value.isLoggedIn) return; loginAndLoadCampuses() }
 fun selectCampus(v:ElectricityLocation){_state.value=_state.value.copy(selectedCampus=v,selectedBuilding=null,selectedRoom=null,buildings=emptyList(),rooms=emptyList()); viewModelScope.launch { val saved=repo.storedBuildings(v.value); if(saved.isNotEmpty()){ _state.value=_state.value.copy(buildings=saved); return@launch }; attempt { _state.value=_state.value.copy(isLoadingLocations=true); repo.buildings(sessionToken,v.value)}.onSuccess{repo.saveLocations(1,it,v.value); _state.value=_state.value.copy(buildings=it,isLoadingLocations=false)}.onFailure{_state.value=_state.value.copy(isLoadingLocations=false,errorMessage=it.message ?: "楼栋列表加载失败")}}}
 fun selectBuilding(v:ElectricityLocation){val campus=_state.value.selectedCampus ?: return; _state.value=_state.value.copy(selectedBuilding=v,selectedRoom=null,rooms=emptyList()); viewModelScope.launch { val parent="${campus.value}|${v.value}"; val saved=repo.storedRooms(parent); if(saved.isNotEmpty()){ _state.value=_state.value.copy(rooms=saved); return@launch }; attempt { _state.value=_state.value.copy(isLoadingLocations=true); repo.rooms(sessionToken,campus.value,v.value)}.onSuccess{repo.saveLocations(2,it,parent); _state.value=_state.value.copy(rooms=it,isLoadingLocations=false)}.onFailure{_state.value=_state.value.copy(isLoadingLocations=false,errorMessage=it.message ?: "房号列表加载失败")}}}
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
