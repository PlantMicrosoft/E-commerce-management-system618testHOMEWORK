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
            │   │   ├── java/                # 源代码目录：存放所有后端业务逻辑Java代码
            │   │   │   └── org/yiqixue/secomm/  # 基础包路径（核心业务包）
            │   │   │       ├── SecommApplication.java  # 应用入口类：启动Spring Boot应用，加载上下文、触发自动配置；
                                                        标注@SpringBootApplication，main方法执行run()启动程序，可选启用JPA审计/事务管理
            │   │   │       ├── config/                 # 配置类目录：替代XML配置，管理框架/第三方组件规则
            │   │   │       │   ├── SecurityConfig.java # Spring Security配置：用户认证、URL权限控制、密码加密、JWT过滤器集成（如/admin/**需ADMIN角色）
            │   │   │       │   ├── WebConfig.java      # Web配置：CORS跨域、请求拦截器、JSON序列化（如日期格式）
            │   │   │       │   └── SwaggerConfig.java  # Swagger配置：API文档生成（标题/版本/Token参数/扫描包路径）
            │   │   │       ├── controller/             # 控制器层：前后端交互入口，仅接收请求/转发/返回响应，无业务逻辑
            │   │   │       │   ├── UserController.java # 用户接口：映射/api/users/*，处理登录/注册/用户信息查询；
                                    @RestController+请求注解，调用UserService，返回统一ResultDTO
            │   │   │       │   ├── ProductController.java # 商品接口：映射/api/products/*，处理商品分页/新增/库存扣减；
                                    @PreAuthorize限制管理员接口，@Valid校验参数
            │   │   │       │   ├── OrderController.java # 订单接口：映射/api/orders/*，处理创建订单/查询订单/取消订单；
                                    校验订单参数合法性，调用OrderService
            │   │   │       │   └── CategoryController.java # 分类接口：映射/api/categories/*，处理分类增删改查
            │   │   │       ├── service/                # 服务层：核心业务逻辑层，分接口+实现类
            │   │   │       │   ├── UserService.java    # 用户服务接口：定义login/register等业务方法签名，无实现
            │   │   │       │   ├── ProductService.java # 商品服务接口：定义分页查询/库存扣减/商品新增等方法签名
            │   │   │       │   ├── OrderService.java   # 订单服务接口：定义创建订单/查询订单/取消订单等方法签名
            │   │   │       │   └── impl/               # 服务实现类：实现接口逻辑，注入Repository/工具类，加事务控制
            │   │   │       │       ├── UserServiceImpl.java # 用户服务实现：@Service注解，实现登录（校验密码+生成JWT）
                                         、注册（校验唯一性+密码加密）；@Transactional保证原子性
            │   │   │       │       ├── ProductServiceImpl.java # 商品服务实现：处理商品分页查询、库存扣减校验、商品新增逻辑
            │   │   │       │       └── OrderServiceImpl.java # 订单服务实现：完成“扣库存→生成订单号→保存订单→清空购物车”完整下单流程；@Transactional保                                        证下单失败回滚
            │   │   │       ├── repository/             # 数据访问层：基于Spring Data JPA，仅负责数据库读写，无业务逻辑
            │   │   │       │   ├── UserRepository.java # 用户数据操作：继承JpaRepository，提供CRUD；
                                    自定义findByUsername/existsByPhone等方法
            │   │   │       │   ├── ProductRepository.java # 商品数据操作：
                                    自定义findByCategoryId（分页）、@Query校验库存等方法
            │   │   │       │   ├── OrderRepository.java # 订单数据操作：
                                    自定义findByUserIdAndStatus/按日期范围查询等方法
            │   │   │       │   ├── CartRepository.java # 购物车数据操作：CRUD+按用户ID查询购物车
            │   │   │       │   ├── CategoryRepository.java # 分类数据操作：CRUD+按名称查询等
            │   │   │       │   └── OrderItemRepository.java # 订单项数据操作：关联订单+商品，CRUD
            │   │   │       ├── entity/                 # JPA实体类：与数据库表一一映射，定义表结构/字段约束/表关系
            │   │   │       │   ├── User.java            # 用户实体：映射t_user表；@Entity+@Table，字段含用户名/密码/角色；
                                @OneToMany关联订单
            │   │   │       │   ├── Product.java        # 商品实体：映射t_product表；字段含ID/名称/价格/库存/分类ID；
                                @Id主键，@Column约束
            │   │   │       │   ├── Order.java          # 订单实体：映射t_order表；字段含订单号/用户ID/状态/创建时间；
                                @CreatedDate自动填充创建时间
            │   │   │       │   ├── Cart.java           # 购物车实体：映射t_cart表；关联用户+商品，字段含数量/选中状态
            │   │   │       │   ├── Category.java       # 分类实体：映射t_category表；字段含名称/排序/父ID
            │   │   │       │   └── OrderItem.java      # 订单项实体：映射t_order_item表；关联订单+商品，字段含单价/数量
            │   │   │       ├── dto/                    # 数据传输对象：隔离实体类，适配前后端交互格式
            │   │   │       │   ├── request/            # 请求DTO：接收前端参数，加校验注解
            │   │   │       │   │   ├── LoginRequest.java # 登录请求：接收username/password，@NotBlank校验非空
            │   │   │       │   │   ├── ProductRequest.java # 商品请求：接收新增/修改商品的参数（名称/价格/库存）
            │   │   │       │   │   └── OrderRequest.java # 订单请求：接收创建订单的参数（收货地址/商品ID列表）
            │   │   │       │   ├── response/           # 响应DTO：返回前端数据，适配展示格式
            │   │   │       │   │   ├── LoginResponse.java # 登录响应：返回token+用户基本信息
            │   │   │       │   │   ├── ProductResponse.java # 商品响应：返回商品信息（含分类名，而非分类ID）
            │   │   │       │   │   └── ResultDTO.java  # 全局响应体：统一返回格式（code/msg/data），适配成功/异常响应
            │   │   │       ├── exception/              # 异常处理：统一管理异常，返回标准化响应
            │   │   │       │   ├── BusinessException.java # 自定义业务异常：如“库存不足”“用户名已存在”，含code/msg
            │   │   │       │   ├── ResourceNotFoundException.java # 资源不存在异常：如“商品/订单不存在”
            │   │   │       │   └── GlobalExceptionHandler.java # 全局异常处理器：@RestControllerAdvice捕获所有异常，
                                返回统一ResultDTO（400参数错/500服务器错）
            │   │   │       └── util/                   # 工具类：通用方法，提高复用性，无业务逻辑
            │   │   │           ├── JwtUtil.java         # JWT工具：生成/验证JWT令牌，解析用户ID/角色
            │   │   │           ├── PasswordUtil.java    # 密码工具：BCrypt加密/校验密码
            │   │   │           └── DateUtil.java        # 日期工具：LocalDateTime格式化/解析，时间戳转换
            │   │   └── resources/          # 资源文件：配置应用运行参数，分环境管理
            │   │       ├── application.properties      # 主配置文件：指定应用名、端口、数据库连接、JPA表生成策略
            │   │       ├── application-dev.properties  # 开发环境配置：本地数据库地址、DEBUG日志、开启Swagger
            │   │       ├── application-prod.properties # 生产环境配置：生产数据库地址、INFO日志、关闭Swagger、优化连接池
            │   │       └── static/                     # 静态资源：图片/JS/CSS，默认映射/static/**路径
            │   └── test/                   # 测试代码：验证各层逻辑正确性，保证代码质量
            │       └── java/
            │           └── org/yiqixue/secomm/
            │               ├── controller/             # 控制器测试：模拟HTTP请求，验证接口响应
            │               │   ├── ProductControllerTest.java # 商品接口测试：MockMvc模拟请求，校验状态码/响应体
            │               │   └── UserControllerTest.java # 用户接口测试：验证登录/注册接口的参数校验/返回结果
            │               └── service/                # 服务层测试：直接调用服务方法，验证核心业务逻辑
            │                   ├── ProductServiceTest.java # 商品服务测试：测试库存扣减、商品搜索逻辑
            │                   └── OrderServiceTest.java # 订单服务测试：测试创建订单、取消订单逻辑
            ├── pom.xml                     # Maven依赖管理：管理Spring Boot/JPA/Security/Swagger等依赖版本
            └── HELP.md                     # Spring Boot帮助文档：自动生成的框架说明文档


 
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
