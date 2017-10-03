package uk.gov.hmrc.taxhistory.model.api

import play.api.libs.json.Json

case class PAYEForAgentDetails (nino: String,
                                taxYears:List[TaxYear])

object PAYEForAgentDetails {
  implicit val format = Json.format[PAYEForAgentDetails]
}