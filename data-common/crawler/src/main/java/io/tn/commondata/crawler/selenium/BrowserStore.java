package io.tn.commondata.crawler.selenium;

import lombok.Data;
import lombok.experimental.Accessors;
import org.openqa.selenium.Cookie;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 浏览器 当前的 localStorage session cookie
 * <br/>
 * 可以通过 {@link SeleniumTool} 的 getStore 获得
 *
 * @author TrueNine
 * @since 2022-10-25
 */
@Data
@Accessors(chain = true)
public class BrowserStore {
  List<Map<String, Object>> sessionStorage;
  List<Map<String, Object>> localStorage;
  Set<Cookie> cookies;
}
