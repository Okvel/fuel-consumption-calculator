package by.liauko.siarhei.cl.recyclerview.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import by.liauko.siarhei.cl.R
import by.liauko.siarhei.cl.entity.CarProfileData
import by.liauko.siarhei.cl.recyclerview.holder.RecyclerViewCarProfileViewHolder
import by.liauko.siarhei.cl.util.ApplicationUtil.profileId

class RecyclerViewCarProfileAdapter(
    val resources: Resources,
    var items: List<CarProfileData>,
    private val listener: RecyclerViewOnItemClickListener
) : RecyclerView.Adapter<RecyclerViewCarProfileViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewCarProfileViewHolder =
        RecyclerViewCarProfileViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recycler_view_item_car_profile,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerViewCarProfileViewHolder, position: Int) {
        val profileData = items[position]
        val bodyType = resources.getStringArray(R.array.body_types)[profileData.bodyType.ordinal]
        val fuelType = resources.getStringArray(R.array.fuel_type)[profileData.fuelType.ordinal]

        if (profileData.id == profileId) {
            holder.cardView.background = ResourcesCompat.getDrawable(resources, R.drawable.selected_car_profile_background, null)
        } else {
            holder.cardView.background = ResourcesCompat.getDrawable(resources, R.drawable.car_profile_background, null)
        }
        holder.profileName.text = profileData.name
        holder.profileDetails.text =
            if (profileData.engineVolume != null) {
                String.format("%s %.1f %s", bodyType, profileData.engineVolume, fuelType)
            } else {
                String.format("%s %s", bodyType, fuelType)
            }
        holder.editButton.setOnClickListener { listener.onItemClick(profileData, false) }
        holder.bind(profileData, listener)
    }

    override fun getItemCount() = items.size

    interface RecyclerViewOnItemClickListener {
        fun onItemClick(item: CarProfileData, isSelect: Boolean)
    }
}
