一、 产品功能介绍 (Product Features)
EmojiGallery 是一款基于 Android 平台的高性能表情包/图片浏览应用。该应用旨在演示现代移动端开发中“海量数据处理”与“离线缓存”的完美结合。
核心功能包括：
1.	海量图库浏览：
o	应用能够瞬间生成并展示 1000 张 独特的猫咪表情图片。
o	采用网格视图（Grid View）布局，自适应不同屏幕尺寸，提供丝滑的浏览体验。
2.	双重存储机制 (核心技术要求)：
o	数据库存储：图片的元数据（如标题、URL地址、ID）被持久化存储在本地 SQLite 数据库中，确保应用重启后数据不丢失。
o	文件系统缓存：图片文件自动缓存至本地磁盘。一旦加载过一次，即使断网也能完整显示图片，节省用户流量。
3.	智能加载与防卡顿：
o	应用仅渲染屏幕可见区域的图片，滑动时动态回收内存，确保即使浏览数千张图片，手机也不会发烫或卡顿。
o	支持图片淡入（Crossfade）动画，提升视觉观感。
________________________________________
二、 程序概要设计 (Program Design)
本程序严格遵循 Google 推荐的 MVVM (Model-View-ViewModel) 设计模式，将界面显示与数据逻辑彻底分离。
1. 数据层 (Model Layer)
负责数据的定义与存取。
•	EmojiEntity: 定义数据表结构，包含 id (主键)、imageUrl (图片链接)、title (标题)。
•	EmojiDao: 提供数据库操作接口，包括 insertAll (批量插入) 和 getAllEmojis (实时查询)。
•	AppDatabase: 基于 Room 框架构建的本地数据库实例，作为单一数据源（Single Source of Truth）。
2. 逻辑层 (ViewModel Layer)
负责业务逻辑处理，充当 UI 与数据的桥梁。
•	EmojiViewModel:
o	初始化时检查数据库状态，若为空则自动生成 1000 条 RoboHash 图片数据。
o	使用 Kotlin Coroutines (协程) 在后台 IO 线程处理数据写入，避免阻塞主线程。
o	将数据库数据转换为 StateFlow (状态流)，供 UI 层订阅。
3. 界面层 (View Layer)
负责图像渲染与用户交互。
•	MainActivity: 使用 Jetpack Compose 构建。
•	LazyVerticalGrid: 负责高性能网格布局。
•	Coil: 第三方图片加载库，负责处理网络请求、内存缓存与磁盘缓存。
________________________________________
三、 软件架构图 (Software Architecture)
架构流向说明：
1.	UI 观察 ViewModel 的数据变化。
2.	ViewModel 从 Room 数据库读取元数据。
3.	UI 根据元数据中的 URL，指示 Coil 加载图片。
4.	Coil 优先从文件缓存读取，无缓存时才发起网络请求。
________________________________________
四、 技术亮点及其实现原理 (Technical Highlights)
本项目采用了 Android 开发领域最前沿的技术栈，具体亮点如下：
1. 声明式 UI (Jetpack Compose)
•	原理：抛弃了传统的 XML 布局文件，完全使用 Kotlin 代码描述界面。
•	优势：代码量减少 50% 以上，开发效率高，且 UI 组件具有极高的复用性。
2. 响应式数据流 (Flow + Room)
•	原理：利用 Kotlin Flow 技术，建立了数据库到 UI 的“实时管道”。
•	实现：dao.getAllEmojis() 返回的是 Flow<List<EmojiEntity>>。这意味着只要数据库中有新数据插入，UI 界面会自动刷新，无需手动编写刷新逻辑。
3. 三级图片缓存策略 (Coil)
•	原理：为了满足“文件存储”和“网络存储”的要求，我们配置了 Coil 图片加载库。
•	实现：
o	代码中使用了 .diskCachePolicy(CachePolicy.ENABLED)。
o	L1 内存缓存：极速读取，用于当前屏幕显示的图片。
o	L2 磁盘缓存：持久化存储，实现断网可看。
o	L3 网络下载：仅在本地无缓存时触发，节省流量。
4. 懒加载渲染 (Lazy Loading)
•	原理：针对 1000 张图片的数据量，普通布局会导致内存溢出 (OOM)。
•	实现：使用了 LazyVerticalGrid。它采用“视窗回收机制”，只绘制屏幕上看得见的十几张图片。当用户滑动时，复用已滑出屏幕的组件来绘制新图片，从而实现无限滑动不卡顿。

