package net.yan100.compose.core.lang;

import net.yan100.compose.core.consts.FileDescriptions;
import net.yan100.compose.core.consts.JavaEnvs;

import javax.annotation.Nullable;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.jar.JarFile;

/**
 * 系统资源定位器
 *
 * @author TrueNine
 * @since 2022-10-29
 */
@Deprecated
public class ResourcesLocator {
  private static final String JAR_PROTOCOL = "jar";
  private static final String FILE_PROTOCOL = "file";
  private static final Class<ResourcesLocator> CLASS_SELF = ResourcesLocator.class;
  private static final ClassLoader LOADER_SELF = CLASS_SELF.getClassLoader();
  private static final String GENERATED_DIR_NAME_SPACE = ".generated";
  private static final String TEMP_DIR_NAME_SPACE = ".temp";
  private static final String APPLICATION_PROTOCOL;
  private static final File APPLICATION_DIR_FILE;
  private static final File GENERATED_DIR_FILE;
  private static final File TEMP_DIR_FILE;
  private static final Set<String> ALL_CLASS_NAME = new ConcurrentSkipListSet<>();
  private static String rootPath;

  static {
    APPLICATION_PROTOCOL = initApplicationProtocol();
    APPLICATION_DIR_FILE = initApplicationDirectory();
    TEMP_DIR_FILE = new File(APPLICATION_DIR_FILE, TEMP_DIR_NAME_SPACE);
    GENERATED_DIR_FILE = new File(APPLICATION_DIR_FILE, GENERATED_DIR_NAME_SPACE);
    // 创建临时文件夹
    var a = TEMP_DIR_FILE.mkdirs();
    var b = GENERATED_DIR_FILE.mkdirs();

    initDefinedClasses();
  }

  private ResourcesLocator() {
  }

  private static String initApplicationProtocol() {
    return Objects.requireNonNull(ResourcesLocator.class.getResource("/")).getProtocol();
  }

  private static void initDefinedClasses() {
    if (FILE_PROTOCOL.equals(APPLICATION_PROTOCOL)) {
      rootPath = Objects.requireNonNull(CLASS_SELF.getResource("/"))
        .toString().replace("file:/", Str.EMPTY)
        .replace("\\", "/");
      scanFilePackages(new File(rootPath));
    } else if (JAR_PROTOCOL.equals(APPLICATION_PROTOCOL)) {
      rootPath = CLASS_SELF.getProtectionDomain().getCodeSource().getLocation().getPath()
        .replace("file:/", Str.EMPTY)
        .split("!")[0];
      scanJarPackages(new File(rootPath));
    }
  }

