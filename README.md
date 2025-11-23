# E-commerce-management-system618testHOMEWORK
电商项目管理结构图

    E-commerce-management-system618testHOMEWORK/
    ├── .gitignore                # Git忽略文件配置
    ├── README.md                 # 项目根目录说明文档
    └── secomm/                   # 核心项目目录
        ├── .DS_Store             # 系统自动生成文件（Mac）
        ├── .idea/                # IDEA项目配置
        ├── .vscode/              # VSCode配置
        ├── backend/              # 后端Spring Boot项目
            ├── src/
            │   ├── main/
            │   │   ├── java/
            │   │   │   └── com/
            │   │   │       └── example/
            │   │   │           └── secomm/
            │   │   │               ├── SecommApplication.java  # 应用程序入口点，启动类
            │   │   │               ├── config/                 # 配置类目录
            │   │   │               │   ├── SecurityConfig.java # Spring Security 配置（用户认证、授权）
            │   │   │               │   ├── WebConfig.java      # Web 配置（CORS跨域、拦截器等）
            │   │   │               │   └── SwaggerConfig.java  # API文档生成器配置
            │   │   │               ├── controller/             # 控制器层，处理HTTP请求
            │   │   │               │   ├── ProductController.java
            │   │   │               │   ├── OrderController.java
            │   │   │               │   ├── UserController.java
            │   │   │               │   └── CategoryController.java
            │   │   │               ├── service/                # 服务层，包含核心业务逻辑
            │   │   │               │   ├── impl/               # 服务接口的实现
            │   │   │               │   │   ├── ProductServiceImpl.java
            │   │   │               │   │   └── UserServiceImpl.java
            │   │   │               │   ├── ProductService.java
            │   │   │               │   └── UserService.java
            │   │   │               ├── repository/             # 数据访问层，与数据库交互
            │   │   │               │   ├── ProductRepository.java
            │   │   │               │   ├── UserRepository.java
            │   │   │               │   └── OrderRepository.java
            │   │   │               ├── entity/                 # JPA实体类，映射数据库表
            │   │   │               │   ├── Product.java
            │   │   │               │   ├── User.java
            │   │   │               │   ├── Order.java
            │   │   │               │   └── Category.java
            │   │   │               ├── dto/                    # 数据传输对象
            │   │   │               │   ├── request/            # 接收前端请求的数据模型
            │   │   │               │   │   ├── LoginRequest.java
            │   │   │               │   │   └── ProductRequest.java
            │   │   │               │   └── response/           # 返回给前端的数据模型
            │   │   │               │       ├── LoginResponse.java
            │   │   │               │       └── ProductResponse.java
            │   │   │               ├── exception/              # 自定义异常和全局异常处理器
            │   │   │               │   ├── ResourceNotFoundException.java
            │   │   │               │   ├── BadRequestException.java
            │   │   │               │   └── GlobalExceptionHandler.java
            │   │   │               └── util/                   # 工具类
            │   │   │                   ├── JwtUtil.java
            │   │   │                   └── DateUtil.java
            │   │   └── resources/
            │   │       ├── application.properties        # 主配置文件
            │   │       ├── application-dev.properties    # 开发环境配置
            │   │       ├── application-prod.properties   # 生产环境配置
            │   │       └── static/                       # 静态资源（如果需要）
            │   └── test/                                 # 单元测试和集成测试
            │       └── java/
            │           └── com/
            │               └── example/
            │                   └── secomm/
            │                       ├── controller/
            │                       └── service/
            ├── pom.xml                                    # Maven 依赖管理文件
            └── HELP.md                                    # Spring Boot 自动生成的帮助文档
    ├── frontend/             # 用户端Vue前端
              ├── node_modules/                # 项目依赖包
              ├── public/                      # 静态资源目录
              │   ├── favicon.ico
              │   └── index.html               # 单页应用(SPA)的入口HTML文件
              ├── src/
              │   ├── api/                     # API 请求封装
              │   │   ├── product.js           # 商品相关API
              │   │   ├── user.js              # 用户相关API
              │   │   └── request.js           # axios实例配置 (拦截器等)
              │   ├── assets/                  # 静态资源
              │   │   ├── images/              # 图片资源
              │   │   └── styles/              # 全局样式
              │   ├── components/              # 公共UI组件
              │   │   ├── common/              # 通用基础组件
              │   │   │   ├── Navbar.vue       # 导航栏
              │   │   │   ├── Footer.vue       # 页脚
              │   │   │   └── ProductCard.vue  # 商品卡片
              │   │   └── icons/               # 图标组件
              │   ├── router/                  # 路由配置
              │   │   └── index.js             # 定义页面路由和导航守卫
              │   ├── store/                   # Pinia 状态管理
              │   │   ├── index.js             # 创建Pinia实例
              │   │   ├── modules/             # 模块划分
              │   │   │   ├── user.js          # 用户状态 (登录、信息)
              │   │   │   └── cart.js          # 购物车状态
              │   ├── utils/                   # 工具函数
              │   │   ├── auth.js              # 权限、Token相关
              │   │   └── format.js            # 格式化函数
              │   ├── views/                   # 页面组件
              │   │   ├── HomeView.vue         # 首页
              │   │   ├── Product/
              │   │   │   ├── ProductList.vue  # 商品列表页
              │   │   │   └── ProductDetail.vue # 商品详情页
              │   │   ├── CartView.vue         # 购物车页面
              │   │   ├── Order/
              │   │   │   ├── OrderCreate.vue  # 创建订单
              │   │   │   └── OrderList.vue    # 订单列表
              │   │   └── User/
              │   │       ├── Login.vue        # 登录页
              │   │       └── Profile.vue      # 用户中心
              │   ├── App.vue                  # 根组件
              │   └── main.js                  # 应用入口文件
              ├── .eslintrc.js                 # ESLint 配置
              ├── .prettierrc                  # Prettier 配置
              ├── index.html                   # 和public下的index.html作用类似，Vite构建入口
              ├── package.json                 # 项目依赖和脚本
              └── vite.config.js               # Vite 构建配置
    ├── frontend_admin/       # 管理员端Vue前端
              ├── node_modules/
              ├── public/
              │   └── index.html
              ├── src/
              │   ├── api/                     # API 请求封装 (针对管理员接口)
              │   │   ├── productManage.js
              │   │   ├── orderManage.js
              │   │   └── request.js
              │   ├── assets/
              │   ├── components/              # 公共和业务组件
              │   │   ├── common/
              │   │   │   ├── Sidebar.vue      # 侧边栏导航
              │   │   │   ├── Header.vue       # 顶部导航/信息栏
              │   │   │   └── Table.vue        # 高级表格组件(带搜索、分页)
              │   │   └── Form/                # 表单相关组件
              │   │       ├── ProductForm.vue
              │   │       └── UserForm.vue
              │   ├── router/
              │   │   └── index.js             # 管理员路由，包含权限守卫
              │   ├── store/
              │   │   └── modules/
              │   │       └── admin.js         # 管理员状态
              │   ├── utils/
              │   ├── views/                   # 管理员页面
              │   │   ├── Login.vue            # 管理员登录页
              │   │   ├── Dashboard.vue        # 控制面板/数据概览
              │   │   ├── Products/
              │   │   │   ├── ProductList.vue  # 商品管理列表
              │   │   │   └── ProductEdit.vue  # 商品编辑/新增
              │   │   ├── Orders/
              │   │   │   └── OrderList.vue    # 订单管理列表
              │   │   ├── Users/
              │   │   │   └── UserList.vue     # 用户管理列表
              │   │   └── Categories/
              │   │       └── CategoryList.vue # 分类管理
              │   ├── App.vue
              │   └── main.js
              ├── package.json
              └── vite.config.js    
    └── uploads/              # 上传文件存储目录（如商品图片等）
