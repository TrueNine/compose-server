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
package net.yan100.compose.rds.core.consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface BaseTableNames {
    String USER = "user";
    String USER_INFO = "user_info";
    String USER_DEPT = "user_dept";
    String DEPT = "dept";
    String ROLE = "role";
    String ROLE_GROUP = "role_group";
    String PERMISSIONS = "permissions";
    String ROLE_PERMISSIONS = "role_permissions";
    String ATTACHMENT = "attachment";
    String ATTACHMENT_LOCATION = "attachment_location";
    String API = "api";
    String API_CALL_RECORD = "api_call_record";
    String ROLE_GROUP_ROLE = "role_group_role";
    String TABLE_ROW_CHANGE_RECORD = "table_row_change_record";
    String TABLE_ROW_DELETE_RECORD = "table_row_delete_record";
    String USER_GROUP = "user_group";
    String USER_GROUP_ROLE_GROUP = "user_group_role_group";
    String USER_GROUP_USER = "user_group_user";
    String USER_ROLE_GROUP = "user_role_group";
    String ADDRESS = "address";
    String ADDRESS_DETAILS = "address_details";
    String FLYWAY_SCHEMA_HISTORY = "flyway_schema_history";

    static List<String> all() {
        return new ArrayList<>(
                Arrays.asList(
                        ADDRESS,
                        ADDRESS_DETAILS,
                        USER,
                        USER_INFO,
                        USER_DEPT,
                        ROLE_GROUP,
                        DEPT,
                        ROLE,
                        ROLE_PERMISSIONS,
                        PERMISSIONS,
                        ATTACHMENT,
                        ATTACHMENT_LOCATION,
                        API,
                        API_CALL_RECORD,
                        ROLE_GROUP_ROLE,
                        TABLE_ROW_CHANGE_RECORD,
                        TABLE_ROW_DELETE_RECORD,
                        USER_GROUP,
                        USER_GROUP_ROLE_GROUP,
                        USER_GROUP_USER,
                        USER_ROLE_GROUP,
                        FLYWAY_SCHEMA_HISTORY));
    }
}
