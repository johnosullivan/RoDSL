
/**
  * Created by johnosullivan on 12/12/17.
  */


object App {


  //trait Decapsulation { val set = Set }

  object gpio { def pin(name: Any): PIN = new PIN(name) }

  class PIN(val name: Any) {
    def set(obj: AnyRef): Int = {
      1
    }
    def direction(obj: AnyRef): PIN = new PIN(name)
  }






  def main(args: Array[String]): Unit = {

      val resetPin = gpio pin 22 direction in set off

      println(resetPin)

      // DO SOMETHING




  }

}
