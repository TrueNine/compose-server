package net.yan100.compose.core.consts;

/**
 * 常用正则表达式常量
 *
 * @author TrueNine
 * @since 2023-04-19
 */
public interface Regexes {
  /**
   * 密码
   */
  String PASSWORD = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[^\\da-zA-Z\\s]).{1,9}$";
  /**
   * 中国的手机
   */
  String CHINA_PHONE = "^1[123456789]\\d{9}$";
}
