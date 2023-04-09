package com.truenine.component.core.consts;

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
  String LOGIC_DELETE_FLAG = "ldf";
  String LEFT_NODE = "rln";
  String RIGHT_NODE = "rrn";
  String LOCK_VERSION = "rlv";
  String PARENT_ID = "rpi";
  String ANY_REFERENCE_ID = "ari";
  String ANY_REFERENCE_TYPE = "typ";
  String TENANT_ID = "rti";

  static List<String> getAll() {
    var al = new String[]{
      ID,
      LOCK_VERSION,
      LEFT_NODE,
      RIGHT_NODE,
      PARENT_ID,
      ANY_REFERENCE_ID,
      ANY_REFERENCE_TYPE,
      TENANT_ID
    };
    return Arrays.asList(al);
  }

  interface Tenant {
    String ROOT_TENANT = Rbac.ROOT_ID;
    String DEFAULT_TENANT = Rbac.ROOT_ID;
  }

  interface Rbac {
    String ROOT_ID = "0";
    String USER_ID = "1";
    String ADMIN_ID = "2";
    String VIP_ID = "3";
  }
}
