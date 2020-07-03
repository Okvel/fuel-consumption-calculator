package by.liauko.siarhei.cl.activity.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.liauko.siarhei.cl.R
import by.liauko.siarhei.cl.activity.FuelDataDialogActivity
import by.liauko.siarhei.cl.activity.LogDataActivity
import by.liauko.siarhei.cl.database.entity.FuelConsumptionEntity
import by.liauko.siarhei.cl.database.entity.LogEntity
import by.liauko.siarhei.cl.entity.AppData
import by.liauko.siarhei.cl.entity.FuelConsumptionData
import by.liauko.siarhei.cl.entity.LogData
import by.liauko.siarhei.cl.recyclerview.RecyclerViewDataAdapter
import by.liauko.siarhei.cl.recyclerview.RecyclerViewSwipeController
import by.liauko.siarhei.cl.repository.AppRepositoryCollection
import by.liauko.siarhei.cl.util.AppResultCodes.ADD_FUEL_CONSUMPTION
import by.liauko.siarhei.cl.util.AppResultCodes.ADD_LOG
import by.liauko.siarhei.cl.util.AppResultCodes.EDIT_FUEL_CONSUMPTION
import by.liauko.siarhei.cl.util.AppResultCodes.EDIT_LOG
import by.liauko.siarhei.cl.util.ApplicationUtil.EMPTY_STRING
import by.liauko.siarhei.cl.util.ApplicationUtil.dataPeriod
import by.liauko.siarhei.cl.util.ApplicationUtil.profileId
import by.liauko.siarhei.cl.util.ApplicationUtil.type
import by.liauko.siarhei.cl.util.DataPeriod
import by.liauko.siarhei.cl.util.DataType
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class DataFragment : Fragment() {

    private lateinit var fragmentView: View
    private lateinit var items: ArrayList<AppData>
    private lateinit var rvAdapter: RecyclerViewDataAdapter
    private lateinit var repositoryCollection: AppRepositoryCollection
    private lateinit var fab: FloatingActionButton
    private lateinit var noDataTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_data, container, false)

        items = arrayListOf()
        initRecyclerView()

        fab = fragmentView.findViewById(R.id.add_fab)
        fab.setOnClickListener {
            when (type) {
                DataType.LOG -> startActivityForResult(
                    Intent(requireContext(), LogDataActivity::class.java),
                    ADD_LOG
                )
                DataType.FUEL -> startActivityForResult(
                    Intent(requireContext(), FuelDataDialogActivity::class.java),
                    ADD_FUEL_CONSUMPTION
                )
            }
        }

        select(type)

        return fragmentView
    }

    private fun initRecyclerView() {
        repositoryCollection = AppRepositoryCollection(requireContext())
        noDataTextView = fragmentView.findViewById(R.id.no_data_text)

        rvAdapter = RecyclerViewDataAdapter(items, resources, repositoryCollection, noDataTextView,
            object : RecyclerViewDataAdapter.RecyclerViewOnItemClickListener {
                override fun onItemClick(item: AppData) {
                    if (item is LogData) {
                        callLogEditActivityForResult(LogDataActivity::class.java, item)
                    } else if (item is FuelConsumptionData) {
                        callFuelConsumptionEditActivityForResult(FuelDataDialogActivity::class.java, item)
                    }
                }
            })

        val recyclerView = fragmentView.findViewById<RecyclerView>(R.id.recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    fab.hide()
                } else if (dy < 0) {
                    fab.show()
                }
            }
        })

        val helper = ItemTouchHelper(
            RecyclerViewSwipeController(
                rvAdapter
            )
        )
        helper.attachToRecyclerView(recyclerView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val time = data.getLongExtra("time", Calendar.getInstance().timeInMillis)
            when (requestCode) {
                ADD_FUEL_CONSUMPTION -> {
                    val litres = data.getStringExtra("litres")?.toDouble() ?: Double.MIN_VALUE
                    val distance = data.getStringExtra("distance")?.toDouble() ?: Double.MIN_VALUE
                    val fuelConsumption = litres * 100 / distance
                    val id = repositoryCollection.getRepository(type).insert(
                        FuelConsumptionEntity(null, fuelConsumption, litres, distance, time, profileId)
                    )
                    if (id != -1L) {
                        items.add(FuelConsumptionData(id, time, fuelConsumption, litres, distance))
                        rvAdapter.refreshRecyclerView()
                    }
                }
                EDIT_FUEL_CONSUMPTION -> {
                    val id = data.getLongExtra("id", -1L)
                    val litres = data.getStringExtra("litres")?.toDouble() ?: Double.MIN_VALUE
                    val distance = data.getStringExtra("distance")?.toDouble() ?: Double.MIN_VALUE
                    val fuelConsumption = litres * 100 / distance
                    val item = items.find { it.id == id } as FuelConsumptionData
                    item.litres = litres
                    item.distance = distance
                    item.fuelConsumption = fuelConsumption
                    item.time = time
                    repositoryCollection.getRepository(type).update(item)
                    rvAdapter.refreshRecyclerView()
                }
                ADD_LOG -> {
                    val title = data.getStringExtra("title")?.trim() ?: EMPTY_STRING
                    val text = data.getStringExtra("text")?.trim() ?: EMPTY_STRING
                    val mileage = data.getStringExtra("mileage")?.toLong() ?: Long.MIN_VALUE
                    val id = repositoryCollection.getRepository(type).insert(
                        LogEntity(null, title, text, mileage, time, profileId)
                    )
                    if (id != -1L) {
                        items.add(LogData(id, time, title, text, mileage))
                        rvAdapter.refreshRecyclerView()
                    }
                }
                EDIT_LOG -> {
                    val id = data.getLongExtra("id", -1L)
                    val item = items.find { it.id == id } as LogData

                    if (data.getBooleanExtra("remove", false)) {
                        val position = items.indexOf(item)
                        rvAdapter.removeItem(position)
                        repositoryCollection.getRepository(type).delete(item)
                    } else {
                        val title = data.getStringExtra("title") ?: EMPTY_STRING
                        val text = data.getStringExtra("text") ?: EMPTY_STRING
                        val mileage = data.getStringExtra("mileage")?.toLong() ?: Long.MIN_VALUE
                        item.title = title
                        item.text = text
                        item.mileage = mileage
                        item.time = time
                        repositoryCollection.getRepository(type).update(item)
                        rvAdapter.refreshRecyclerView()
                    }
                }
            }
        }
    }

    private fun callLogEditActivityForResult(activityClass: Class<*>, item: LogData) {
        val intent = Intent(requireContext(), activityClass)
        intent.putExtra("title", R.string.activity_log_title_edit)
        intent.putExtra("id", item.id)
        intent.putExtra("time", item.time)
        intent.putExtra("log_title", item.title)
        intent.putExtra("mileage", item.mileage)
        intent.putExtra("text", item.text)
        startActivityForResult(intent, EDIT_LOG)
    }

    private fun callFuelConsumptionEditActivityForResult(activityClass: Class<*>, item: FuelConsumptionData) {
        val intent = Intent(requireContext(), activityClass)
        intent.putExtra("title", R.string.data_dialog_title_edit)
        intent.putExtra("positive_button", R.string.data_dialog_positive_button_edit)
        intent.putExtra("id", item.id)
        intent.putExtra("time", item.time)
        intent.putExtra("litres", item.litres)
        intent.putExtra("distance", item.distance)
        startActivityForResult(intent, EDIT_FUEL_CONSUMPTION)
    }

    private fun select(type: DataType) {
        items.clear()
        when (dataPeriod) {
            DataPeriod.ALL -> items.addAll(repositoryCollection.getRepository(type).selectAllByProfileId(profileId))
            else -> items.addAll(repositoryCollection.getRepository(type).selectAllByProfileIdAndPeriod(profileId))
        }
        rvAdapter.refreshRecyclerView()
    }
}