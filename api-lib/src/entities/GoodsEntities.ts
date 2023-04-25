import {BrandEntityRequestParam, BrandEntityResponseResult, CategoryEntityRequestParam, CategoryEntityResponseResult} from "./SiftEntities";
import {AnyEntity, AttachmentEntityRequestParam, UserEntityResponseResult} from "./BaiscEntities";
import {GoodsChangeRecordTyping, GoodsInfoTyping, GoodsTyping} from "../enums";
import {Duration} from "moment";

export default {};

/**
 * 商品信息
 */
export interface GoodsInfoEntityRequestParam {
  goodsSellingInfoId: number;
  title: string;
  secondaryTitle?: string;
  brandId?: number;
  categoryId?: bigint;
  weightG: number;
  unit: string;
  costPrice: number;
  sellingPrice: number;
  limitSelling: number;
  miniSelling: number;
  serviceDurationTime: Duration;
  providerPayloadItems: string[];
  customerReadyItems: string[];
  /**
   * 商品类型
   */
  type: GoodsTyping;
  /**
   * 商品信息类型
   */
  infoType: GoodsInfoTyping;
  sellingInfo?: GoodsSellingInfoEntityRequestParam;
  category?: CategoryEntityRequestParam;
  brand?: BrandEntityRequestParam;
  goodsParams?: GoodsParamsEntityRequestParam[];
  detailsImages?: GoodsInfoDetailsImagesEntityRequestParam[];
  goodsUnits?: GoodsUnitEntityRequestParam[];
}

/**
 * 商品信息
 */
export interface GoodsInfoEntityResponseResult extends GoodsInfoEntityRequestParam, AnyEntity {
  goodsInfoCode: string;
  sellingInfo: GoodsSellingInfoEntityResponseResult;
  category?: CategoryEntityResponseResult;
  brand?: BrandEntityResponseResult;
  goodsParams?: GoodsParamsEntityResponseResult[];
  detailsImages?: GoodsInfoDetailsImagesEntityResponseResult[];
  goodsUnits?: GoodsUnitEntityResponseResult[];
}

/**
 * 商品单位
 */
export interface GoodsUnitEntityRequestParam {
  extendsGoodsInfoId: number;
  goodsInfoId: number;
  forever: boolean;
  quantity: number;
  activated: boolean;
  extendsGoodsInfo?: GoodsInfoEntityRequestParam;
  specifications?: GoodsUnitSpecificationEntityRequestParam[];
  info?: GoodsInfoEntityRequestParam;
}

/**
 * 商品单位
 */
export interface GoodsUnitEntityResponseResult extends GoodsUnitEntityRequestParam, AnyEntity {
  goodsCode: string;
  extendsGoodsInfo: GoodsInfoEntityResponseResult;
  info: GoodsInfoEntityResponseResult;
  specifications: GoodsUnitSpecificationEntityResponseResult[];
  changeRecords?: GoodsUnitChangeRecordEntityResponseResult[];
}

export interface GoodsUnitSpecificationEntityRequestParam {
  specName: string;
  specValue: string;
}

export interface GoodsUnitSpecificationEntityResponseResult extends GoodsUnitSpecificationEntityRequestParam, AnyEntity {
  specCode: string;
}

export interface GoodsParamsEntityRequestParam {
  paramName: string;
  paramValue: string;
}

/**
 * 商品参数返回值
 */
export interface GoodsParamsEntityResponseResult extends GoodsParamsEntityRequestParam, AnyEntity {
}

export interface GoodsInfoDetailsImagesEntityRequestParam {
  imgId: number;
  ordered: number;
  goodsInfoId: number;
  image: AttachmentEntityRequestParam;
}

export interface GoodsInfoDetailsImagesEntityResponseResult extends GoodsInfoDetailsImagesEntityRequestParam, AnyEntity {
  goodsInfo?: GoodsInfoEntityResponseResult[];
}

export interface GoodsUnitChangeRecordEntityRequestParam {
  goodsUnitId: number;
  modifierUserId: number;
  newPrice?: number;
  newTitle?: string;
  changeType: GoodsChangeRecordTyping;
}

export interface GoodsUnitChangeRecordEntityResponseResult extends GoodsUnitChangeRecordEntityRequestParam, AnyEntity {
  oldPrice?: number;
  oldTitle?: string;
  modifierUser: UserEntityResponseResult;
  goodsUnit: GoodsUnitEntityResponseResult;
}

export interface GoodsSellingInfoEntityRequestParam {
  lostPrice: number;
  distributionPercent: number;
  givePoints: number;
  maxSellingPoints: number;
}

export interface GoodsSellingInfoEntityResponseResult extends GoodsSellingInfoEntityRequestParam, AnyEntity {
}

export interface GoodsGroupEntityRequestParam {
  goodsInfoId: number;
  title: string;
  doc?: string;
  info?: GoodsInfoEntityRequestParam;
}

export interface GoodsGroupEntityResponseResult extends GoodsGroupEntityRequestParam, AnyEntity {
  groupCode: string;
  goodsUnits?: GoodsUnitEntityResponseResult[];
}
