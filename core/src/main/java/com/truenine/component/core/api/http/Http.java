package com.truenine.component.core.api.http;


import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * http
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public class Http {
  public static final String LOCAL_HOST_IP = "127.0.0.1";
  public static final String LOCAL_HOST_V6 = "0:0:0:0:0:0:0:1";
  public static final String LOCAL_HOST = "localhost";


  public static boolean isLocalAddress(jakarta.servlet.http.HttpServletRequest request) {
    var remoteHost = request.getRemoteAddr();
    if (LOCAL_HOST_IP.equalsIgnoreCase(remoteHost)) {
      return true;
    }
    if (LOCAL_HOST.equals(remoteHost)) {
      return true;
    }
    return LOCAL_HOST_V6.equalsIgnoreCase(remoteHost);
  }


  public static String getRequestIpAddress(jakarta.servlet.http.HttpServletRequest request) {
    var remoteAddress = request.getRemoteAddr();
    if (isLocalAddress(request)) {
      try {
        InetAddress address = InetAddress.getLocalHost();
        remoteAddress = address.getHostAddress();
      } catch (UnknownHostException e) {
        remoteAddress = LOCAL_HOST_IP;
      }
    }
    return remoteAddress;
  }


}
