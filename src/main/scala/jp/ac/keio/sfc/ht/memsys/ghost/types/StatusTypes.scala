package jp.ac.keio.sfc.ht.memsys.ghost.types

/**
 * Created by aqram on 10/15/14.
 */
object StatusTypes {

  case object STANDBY extends StatusTypes(0)
  case object RUNNING extends StatusTypes(1)

  val values = Array(STANDBY, RUNNING)

}

sealed abstract class StatusTypes(val code:Int){
  val name = toString
}
