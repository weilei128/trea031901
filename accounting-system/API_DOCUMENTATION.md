# 个人收支记账系统 - API接口文档

## 基础信息

- **基础URL**: `http://localhost:8080/api`
- **数据格式**: JSON
- **统一响应格式**:
```json
{
    "code": 200,
    "msg": "成功",
    "data": {}
}
```

## 响应状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录或登录已过期 |
| 403 | 无权操作 |
| 500 | 服务器错误 |

---

## 用户模块

### 1. 用户注册

**接口**: `POST /user/register`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 手机号或邮箱 |
| password | string | 是 | 密码（6-20位） |

**请求示例**:
```json
{
    "username": "13800138000",
    "password": "123456"
}
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "成功",
    "data": {
        "userId": 123456789
    }
}
```

**错误码**:
- 400: 用户名必须是手机号或邮箱格式
- 400: 密码长度必须在6-20位之间
- 500: 用户已存在

---

### 2. 用户登录

**接口**: `POST /user/login`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 手机号或邮箱 |
| password | string | 是 | 密码 |

**请求示例**:
```json
{
    "username": "13800138000",
    "password": "123456"
}
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "成功",
    "data": {
        "userId": 123456789,
        "username": "13800138000",
        "token": "eyJhbGciOiJIUzI1NiJ9..."
    }
}
```

**错误码**:
- 400: 用户名不能为空
- 400: 密码不能为空
- 500: 用户不存在
- 500: 密码错误

---

### 3. 修改密码

**接口**: `POST /user/update-password`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| oldPassword | string | 是 | 旧密码 |
| newPassword | string | 是 | 新密码（6-20位） |

**请求示例**:
```json
{
    "oldPassword": "123456",
    "newPassword": "654321"
}
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "成功",
    "data": null
}
```

**错误码**:
- 401: 用户未登录
- 401: 登录已过期，请重新登录
- 400: 旧密码不能为空
- 400: 新密码不能为空
- 400: 新密码长度必须在6-20位之间
- 500: 用户不存在
- 500: 旧密码错误

---

## 记账模块

### 4. 添加收支记录

**接口**: `POST /record/add`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| amount | number | 是 | 金额（必须大于0） |
| type | string | 是 | 类型：INCOME(收入)/EXPENSE(支出) |
| category | string | 是 | 分类：餐饮/薪资/购物/交通/娱乐/医疗/教育/其他 |
| remark | string | 否 | 备注 |

**请求示例**:
```json
{
    "amount": 100.50,
    "type": "EXPENSE",
    "category": "餐饮",
    "remark": "午餐"
}
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "成功",
    "data": {
        "recordId": 987654321
    }
}
```

**错误码**:
- 401: 用户未登录
- 400: 金额不能为空
- 400: 金额必须大于0
- 400: 类型不能为空
- 400: 类型必须是INCOME(收入)或EXPENSE(支出)
- 400: 分类不能为空
- 400: 分类必须是：餐饮、薪资、购物、交通、娱乐、医疗、教育、其他

---

### 5. 修改收支记录

**接口**: `POST /record/update`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 记录ID |
| amount | number | 否 | 金额（必须大于0） |
| type | string | 否 | 类型：INCOME(收入)/EXPENSE(支出) |
| category | string | 否 | 分类：餐饮/薪资/购物/交通/娱乐/医疗/教育/其他 |
| remark | string | 否 | 备注 |

**请求示例**:
```json
{
    "id": 987654321,
    "amount": 150.00,
    "type": "EXPENSE",
    "category": "餐饮",
    "remark": "晚餐"
}
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "成功",
    "data": null
}
```

**错误码**:
- 401: 用户未登录
- 400: 记录ID不能为空
- 400: 金额必须大于0
- 400: 类型必须是INCOME(收入)或EXPENSE(支出)
- 400: 分类必须是：餐饮、薪资、购物、交通、娱乐、医疗、教育、其他
- 500: 记录不存在
- 403: 无权操作此记录

---

### 6. 删除收支记录

**接口**: `POST /record/delete/{recordId}`

**请求头**:
```
Authorization: Bearer {token}
```

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| recordId | number | 是 | 记录ID |

**响应示例**:
```json
{
    "code": 200,
    "msg": "成功",
    "data": null
}
```

