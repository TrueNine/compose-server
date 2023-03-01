package com.truenine.component.core.db;

import java.util.Arrays;
import java.util.List;

/**
 * 数据库基础字段
 *
 * @author TrueNine
 * @since 2022-12-04
 */
public class Bf {
  public static final String DEL_STATE = "cds";
  public static final String ID = "id";
  public static final String CREATE_BY = "ccb";
  public static final String LOGIC_DELETE_FLAG = "ldf";
  public static final String CREATE_TIME = "cct";
  public static final String MODIFY_BY = "cmb";
  public static final String MODIFY_TIME = "cmt";
  public static final String LEFT_NODE = "cln";
  public static final String RIGHT_NODE = "crn";
  public static final String LOCK_VERSION = "clv";
  public static final String PARENT_ID = "cpi";
  public static final String ANY_REFERENCE_ID = "ari";
  public static final String ANY_REFERENCE_TYPE = "typ";
  public static final String TREE_GROUP_UID = "cgu";
  public static final String TENANT_ID = "cti";

  public static List<String> defaultAll() {
    return List.of(
      ID,
      DEL_STATE,
      CREATE_BY,
      CREATE_TIME,
      MODIFY_BY,
      MODIFY_TIME,
      LOCK_VERSION,
      TENANT_ID
    );
  }

  public static List<String> getAll() {
    var al = new String[]{
      ID,
      DEL_STATE,
      CREATE_BY,
      CREATE_TIME,
      MODIFY_BY,
      MODIFY_TIME,
      LOCK_VERSION,
      LEFT_NODE,
      RIGHT_NODE,
      PARENT_ID,
      TREE_GROUP_UID,
      ANY_REFERENCE_ID,
      ANY_REFERENCE_TYPE,
      TENANT_ID
    };
    return Arrays.asList(al);
  }

  public static class Def {
    public static final String EMPTY_TYP = "0";
  }

  public static class Tenant {
    public static final String ROOT_TENANT = Rbac.ROOT_ID;
    public static final String DEFAULT_TENANT = Rbac.ROOT_ID;
  }

  public static class Rbac {
    public static final String ROOT_ID = "0";
    public static final String USER_ID = "1";
    public static final String ADMIN_ID = "2";
    public static final String VIP_ID = "3";
  }
}

