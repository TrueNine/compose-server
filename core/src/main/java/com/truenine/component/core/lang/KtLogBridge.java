package com.truenine.component.core.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KtLogBridge {
  public static Logger getLog(Class<?> clazz) {
    return LoggerFactory.getLogger(clazz);
  }
}