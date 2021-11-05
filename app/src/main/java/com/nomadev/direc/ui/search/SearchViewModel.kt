package com.nomadev.direc.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.nomadev.direc.data.local.Db
import com.nomadev.direc.data.local.PasienEntity
import com.nomadev.direc.model.PasienModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private var localDb = Db.buildDatabase(application)
    private var pasienDao = localDb.pasienDao()
    private var db = FirebaseFirestore.getInstance()
    private var dbRef = db.collection("pasien")
    private val listSearch = MutableLiveData<List<PasienModel>>()

    fun addPasien(list: List<PasienModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            pasienDao.deleteAllPatients()
            for (i in list.indices) {
                val id = list[i].id.toString()
                val name = list[i].nama.toString()
                val phone = list[i].telepon.toString()
                val address = list[i].alamat.toString()
                val pasienEntity = PasienEntity(id, name, phone, address)
                pasienDao.addPatient(pasienEntity)
            }
        }
    }

    fun searchPatient(searchQuery: String): LiveData<List<PasienEntity>> =
        pasienDao.searchPatient(searchQuery)

    fun getSearchPatients(listId: List<String>): LiveData<List<PasienModel>> {
        val array: ArrayList<PasienModel> = arrayListOf()

        for (i in listId.indices) {
            val query = dbRef.whereEqualTo("id", listId[i])

            query.get().addOnCompleteListener {
                val model = it.result?.toObjects(PasienModel::class.java)
                if (model != null) {
                    array.addAll(model)
                    listSearch.postValue(array)
                }
            }
        }

        return listSearch
    }
}