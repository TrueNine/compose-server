package io.github.truenine.composeserver.psdk.wxpa.core

import io.github.truenine.composeserver.holders.EventPublisherHolder
import io.github.truenine.composeserver.psdk.wxpa.api.IWxpaWebClient
import io.github.truenine.composeserver.psdk.wxpa.event.*
import io.github.truenine.composeserver.psdk.wxpa.exception.WxpaApiException
import io.github.truenine.composeserver.psdk.wxpa.exception.WxpaTokenException
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaTicket
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaToken
import io.github.truenine.composeserver.psdk.wxpa.properties.WxpaProperties
import io.github.truenine.composeserver.slf4j
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

private val log = slf4j<WxpaTokenManager>()

/**
 * WeChat Official Account token manager.
 *
 * Responsible for obtaining, caching, and refreshing access_token and jsapi_ticket.
 *
 * @author TrueNine
 * @since 2025-08-08
 */
class WxpaTokenManager(private val apiClient: IWxpaWebClient, private val properties: WxpaProperties) {
  private val lock = ReentrantReadWriteLock()

  @Volatile private var currentToken: WxpaToken? = null

  @Volatile private var currentTicket: WxpaTicket? = null

  /**
   * Get a valid access token.
   *
   * @return a valid access token, or throws an exception if retrieval fails
   * @throws WxpaTokenException when token retrieval fails
   */
  fun getValidAccessToken(): String {
    lock.read {
      val token = currentToken
      if (token != null && !token.isExpired) {
        log.debug("Using cached access token")
        // Publish token usage event
        publishTokenUsedEvent(TokenType.ACCESS_TOKEN, "getValidAccessToken")
        return token.accessToken
      }
    }

    // Token is expired or missing; publish expiration event and refresh
    publishTokenExpiredEvent(TokenType.ACCESS_TOKEN, currentToken, null, "Access token expired or missing")
    return refreshAccessToken().accessToken
  }

  /**
   * Get a valid JSAPI ticket.
   *
   * @return a valid JSAPI ticket, or throws an exception if retrieval fails
   * @throws WxpaTokenException when ticket retrieval fails
   */
  fun getValidJsapiTicket(): String {
    lock.read {
      val ticket = currentTicket
      if (ticket != null && !ticket.isExpired) {
        log.debug("Using cached jsapi ticket")
        // Publish token usage event
        publishTokenUsedEvent(TokenType.JSAPI_TICKET, "getValidJsapiTicket")
        return ticket.ticket
      }
    }

    // Ticket is expired or missing; publish expiration event and refresh
    publishTokenExpiredEvent(TokenType.JSAPI_TICKET, null, currentTicket, "JSAPI ticket expired or missing")
    return refreshJsapiTicket().ticket
  }

  /**
   * Refresh the access token.
   *
   * @return new token information
   * @throws WxpaTokenException when token refresh fails
   */
  fun refreshAccessToken(): WxpaToken {
    return lock.write {
      log.info("Refreshing access token for appId: {}", properties.appId)

      try {
        val response =
          apiClient.getAccessToken(
            appId = properties.appId ?: throw WxpaTokenException("AppId is not configured"),
            secret = properties.appSecret ?: throw WxpaTokenException("AppSecret is not configured"),
          )

        if (response == null) {
          throw WxpaTokenException("API response is null")
        }

        if (response.isError) {
          throw WxpaApiException(response.errorCode, response.errorMessage)
        }

        val accessToken = response.accessToken ?: throw WxpaTokenException("Access token is null in response")

        val expiresIn = response.expireInSecond ?: throw WxpaTokenException("Expires in is null in response")

        val newToken = WxpaToken(accessToken, expiresIn)
        currentToken = newToken

        log.info("Access token refreshed successfully, expires in {} seconds", expiresIn)
        return@write newToken
      } catch (e: Exception) {
        log.error("Failed to refresh access token", e)
        when (e) {
          is WxpaTokenException,
          is WxpaApiException -> throw e

          else -> throw WxpaTokenException("Failed to refresh access token: ${e.message}", e)
        }
      }
    }
  }

