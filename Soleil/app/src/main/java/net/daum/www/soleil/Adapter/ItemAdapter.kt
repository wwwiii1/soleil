package net.daum.www.soleil.Adapter

import android.content.Context
import android.net.Uri
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import net.daum.www.soleil.R
import net.daum.www.soleil.Utils.FirebaseUse
import net.daum.www.soleil.Vo.ContentItem

/**
 * Created by thswl on 2018-02-14.
 */
class ItemAdapter constructor(context : Context, items : List<ContentItem>) : RecyclerView.Adapter<ContentsViewHolder>(){

    private var context : Context
    private var items : List<ContentItem>
    var user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var userEmail : String = user!!.email.toString()


    /* storage */
    var storage: FirebaseStorage = FirebaseStorage.getInstance()

    /*realtime*/
    lateinit var mGoodList: DatabaseReference
    lateinit var mBadList : DatabaseReference
    lateinit var mDatabase: FirebaseDatabase

    var firebaseuse = FirebaseUse()

    init{
        this.context = context
        this.items = items
    }

    override fun onBindViewHolder(holder: ContentsViewHolder, position: Int) {

        var item: ContentItem = items.get(holder.adapterPosition)

        mDatabase = FirebaseDatabase.getInstance()

        var storageRef: StorageReference = storage.getReferenceFromUrl("gs://soleil-f18af.appspot.com").child("images/" + item.picture + ".png")
        /* datavalue에 . 이 못들어가 제거 */
        var substracteMail = userEmail.replace(".", "")

            holder.bad_btn.setOnClickListener({
                mBadList = mDatabase.getReference("good_bad_list").child(item.content_idx).child("bad_id_list")

                /* 신고시 현제 아이디가 bad 아이디 리스트에 들어감  */
                /* 현제 아이디의 중복 신고를 막음*/
                mBadList.child(substracteMail).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (dataSnapshot.value == null) {
                            /* 좋아요 , 신고 등 숫자들도 String으로 통일해서 db에 저장돼 있음*/
                            mBadList.child(substracteMail).setValue("1")
                            var bad_up: Int = item.bad!!.toInt()
                            bad_up += 1
                            item.bad = bad_up.toString()
                            FirebaseUse.mRootDatabaseRe.child(item.content_idx).child("bad").setValue(bad_up.toString())
                            holder.bad.setText(item.bad + "")
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
            })

            holder.good_btn.setOnClickListener({
                mGoodList = mDatabase.getReference("good_bad_list").child(item.content_idx).child("good_id_list")
                /* 신고시 현제 아이디가 good 아이디 리스트에 들어감  */
                /* 현제 아이디의 중복 좋아요를 막음*/
                mGoodList.child(substracteMail).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // var snapshotValueCheck: String = dataSnapshot.value as String

                        if (dataSnapshot.value == null) {
                            mGoodList.child(substracteMail).setValue("1")
                            var good_up: Int = item.good!!.toInt()
                            good_up += 1
                            item.good = good_up.toString()
                            FirebaseUse.mRootDatabaseRe.child(item.content_idx).child("good").setValue(good_up.toString())
                            holder.good.setText(item.good + "")
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
            })

            holder.content_idx.setText(item.content_idx)
            //string-> picasso
            storageRef.getDownloadUrl().addOnSuccessListener(object : OnSuccessListener<Uri> {
                override fun onSuccess(uri: Uri) {
                    Picasso.with(context)
                            .load(uri.toString())
                            .into(holder.picture)
                }
            }).addOnFailureListener(object : OnFailureListener {
                override fun onFailure(@NonNull e: Exception) {
                }
            })

            holder.content.setText(item.content + "")
            holder.latitude.setText(item.latitude + "")
            holder.longitude.setText(item.longitude + "")
            holder.user_id.setText(item.user_id + "")
            holder.created_date.setText(item.created_date + "")
            holder.good.setText(item.good + "")
            holder.bad.setText(item.bad + "")
            holder.delete_btn.setImageResource(R.drawable.ic_linear_scale_black_24dp)

        if (userEmail.equals(item.user_id)) {
            holder.delete_text.setText("삭제")
            holder.delete_btn.setImageResource(R.drawable.ic_clear_black_24dp)
            holder.delete_btn.setOnClickListener({
                //realtime database delete
                mDatabase.getReference("good_bad_list").child(item.content_idx).removeValue()
                FirebaseUse.mRootDatabaseRe.child(item.content_idx).removeValue()
                storageRef.delete().addOnSuccessListener {
                }.addOnFailureListener {
                }
            })
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ContentsViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.travel_item, parent,false)
        var ContentsViewHolder = ContentsViewHolder(view)

        return ContentsViewHolder

    }

    override fun getItemCount(): Int = this.items.size
}


class ContentsViewHolder constructor(itemView : View) : RecyclerView.ViewHolder(itemView){
    var content_idx = itemView.findViewById<TextView>(R.id.content_idx)
    var picture = itemView.findViewById<ImageView>(R.id.picture_show)
    var content = itemView.findViewById<TextView>(R.id.content_show)
    var latitude = itemView.findViewById<TextView>(R.id.latitude)
    var longitude = itemView.findViewById<TextView>(R.id.longitude)
    var user_id = itemView.findViewById<TextView>(R.id.id_show)
    var created_date = itemView.findViewById<TextView>(R.id.card_created)
    var good = itemView.findViewById<TextView>(R.id.good_text)
    var bad = itemView.findViewById<TextView>(R.id.bad_text)
    var good_btn = itemView.findViewById<ImageButton>(R.id.good_btn)
    var bad_btn = itemView.findViewById<ImageButton>(R.id.bad_btn)
    var delete_text = itemView.findViewById<TextView>(R.id.delete_text)
    var delete_btn = itemView.findViewById<ImageButton>(R.id.delete_btn)
}