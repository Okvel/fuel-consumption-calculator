package by.liauko.siarhei.cl.repository

import android.content.Context
import android.os.AsyncTask
import by.liauko.siarhei.cl.database.CarLogDatabase
import by.liauko.siarhei.cl.database.entity.CarProfileEntity
import by.liauko.siarhei.cl.entity.CarProfileData
import by.liauko.siarhei.cl.util.CarBodyType
import by.liauko.siarhei.cl.util.CarFuelType

class CarProfileRepository(context: Context) : Repository<CarProfileData, CarProfileEntity> {

    private val database = CarLogDatabase(context)

    override fun selectAll() =
        SelectAllCarProfileAsyncTask(database).execute().get().map { convertToData(it) }

    override fun insert(entity: CarProfileEntity): Long =
        InsertCarProfileAsyncTask(database).execute(entity).get()

    override fun update(data: CarProfileData) {
        UpdateCarProfileAsyncTask(database).execute(convertToEntity(data))
    }

    override fun delete(data: CarProfileData) {
        DeleteCarProfileAsyncTask(database).execute(convertToEntity(data))
    }

    private fun convertToEntity(data: CarProfileData) =
        CarProfileEntity(
            data.id,
            data.name,
            data.bodyType.name,
            data.fuelType.name,
            data.engineVolume
        )

    private fun convertToData(entity: CarProfileEntity) =
        CarProfileData(
            entity.id!!,
            entity.name,
            CarBodyType.valueOf(entity.bodyType),
            CarFuelType.valueOf(entity.fuelType),
            entity.engineVolume
        )
}

class SelectAllCarProfileAsyncTask(private val db: CarLogDatabase) : AsyncTask<Unit, Unit, List<CarProfileEntity>>() {

    override fun doInBackground(vararg params: Unit?) =
        db.carProfileDao().findAll()
}

class InsertCarProfileAsyncTask(private val db: CarLogDatabase) : AsyncTask<CarProfileEntity, Unit, Long>() {

    override fun doInBackground(vararg params: CarProfileEntity?) =
        db.carProfileDao().insert(params[0]!!)
}

class InsertAllCarProfileAsyncTask(private val db: CarLogDatabase) : AsyncTask<List<CarProfileEntity>, Unit, Unit>() {

    @Suppress("UNCHECKED_CAST")
    override fun doInBackground(vararg params: List<CarProfileEntity>?) {
        db.carProfileDao().insertAll(params[0]!!)
    }
}

class UpdateCarProfileAsyncTask(private val db: CarLogDatabase) : AsyncTask<CarProfileEntity, Unit, Unit>() {

    override fun doInBackground(vararg params: CarProfileEntity?) {
        db.carProfileDao().update(params[0]!!)
    }
}

class DeleteCarProfileAsyncTask(private val db: CarLogDatabase) : AsyncTask<CarProfileEntity, Unit, Unit>() {

    override fun doInBackground(vararg params: CarProfileEntity?) {
        db.carProfileDao().delete(params[0]!!)
    }
}

class DeleteAllCarProfilesAsyncTask(private val db: CarLogDatabase) : AsyncTask<Unit, Unit, Unit>() {

    override fun doInBackground(vararg params: Unit?) {
        db.carProfileDao().deleteAll()
    }

}