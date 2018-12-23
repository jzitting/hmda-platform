package hmda.validation.rules.lar.quality

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.config.ConfigFactory
import hmda.model.filing.lar.LoanApplicationRegister
import hmda.validation.dsl.{
  ValidationFailure,
  ValidationResult,
  ValidationSuccess
}
import hmda.validation.rules.AsyncEditCheck
import hmda.validation.{AS, EC, MAT}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import scala.concurrent.Future

object Q604 extends AsyncEditCheck[LoanApplicationRegister] {

  case class TractValidate(tract: String)
  case class Tractvalidated(isValid: Boolean)
  case class CountyValidate(county: String)
  case class Countyvalidated(isValid: Boolean)

  override def name: String = "Q604"

  val config = ConfigFactory.load()

  val host = config.getString("hmda.census.http.host")
  val port = config.getInt("hmda.census.http.port")

  override def apply[as: AS, mat: MAT, ec: EC](
      lar: LoanApplicationRegister): Future[ValidationResult] = {

    val county = lar.geography.county
    val state = lar.geography.state

    if (county.toLowerCase != "na" && state.toLowerCase != "na") {
      countyIsValid(county).map {
        case true  => ValidationSuccess
        case false => ValidationFailure
      }
    } else {
      Future.successful(ValidationSuccess)
    }
  }

  private def sendMessageRequest[as: AS, mat: MAT, ec: EC](
      message: CountyValidate): Future[HttpRequest] = {
    val uri1 = s"http://$host:$port/census/validate/county"
    println("This is the URI")
    println(uri1)
    Marshal(message).to[RequestEntity].map { entity =>
      HttpRequest(
        method = HttpMethods.POST,
        uri = uri1,
        entity = entity
      )
    }
  }

  protected def executeRequest[as: AS, mat: MAT, ec: EC](
      httpRequest: HttpRequest): Future[HttpResponse] = {
    Http().singleRequest(httpRequest)
  }

  private def unmarshallResponse[as: AS, mat: MAT, ec: EC](
      response: HttpResponse): Future[Countyvalidated] = {
    val unmarshalledResponse = Unmarshal(response.entity)

    if (response.status == StatusCodes.OK) {
      unmarshalledResponse.to[Countyvalidated]
    } else {
      unmarshalledResponse.to[Countyvalidated]
    }
  }

  def countyIsValid[as: AS, mat: MAT, ec: EC](
      county: String): Future[Boolean] = {

    val countyValidate = CountyValidate(county)
    for {
      messageRequest <- sendMessageRequest(countyValidate)
      response <- executeRequest(messageRequest)
      messageOrErrorResponse <- unmarshallResponse(response)
    } yield messageOrErrorResponse.isValid
//    val client = CensusServiceClient(
//      GrpcClientSettings.connectToServiceAt(host, port).withTls(false)
//    )
//    for {
//      response <- client
//        .validateCounty(ValidCountyRequest(county))
//        .map(response => response.isValid)
//      _ <- client.close()
//      closed <- client.closed()
//    } yield (response, closed)._1
  }

}
