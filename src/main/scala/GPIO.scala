
// High level abstraction
abstract class Pin(val pin: Int) extends GPIO(pin) with Config
// IO case object for gpio pin direction
abstract class IO(val direction: String)
case object in extends IO("in")
case object out extends IO("out")
// Analog and Digital
abstract class Value(val value: String)
case class Analog(override val value: String) extends Value(value)
abstract class Digital(val number: Int) extends Value(number.toString)
// Case class for the current GPIO status
case object on extends Digital(1)
case object off extends Digital(0)


trait GPIOFoundation {
  val filepath: String
}

trait Config extends GPIOFoundation {
  val filepath = "/sys/class/gpio"
}

object GPIO {
  def apply(pin: Int) = new GPIO(pin) with Config
}

class GPIO(pin: Int) {

  def write(value: Value): Unit = {

  }

  def On: Unit = {

  }


  def Off: Unit = {

  }

  def Read: Boolean = {
    true
  }

  def open(io: IO): Unit = {


  }

  def close: Unit = {


  }


}