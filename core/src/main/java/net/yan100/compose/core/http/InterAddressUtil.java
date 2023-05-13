package net.yan100.compose.core.http;


import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author TrueNine
 * @since 2022-10-28
 * @deprecated 该类等待修缮
 */
@Deprecated
public interface InterAddressUtil {
  String LOCAL_HOST_IP = "127.0.0.1";
  String LOCAL_HOST_V6 = "0:0:0:0:0:0:0:1";
  String LOCAL_HOST = "localhost";


  static boolean isLocalAddress(jakarta.servlet.http.HttpServletRequest request) {
    var remoteHost = request.getRemoteAddr();
    if (LOCAL_HOST_IP.equalsIgnoreCase(remoteHost)) {
      return true;
    }
    if (LOCAL_HOST.equals(remoteHost)) {
      return true;
    }
    return LOCAL_HOST_V6.equalsIgnoreCase(remoteHost);
  }


  static String getRequestIpAddress(jakarta.servlet.http.HttpServletRequest request) {
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
