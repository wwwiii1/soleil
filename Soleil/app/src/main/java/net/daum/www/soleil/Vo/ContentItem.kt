package net.daum.www.soleil.Vo

/**
 * Created by thswl on 2018-02-14.
 */
class ContentItem{

    var content_idx : String? = null
    var user_id : String? = null
    var longitude : String? = null
    var latitude : String? = null
    var good : String? = null
    var bad : String? = null
    var content : String? = null
    var picture : String? = null
    var created_date : String?= null

    override fun toString(): String{
        return "ContentItem(content_idx=$content_idx, user_id=$user_id, longitude=$longitude,latitude=$latitude, good=$good, bad=$bad, content=$content, picture=$picture, created_date=$created_date"
    }

    fun toMap():Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("content_idx", content_idx!!)
        result.put("user_id", user_id!!)
        result.put("longitude", longitude!!)
        result.put("latitude", latitude!!)
        result.put("good", good!!)
        result.put("bad", bad!!)
        result.put("content", content!!)
        result.put("picture", picture!!)
        result.put("created_date", created_date!!)
        // result.put("mUpload", mUploada);
        return result
    }
}