**错误码**:
- 401: 用户未登录
- 500: 记录不存在
- 403: 无权操作此记录

---

### 7. 查询收支记录列表

**接口**: `POST /record/list`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startTime | string | 否 | 开始日期（格式：yyyy-MM-dd） |
| endTime | string | 否 | 结束日期（格式：yyyy-MM-dd） |
| type | string | 否 | 类型：INCOME/EXPENSE |
| category | string | 否 | 分类 |
| pageNum | number | 否 | 页码，默认1 |
| pageSize | number | 否 | 每页大小，默认10 |

**请求示例**:
```json
{
    "startTime": "2024-01-01",
    "endTime": "2024-12-31",
    "type": "EXPENSE",
    "category": "餐饮",
    "pageNum": 1,
    "pageSize": 10
}
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "成功",
    "data": {
        "total": 50,
        "list": [
            {
                "id": 987654321,
                "userId": 123456789,
                "amount": 100.50,
                "type": "EXPENSE",
                "category": "餐饮",
                "remark": "午餐",
                "createTime": "2024-01-15 12:30:00",
                "updateTime": "2024-01-15 12:30:00"
            }
        ],
        "pageNum": 1,
        "pageSize": 10,
        "totalPages": 5
    }
}
```

**错误码**:
- 401: 用户未登录

---

### 8. 获取记录详情

**接口**: `GET /record/detail/{recordId}`

**请求头**:
```
Authorization: Bearer {token}
```

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| recordId | number | 是 | 记录ID |

**响应示例**:
```json
{
    "code": 200,
    "msg": "成功",
    "data": {
        "id": 987654321,
        "userId": 123456789,
        "amount": 100.50,
        "type": "EXPENSE",
        "category": "餐饮",
        "remark": "午餐",
        "createTime": "2024-01-15 12:30:00",
        "updateTime": "2024-01-15 12:30:00"
    }
}
```

**错误码**:
- 401: 用户未登录
- 500: 记录不存在
- 403: 无权查看此记录

---

## 统计模块

### 9. 按周统计收支

**接口**: `POST /statistics/week`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| date | string | 否 | 指定日期（格式：yyyy-MM-dd），不传则使用当前日期 |

**请求示例**:
```json
{
    "date": "2024-01-15"
}
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "成功",
    "data": {
        "startDate": "2024-01-15",
        "endDate": "2024-01-21",
        "totalIncome": 5000.00,
        "totalExpense": 1500.50,
        "balance": 3499.50,
        "incomeByCategory": {
            "薪资": {
                "amount": 5000.00,
                "percent": 100.00
            }
        },
        "expenseByCategory": {
            "餐饮": {
                "amount": 800.00,
                "percent": 53.31
            },
            "购物": {
                "amount": 500.50,
                "percent": 33.32
            },
            "交通": {
                "amount": 200.00,
                "percent": 13.33
            }
        }
    }
}
```

**错误码**:
- 401: 用户未登录

---

### 10. 按月统计收支

**接口**: `POST /statistics/month`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| date | string | 否 | 指定月份（格式：yyyy-MM），不传则使用当前月份 |

**请求示例**:
```json
{
    "date": "2024-01"
}
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "成功",
    "data": {
        "startDate": "2024-01-01",
        "endDate": "2024-01-31",
        "totalIncome": 15000.00,
        "totalExpense": 5000.00,
        "balance": 10000.00,
        "incomeByCategory": {
            "薪资": {
                "amount": 15000.00,
                "percent": 100.00
            }
        },
        "expenseByCategory": {
            "餐饮": {
                "amount": 2000.00,
                "percent": 40.00
            },
            "购物": {
                "amount": 1500.00,
                "percent": 30.00
            },
            "交通": {
                "amount": 800.00,
                "percent": 16.00
            },
            "娱乐": {
                "amount": 700.00,
                "percent": 14.00
            }
        }
    }
}
```

**错误码**:
- 401: 用户未登录

---

## 分类说明

### 收入分类
- 薪资

### 支出分类
- 餐饮
- 购物
- 交通
- 娱乐
- 医疗
- 教育
- 其他

---

## 认证说明

除注册和登录接口外，其他所有接口都需要在请求头中携带JWT令牌：

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

令牌有效期为7天，过期后需要重新登录获取新令牌。
