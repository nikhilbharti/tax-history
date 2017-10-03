package uk.gov.hmrc.taxhistory.model.api

import play.api.libs.json.Json

case class TaxYear (taxYear: Int,
                   employments: List[Employment])

object TaxYear {
  implicit val format = Json.format[TaxYear]
}