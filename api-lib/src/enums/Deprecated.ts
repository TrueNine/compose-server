export default {};

export const ContactStatus = [
  {
    l: "待联络",
    v: 1,
  },
  {
    l: "待跟进",
    v: 1,
  },
  {
    l: "已完成",
    v: 2,
  },
  {
    l: "已服务",
    v: 3,
  },
  {
    l: "已派单",
    v: 4,
  },
  {
    l: "已售后",
    v: 5,
  },
  {
    l: "不联络",
    v: 6,
  },
];

/**
 * 分销结款方式
 */
export const Payouts = [
  {
    l: "线下打款",
    v: 1,
  },
  {
    l: "银行卡支付",
    v: 2,
  },
  {
    l: "微信支付",
    v: 3,
  },
  {
    l: "支付宝支付",
    v: 4,
    d: true,
  },
  {
    l: "云闪付",
    v: 5,
    d: true,
  },
  {
    l: "paypal",
    v: 6,
    d: true,
  },
];

/**
 * 分销申请方式
 */
export const HowApply = [
  {
    l: "自动",
    v: 1,
  },
  {
    l: "申请",
    v: 2,
  },
  {
    l: "购买指定商品",
    v: 3,
  },
  {
    l: "累计消费",
    v: 4,
  },
];
