import { AnyEntity, AttachmentEntityResponseResult } from "./BaiscEntities";

export default {};

export interface BrandEntityRequestParam {
  ordered: number;
  logoImgId?: bigint;
  title: string;
  doc?: string;
}

export interface BrandEntityResponseResult extends BrandEntityRequestParam, AnyEntity {
  logoImage?: AttachmentEntityResponseResult;
}

export interface CategoryEntityRequestParam {
  ordered: number;
  title: string;
  doc?: string;
}
