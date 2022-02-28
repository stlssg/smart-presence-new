package it.polito.interdisciplinaryProjects2021.smartpresence.introduction

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import com.google.android.material.card.MaterialCardView
import it.polito.interdisciplinaryProjects2021.smartpresence.R
import java.util.*
import kotlin.collections.ArrayList

class InfoBrandFragment : Fragment() {

    private lateinit var brandInfoRecyclerView: RecyclerView
    private lateinit var brandInfoList: ArrayList<BrandInfo>
    private lateinit var tempBrandInfoList: ArrayList<BrandInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info_brand, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        brandInfoList = arrayListOf<BrandInfo>()
        tempBrandInfoList = arrayListOf<BrandInfo>()
        brandInfoList.add(BrandInfo(R.drawable.huawei_logo, getString(R.string.huawei_description)))
        brandInfoList.add(BrandInfo(R.drawable.samsung_logo, getString(R.string.samsung_description)))
        brandInfoList.add(BrandInfo(R.drawable.oneplus_logo, getString(R.string.onePlus_description)))
        brandInfoList.add(BrandInfo(R.drawable.meizu_logo, getString(R.string.meizu_description)))
        brandInfoList.add(BrandInfo(R.drawable.ic_baseline_phone_android_24, "More information will be available soon."))
        tempBrandInfoList.addAll(brandInfoList)

        brandInfoRecyclerView = view.findViewById<RecyclerView>(R.id.brandInfoRecyclerView)
        brandInfoRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val rvAdapter = BrandInfoCardAdapter(tempBrandInfoList, requireContext())
        brandInfoRecyclerView.adapter = rvAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val item = menu.findItem(R.id.search_faq)
        val searchView = item.actionView as SearchView
        searchView.setIconifiedByDefault(true)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                tempBrandInfoList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    brandInfoList.forEach {
                        if (it.brandInfoText.toLowerCase(Locale.getDefault()).contains(searchText)) {
                            tempBrandInfoList.add(it)
                        }
                    }
                    brandInfoRecyclerView.adapter?.notifyDataSetChanged()
                } else {
                    tempBrandInfoList.clear()
                    tempBrandInfoList.addAll(brandInfoList)
                    brandInfoRecyclerView.adapter?.notifyDataSetChanged()
                }
                return false
            }
        })
    }

}

class BrandInfoCardAdapter (
    private val brandInfoList: ArrayList<BrandInfo>,
    val context: Context,
): RecyclerView.Adapter<BrandInfoCardAdapter.BrandInfoCardViewHolder>() {
    class BrandInfoCardViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val brandInfoImage: ImageView = v.findViewById(R.id.brandInfoImage)
        val brandInfoText: TextView = v.findViewById(R.id.brandInfoText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandInfoCardViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.brand_info_card, parent, false)
        return BrandInfoCardViewHolder(v)
    }

    override fun onBindViewHolder(holder: BrandInfoCardViewHolder, position: Int) {
        holder.brandInfoImage.setImageResource(brandInfoList[position].brandImageID)
        holder.brandInfoText.text = brandInfoList[position].brandInfoText
    }

    override fun getItemCount(): Int {
        return brandInfoList.size
    }
}

data class BrandInfo(var brandImageIDInput: Int, var brandInfoTextInput: String) {
    var brandImageID: Int = brandImageIDInput
    var brandInfoText: String = brandInfoTextInput
    var id: String = ""
}