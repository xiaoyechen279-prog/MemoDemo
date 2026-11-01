# MemoDemo 备忘录

一个用 Java 写的本地备忘录 App：记事、改字、标重要、搜索、删除，数据全存在手机本地的 SQLite 里，卸载即清空、离线可用。

> 本项目由**伍金灿**开发，因其无法登录 GitHub，由本人代为托管、代为发布。

## 功能

| 功能 | 说明 |
| --- | --- |
| 新建备忘录 | 右下角悬浮按钮 → 弹窗填写标题、内容，可勾选“重要” |
| 编辑备忘录 | 点击列表项 → 弹窗回填原有内容，改完保存 |
| 标记重要 | 弹窗里勾选“重要”，列表项显示星标角标 |
| 删除备忘录 | 长按列表项 → 确认弹窗（删除按钮标红），确认后删除 |
| 实时搜索 | 顶部搜索框按标题/内容过滤，输入即筛选，右侧一键清空 |
| 空状态提示 | 无记录时显示引导文案；搜索无结果时显示另一套文案，两者分开 |
| 计数 | 顶部显示当前列表条数，随搜索联动 |
| 沉浸式适配 | 内容延伸到状态栏/导航栏下方，头部、列表、悬浮按钮各自避让系统栏 |
| 深色模式 | `values-night` 下另给一套配色，跟随系统深浅色 |

## 技术栈

- 语言：Java（JDK 11 编译级别）
- 最低 SDK 24，目标/编译 SDK 37
- UI：View 体系 + ListView + 自定义 BaseAdapter，Material Components（ExtendedFloatingActionButton、TextInputLayout、MaterialCheckBox、MaterialAlertDialogBuilder）
- 存储：SQLite（SQLiteOpenHelper + DAO 手写 SQL，无 Room、无网络）
- 构建：Gradle Kotlin DSL + Version Catalog（`gradle/libs.versions.toml`）

## 目录结构

```
MemoDemo/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/memodemo/
│       │   ├── MainActivity.java     # 唯一 Activity：列表、搜索、弹窗、空状态
│       │   ├── Memo.java             # 数据模型：id / title / content / isImportant
│       │   ├── MemoDao.java          # 增删改查，全部手写 SQL
│       │   ├── DBHelper.java         # 建表与升级（v1 → v2 增加 is_important 字段）
│       │   └── MemoAdapter.java      # 列表适配器，星标与点击回调
│       └── res/                      # 布局、drawable、颜色、深浅色主题
├── gradle/libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```

## 数据库

库名 `memo_db`，表 `memo`：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `_id` | INTEGER PRIMARY KEY AUTOINCREMENT | 主键 |
| `title` | TEXT | 标题，保存时校验非空 |
| `content` | TEXT | 正文 |
| `is_important` | INTEGER DEFAULT 0 | 0 普通、1 重要 |

`onUpgrade` 里对 v1 老库执行 `ALTER TABLE memo ADD COLUMN is_important INTEGER DEFAULT 0`，升级不丢数据。

## 构建与运行

```bash
# 需要本地已装 Android SDK，并在根目录放 local.properties（该文件不入库）
# sdk.dir=C\:\\Users\\<你的用户名>\\AppData\\Local\\Android\\Sdk

./gradlew assembleDebug        # 产物 app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug         # 直接装到已连接的设备/模拟器
```

用 Android Studio 打开根目录也可以直接 Run。

## 说明

- `local.properties`、`build/`、`.gradle/`、`.idea/` 均未入库。
- 库中没有硬编码的密钥或签名文件，release 版本请自行配置 `signingConfigs`。
