export default {};

/**
 * 分页参数入参请求
 */
export interface PageModelRequestParam {
  offset: number;
  pageSize: number;
}

/**
 * 分页后数据统一返回
 */
export interface PagedResponseResult<D> {
  dataList?: D[];
  total: number;
  size: number;
  pageSize: number;
  offset: number;
}

/**
 * 分页后随参数
 */
export const PageablePageSizes = [
  {
    label: "5 每页",
    value: 5,
  },
  {
    label: "10 每页",
    value: 10,
  },
  {
    label: "15 每页",
    value: 15,
  },
  {
    label: "20 每页",
    value: 20,
  },
  {
    label: "30 每页",
    value: 30,
  },
  {
    label: "42 每页",
    value: 42,
  },
];
