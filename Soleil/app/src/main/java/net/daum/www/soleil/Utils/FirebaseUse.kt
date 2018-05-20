package net.daum.www.soleil.Utils

import com.google.firebase.database.*
import net.daum.www.soleil.Adapter.ItemAdapter
import net.daum.www.soleil.View.Fragment.ContentListFragment
import net.daum.www.soleil.Vo.ContentItem
import java.text.DecimalFormat

/**
 * Created by thswl on 2018-03-14.
 */
class FirebaseUse{
    /* path 공유할 변수 선언 필요*/

    companion object {
        /*firebase*/
        var mInstance: DatabaseReference = FirebaseDatabase.getInstance().getReference()
        lateinit var mRootDatabaseRe: DatabaseReference
        var isFirebaseMAkeLayer = true
        lateinit var eventListner : ChildEventListener
        var isAddModel : ContentItem? = null

    }

     fun makeLayerChild ( latitude : Double, longitude : Double) : DatabaseReference {

         isAddModel!!.content_idx = "first_setting..."
        if(isFirebaseMAkeLayer == true) {
            /* 다른 방식 생각해야 될듯 */
            var locationFirst = DecimalFormat("#")
            var locationSecond = DecimalFormat("#.#")
            var locationThird = DecimalFormat("#.##")

            var substracteFirstLati = locationFirst.format(latitude).toString().replace(".", "")
            var substracteSecondLati = locationSecond.format(latitude).toString().replace(".", "")
            var substracteThirdLati = locationThird.format(latitude).toString().replace(".", "")
            var substractFirstLongi = locationFirst.format(longitude).toString().replace(".", "")
            var substractSecondLongi = locationSecond.format(longitude).toString().replace(".", "")
            var substractThirdLongi = locationThird.format(longitude).toString().replace(".", "")

            var child = mInstance
                    .child(substracteFirstLati)
                    .child(substracteSecondLati)
                    .child(substracteThirdLati)
                    .child(substractFirstLongi)
                    .child(substractSecondLongi)
                    .child(substractThirdLongi)
                    .child("contents")

            isFirebaseMAkeLayer = false
            mRootDatabaseRe = child
            return mRootDatabaseRe
        }else{
            return mRootDatabaseRe
        }
    }

    fun getDatashot(Items : ArrayList<ContentItem>, mAdapter : ItemAdapter){

        eventListner = mRootDatabaseRe.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                        var snapshotLatitude = dataSnapshot.getValue(ContentItem::class.java).latitude!!.toDouble()
                        var curLatitudeMinus = ContentListFragment.latitude!! - 0.0005
                        var curLatitudePlus = ContentListFragment.latitude!! + 0.0005

                        var snapshotLongitude = dataSnapshot.getValue(ContentItem::class.java).longitude!!.toDouble()
                        var curLongitudeMinus = ContentListFragment.longitude!! - 0.0005
                        var curLongitutdePlus = ContentListFragment.longitude!! + 0.0005

                        if (dataSnapshot.getValue(ContentItem::class.java).bad!!.toInt() > 10) {
                            return
                        }

                        if (snapshotLatitude >= curLatitudeMinus && snapshotLatitude <= curLatitudePlus) {
                            if (snapshotLongitude >= curLongitudeMinus && snapshotLongitude <= curLongitutdePlus) {
                                var model = dataSnapshot.getValue(ContentItem::class.java)
                                if(!(isAddModel!!.content_idx.equals(model.content_idx))) {
                                    Items.add(dataSnapshot.getValue(ContentItem::class.java))
                                    mAdapter!!.notifyDataSetChanged()
                                    isAddModel = model
                                }
                            }
                        }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                    mAdapter!!.notifyDataSetChanged()
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        var model = dataSnapshot.getValue(ContentItem::class.java)
                        var index = getItemIndex(model, Items)
                        if ( index != -1) {
                            Items.removeAt(index)
                            mAdapter!!.notifyItemRemoved(index)
                        }
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })

    }

    private fun getItemIndex(content: ContentItem, Items : ArrayList<ContentItem>): Int {
        var index = -1
        for (i in 0 until Items.size) {
            if (Items.get(i).content_idx.equals(content.content_idx)) {
                index = i
                break
            }
        }
        return index
    }


}