package net.yan100.compose.core.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class KtLogBridge {
  public static Logger getLog(Class<?> clazz) {
    return LoggerFactory.getLogger(clazz);
  }
}
