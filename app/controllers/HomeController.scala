package controllers

import java.io.PrintWriter
import java.util.concurrent.{Executors, ThreadFactory}

import akka.stream.scaladsl.StreamConverters
import javax.inject._
import play.api.Logger
import play.api.mvc._

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}
import scala.util.Random

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val singlePool: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor((r: Runnable) => {
    new Thread(r, "writerWorker")
  }))

  def index() = Action { implicit request: Request[AnyContent] =>

    val stream = StreamConverters.asOutputStream()
      .mapMaterializedValue { outputStream =>
        Future {
          val writer = new PrintWriter(outputStream)
          try {
            println("start")
            (0 until 1000).foreach { _ =>
              writer.write(Random.nextString(1000))
            }

            println("end")
          } catch {
            case e: Exception => Logger.info("Oops", e)
          }
          writer.flush()
          writer.close()
        }(singlePool)
      }

    Ok.chunked(stream).as("text/plain")

  }
}
