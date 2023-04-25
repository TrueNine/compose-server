import {AnyEntity, AttachmentEntityResponseResult} from "./BaiscEntities";

export default {};

export interface BrandEntityRequestParam {
  ordered: number;
  logoImgId?: bigint;
  title: string;
  doc?: string;
}


/**
 * 品牌
 */
export interface BrandEntityResponseResult extends BrandEntityRequestParam, AnyEntity {
  logoImage?: AttachmentEntityResponseResult;
}

/**
 * 分类
 */
export interface CategoryEntityRequestParam {
  ordered: number;
  title: string;
  doc?: string;
}

/**
 * 分类
 */
export interface CategoryEntityResponseResult extends CategoryEntityRequestParam, AnyEntity {

}
