export default {};

/**
 * 商品类型
 */
export enum GoodsTyping {
  /**
   * 实体商品
   */
  PHYSICAL_GOODS = 0,
  /**
   * 服务商品
   */
  SERVICE_GOODS = 1,
  /**
   * 虚拟商品
   */
  VIRTUAL_GOODS = 2,
}

/**
 * 商品单位更改记录
 */
export enum GoodsChangeRecordTyping {
  /**
   * 改价格
   */
  CHANGE_PRICE = 0,
  /**
   * 改标题
   */
  CHANGE_TITLE = 1,
}
