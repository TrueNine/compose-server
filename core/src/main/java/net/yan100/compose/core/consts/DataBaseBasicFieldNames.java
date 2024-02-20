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

import java.util.Arrays;
import java.util.List;

/**
 * 数据库基础字段
 *
 * @author TrueNine
 * @since 2022-12-04
 */
public interface DataBaseBasicFieldNames {
    String ID = "id";
    String CREATE_ROW_DATETIME = "crd";
    String MODIFY_ROW_DATETIME = "mrd";
    String LOGIC_DELETE_FLAG = "ldf";
    String LEFT_NODE = "rln";
    String RIGHT_NODE = "rrn";
    String NODE_LEVEL = "nlv";
    String TREE_GROUP_ID = "tgi";
    String LOCK_VERSION = "rlv";
    String PARENT_ID = "rpi";
    String ANY_REFERENCE_ID = "ari";
    String ANY_REFERENCE_TYPE = "typ";
    String TENANT_ID = "rti";

    static List<String> getAll() {
        var al =
                new String[] {
                    ID,
                    LOCK_VERSION,
                    CREATE_ROW_DATETIME,
                    MODIFY_ROW_DATETIME,
                    LEFT_NODE,
                    NODE_LEVEL,
                    TREE_GROUP_ID,
                    RIGHT_NODE,
                    PARENT_ID,
                    LOGIC_DELETE_FLAG,
                    ANY_REFERENCE_ID,
                    ANY_REFERENCE_TYPE,
                    TENANT_ID
                };
        return Arrays.asList(al);
    }

    interface Tenant {
        Long ROOT_TENANT = Rbac.ROOT_ID;
        String ROOT_TENANT_STR = ROOT_TENANT.toString();
        Long DEFAULT_TENANT = Rbac.ROOT_ID;
        String DEFAULT_TENANT_STR = DEFAULT_TENANT.toString();
    }

    interface Rbac {
        Long ROOT_ID = 0L;
        String ROOT_ID_STR = ROOT_ID.toString();
        Long USER_ID = 1L;
        String USER_ID_STR = USER_ID.toString();
        Long ADMIN_ID = 2L;
        String ADMIN_ID_STR = ADMIN_ID.toString();
        Long VIP_ID = 3L;
        String VIP_ID_STR = VIP_ID.toString();
    }
}
