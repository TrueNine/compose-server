/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.core.consts;

import java.util.Locale;

/**
 * java17属性键
 *
 * @author TrueNine
 * @since 2022-10-28
 * @deprecated 有更好的替代方式
 */
@Deprecated
public final class Java17PropertyKeys extends JavaEnvs {
    Java17PropertyKeys() {}

    @Override
    public String jVersion() {
        return f().getProperty(J17Keys.VERSION);
    }

    @Override
    public String userDir() {
        return f().getProperty(J17Keys.USER_DIR);
    }

    @Override
    public String userHome() {
        return f().getProperty(J17Keys.User_HOME);
    }

    @Override
    public String arch() {
        return f().getProperty(J17Keys.CPU_ARCH);
    }

    @Override
    public String osName() {
        return f().getProperty(J17Keys.OS_NAME);
    }

    @Override
    public boolean osIsWin() {
        return this.osName().toLowerCase(Locale.ROOT).contains("win");
    }

    @Override
    public String fileSep() {
        return f().getProperty(J17Keys.SYS_FILE_SEPARATOR);
    }

    @Override
    public String pathSep() {
        return f().getProperty(J17Keys.SYS_PATH_SEPARATOR);
    }

    @Override
    public String lineSep() {
        return f().getProperty(J17Keys.SYS_LINE_SEPARATOR);
    }

    @Override
    public String encodeN() {
        return f().getProperty(J17Keys.SYS_ENCODING);
    }
}
