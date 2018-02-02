import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths, StandardOpenOption}

class GPIOException(msg: String) extends RuntimeException(msg)

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

  this: GPIOFoundation => import scala.concurrent.ExecutionContext.Implicits.global

  def open(io: IO): Unit = {
    write(Analog(pin.toString), Paths get s"$filepath/export")
    write(Analog(io.direction), Paths get s"$filepath/gpio$pin/direction")
  }

  def close: Unit = {
    val portAccessFile = new File(s"$filepath/gpio$pin")
    if (portAccessFile.exists()) {
      write(Analog(pin.toString), Paths get s"$filepath/unexport")
    }
  }

  def write(value: Value): Unit = {
    write(value, Paths get s"$filepath/gpio$pin/value")
  }

  def read: Value = {
    val asAnalog: PartialFunction[String, Analog] = { case v @ _ => Analog(v) }
    val readValue = asDigital orElse asAnalog
    readValue(readFile)
  }

  def readAnalog: Analog = Analog(readFile)

  def asyncReadAnalog: Future[Analog] = Future { readAnalog }

  def readDigital: Try[Digital] = Try {
    val failWithReadException: PartialFunction[String, Digital] = { case _ => throw new GPIOException("") }
    val readAsDigitalOrFail = asDigital orElse failWithReadException
    readAsDigitalOrFail(readFile)
  }

  def asyncReadDigital: Future[Digital] = Future {
    readDigital match {
      case Success(d) => d
      case Failure(e) => throw e
    }
  }

  private val asDigital: PartialFunction[String, Digital] = {
    case "0" => off
    case "1" => on
  }

  private def readFile: String = Files.readAllLines(Paths get s"$filepath/gpio$pin/value", StandardCharsets.UTF_8)

  private def write(value: Value, path: Path): Unit = {
    Files write(path, value.value.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE)
  }

}