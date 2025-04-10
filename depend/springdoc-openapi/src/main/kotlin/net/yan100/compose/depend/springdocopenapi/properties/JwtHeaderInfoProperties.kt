package net.yan100.compose.depend.springdocopenapi.properties

import net.yan100.compose.consts.IHeaders

class JwtHeaderInfoProperties {
  var authTokenName = IHeaders.AUTHORIZATION
  var refreshTokenName = IHeaders.X_REFRESH
}
