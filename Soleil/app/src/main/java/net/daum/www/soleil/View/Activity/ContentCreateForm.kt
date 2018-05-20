package net.daum.www.soleil.View.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.content_create_form.*
import net.daum.www.soleil.R
import net.daum.www.soleil.Utils.FirebaseUse
import net.daum.www.soleil.Utils.GpsInfo
import net.daum.www.soleil.Utils.ImageCircular
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


/**
 * Created by thswl on 2018-02-18.
 */
class ContentCreateForm : AppCompatActivity() {

    val TAG: String = "insertPhoto"
    private val REQUEST_ERROR = 0

    var filePath: Uri? = null
    lateinit var contentKey: String
    lateinit var mContext: Context

    var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    /*image*/
    var mImageCircular = ImageCircular()
    var bBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_create_form)

        /* 현제 user 아이디 받아오기 */
        user_id.text = user!!.email.toString()
        mContext = this@ContentCreateForm

        Album_camera_button.setOnClickListener({
            var intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 0)

        })

        dataUploadButton.setOnClickListener({
            uploadFile()
        })
    }

    /* 결과처리 */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //request 코드가 0, ok를 선택했고 데이터에 들어있다면
        if (requestCode == 0 && resultCode == RESULT_OK) {

            filePath = data!!.getData();
            Log.d(TAG, "uri :" + filePath.toString())
            try {
                /* Uri 파일을 bitmap으로 만들어서 ImageView에 집어 넣는다.*/
                bBitmap = mImageCircular.circular(filePath, mContext)
                visit_photo.setImageBitmap(bBitmap)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    fun uploadFile() {

        if (filePath != null) {
            FirebaseUse.isAddModel = null
            var today: Date = Date()
            var date = SimpleDateFormat("yyyy/MM/dd")
            var bindEdit: DatabaseReference
            var gpsInfo = GpsInfo(mContext)
            bindEdit = FirebaseUse.mRootDatabaseRe.push()
            contentKey = bindEdit.key

            var map2 = HashMap<String, String>()
            map2.put("content", data_content.text.toString())
            map2.put("user_id", user!!.email.toString())
            map2.put("longitude", gpsInfo.longitude.toString())
            map2.put("latitude", gpsInfo.latitude.toString())
            map2.put("created_date", date.format(today).toString())
            map2.put("good", "0")
            map2.put("bad", "0")
            map2.put("content_idx", contentKey)
            map2.put("picture", contentKey)

            bindEdit.setValue(map2)
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("업로드중..")
            progressDialog.show()

            /* storage */
            var storage: FirebaseStorage = FirebaseStorage.getInstance()

            /* storage 주소와 폴더 파일명을 지정해 준다. */
            var storageRef: StorageReference = storage.getReferenceFromUrl("gs://soleil-f18af.appspot.com").child("images/" + contentKey + ".png")

            /*
            storageRef.putFile(filePath!!)
                    .addOnSuccessListener {
                        /* 업로드 진행 diglog 상자 닫기 */
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "업로드 완료", Toast.LENGTH_SHORT).show()
                    }
                    /* 실패시 */
                    .addOnFailureListener({
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "업로드 실패", Toast.LENGTH_SHORT).show()
                    })
                    /* 진행중 */
                    .addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot> {
                        override fun onProgress(taskSnapshot: UploadTask.TaskSnapshot) {
                            val progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount()
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + (progress.toInt()) + "% ...")
                        }
                    })
            */
            /* bitmap을 한번에 storage로 업로드*/
            var boas : ByteArrayOutputStream = ByteArrayOutputStream()
            bBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, boas)
            var data = boas.toByteArray()
            var uploadTask : UploadTask = storageRef.putBytes(data)
            uploadTask.addOnFailureListener(object: OnFailureListener {
                override fun onFailure(@NonNull exception:Exception) {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "업로드 실패", Toast.LENGTH_SHORT).show()
                }
            }).addOnSuccessListener(object: OnSuccessListener<UploadTask.TaskSnapshot> {
                override fun onSuccess(taskSnapshot:UploadTask.TaskSnapshot) {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "업로드 완료", Toast.LENGTH_SHORT).show()

                }
            })
        } else if (filePath == null) {
            Toast.makeText(applicationContext, "파일을 먼저 선택하세요", Toast.LENGTH_SHORT).show()
        }
    }

}