package net.daum.www.soleil.Vo

/**
 * Created by thswl on 2018-02-14.
 */
class UserVo{

    var user_idx : String? = null
    var id : String? = null
    var pw : String? = null
    var author : String? = null

    override fun toString() : String{
        return "UserVo(user_idx=$user_idx, id=$id, pw=$pw, author=$author"
    }
}