# 个人收支记账系统

基于 Java + Spring Boot + H5 开发的个人收支记账系统，使用 CSV 文件存储数据。

## 项目结构

```
accounting-system/
├── src/
│   └── main/
│       ├── java/com/accounting/
│       │   ├── AccountingApplication.java    # 启动类
│       │   ├── config/                       # 配置类
│       │   ├── controller/                   # 控制器层
│       │   │   ├── UserController.java       # 用户接口
│       │   │   ├── RecordController.java     # 记账接口
│       │   │   └── StatisticsController.java # 统计接口
│       │   ├── service/                      # 服务层
│       │   │   ├── UserService.java          # 用户服务
│       │   │   └── RecordService.java        # 记账服务
│       │   ├── entity/                       # 实体类
│       │   │   ├── User.java                 # 用户实体
│       │   │   └── Record.java               # 记录实体
│       │   ├── dto/                          # 数据传输对象
│       │   │   ├── Result.java               # 统一响应结果
│       │   │   ├── PageResult.java           # 分页结果
│       │   │   ├── UserRegisterDTO.java      # 注册请求
│       │   │   ├── UserLoginDTO.java         # 登录请求
│       │   │   ├── UserUpdatePasswordDTO.java# 修改密码请求
│       │   │   ├── RecordAddDTO.java         # 添加记录请求
│       │   │   ├── RecordUpdateDTO.java      # 修改记录请求
│       │   │   ├── RecordQueryDTO.java       # 查询记录请求
│       │   │   └── StatisticsDTO.java        # 统计请求
│       │   ├── util/                         # 工具类
│       │   │   ├── CsvUtil.java              # CSV操作工具
│       │   │   ├── JwtUtil.java              # JWT工具
│       │   │   ├── DateUtil.java             # 日期工具
│       │   │   └── IdUtil.java               # ID生成工具
│       │   └── exception/                    # 异常处理
│       │       ├── BusinessException.java    # 业务异常
│       │       └── GlobalExceptionHandler.java # 全局异常处理
│       └── resources/
│           ├── application.yml               # 配置文件
│           └── static/
│               └── index.html                # H5前端页面
├── data/                                     # 数据存储目录
│   ├── users.csv                             # 用户数据
│   └── records.csv                           # 记账数据
├── pom.xml                                   # Maven配置
├── API_DOCUMENTATION.md                      # API接口文档
└── README.md                                 # 项目说明
```

## 技术栈

- **后端**: Java 11, Spring Boot 2.7.14
- **数据存储**: CSV 文件 (OpenCSV)
- **认证**: JWT (JSON Web Token)
- **前端**: HTML5, CSS3, JavaScript (原生)
- **构建工具**: Maven

## 核心功能

### 1. 用户模块
- 用户注册（支持手机号/邮箱+密码）
- 用户登录
- 修改密码
- JWT Token 认证

### 2. 记账模块
- 添加收支记录（金额、类型、分类、备注）
- 修改/删除自己的收支记录
- 查询收支记录（支持时间范围、类型、分类筛选，分页）
- 仅本人可操作自己的记录

### 3. 统计模块
- 按周统计收支总额
- 按月统计收支总额
- 按分类统计占比

## 快速开始

### 环境要求
- JDK 11 或更高版本
- Maven 3.6 或更高版本

### 1. 编译项目

```bash
cd accounting-system
mvn clean compile
```

### 2. 运行项目

```bash
mvn spring-boot:run
```

或者编译后运行：

```bash
mvn clean package
java -jar target/accounting-system-1.0.0.jar
```

### 3. 访问系统

启动成功后，打开浏览器访问：

```
http://localhost:8080
```

## 操作步骤

### 1. 注册账号
1. 打开首页，切换到"注册"标签
2. 输入手机号或邮箱
3. 输入密码（6-20位）
4. 点击"注册"按钮
5. 注册成功后切换到登录页面

### 2. 登录系统
1. 输入注册时的手机号/邮箱和密码
2. 点击"登录"按钮
3. 登录成功后进入主界面

### 3. 记账
1. 点击"记一笔"标签
2. 选择类型（收入/支出）
3. 输入金额
4. 选择分类
5. 填写备注（可选）
6. 点击"保存"按钮

### 4. 查看收支明细
1. 点击"收支明细"标签
2. 可设置筛选条件（时间范围、类型、分类）
3. 点击"查询"按钮
4. 支持分页浏览
5. 可对记录进行编辑或删除

### 5. 查看统计
1. 点击"统计分析"标签
2. 选择统计类型（本周/本月）
3. 选择日期（可选）
4. 点击"查看统计"按钮
5. 查看收支总额、结余和分类占比

### 6. 修改密码
1. 点击右上角"修改密码"按钮
2. 输入旧密码
3. 输入新密码（6-20位）
4. 点击"确认"按钮

### 7. 退出登录
点击右上角"退出"按钮即可退出登录。

## 数据存储

系统使用 CSV 文件存储数据，默认存储在项目根目录的 `data` 文件夹下：

- `users.csv` - 存储用户信息
- `records.csv` - 存储收支记录

CSV 文件格式：

**users.csv**:
```csv
id,username,password,userType,createTime,updateTime
123456789,13800138000,123456,PHONE,2024-01-15 10:00:00,2024-01-15 10:00:00
```

**records.csv**:
```csv
id,userId,amount,type,category,remark,createTime,updateTime
987654321,123456789,100.50,EXPENSE,餐饮,午餐,2024-01-15 12:30:00,2024-01-15 12:30:00
```

## API 接口

详细的 API 接口文档请参考 [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

### 接口列表

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/user/register | POST | 用户注册 |
| /api/user/login | POST | 用户登录 |
| /api/user/update-password | POST | 修改密码 |
| /api/record/add | POST | 添加记录 |
| /api/record/update | POST | 修改记录 |
| /api/record/delete/{id} | POST | 删除记录 |
| /api/record/list | POST | 查询记录列表 |
| /api/record/detail/{id} | GET | 获取记录详情 |
| /api/statistics/week | POST | 按周统计 |
| /api/statistics/month | POST | 按月统计 |

## 配置说明

可在 `application.yml` 中修改配置：

```yaml
server:
  port: 8080  # 服务端口

data:
  path: ./data  # 数据文件存储路径
```

## 注意事项

1. **密码安全**: 当前版本密码以明文存储在 CSV 文件中，生产环境建议加密存储
2. **数据备份**: 建议定期备份 `data` 目录下的 CSV 文件
3. **并发访问**: CSV 文件存储不适合高并发场景，多用户同时操作可能会有数据冲突
4. **Token 有效期**: JWT Token 有效期为 7 天，过期后需要重新登录

## 开发说明

### 添加新的收支分类

如需添加新的收支分类，需要修改以下文件：

1. `RecordAddDTO.java` - 修改分类校验正则
2. `RecordUpdateDTO.java` - 修改分类校验正则
3. `index.html` - 添加分类选项

### 修改数据存储路径

在 `application.yml` 中修改 `data.path` 配置：

```yaml
data:
  path: /path/to/your/data
```

## 许可证

MIT License
