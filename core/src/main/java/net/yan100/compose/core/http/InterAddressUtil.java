/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.core.http;

import jakarta.annotation.Nullable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author TrueNine
 * @since 2022-10-28
 */
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
            } catch (Exception ignore) {
                remoteAddress = LOCAL_HOST_IP;
            }
        }
        return remoteAddress;
    }

    @Nullable static String getLocalHostName() {
        String hostName = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostName = addr.getHostName();
        } catch (Exception ignore) {
        }
        return hostName;
    }

    static List<String> getAllLocalHostIP() {
        List<String> result = new ArrayList<>();
        try {
            String hostName = getLocalHostName();
            if (hostName != null) {
                var addresses = InetAddress.getAllByName(hostName);
                result.addAll(
                        Arrays.stream(addresses)
                                .map(InetAddress::getHostAddress)
                                .collect(Collectors.toSet()));
            }
        } catch (Exception ignore) {
        }
        return result;
    }
}
