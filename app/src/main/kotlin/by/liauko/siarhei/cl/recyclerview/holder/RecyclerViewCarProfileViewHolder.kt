package by.liauko.siarhei.cl.recyclerview.holder

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.liauko.siarhei.cl.R
import by.liauko.siarhei.cl.model.CarProfileModel
import by.liauko.siarhei.cl.recyclerview.adapter.RecyclerViewCarProfileAdapter
import com.google.android.material.card.MaterialCardView

class RecyclerViewCarProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val cardView: MaterialCardView = itemView.findViewById(R.id.car_profile_card_view)
    val profileName: TextView = itemView.findViewById(R.id.car_profile_name)
    val profileDetails: TextView = itemView.findViewById(R.id.car_profile_details)
    val editButton: ImageButton = itemView.findViewById(R.id.edit_car_profile_button)

    fun bind(item: CarProfileModel, listener: RecyclerViewCarProfileAdapter.RecyclerViewOnItemClickListener) {
        itemView.setOnClickListener { listener.onItemClick(item, true) }
    }
}
