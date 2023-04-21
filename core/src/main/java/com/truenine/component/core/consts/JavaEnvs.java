package com.truenine.component.core.consts;

import java.util.Properties;


/**
 * java 环境变量封装
 * 利用 System.getProperties 来获取一些简单的信息
 *
 * @author TrueNine
 * @since 2022-09-24
 * @deprecated 有更好的替代方式
 */
@Deprecated
public sealed abstract class JavaEnvs permits Java17PropertyKeys {


  /**
   * 使用工厂方法获取实例
   *
   * @return {@link JavaEnvs}
   */
  public static JavaEnvs o() {
    return new Java17PropertyKeys();
  }

  protected static Properties f() {
    return System.getProperties();
  }

  public abstract String jVersion();

  public abstract String userDir();

  public abstract String userHome();

  public abstract String arch();

  public abstract String osName();

  public abstract boolean osIsWin();

  public abstract String fileSep();

  public abstract String pathSep();

  public abstract String lineSep();

  public abstract String encodeN();

  static class J17Keys {
    public static final String USER_NAME = "user.name";
    public static final String USER_DIR = "user.dir";
    public static final String User_HOME = "user.home";
    public static final String VERSION = "java.specification.version";
    public static final String CPU_ARCH = "sun.cpu.isalist";
    public static final String OS_ARCH = "os.arch";
    public static final String OS_NAME = "os.name";

    public static final String SYS_ENCODING = "native.encoding";
    public static final String SYS_FILE_SEPARATOR = "file.separator";
    public static final String SYS_PATH_SEPARATOR = "path.separator";
    public static final String SYS_LINE_SEPARATOR = "line.separator";

  }
}
