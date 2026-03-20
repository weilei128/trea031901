# 个人收支记账系统 API 文档

## 概述

本文档描述了个人收支记账系统的所有API接口，包括用户模块、记账模块和统计模块。

## 统一响应格式

所有接口返回统一的JSON格式：

```json
{
    "code": 200,
    "msg": "成功",
    "data": { ... }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 响应状态码，200表示成功，其他表示失败 |
| msg | String | 响应消息 |
| data | Object | 响应数据，可为null |

## 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录或token无效 |
| 403 | 无权限操作 |
| 500 | 服务器内部错误 |

---

## 用户模块

### 1. 用户注册

**接口地址**：`POST /api/user/register`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名（3-20字符） |
| password | String | 是 | 密码（6-20字符） |
| phone | String | 否 | 手机号（选填） |
| email | String | 否 | 邮箱（选填） |

**请求示例**：

```json
{
    "username": "zhangsan",
    "password": "123456",
    "phone": "13800138000",
    "email": "zhangsan@example.com"
}
```

**响应示例**：

```json
{
    "code": 200,
    "msg": "成功",
    "data": null
}
```

---

### 2. 用户登录

**接口地址**：`POST /api/user/login`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| account | String | 是 | 账号（用户名/手机号/邮箱） |
| password | String | 是 | 密码 |

**请求示例**：

```json
{
    "account": "zhangsan",
    "password": "123456"
}
```

**响应示例**：

```json
{
    "code": 200,
    "msg": "成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
}
```

---

### 3. 修改密码

**接口地址**：`POST /api/user/change-password`

**请求头**：`Authorization: Bearer {token}`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| oldPassword | String | 是 | 原密码 |
| newPassword | String | 是 | 新密码（6-20字符） |

**请求示例**：

```json
{
    "oldPassword": "123456",
    "newPassword": "654321"
}
```

---

### 4. 获取用户信息

**接口地址**：`GET /api/user/info`

**请求头**：`Authorization: Bearer {token}`

**响应示例**：

```json
{
    "code": 200,
    "msg": "成功",
    "data": {
        "id": 1,
        "username": "zhangsan",
        "phone": "13800138000",
        "email": "zhangsan@example.com",
        "createTime": "2024-01-01T12:00:00"
    }
}
```

---

## 记账模块

### 1. 添加收支记录

**接口地址**：`POST /api/record`

**请求头**：`Authorization: Bearer {token}`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| amount | BigDecimal | 是 | 金额（大于0） |
| type | String | 是 | 类型（收入/支出） |
| category | String | 是 | 分类（餐饮/薪资/购物等） |
| remark | String | 否 | 备注 |

**请求示例**：

```json
{
    "amount": 50.00,
    "type": "支出",
    "category": "餐饮",
    "remark": "午餐"
}
```

**可选分类**：餐饮、薪资、购物、交通、娱乐、医疗、教育、住房、其他收入、其他支出

---

### 2. 查询收支记录

**接口地址**：`GET /api/record`

**请求头**：`Authorization: Bearer {token}`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startTime | String | 否 | 开始时间（格式：yyyy-MM-dd） |
| endTime | String | 否 | 结束时间（格式：yyyy-MM-dd） |
| type | String | 否 | 类型（收入/支出） |
| category | String | 否 | 分类 |
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |

**响应示例**：

```json
{
    "code": 200,
    "msg": "成功",
    "data": {
        "list": [
            {
                "id": 1,
                "userId": 1,
                "amount": 50.00,
                "type": "支出",
                "category": "餐饮",
                "remark": "午餐",
                "createTime": "2024-01-01T12:00:00"
            }
        ],
        "total": 100,
        "page": 1,
        "pageSize": 10,
        "totalPages": 10
    }
}
```

---

### 3. 修改收支记录

**接口地址**：`PUT /api/record/{id}`

**请求头**：`Authorization: Bearer {token}`

**路径参数**：`id` - 记录ID

**请求参数**：同添加记录

---

### 4. 删除收支记录

**接口地址**：`DELETE /api/record/{id}`

**请求头**：`Authorization: Bearer {token}`

**路径参数**：`id` - 记录ID

---

### 5. 获取单条记录详情

**接口地址**：`GET /api/record/{id}`

**请求头**：`Authorization: Bearer {token}`

**路径参数**：`id` - 记录ID

---

### 6. 获取所有分类

**接口地址**：`GET /api/record/categories`

**请求头**：`Authorization: Bearer {token}`

**响应示例**：

```json
{
    "code": 200,
    "msg": "成功",
    "data": ["餐饮", "薪资", "购物", "交通", "娱乐", "医疗", "教育", "住房", "其他收入", "其他支出"]
}
```

---

## 统计模块

### 1. 本周统计

**接口地址**：`GET /api/statistics/week`

**请求头**：`Authorization: Bearer {token}`

**响应示例**：

```json
{
    "code": 200,
    "msg": "成功",
    "data": {
        "startDate": "2024-01-01",
        "endDate": "2024-01-07",
        "totalIncome": 10000.00,
        "totalExpense": 3000.00,
        "balance": 7000.00,
        "recordCount": 20
    }
}
```

---

### 2. 本月统计

**接口地址**：`GET /api/statistics/month`

**请求头**：`Authorization: Bearer {token}`

响应格式同本周统计。

---

### 3. 自定义时间段统计

**接口地址**：`GET /api/statistics/custom`

**请求头**：`Authorization: Bearer {token}`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | String | 是 | 开始日期（yyyy-MM-dd） |
| endDate | String | 是 | 结束日期（yyyy-MM-dd） |

响应格式同本周统计。

---

### 4. 分类统计

**接口地址**：`GET /api/statistics/category`

**请求头**：`Authorization: Bearer {token}`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | String | 否 | 类型（收入/支出） |
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |

**响应示例**：

```json
{
    "code": 200,
    "msg": "成功",
    "data": {
        "totalAmount": 3000.00,
        "type": "支出",
        "categoryStats": [
            {
                "category": "餐饮",
                "amount": 1500.00,
                "percentage": 50.00
            },
            {
                "category": "交通",
                "amount": 500.00,
                "percentage": 16.67
            }
        ]
    }
}
```

---

## 异常处理

系统会对以下异常情况进行处理：

1. **用户未登录**：返回code=401，msg="用户未登录"
2. **token无效**：返回code=401，msg="无效的token"
3. **记录不存在**：返回code=400，msg="记录不存在"
4. **无权限操作**：返回code=403，msg="无权限修改/删除该记录"
5. **参数错误**：返回code=400，并包含具体的错误信息
6. **服务器错误**：返回code=500，msg="服务器内部错误"

---

## 数据存储说明

系统使用CSV文件存储数据，数据文件位于`data/`目录下：

- `users.csv`：存储用户信息
- `records.csv`：存储收支记录

CSV文件会在系统首次启动时自动创建。
