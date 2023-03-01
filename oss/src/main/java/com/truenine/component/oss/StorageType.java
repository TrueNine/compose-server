package com.truenine.component.oss;


import com.truenine.component.core.dev.BetaTest;

/**
 * 存储类型
 *
 * @author TrueNine
 * @since 2022-10-28
 * @deprecated 字段属性不符合规范
 */
@BetaTest
@Deprecated
public enum StorageType {
  /**
   * 本地公共
   */
  L_PUBLIC("local_public"),
  /**
   * 本地个人
   */
  L_PERSONAL("local_personal"),
  /**
   * 本地 admin
   */
  L_ADMIN("local_admin"),
  /**
   * 远程 admin
   */
  R_ADMIN("remote_admin"),
  /**
   * 远程公共
   */
  R_PUBLIC("remote_public"),
  /**
   * 远程，个人
   */
  R_PERSONAL("remote_personal");
  private final String type;

  StorageType(String type) {
    this.type = type;
  }

  String val() {
    return this.type;
  }
}
