package net.daum.www.soleil.View.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.content_list_fragment.view.*
import net.daum.www.soleil.Adapter.ItemAdapter
import net.daum.www.soleil.R
import net.daum.www.soleil.Utils.FirebaseUse
import net.daum.www.soleil.Utils.GpsInfo
import net.daum.www.soleil.View.Activity.ContentCreateForm
import net.daum.www.soleil.Vo.ContentItem

/**
 * Created by thswl on 2018-03-14.
 */
class ContentListFragment : Fragment() {

    init{

    }

    /*firebase*/
   // lateinit var mReference: DatabaseReference

    /*mark*/
    var currentLife: Int? = null
    private val REQUEST_ERROR = 0

    /* recycler view */
    lateinit var m: RecyclerView
    lateinit var mContext: Context
    lateinit var mAdapter: ItemAdapter
    var Items = ArrayList<ContentItem>()

    /*refresh*/
    lateinit var swipeRefreshLayout : SwipeRefreshLayout

    /*utils*/
    var firebaseuse = FirebaseUse()

    companion object {
        var latitude: Double? = null
        var longitude: Double? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.content_list_fragment,container,false)

        m = rootView.findViewById(R.id.my_recycler_view)
        currentLife = 0
        Items.clear()

        mContext = activity!!.applicationContext
        var llm = LinearLayoutManager(activity)
        //llm.reverseLayout = true
        //llm.stackFromEnd = true
        llm.orientation
        m!!.setHasFixedSize(true)
        m!!.layoutManager = llm

        firebaseuse.makeLayerChild(GpsInfo.lat, GpsInfo.lon)

        mAdapter = ItemAdapter(mContext!!, Items)
        m.adapter = mAdapter

        /* location 받아오기 */
            latitude = GpsInfo.lat
            longitude = GpsInfo.lon
            firebaseuse.getDatashot(Items, mAdapter)
            Toast.makeText( activity, "당신의 위도 : "+ latitude + ", 경도 : "+ longitude, Toast.LENGTH_SHORT)

        /*
        if(gpsInfo.isGetLocation()){
            latitude = gpsInfo.latitude
            longitude = gpsInfo.longitude
            firebaseuse.getDatashot(Items, mAdapter)
            Toast.makeText( activity, "당신의 위도 : "+ latitude + ", 경도 : "+ longitude, Toast.LENGTH_SHORT)
        }else {
            //GPS를 사용할 수 없으므로
            gpsInfo.showSettingsAlert()
        }
*/
        /* fragment button 은 activity 와 다르다*/
        rootView.add_button.setOnClickListener {
            var intent = Intent(activity , ContentCreateForm::class.java)
            startActivity(intent)
        }

        /* 새로고침 */
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_layout)
        swipeRefreshLayout.setOnRefreshListener({
            /* fragment restore */
            var ft : FragmentTransaction = fragmentManager!!.beginTransaction()
            ft.detach(this).attach(this).commit()
            swipeRefreshLayout.isRefreshing = false
        })

        return rootView
    }

    override fun onResume(){
        super.onResume()
    }

}