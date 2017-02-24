package security

import javax.inject.Inject

import com.google.inject.ImplementedBy
import models.Token
import pdi.jwt.{JwtAlgorithm, Jwt}
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.Results
import play.api.mvc.Security.AuthenticatedBuilder

import scala.util.{Failure, Success}


/**
  * Trait for providing a way to build authenticated actions
  */
@ImplementedBy(classOf[TokenSecuredAuth])
trait Auth {
  def Authenticated: AuthenticatedBuilder[Token]
}

/**
  * Provide authentication via JWT token
  */
class TokenSecuredAuth @Inject() (configuration: Configuration) extends Auth {
  /**
    * An action builder for requests requiring a JWT token as a proof of authentication.
    */
  val Authenticated = new AuthenticatedBuilder(
    userinfo = request => {
      import TokenSecuredAuth.getToken
      val SECRET_KEY: String = configuration.underlying.getString("auth0.secret_key")
      request.headers.get("Authorization")
        .flatMap(tokenString => TokenParser.decodeToken(tokenString, SECRET_KEY))
        .flatMap(decodedTokenString => getToken(TokenParser.parseToken(decodedTokenString)))
    },
    onUnauthorized = request =>
      Results.Unauthorized
  )
}

object TokenSecuredAuth {
  def getToken(token: Token): Option[Token] = {
    (token.sub, token.app_metadata) match {
      case (Some(userId), Some(appMetaData)) => Some(token)
      case _ => None
    }
  }
}

object TokenParser {
  def decodeToken(tokenHeader: String, SECRET_KEY: String): Option[String] = {
    val tokenString: String = tokenHeader.stripPrefix("Bearer ")
    Jwt.decodeRawAll(tokenString, SECRET_KEY, Seq(JwtAlgorithm.HS256)) match {
      case Success(res) =>
        Some(res._2) // Token payload as at position 2.
      case Failure(res) =>
        None  // No token, access denied
    }
  }

  def parseToken(decodedTokenString: String): Token = {
    Json.parse(decodedTokenString).as[Token]
  }
}
