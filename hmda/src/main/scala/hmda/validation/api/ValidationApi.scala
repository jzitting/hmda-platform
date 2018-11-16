package hmda.validation.api

import cats.Semigroup
import hmda.model.validation._
import hmda.validation.dsl.{
  ValidationFailure,
  ValidationResult,
  ValidationSuccess
}
import hmda.validation.rules.EditCheck
import cats.implicits._
import hmda.validation.HmdaValidation

trait ValidationApi[A] {

  implicit val sg = new Semigroup[A] {
    override def combine(x: A, y: A): A = x
  }

  def check[B](
      editCheck: EditCheck[B],
      input: B,
      errorId: String,
      validationErrorType: ValidationErrorType,
      validationErrorEntity: ValidationErrorEntity): HmdaValidation[B] = {
    convertResult(input,
                  editCheck(input),
                  editCheck.name,
                  errorId,
                  validationErrorType,
                  validationErrorEntity)
  }

  def convertResult[B](
      input: B,
      result: ValidationResult,
      editName: String,
      uli: String,
      validationErrorType: ValidationErrorType,
      validationErrorEntity: ValidationErrorEntity): HmdaValidation[B] =
    result match {

      case ValidationSuccess => input.validNel

      case ValidationFailure =>
        validationErrorType match {
          case Syntactical =>
            SyntacticalValidationError(uli, editName, validationErrorEntity).invalidNel
          case Validity =>
            ValidityValidationError(uli, editName, validationErrorEntity).invalidNel
          case Quality =>
            QualityValidationError(uli, editName).invalidNel
          case Macro => MacroValidationError(editName).invalidNel
        }
    }

}
