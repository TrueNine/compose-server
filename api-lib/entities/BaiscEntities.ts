import { PointModel } from "../defineds/DataType";
import { AttachmentStorageTyping, GenderTyping } from "../enums/BasicEnums";

export default {};

export interface AnyEntity {
  id: number;
}

export interface AttachmentLocationEntityRequestParam extends AnyEntity {
  baseUrl: string;
  name: string;
  doc?: string;
  type?: AttachmentStorageTyping;
}

export interface AttachmentLocationEntityResponseResult extends AttachmentLocationEntityRequestParam, AnyEntity {
  rn: boolean;
}

export interface AttachmentEntityRequestParam {
  attachmentLocationId?: number;
  location?: AttachmentLocationEntityRequestParam;
  metaName: string;
  saveName: string;
  size: number;
  mimeType: string;
}

export interface AttachmentEntityResponseResult extends AttachmentEntityRequestParam, AnyEntity {
  fullPath: string;
  location?: AttachmentLocationEntityResponseResult;
}

export interface UserEntityRequestParam {
  account: string;
  nickName: string;
  doc?: string;
  pwdEnc?: string;
}

export interface UserEntityResponseResult extends UserEntityRequestParam, AnyEntity {
  lastLoginTime?: Date;
  info?: UserInfoEntityResponseResult;
  band: boolean;
  roleGroups: RoleGroupEntityResponseResult[];
  banTime?: Date;
}

export interface RoleGroupEntityRequestParam {
  name: string;
  doc?: string;
}

export interface RoleGroupEntityResponseResult extends RoleGroupEntityRequestParam, AnyEntity {
  roles: RoleEntityResponseResult[];
}

export interface RoleEntityRequestParam {
  name: string;
  doc?: string;
}

export interface RoleEntityResponseResult extends RoleEntityRequestParam {
  permissions: PermissionsResponseResult[];
}

export interface PermissionsRequestParam {
  name: string;
  doc?: string;
}

export interface PermissionsResponseResult extends PermissionsRequestParam, AnyEntity {}

export interface UserInfoEntityRequestParam {
  userId: bigint;
  avatarImgId?: bigint;
  avatarImage?: AttachmentEntityRequestParam;
  firstName: string;
  lastName: string;
  email?: string;
  birthday?: Date;
  addressDetailsId?: number;
  addressDetails?: AddressDetailsEntityRequestParam;
  phone?: string;
  idCard?: string;
  gender: GenderTyping;
  wechatOpenId?: string;
  wechatOauth2Id?: string;
}

export interface UserInfoEntityResponseResult extends UserInfoEntityRequestParam, AnyEntity {
  user: UserEntityResponseResult;
  avatarImage?: AttachmentEntityResponseResult;
  fullName: string;
  addressDetails?: AddressDetailsEntityResponseResult;
}

export interface AddressDetailsEntityRequestParam {
  addressId: number;
  addressDetails: string;
  center?: PointModel;
}

export interface AddressDetailsEntityResponseResult extends AddressDetailsEntityRequestParam, AnyEntity {
  address: AddressEntityResponseResult;
}

export interface AddressEntityRequestParam {
  code?: string;
  name: string;
}

export interface AddressEntityResponseResult extends AddressEntityRequestParam, AnyEntity {
  level?: number;
  details?: AddressDetailsEntityResponseResult[];
}
