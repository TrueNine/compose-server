# 关联关系

每个实体都继承 `AnyEntity`、`IEntity`、`ITreeEntity` 等的关系实体。

### 基础表定义

```mermaid
erDiagram
  AnyEntity["最基础的实体"] {
    id id "主键"
  }
  IEntity["基础表"] {
    long rlv "乐观锁版本号"
    bool ldf "逻辑删除标记"
    datetime crd "数据创建时间"
    datetime mrd "数据修改时间"
  }
  any["任意表（一般其他表以id外键过来"] {
    id id "主键"
  }
```

### 线索树表表示法

```mermaid
erDiagram
  TreeEntity["线索树表"] {
    str tgi "一组（一颗树）的统一表示码"
    id rpi "父节点id"
    long rln "左节点 值"
    long rrn "右节点 值"
    int nlv "节点深度"
  }
  TreeEntity ||--o| TreeEntity: rpi
```

### 用户

```mermaid
erDiagram
  usr["用户表"] {
  }
  usr |o--o| usr: create_user_id
```

### 用户信息

```mermaid
erDiagram
  user_info["用户信息表"] {
  }
  user_info |o--o| usr: user_id
  user_info |o--|| usr: create_user_id
  user_info }o--o| attachment: avatar_img_id
  user_info }o--o| address_details: address_details_id
  user_info }o--o| address: "address_id, addressCode"
```

### rbac 角色、角色组、权限

```mermaid
erDiagram
  role["角色表"] {
  }
  role_group["角色组"] {
  }
  permissions["权限"] {
  }
```

```mermaid
erDiagram
  role_permissions["角色 权限关联表"] {
  }
  role_permissions }o--o{ role: role_id
  role_permissions }o--o{ permissions: permission_id

  role_group_role["角色组 角色关联"] {
  }
  role_group_role }o--o{ role_group: role_group_id
  role_group_role }o--o{ role: role_id

  user_role_group["用户 角色组关联"] {
  }
  user_role_group }o--o{ usr: user_id
  user_role_group }o--o{ role_group: role_group_id
```

### 部门

```mermaid
erDiagram
  dept["部门表"] {
  }
```

```mermaid
erDiagram
  user_dept["用户 部门关联表"] {
  }
  user_dept }o--o{ usr: user_id
  user_dept }o--o{ dept: dept_id
```

### 接口

```mermaid
erDiagram
  api["接口表"] {
  }
  api }o--o| permissions: permissions_id
```

```mermaid
erDiagram
  api_call_record["接口调用记录表"] {
  }
  api_call_record ||--|| api: api_id
```

### 附件

```mermaid
erDiagram
  attachment["附件表"] {
    int att_type "附件类型"
  }
  attachment |o--o| attachment: url_id
```

### 地址

```mermaid
erDiagram
  address["地址表"] {
    str code "地址编码"
    bool leaf "是否为叶子节点"
  }

```

```mermaid
erDiagram
  address_details["地址详情表"] {
    str address_id "地址 id（已弃用）"
  }
  address_details |o--o| address: "address_id, code"
```

### 表变更记录

```mermaid
erDiagram
  table_row_delete_record["表行删除记录表"] {
  }
  table_row_delete_record ||--|| usr: "user_id, user_account"

  table_row_change_record["表行变更记录表"] {
  }
  table_row_change_record ||--|| usr: "create_user_id, create_user_account"
  table_row_change_record ||--|| usr: "last_modify_user_id, last_modify_user_account"
```

### 证件

```mermaid
erDiagram
  idcard_2["二代身份证表"] {
  }
  idcard_2 |o--o| usr: user_id
  idcard_2 |o--o| user_info: user_info_id
  idcard_2 |o--o| address_details: address_details_id
```

```mermaid
erDiagram
  dis_cert_2["二代残疾证"] {
  }
  dis_cert_2 |o--o| usr: user_id
  dis_cert_2 |o--o| user_info: user_info_id
  dis_cert_2 |o--o| address_details: address_details_id
```

```mermaid
erDiagram
  household_cert["户口表"] {
  }
  household_cert |o--o| usr: user_id
  household_cert |o--o| user_info: user_info_id
  household_cert |o--o| address_details: address_details_id
```

```mermaid
erDiagram
  bank_card["银行卡表"] {
  }
  bank_card }o--o| usr: user_id
  bank_card }o--o| user_info: user_info_id
```

```mermaid
erDiagram
  biz_cert["营业执照表"] {
  }
  biz_cert }o--o| usr: user_id
  biz_cert }o--o| user_info: user_info_id
```

### 审核审计表

```mermaid
erDiagram
  audit["审核审计表"] {
    id ref_id "审核主题对象外键id"
    int ref_type "外键类型"
  }
  audit }o--o{ any: "ref_id"
  audit }o--o| usr: "user_id"
```
```mermaid
erDiagram
  audit_attachment["审核附件表"]
  audit_attachment }o--|| audit: audit_id
  audit_attachment }o--|| attachment: att_id
```
### 证件关联

```mermaid
erDiagram
  cert["证件表"] {
    str wm_code "证件水印码"
    id wm_att_id "水印文件"
    id att_id "证件源文件"
  }
  cert |o--o| usr: user_id
  cert |o--|| usr: create_user_id
  cert |o--o| user_info: user_info_id
  cert |o--|| attachment: att_id
  cert |o--|| attachment: wm_att_id
```
