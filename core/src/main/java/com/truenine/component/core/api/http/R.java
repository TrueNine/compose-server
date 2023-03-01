package com.truenine.component.core.api.http;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 响应结果包装
 *
 * @author TrueNine
 * @since 2022-09-24
 */
@Schema(title = "全局返回包装器")
public class R<D>
  implements Cloneable, Serializable {
  @JsonIgnore
  @Expose(deserialize = false)
  private static final Status _200 = Status.valueOf(200);

  @JsonIgnore
  @Expose(deserialize = false)
  private static final R<?> PROTOTYPE = new R<>();
  @JsonIgnore
  @Expose(deserialize = false)
  private static ObjectMapper mapper = null;

  @Nonnull
  @Schema(title = "是否请求成功", defaultValue = "true", example = "true")
  private Boolean ok = false;

  @Nullable
  @Schema(title = "http 消息", defaultValue = "ok", example = "ok")
  private String msg;

  @Nullable
  @Schema(title = "提示消息", defaultValue = "请求成功", example = "请求成功")
  private String alt;

  @Nonnull
  @Schema(title = "http 响应码", defaultValue = "200", example = "200")
  private Integer code = Status._509.getCode();

  @Nullable
  @Schema(title = "携带数据", defaultValue = "null", example = "{\"name\": \"张三\"}")
  private Object data;

  private R() {
  }

  public static <D> R<D> successfully(Integer code) {
    return successfully(Status.valueOf(code));
  }

  public static R<Object> successfully() {
    return R.successfully(Status._200);
  }

  public static <D> R<D> successfully(D data) {
    return successfully(data, Status._200);
  }

  public static <D> R<D> successfully(D data, Integer code) {
    return successfully(data, Status.valueOf(code));
  }

  public static <D> R<D> successfully(Status status) {
    return successfully(null, status);
  }

  public static <D> R<D> successfully(D data, Status status) {
    return creator(status.isOk(), status.getCode(), status.getAlert(), status.getMessage(), data);
  }

  public static <D> R<D> successfully(D data, String alt) {
    return creator(_200.isOk(), _200.getCode(), alt, _200.getMessage(), data);
  }

  public static R<Object> failed(Integer code) {
    return failed(Status.valueOf(code));
  }

  public static R<Object> failed(Throwable exception, Integer code) {
    return failed(exception, Status.valueOf(code));
  }

  public static R<Object> failed(Throwable exception, Integer code, String msg) {
    return failed(exception, Status.valueOf(code));
  }

  public static R<Object> failed(Status status) {
    return failed(null, status);
  }

  public static R<Object> failed(Throwable exception, Status status) {
    return creator(status.isOk(),
      status.getCode(),
      status.getAlert(),
      status.getMessage(),
      exception != null ? exception.getClass() : null);
  }

  public static <D> R<D> failed(D data, Status status) {
    return creator(status.isOk(), status.getCode(), status.getAlert(), status.getMessage(), data);
  }

  public static <D> R<D> failed(D exception, Integer code) {
    return failed(exception, Status.valueOf(code));
  }

  public static <D> R<D> failed(D exception) {
    return failed(exception, Status._500);
  }

  @SuppressWarnings("unchecked")
  private static <D> R<D> creator(Boolean ok, Integer code, String alt, String msg, D data) {
    var r = (R<D>) PROTOTYPE.clone();
    r.setAlt(alt);
    r.setMsg(msg);
    r.setOk(ok);
    r.setCode(code);
    r.setData(data);
    return r;
  }

  public static void includeMapper(ObjectMapper mapper) {
    if (null == R.mapper) {
      R.mapper = mapper;
    }
  }

  public Map<String, Object> asMap() {
    var res = new HashMap<String, Object>(4);
    res.put("msg", msg);
    res.put("alt", alt);
    res.put("code", code);
    res.put("data", data);
    return res;
  }

  @Override
  @SuppressWarnings("unchecked")
  public R<D> clone() {
    R<D> clone;
    try {
      clone = (R<D>) super.clone();
      return clone;
    } catch (CloneNotSupportedException e) {
      clone = new R<>();
    }
    return clone;
  }

  public void writeJson(final HttpServletResponse response) throws IOException {
    response.setStatus(this.code);
    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaTypes.JSON.media());
    final var writer = response.getWriter();
    var status = Status.valueOf(response.getStatus());
    writer.print(
      mapper.writeValueAsString(
        R.creator(response.getStatus() < 400,
          status.getCode(),
          status.getAlert(),
          status.getMessage(),
          data)
      )
    );
    writer.flush();
  }

  @SneakyThrows
  @Override
  public String toString() {
    if (this.data instanceof Throwable e) {
      var data = e.getClass().getName();
      var code = getCode();
      var msg = getMsg();
      var alt = getAlt();
      var r = clone();
      r.data = data;
      r.msg = msg;
      r.alt = alt;
      r.code = code;
    }
    return "R = {"
      + "ok = " + ok + ", "
      + "alt = " + alt + ", "
      + "msg = " + msg + ", "
      + "data = " + data
      + "}";
  }

  @Override
  public boolean equals(Object obj) {
    if (Objects.isNull(obj)) {
      return false;
    } else if (obj.hashCode() == this.hashCode()) {
      return true;
    } else if (obj instanceof R<?> that) {
      return
        that.ok.equals(ok)
          && that.alt.equals(alt)
          && that.msg.equals(msg)
          && Objects.equals(that.data, data);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(ok, msg, alt, data);
  }

  /*
   * getters and setters
   * 所有set 不对外公开
   */

  private void setOk(Boolean ok) {
    this.ok = ok;
  }

  @SuppressWarnings("unchecked")
  public D getData() {
    return (D) data;
  }

  private void setData(D data) {
    this.data = data;
  }

  public Integer getCode() {
    return code;
  }

  private void setCode(Integer code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  private void setMsg(String msg) {
    this.msg = msg;
  }

  public String getAlt() {
    return alt;
  }

  private void setAlt(String alt) {
    this.alt = alt;
  }

  public Boolean isOk() {
    return ok;
  }
}
