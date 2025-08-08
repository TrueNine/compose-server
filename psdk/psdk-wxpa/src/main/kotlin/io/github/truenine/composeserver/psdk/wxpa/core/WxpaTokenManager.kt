package io.github.truenine.composeserver.psdk.wxpa.core

import io.github.truenine.composeserver.holders.EventPublisherHolder
import io.github.truenine.composeserver.psdk.wxpa.api.IWxpaWebClient
import io.github.truenine.composeserver.psdk.wxpa.event.TokenExpiredEvent
import io.github.truenine.composeserver.psdk.wxpa.event.TokenType
import io.github.truenine.composeserver.psdk.wxpa.event.TokenUsedEvent
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
 * # 微信公众号Token管理器
 *
 * 负责管理access_token和jsapi_ticket的获取、缓存和刷新
 *
 * @author TrueNine
 * @since 2025-08-08
 */
class WxpaTokenManager(private val apiClient: IWxpaWebClient, private val properties: WxpaProperties) {
  private val lock = ReentrantReadWriteLock()

  @Volatile private var currentToken: WxpaToken? = null

  @Volatile private var currentTicket: WxpaTicket? = null

  /**
   * ## 获取有效的Access Token
   *
   * @return 有效的Access Token，如果获取失败则抛出异常
   * @throws WxpaTokenException Token获取失败
   */
  fun getValidAccessToken(): String {
    lock.read {
      val token = currentToken
      if (token != null && !token.isExpired) {
        log.debug("Using cached access token")
        // 发布Token使用事件
        publishTokenUsedEvent(TokenType.ACCESS_TOKEN, "getValidAccessToken")
        return token.accessToken
      }
    }

    // Token已过期或不存在，发布过期事件并刷新
    publishTokenExpiredEvent(TokenType.ACCESS_TOKEN, currentToken, null, "Access token expired or missing")
    return refreshAccessToken().accessToken
  }

  /**
   * ## 获取有效的JSAPI Ticket
   *
   * @return 有效的JSAPI Ticket，如果获取失败则抛出异常
   * @throws WxpaTokenException Ticket获取失败
   */
  fun getValidJsapiTicket(): String {
    lock.read {
      val ticket = currentTicket
      if (ticket != null && !ticket.isExpired) {
        log.debug("Using cached jsapi ticket")
        // 发布Token使用事件
        publishTokenUsedEvent(TokenType.JSAPI_TICKET, "getValidJsapiTicket")
        return ticket.ticket
      }
    }

    // Ticket已过期或不存在，发布过期事件并刷新
    publishTokenExpiredEvent(TokenType.JSAPI_TICKET, null, currentTicket, "JSAPI ticket expired or missing")
    return refreshJsapiTicket().ticket
  }

  /**
   * ## 刷新Access Token
   *
   * @return 新的Token信息
   * @throws WxpaTokenException Token刷新失败
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
   * ## 刷新JSAPI Ticket
   *
   * @return 新的Ticket信息
   * @throws WxpaTokenException Ticket刷新失败
   */
  fun refreshJsapiTicket(): WxpaTicket {
    return lock.write {
      log.info("Refreshing jsapi ticket")

      try {
        // 优先使用缓存且未过期的 access token，否则刷新
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

  /** ## 强制刷新所有Token */
  fun forceRefreshAll() {
    lock.write {
      log.info("Force refreshing all tokens")
      currentToken = null
      currentTicket = null
    }

    // 先刷新access token，再刷新ticket
    val newToken = refreshAccessToken()
    refreshJsapiTicket()

    log.info("All tokens refreshed successfully")
  }

  /**
   * ## 同时刷新Access Token和JSAPI Ticket
   *
   * @return Pair<WxpaToken, WxpaTicket>
   */
  fun refreshBoth(): Pair<WxpaToken, WxpaTicket> {
    log.info("Refreshing both access token and jsapi ticket")

    val token = refreshAccessToken()
    val ticket = refreshJsapiTicket()

    return token to ticket
  }

  /** ## 检查Token状态 */
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

  /** ## 发布Token过期事件 */
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

  /** ## 发布Token使用事件 */
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
