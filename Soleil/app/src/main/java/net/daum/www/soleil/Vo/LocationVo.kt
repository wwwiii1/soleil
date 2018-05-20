package net.daum.www.soleil.Vo

/**
 * Created by thswl on 2018-02-20.
 */
class LocationVo{
    var longitude : Double? = null
    var latitude : Double? = null

    override fun toString(): String{
        return "LocationVo(longtitude=$longitude, latitude=$latitude)"
    }
}