  private static void scanJarPackages(File jarfile) {
    try (JarFile file = new JarFile(jarfile)) {
      var entries = file.entries();
      while (entries.hasMoreElements()) {
        var entry = entries.nextElement();
        var name = entry.getName();
        if (name.endsWith(FileDescriptions.JAVA_CLASS)) {
          var definedClassName = name
            .replace("\\", "/")
            .replace(rootPath, Str.EMPTY)
            .replace(FileDescriptions.JAVA_CLASS, Str.EMPTY)
            .replace("/", ".");
          ALL_CLASS_NAME.add(definedClassName);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void scanFilePackages(File classNode) {
    var fs = Objects.requireNonNull(classNode.listFiles());
    for (File child : fs) {
      if (child.isDirectory()) {
        scanFilePackages(child);
      } else {
        if (child.getName().endsWith(FileDescriptions.JAVA_CLASS)) {
          var defineClassName =
            child.getAbsolutePath()
              .replace("\\", "/")
              .replace(rootPath, Str.EMPTY)
              .replace(FileDescriptions.JAVA_CLASS, Str.EMPTY)
              .replace("/", ".")
              .replace("BOOT-INF.classes.", Str.EMPTY);
          ALL_CLASS_NAME.add(defineClassName);
        }
      }
    }
  }

  public static Set<String> getClassNamePool() {
    return ALL_CLASS_NAME;
  }

  private static File initApplicationDirectory() {
    // 初始化根路径
    URI uri;
    try {
      uri = Objects.requireNonNull(
        ResourcesLocator.class.getResource("/")
      ).toURI();
    } catch (URISyntaxException e) {
      throw new RuntimeException("获取文件根路径异常", e);
    }

    // 判断 是否为 jar 环境
    if (JAR_PROTOCOL.equals(APPLICATION_PROTOCOL)) {
      var meta = uri.toString();
      var jar = meta.replace("jar:file:/", "").split("!/")[0];
      return new File(jar).getParentFile();
    } else if (FILE_PROTOCOL.equals(APPLICATION_PROTOCOL)) {
      Path path = Path.of(uri);
      return path.toFile();
    } else {
      return null;
    }
  }

  public static URL classpathUrl(String internalPath) {
    return LOADER_SELF.getResource(internalPath);
  }

  public static InputStream classpathInputStream(String internalPath) {
    try {
      return LOADER_SELF.getResourceAsStream(internalPath);
    } catch (Throwable e) {
      return null;
    }
  }

  public static BufferedReader classpathReader(String internalPath) {
    return new BufferedReader(new InputStreamReader(Objects.requireNonNull(
      classpathInputStream(internalPath)
    )));
  }

  public static byte[] readClasspathByte(String internalPath) {
    try (var ins = classpathInputStream(internalPath)) {
      assert ins != null;
      return ins.readAllBytes();
    } catch (IOException | NullPointerException e) {
      return null;
    }
  }


  /**
   * 返回执行目录，区别如下：
   * 1. 在 IDE 内 为项目根目录
   * 2. 在 命令行执行情况下，为当前命令行的执行路径
   * 3. 如果在被打包成 jar 后，为当前命令行的执行路径
   * 其原理使用 系统属性 user.dir 实现
   * <p>
   * 万不可将其作为获取 resources 的方式
   *
   * @return 执行目录
   */
  public static File getExecuteDir() {
    return new File(JavaEnvs.o().userDir());
  }

  public static File getGenerateDir() {
    var gen = new File(getExecuteDir(), "/" + GENERATED_DIR_NAME_SPACE);
    var created = gen.mkdirs();
    return gen;
  }

  public static String getGenerateDirPath() {
    return getGenerateDir().getAbsolutePath().replace("\\", "/");
  }

  /**
   * 获取一个根路径文件，区别如下：
   * 1. 处于 ide 或者 classes 环境时，和 classes 同级
   * 2. 处于 jar 时，为 jar 同级目录
   *
   * @return root dir
   */
  public static File getAppDir() {
    return APPLICATION_DIR_FILE;
  }

  public static File getTempDir() {
    return TEMP_DIR_FILE;
  }

  public static String getExecuteTempDirectoryPath() {
    return getTempDir().getAbsolutePath().replace("\\", "/");
  }

  public static File createTempFile(String filename) {
    var f = new File(TEMP_DIR_FILE, filename);
    try {
      var c = f.getParentFile().mkdirs();
      var b = f.createNewFile();
    } catch (IOException e) {
      throw new RuntimeException("创建文件失败", e);
    }
    return f;
  }

  public static File createTempDir(String dirname) {
    var f = new File(TEMP_DIR_FILE, dirname);
    var b = f.mkdirs();
    return f;
  }

  public static @Nullable File createGenerateFile(String path, String name) {
    try {
      var gen = getGenerateDir();
      var parent = new File(gen, path);
      var b = parent.mkdirs();
      var newFile = new File(parent, name);
      var c = newFile.createNewFile();
      return newFile;
    } catch (IOException e) {
      return null;
    }
  }

  public static File createTempFile(String path, String name) {
    return createTempFile(path + "/" + name);
  }

  public static File createTempFile(String path, String name, String descriptor) {
    return createTempFile(path, name + "." + descriptor);
  }
}
