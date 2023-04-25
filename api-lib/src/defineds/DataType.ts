export default {};

/**
 * 错误消息统一返回格式
 */
export interface ErrorMessage {
  msg?: string;
  code: number;
}

/**
 * 地址坐标数据类型
 */
export interface PointModel {
  x?: number;
  y?: number;
}

/**
 * http 返回头
 */
export const HttpHeaders = {
  contentType: "Content-Type",
  contentLength: "Content-Length",
};

/**
 * http mediaType
 */
export const HttpMediaTypes = {
  urlEncode: "application/x-www-form-urlencoded",
  json: "application/json",
};

/**
 * http 返回类型
 */
export const HttpRequestHeaders = {
  formType: {
    [HttpHeaders.contentType]: HttpMediaTypes.urlEncode,
  },
  jsonType: {
    [HttpHeaders.contentType]: HttpMediaTypes.json,
  },
};
