export default {};

export interface RouteOption {
  name: string;
  uri?: string;
  href?: string;
  tags?: string[];
  disabled?: true;
  sub?: RouteOption[];
}

export const MenuRoutes: RouteOption[] = [
  {
    name: "概览",
    uri: "workbench",
    tags: ["开发中"],
  },
  {
    name: "数据统计",
    uri: "statistics",
    sub: [
      {
        name: "数据总览",
        uri: "all",
      },
      {
        name: "领导端",
        uri: "leader",
      },
      {
        name: "网点端",
        uri: "endpoint",
      },
      {
        name: "渠道端",
        uri: "provider",
      },
      {
        name: "维护端",
        uri: "dev",
      },
    ],
  },
  {
    name: "工作台",
    uri: "workbench",
    sub: [
      {
        name: "超管",
        uri: "admin",
        tags: ["BETA"],
      },
      {
        name: "客服",
        tags: ["开发中"],
        uri: "customer",
      },
      {
        name: "渠道",
        uri: "channel",
      },
      {
        name: "网点",
        uri: "endpoint",
      },
      {
        name: "分销",
        uri: "distribution/group",
      },
      {
        name: "服务商",
        uri: "provider",
      },
    ],
  },
  {
    name: "订单",
    uri: "order",
    sub: [
      {
        name: "订单手动录入",
        uri: "input",
        tags: ["开发中"],
      },
      {
        name: "订单跟踪",
        uri: "query",
      },
      {
        name: "外部订单导入",
        uri: "import",
        tags: ["随便"],
      },
      {
        name: "订单导出",
        uri: "export",
      },
      {
        name: "订单配置",
        uri: "config",
      },
    ],
  },
  {
    name: "维保",
    uri: "maintenance",
    sub: [
      {
        name: "维保跟踪",
        uri: "query",
      },
      {
        name: "维保管理",
        uri: "admin",
      },
    ],
  },
  {
    name: "用户",
    uri: "user",
    sub: [
      {
        name: "用户信息管理",
        uri: "info/admin",
      },
    ],
  },
  {
    name: "渠道",
    uri: "channel",
    sub: [
      {
        name: "渠道管理",
        uri: "admin",
      },
    ],
  },
  {
    name: "服务商",
    uri: "provider",
    sub: [
      {
        name: "服务商管理",
        uri: "admin",
      },
    ],
  },
  {
    name: "网点",
    uri: "endpoint",
    sub: [
      {
        name: "网点管理",
        uri: "admin",
      },
    ],
  },
  {
    name: "资源",
    uri: "resources",
    sub: [
      {
        name: "总览",
        uri: "all",
      },
      {
        name: "服务",
        uri: "services",
      },
      {
        name: "商品",
        uri: "goods",
      },
      {
        name: "维保",
        uri: "maintenance",
      },
      {
        name: "类目/分类",
        uri: "category",
      },
      {
        name: "品牌",
        uri: "brand",
      },
      {
        name: "物流",
        uri: "delivery",
        sub: [
          {
            name: "配送规则配置",
            uri: "rule/config",
          },
        ],
      },
    ],
  },
  {
    name: "仓库",
    uri: "repository",
  },
  {
    name: "营销",
    uri: "marketing",
    disabled: true,
    sub: [
      {
        name: "优惠券",
        uri: "coupons",

        sub: [
          {
            name: "优惠券配置",
            uri: "config",
          },
          {
            name: "礼品卡",
            uri: "physical",
          },
        ],
      },
      {
        name: "轮播图",
        disabled: true,
        uri: "swiper",
      },
      {
        name: "VIP",
        uri: "vip",
        sub: [
          {
            name: "基础配置",
            uri: "config",
          },
          {
            name: "奖励/勋章",
            uri: "reward",
          },
        ],
      },
      {
        name: "储值",
        disabled: true,
        uri: "recharge",
      },
      {
        name: "积分",
        disabled: true,
        uri: "points",
      },
    ],
  },
  {
    name: "分销",
    uri: "distribution",
    sub: [
      {
        name: "分销跟踪",
        uri: "query",
      },
      {
        name: "个人分销配置",
        uri: "user/config",
      },
      {
        name: "分销组配置",
        uri: "group/config",
      },
      {
        name: "分销组内配置",
        uri: "employee/config",
      },
    ],
  },
  {
    name: "系统配置",
    disabled: true,
    uri: "system",
    sub: [
      {
        name: "通用配置",
        uri: "uni/config",
      },
      {
        name: "支付配置",
        uri: "payment/config",
      },
    ],
  },
  {
    name: "向开发反馈",
    uri: "feedback",
    tags: ["开发中"],
  },
];
