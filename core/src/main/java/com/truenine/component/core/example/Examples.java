package com.truenine.component.core.example;


import com.truenine.component.core.annotations.BetaTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 例子
 * java 例子收藏
 *
 * @author TrueNine
 * @since 2022-10-28
 * @deprecated 这只是一个写例子的类
 */
@Deprecated
public class Examples {

  /**
   * 尝试读取 jar 内的 class 文件并执行
   *
   * @return url
   * @throws URISyntaxException url
   * @throws IOException        url
   */
  @Deprecated
  @BetaTest
  public URL wrsRoot() throws URISyntaxException, IOException {
    var root = Exception.class.getResource("");
    var uri = root.toURI();
    System.out.println(uri);
    var jar = "jar";
    if (uri.toString().contains(jar)) {
      var basePath = uri.toString().split("!/")[0] + "!/";
      var path = uri.toString().split("!/")[0].substring(10);
      System.out.println(path);

      var jf = new JarFile(path);
      try (jf) {
        var f = jf.entries();
        while (f.hasMoreElements()) {
          JarEntry jarEntry = f.nextElement();
          String named = jarEntry.getName();
          System.out.println(named);
          var clzPath = basePath + named;
          if (clzPath.endsWith(".class")) {
            var urlLoader = new URLClassLoader(new URL[]{new URL(path)});
            try (urlLoader) {
              var clz = urlLoader.loadClass(clzPath);
              System.out.println(clz);
            } catch (ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
          }
        }
      }
    } else {
      System.out.println(root);
    }
    return Examples.class.getResource("");
  }
}
