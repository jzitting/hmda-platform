package hmda.query

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import hmda.query.projections.filing.SubmissionCassandraProjection
import hmda.query.projections.institutions.InstitutionCassandraProjection

object HmdaProjectionQuery {

  def startUp(implicit system: ActorSystem): Unit = {
    implicit val materializer = ActorMaterializer()
    val institutionProjection = new InstitutionCassandraProjection(system, materializer)
    institutionProjection.startUp()

    val larProjection = new SubmissionCassandraProjection(system, materializer)
    larProjection.startUp()
  }

}
