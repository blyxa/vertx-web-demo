package acme

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.impl.BodyHandlerImpl
import org.apache.commons.lang3.exception.ExceptionUtils
import scala.jdk.CollectionConverters._

object Main extends App{
  val logger = LoggerFactory.getLogger("Main")
  val vertx = Vertx.vertx()
  val server: HttpServer = vertx.createHttpServer()
  val router = Router.router(vertx)

  /*
  Catch all default error handler
   */
  router.route().failureHandler{ctx=>
    val t = ctx.failure()
    logger.error(
      s"""
         |default failure handler
         |                ctx.failed()[${ctx.failed()}]
         |                     message[${ctx.failure().getMessage}]
         |                   throwable[$t]
         |                       stack[${ExceptionUtils.getStackTrace(t)}]
         |                      status[${ctx.statusCode()}]
         |ctx.response().getStatusCode[${ctx.response().getStatusCode}]
         |
         |""".stripMargin)
    val res = ctx.response()
    res.setStatusCode(ctx.statusCode()).end(ctx.failure().getMessage)
  }

  /*
  Configure the body handler
   */
  val bodyHandler = new BodyHandlerImpl("/tmp")
  bodyHandler.setBodyLimit(1000)
  router.post("/upload").handler(bodyHandler)

  /*
  successful body handler
   */
  router.post("/upload").handler { ctx =>
    logger.info(
      s"""
         |handling upload
         |${ctx.fileUploads().asScala.map(f=>s"${f.name()} ${f.size()}").mkString("\n")}
         |""".stripMargin)

    ctx.response().end()

  }
  server.requestHandler(router).listen(9999)
}
