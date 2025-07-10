package io.github.truenine.composeserver.depend.springdocopenapi.properties

import io.github.truenine.composeserver.consts.IHeaders

class JwtHeaderInfoProperties {
  var authTokenName = IHeaders.AUTHORIZATION
  var refreshTokenName = IHeaders.X_REFRESH
}
