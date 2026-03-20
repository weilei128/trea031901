# IDEA 启动项目详细指南

## 问题说明

当前遇到的错误是Java版本不兼容问题：
```
UnsupportedClassVersionError: class file version 55.0, this version only recognizes up to 52.0
```

- 版本55.0 = Java 11
- 版本52.0 = Java 8

已修改pom.xml指定Java 8编译，需要重新构建项目。

---

## 解决方案：使用IDEA启动（推荐）

### 步骤1：打开项目
1. 打开IntelliJ IDEA
2. 选择 `File` → `Open`
3. 选择项目目录：`D:\workspace\traeTest\trae0319\trae0319_dogFooding\trea0319`
4. 点击 `OK`

### 步骤2：配置项目SDK
1. 打开 `File` → `Project Structure`（或按 `Ctrl+Alt+Shift+S`）
2. 在 `Project Settings` → `Project` 中：
   - `Project SDK`：选择已安装的JDK（推荐JDK 8或11）
   - `Project language level`：选择 `8 - Lambdas, type annotations etc.`
   - 点击 `Apply`

### 步骤3：配置Maven（如有）
1. 打开 `File` → `Settings` → `Build, Execution, Deployment` → `Build Tools` → `Maven`
2. 配置正确的Maven安装路径
3. 勾选 `Use settings from pom.xml`

### 步骤4：重新构建项目
方式一：使用IDEA构建：
1. 点击右侧 `Maven` 面板
2. 展开 `accounting-system` → `Lifecycle`
3. 双击 `clean`
4. 双击 `install` 或 `package`

方式二：使用IDEA的Build功能：
1. 点击菜单 `Build` → `Rebuild Project`
2. 等待构建完成

### 步骤5：启动项目
1. 在IDEA中找到启动类：`src/main/java/com/example/accounting/AccountingSystemApplication.java`
2. 右键点击该文件 → `Run 'AccountingSystemApplication'`
3. 或点击类文件中main方法左侧的绿色三角按钮

### 步骤6：访问系统
1. 等待控制台输出完成，看到类似以下内容：
   ```
   Started AccountingSystemApplication in X seconds
   ```
2. 打开浏览器访问：`http://localhost:8080`

---

## 常见问题处理

### 问题1：依赖下载失败
解决：
1. 检查网络连接
2. 在Maven设置中配置阿里云镜像：
   - 打开 `settings.xml`（通常在 `C:\Users\用户名\.m2\`）
   - 添加镜像配置：
   ```xml
   <mirrors>
       <mirror>
           <id>aliyun</id>
           <mirrorOf>central</mirrorOf>
           <url>https://maven.aliyun.com/repository/central</url>
       </mirror>
   </mirrors>
   ```

### 问题2：端口被占用
解决：
1. 修改 `src/main/resources/application.properties`：
   ```properties
   server.port=8081
   ```
2. 重新启动

### 问题3：程序包不存在
解决：
1. 右键点击项目 → `Maven` → `Reimport`
2. 或点击Maven面板的刷新按钮

### 问题4：中文乱码
解决：
1. 打开 `File` → `Settings` → `Editor` → `File Encodings`
2. 所有编码设置为 `UTF-8`

---

## 项目验证步骤

启动成功后，请按以下顺序验证功能：

### 1. 注册账号
- 访问 `http://localhost:8080`
- 点击"立即注册"
- 填写用户名、密码注册

### 2. 登录系统
- 使用注册的账号登录

### 3. 测试记账功能
- 点击"记账"
- 选择类型（收入/支出）
- 输入金额、选择分类
- 点击"保存"

### 4. 查看账单
- 点击"账单"查看已记录的收支

### 5. 查看统计
- 点击"统计"查看收支统计图表

---

## 项目结构确认

启动前请确认以下关键文件存在：
```
src/
└── main/
    ├── java/
    │   └── com/example/accounting/
    │       ├── AccountingSystemApplication.java  ✅
    │       ├── controller/
    │       │   ├── UserController.java          ✅
    │       │   ├── RecordController.java        ✅
    │       │   └── StatisticsController.java    ✅
    │       ├── service/
    │       │   ├── UserService.java             ✅
    │       │   ├── RecordService.java           ✅
    │       │   └── StatisticsService.java       ✅
    │       └── util/
    │           ├── CsvUtil.java                 ✅
    │           └── JwtUtil.java                 ✅
    └── resources/
        ├── application.properties               ✅
        └── static/
            └── index.html                       ✅
```

---

## 启动成功标志

看到以下控制台输出表示启动成功：
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::               (v2.7.18)

INFO  --- [main] c.e.a.AccountingSystemApplication  : Starting AccountingSystemApplication
INFO  --- [main] c.e.a.AccountingSystemApplication  : Started AccountingSystemApplication in X.X seconds
```

如果仍然遇到问题，请检查：
1. IDEA是否以管理员身份运行
2. JDK版本是否正确安装
3. 项目路径是否有中文或特殊字符
