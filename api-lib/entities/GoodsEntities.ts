import { BrandEntityRequestParam, BrandEntityResponseResult, CategoryEntityRequestParam } from "./SiftEntities";
import { AnyEntity, AttachmentEntityRequestParam, UserEntityResponseResult } from "./BaiscEntities";
import { GoodsChangeRecordTyping, GoodsTyping } from "../enums/GoodsEnums";

export default {};

export interface GoodsInfoEntityRequestParam {
  goodsSellingInfoId: number;
  sellingInfo?: GoodsSellingInfoEntityRequestParam;
  type: GoodsTyping;
  title: string;
  secondaryTitle?: string;
  brandId?: number;
  brand?: BrandEntityRequestParam;
  categoryId?: bigint;
  category?: CategoryEntityRequestParam;
  weightG: number;
  unit: string;
  costPrice: number;
  sellingPrice: number;
  limitSelling: number;
  miniSelling: number;
  serviceDurationTime: string;
  providerPayloadItems: string[];
  customerReadyItems: string[];
}

export interface GoodsInfoEntityResponseResult extends GoodsInfoEntityRequestParam, AnyEntity {
  sellingInfo: GoodsSellingInfoEntityResponseResult;
  goodsInfoCode: string;
  brand?: BrandEntityResponseResult;
  category?: CategoryEntityRequestParam;
  goodsParams: GoodsParamsEntityRequestParam[];
  detailsImages?: GoodsInfoDetailsImagesEntityResponseResult[];
}

export interface GoodsUnitEntityRequestParam {
  extendsGoodsInfoId: number;
  goodsInfoId: number;
  forever: boolean;
  quantity: number;
  activated: boolean;
  specifications?: GoodsUnitSpecificationEntityRequestParam[];
}

export interface GoodsUnitEntityResponseResult extends GoodsUnitEntityRequestParam, AnyEntity {
  extendsGoodsInfo: GoodsInfoEntityResponseResult;
  info: GoodsInfoEntityResponseResult;
  goodsCode: string;
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

export interface GoodsInfoDetailsImagesEntityRequestParam {
  imgId: number;
  image: AttachmentEntityRequestParam;
  goodsInfoId: number;
  ordered: number;
}

export interface GoodsInfoDetailsImagesEntityResponseResult extends GoodsInfoDetailsImagesEntityRequestParam, AnyEntity {
  goodsInfo?: GoodsInfoEntityResponseResult[];
}

export interface GoodsUnitChangeRecordEntityRequestParam {
  changeType: GoodsChangeRecordTyping;
  goodsUnitId: number;
  modifierUserId: number;
  newPrice?: number;
  newTitle?: string;
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

export interface GoodsSellingInfoEntityResponseResult extends GoodsSellingInfoEntityRequestParam, AnyEntity {}

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
