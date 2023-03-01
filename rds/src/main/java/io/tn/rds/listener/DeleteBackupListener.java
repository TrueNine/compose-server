package io.tn.rds.listener;

import io.tn.rds.event.DelEvent;
import jakarta.persistence.PreRemove;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 备份删除监听器
 *
 * @author TrueNine
 * @since 2022-12-13
 */
@Slf4j
@Component
public class DeleteBackupListener {

  private ApplicationEventPublisher pub;

  public DeleteBackupListener() {
    log.info("注册数据删除监听器 = {}", this.getClass());
  }

  @Autowired
  public void setPub(ApplicationEventPublisher pub) {
    this.pub = pub;
  }

  @PreRemove
  void a(Object obj) {
    if (null != obj) {
      pub.publishEvent(new DelEvent(obj));
    }
  }
}