  /**
   * Refresh the JSAPI ticket.
   *
   * @return new ticket information
   * @throws WxpaTokenException when ticket refresh fails
   */
  fun refreshJsapiTicket(): WxpaTicket {
    return lock.write {
      log.info("Refreshing jsapi ticket")

      try {
        // Prefer a cached, non-expired access token; otherwise refresh it first
        val token = currentToken?.takeUnless { it.isExpired } ?: refreshAccessToken()
        val accessToken = token.accessToken

        val response = apiClient.getTicket(accessToken)

        if (response == null) {
          throw WxpaTokenException("Ticket API response is null")
        }

        if (response.isError) {
          throw WxpaApiException(response.errorCode, response.errorMessage)
        }

        val ticket = response.ticket ?: throw WxpaTokenException("Ticket is null in response")

        val expiresIn = response.expireInSecond ?: throw WxpaTokenException("Expires in is null in response")

        val newTicket = WxpaTicket(ticket, expiresIn)
        currentTicket = newTicket

        log.info("JSAPI ticket refreshed successfully, expires in {} seconds", expiresIn)
        return@write newTicket
      } catch (e: Exception) {
        log.error("Failed to refresh jsapi ticket", e)
        when (e) {
          is WxpaTokenException,
          is WxpaApiException -> throw e

          else -> throw WxpaTokenException("Failed to refresh jsapi ticket: ${e.message}", e)
        }
      }
    }
  }

  /** Force refresh all tokens. */
  fun forceRefreshAll() {
    lock.write {
      log.info("Force refreshing all tokens")
      currentToken = null
      currentTicket = null
    }

    // Refresh access token first, then refresh ticket
    val newToken = refreshAccessToken()
    refreshJsapiTicket()

    log.info("All tokens refreshed successfully")
  }

  /**
   * Refresh both access token and JSAPI ticket.
   *
   * @return a pair of (WxpaToken, WxpaTicket)
   */
  fun refreshBoth(): Pair<WxpaToken, WxpaTicket> {
    log.info("Refreshing both access token and jsapi ticket")

    val token = refreshAccessToken()
    val ticket = refreshJsapiTicket()

    return token to ticket
  }

  /** Check current token status. */
  fun getTokenStatus(): Map<String, Any> {
    return lock.read {
      mapOf(
        "hasAccessToken" to (currentToken != null),
        "accessTokenExpired" to (currentToken?.isExpired ?: true),
        "hasJsapiTicket" to (currentTicket != null),
        "jsapiTicketExpired" to (currentTicket?.isExpired ?: true),
      )
    }
  }

  /** Publish token expiration event. */
  private fun publishTokenExpiredEvent(tokenType: TokenType, currentToken: WxpaToken?, currentTicket: WxpaTicket?, reason: String) {
    try {
      val event =
        TokenExpiredEvent(
          source = this,
          appId = properties.appId ?: "unknown",
          tokenType = tokenType,
          currentToken = currentToken,
          currentTicket = currentTicket,
          reason = reason,
        )

      EventPublisherHolder.get()?.publishEvent(event)
      log.debug("Published TokenExpiredEvent: type={}, reason={}", tokenType, reason)
    } catch (e: Exception) {
      log.warn("Failed to publish TokenExpiredEvent", e)
    }
  }

  /** Publish token usage event. */
  private fun publishTokenUsedEvent(tokenType: TokenType, usageContext: String) {
    try {
      val event = TokenUsedEvent(source = this, appId = properties.appId ?: "unknown", tokenType = tokenType, usageContext = usageContext)

      EventPublisherHolder.get()?.publishEvent(event)
      log.debug("Published TokenUsedEvent: type={}, context={}", tokenType, usageContext)
    } catch (e: Exception) {
      log.warn("Failed to publish TokenUsedEvent", e)
    }
  }
}
