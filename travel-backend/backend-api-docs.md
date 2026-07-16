# 山水记 · 后端 API 接口与数据库设计文档

> 基于前端项目 `src/api/index.ts`、`src/types/`、`src/stores/`、`src/data/`、`src/views/SharedMap.vue`、`src/components/map/` 分析整理
>
> 前端技术栈：Vue 3 + TypeScript + Pinia + Axios + Element Plus + 高德地图 JSAPI 2.0
>
> 文档生成日期：2026-07-12 · 最后更新：2026-07-12（同行地图模块集成）

---

## 目录

1. [通用规范](#1-通用规范)
2. [数据库设计](#2-数据库设计)
3. [认证接口](#3-认证接口)
4. [目的地接口](#4-目的地接口)
5. [旅行者接口](#5-旅行者接口)
6. [统计接口](#6-统计接口)
7. [共同足迹接口](#7-共同足迹接口)
8. [同行地图接口](#8-同行地图接口)
9. [错误码约定](#9-错误码约定)

---

## 1. 通用规范

### 1.1 基础配置

| 项目 | 值 |
|------|-----|
| Base URL | `/api`（由环境变量 `VITE_API_BASE_URL` 控制，默认 `http://localhost:3000/api`） |
| 超时时间 | 10000ms |
| 请求格式 | `application/json` |
| 响应格式 | `application/json` |
| 认证方式 | Bearer Token（请求头 `Authorization: Bearer <token>`） |

### 1.2 请求拦截器行为

前端 Axios 实例在请求拦截器中自动从 `localStorage` 读取 key `auth_token`，附加到请求头：

```typescript
// 前端自动行为，后端需据此校验
Authorization: Bearer <token>
```

### 1.3 响应格式约定

建议后端统一使用以下响应格式：

```json
{
  "code": 200,
  "data": { ... },
  "message": "success"
}
```

---

## 2. 数据库设计

### 2.1 表：`destinations`（目的地）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | VARCHAR(64) | 是 | 主键，唯一标识，如 `kunming-m`、`dali-f`。后缀 `-m` = 男方，`-f` = 女方 |
| `name` | VARCHAR(100) | 是 | 目的地名称，如"昆明"、"大理" |
| `region` | VARCHAR(50) | 是 | 所属地区，如"云南"、"东北"、"四川" |
| `country` | VARCHAR(50) | 是 | 国家，如"中国"、"韩国" |
| `status` | VARCHAR(10) | 是 | 状态：`visited`（已踏足）/ `wishlist`（向往中） |
| `description` | TEXT | 是 | 目的地描述文案（长篇中文） |
| `highlights` | JSON / TEXT | 否 | 亮点推荐列表，JSON 数组：`["滇池观鸥", "西山龙门"]` |
| `best_season` | VARCHAR(100) | 否 | 最佳旅行季节，如"3月–10月" |
| `images` | JSON / TEXT | 否 | 图片列表，JSON 数组（见下方结构） |
| `latitude` | DECIMAL(10, 7) | 否 | 纬度 |
| `longitude` | DECIMAL(10, 7) | 否 | 经度 |
| `rating` | DECIMAL(2, 1) | 否 | 评分，范围 0.0 ~ 5.0 |
| `tags` | JSON / TEXT | 否 | 标签列表，JSON 数组：`["春城", "少数民族文化"]` |
| `created_at` | DATETIME | 是 | 创建时间 |
| `updated_at` | DATETIME | 是 | 更新时间 |

**`images` JSON 字段结构：**

```json
[
  {
    "id": "km-1",
    "url": "https://example.com/image.jpg",
    "alt": "图片描述",
    "width": 800,
    "height": 600
  }
]
```

### 2.2 表：`traveler_destinations`（旅行者-目的地关联）

由于同一个目的地可能被男方、女方或两人共同访问，需要关联表记录归属关系。

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | BIGINT | 是 | 主键，自增 |
| `destination_id` | VARCHAR(64) | 是 | 外键，关联 `destinations.id` |
| `traveler_id` | VARCHAR(10) | 是 | 旅行者标识：`male` / `female` |
| `created_at` | DATETIME | 是 | 创建时间 |

**唯一约束：** `UNIQUE(destination_id, traveler_id)` — 同一目的地对同一旅行者不重复。

### 2.3 表：`users`（管理员用户）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | BIGINT | 是 | 主键，自增 |
| `username` | VARCHAR(50) | 是 | 用户名，唯一 |
| `password` | VARCHAR(255) | 是 | 密码（BCrypt 加密存储） |
| `created_at` | DATETIME | 是 | 创建时间 |
| `updated_at` | DATETIME | 是 | 更新时间 |

**初始演示账号：** `admin` / `admin123`（前端 hardcode 于 `src/stores/auth.ts:15`）

### 2.4 实体关系图（ER）

```
┌──────────────┐       ┌─────────────────────────┐       ┌──────────────┐
│    users     │       │  traveler_destinations   │       │ destinations │
├──────────────┤       ├─────────────────────────┤       ├──────────────┤
│ id (PK)      │       │ id (PK)                  │       │ id (PK)      │
│ username     │       │ destination_id (FK)      │──┐    │ name         │
│ password     │       │ traveler_id              │  │    │ region       │
│ created_at   │       │ created_at               │  │    │ country      │
│ updated_at   │       └─────────────────────────┘  │    │ description  │
└──────────────┘                                     ├─── │ highlights   │
                                                      │    │ best_season  │
                                                      │    │ images       │
                                                      │    │ latitude     │
                                                      │    │ longitude    │
                                                      │    │ rating       │
                                                      │    │ tags         │
                                                      │    │ created_at   │
                                                      │    │ updated_at   │
                                                      └─── └──────────────┘
```

### 2.5 建表 SQL（MySQL 示例）

```sql
-- 目的地表
CREATE TABLE destinations (
    id          VARCHAR(64)    PRIMARY KEY,
    name        VARCHAR(100)   NOT NULL,
    region      VARCHAR(50)    NOT NULL,
    country     VARCHAR(50)    NOT NULL DEFAULT '中国',
    status      VARCHAR(10)    NOT NULL DEFAULT 'wishlist' COMMENT 'visited | wishlist',
    description TEXT           NOT NULL,
    highlights  JSON           DEFAULT NULL,
    best_season VARCHAR(100)   DEFAULT NULL,
    images      JSON           DEFAULT NULL,
    latitude    DECIMAL(10, 7) DEFAULT NULL,
    longitude   DECIMAL(10, 7) DEFAULT NULL,
    rating      DECIMAL(2, 1)  DEFAULT 4.5,
    tags        JSON           DEFAULT NULL,
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 旅行者-目的地关联表
CREATE TABLE traveler_destinations (
    id              BIGINT      AUTO_INCREMENT PRIMARY KEY,
    destination_id  VARCHAR(64) NOT NULL,
    traveler_id     VARCHAR(10) NOT NULL COMMENT 'male | female',
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dest_traveler (destination_id, traveler_id),
    FOREIGN KEY (destination_id) REFERENCES destinations(id) ON DELETE CASCADE
);

-- 管理员用户表
CREATE TABLE users (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

## 3. 认证接口

### 3.1 登录

```
POST /api/auth/login
```

**请求体：**

```json
{
  "username": "admin",
  "password": "admin123"
}
```

**成功响应：**

```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "user": {
      "username": "admin"
    }
  },
  "message": "登录成功"
}
```

**失败响应：**

```json
{
  "code": 401,
  "data": null,
  "message": "用户名或密码错误"
}
```

**前端行为：** 登录成功后，`token` 存入 `localStorage`（key: `auth_token`），用户信息存入 `localStorage`（key: `travel_user`），后续请求自动携带 Bearer Token。

### 3.2 登出

前端仅在本地清除 `localStorage` 中的 `auth_token` 和 `travel_user`，无需后端接口。如有 Token 黑名单需求可扩展：

```
POST /api/auth/logout
```

---

## 4. 目的地接口

### 4.1 获取所有目的地

```
GET /api/destinations
```

**响应数据结构：**

```json
{
  "code": 200,
  "data": [
    {
      "id": "kunming-m",
      "name": "昆明",
      "region": "云南",
      "country": "中国",
      "description": "春城昆明，四季如春...",
      "highlights": ["滇池观鸥", "西山龙门", "翠湖公园"],
      "bestSeason": "3月–10月",
      "images": [
        {
          "id": "km-1",
          "url": "https://example.com/kunming-1.jpg",
          "alt": "滇池",
          "width": 800,
          "height": 600
        }
      ],
      "coordinates": { "lat": 25.0389, "lng": 102.7183 },
      "rating": 4.5,
      "tags": ["春城", "少数民族文化", "自然风光"]
    }
  ],
  "message": "success"
}
```

### 4.2 获取单个目的地

```
GET /api/destinations/{id}
```

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | string | 目的地 ID，如 `kunming-m` |

### 4.3 按地区筛选

```
GET /api/destinations?region={region}
```

**查询参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `region` | string | 地区名称，如 `云南`、`东北` |

### 4.4 新增目的地（需认证）

```
POST /api/destinations
```

**请求头：** `Authorization: Bearer <token>`

**请求体：**

```json
{
  "name": "成都",
  "region": "四川",
  "country": "中国",
  "description": "天府之国，熊猫故乡...",
  "highlights": ["大熊猫基地", "宽窄巷子", "锦里", "都江堰"],
  "bestSeason": "3月–6月、9月–11月",
  "images": [
    {
      "id": "cd-1",
      "url": "https://example.com/chengdu-1.jpg",
      "alt": "大熊猫",
      "width": 800,
      "height": 600
    }
  ],
  "latitude": 30.5728,
  "longitude": 104.0668,
  "rating": 4.6,
  "tags": ["美食", "熊猫", "休闲"],
  "owner": "male"
}
```

**owner 字段说明：** `"male"` / `"female"` / `"both"` — 控制目的地归属哪位旅行者。

### 4.5 更新目的地（需认证）

```
PUT /api/destinations/{id}
```

**请求头：** `Authorization: Bearer <token>`

**请求体：** 同新增接口，`owner` 字段控制归属变更。

### 4.6 删除目的地（需认证）

```
DELETE /api/destinations/{id}
```

**请求头：** `Authorization: Bearer <token>`

---

## 5. 旅行者接口

### 5.1 获取旅行者资料及目的地列表

```
GET /api/travelers/{travelerId}
```

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `travelerId` | string | `male` 或 `female` |

**响应数据结构：**

```json
{
  "code": 200,
  "data": {
    "id": "male",
    "name": "他的旅程",
    "avatar": "https://example.com/avatar.jpg",
    "destinations": [
      {
        "id": "kunming-m",
        "name": "昆明",
        "region": "云南",
        "country": "中国",
        "description": "...",
        "highlights": ["滇池观鸥", "西山龙门"],
        "bestSeason": "3月–10月",
        "images": [ ... ],
        "coordinates": { "lat": 25.0389, "lng": 102.7183 },
        "rating": 4.5,
        "tags": ["春城", "少数民族文化"]
      }
    ]
  },
  "message": "success"
}
```

**后端实现建议：** 通过 `traveler_destinations` 表 JOIN `destinations` 表查询。

```sql
SELECT d.*
FROM destinations d
INNER JOIN traveler_destinations td ON td.destination_id = d.id
WHERE td.traveler_id = ?
```

---

## 6. 统计接口

### 6.1 获取旅行统计数据

```
GET /api/stats
```

**响应数据结构：**

```json
{
  "code": 200,
  "data": {
    "maleCount": 15,
    "femaleCount": 8,
    "sharedCount": 3,
    "totalRegions": 10,
    "countries": ["中国", "韩国"],
    "visitedCount": 11,
    "wishlistCount": 12
  },
  "message": "success"
}
```

**字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| `maleCount` | int | 男方目的地总数 |
| `femaleCount` | int | 女方目的地总数 |
| `sharedCount` | int | 共同目的地数量（双方都有记录的目的地名称去重计数） |
| `totalRegions` | int | 双方覆盖的地区总数（去重） |
| `countries` | string[] | 涉及的国家列表（去重） |
| `visitedCount` | int | 已踏足目的地数量（按名称去重，双方取并集） |
| `wishlistCount` | int | 向往中目的地数量（按名称去重，双方取并集） |

> **注意：** `visitedCount` / `wishlistCount` 仅统计 `status` 字段。同一个目的地如被双方都标记为 `visited`，只计 1 次。`sharedCount` 统计双方都有记录的目的地（不限状态），与前端 store 的 `sharedOnly` 逻辑一致。

**后端实现建议：**

```sql
-- maleCount
SELECT COUNT(*) FROM traveler_destinations WHERE traveler_id = 'male';

-- femaleCount
SELECT COUNT(*) FROM traveler_destinations WHERE traveler_id = 'female';

-- sharedCount: 双方都有的目的地(按名称匹配)
SELECT COUNT(DISTINCT d.name)
FROM destinations d
JOIN traveler_destinations td_m ON td_m.destination_id = d.id AND td_m.traveler_id = 'male'
JOIN traveler_destinations td_f ON td_f.destination_id = d.id AND td_f.traveler_id = 'female';

-- visitedCount: 双方 visited 目的地的并集（按名称去重）
SELECT COUNT(DISTINCT d.name)
FROM destinations d
JOIN traveler_destinations td ON td.destination_id = d.id
WHERE d.status = 'visited';

-- wishlistCount: 双方 wishlist 目的地的并集（按名称去重）
SELECT COUNT(DISTINCT d.name)
FROM destinations d
JOIN traveler_destinations td ON td.destination_id = d.id
WHERE d.status = 'wishlist';

-- totalRegions & countries
SELECT COUNT(DISTINCT d.region) AS totalRegions,
       JSON_ARRAYAGG(DISTINCT d.country) AS countries
FROM destinations d
JOIN traveler_destinations td ON td.destination_id = d.id;
```

---

## 7. 共同足迹接口

### 7.1 获取共同足迹分析

```
GET /api/shared
```

**响应数据结构：**

```json
{
  "code": 200,
  "data": [
    {
      "name": "大理",
      "maleVisited": true,
      "femaleVisited": true,
      "region": "云南",
      "coordinates": { "lat": 25.6065, "lng": 100.2681 }
    },
    {
      "name": "昆明",
      "maleVisited": true,
      "femaleVisited": true,
      "region": "云南",
      "coordinates": { "lat": 25.0389, "lng": 102.7183 }
    },
    {
      "name": "哈尔滨",
      "maleVisited": true,
      "femaleVisited": true,
      "region": "东北",
      "coordinates": { "lat": 45.8038, "lng": 126.535 }
    },
    {
      "name": "丽江",
      "maleVisited": true,
      "femaleVisited": false,
      "region": "云南",
      "coordinates": { "lat": 26.8721, "lng": 100.2299 }
    }
  ]
}
```

**字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| `name` | string | 目的地名称 |
| `maleVisited` | boolean | 男方是否有此目的地记录 |
| `femaleVisited` | boolean | 女方是否有此目的地记录 |
| `region` | string | 所属地区 |
| `coordinates` | object | 经纬度坐标 `{ lat: number, lng: number }`，用于地图标注。同名目的地取任意一条的坐标即可（坐标相同） |

**说明：** 此接口返回所有目的地（按名称去重）及其归属关系。前端根据 `maleVisited && femaleVisited` 筛选出"共同足迹"，以 `maleVisited && !femaleVisited` 筛选"他独有的"，以 `!maleVisited && femaleVisited` 筛选"她独有的"。坐标数据直接用于同行地图页面的标记渲染。

**后端实现建议：**

```sql
SELECT
    d.name,
    d.region,
    d.latitude,
    d.longitude,
    MAX(CASE WHEN td.traveler_id = 'male' THEN 1 ELSE 0 END) AS male_visited,
    MAX(CASE WHEN td.traveler_id = 'female' THEN 1 ELSE 0 END) AS female_visited
FROM destinations d
JOIN traveler_destinations td ON td.destination_id = d.id
GROUP BY d.name, d.region, d.latitude, d.longitude;
```

---

## 8. 同行地图接口

> 本节对应前端 `src/views/SharedMap.vue`、`src/components/map/MapCore.vue` 的数据需求。

### 8.1 地图标记数据

同行地图页面需要在地图上展示所有目的地的标记点。数据由已有的两个接口组合提供：

| 数据需求 | 来源接口 | 说明 |
|---------|---------|------|
| 所有目的地（含坐标） | `GET /api/destinations` | 提供 `coordinates` 字段用于地图定位 |
| 归属关系 | `GET /api/shared` | 提供 `maleVisited` / `femaleVisited` 用于标记颜色区分 |

### 8.2 标记类型分类规则

前端 `MapCore.vue` 根据目的地 ID 后缀判断标记颜色：

| 标记颜色 | 条件 | 色值 |
|---------|------|------|
| 檀木棕（他的足迹） | 仅男方有此目的地 | `#8B5E3C` |
| 墨绿（她的足迹） | 仅女方有此目的地 | `#2D4A3E` |
| 鎏金（共同足迹） | 双方都有此目的地 | `#C9A96E` |

### 8.3 地图初始化数据流

```
SharedMap.vue onMounted
        │
        ├──▶ sharedApi.getShared()     ──▶ 获取归属关系 + 坐标（优先 API）
        │    失败时 fallback ──▶ store.shared（Pinia mock 数据）
        │
        ├──▶ loadAMap()                ──▶ 加载高德地图 JSAPI 2.0
        │    （使用 VITE_AMAP_KEY / VITE_AMAP_SECRET）
        │
        └──▶ MapCore 渲染标记
             ├── 按名称去重（同坐标合并）
             ├── 根据归属类型分配颜色
             └── 自适应缩放至所有标记可见
```

### 8.4 坐标数据要求

每个目的地 **必须** 提供有效的经纬度坐标，否则对应标记不会在地图上渲染。

| 目的地 | 坐标 (lat, lng) | 备注 |
|--------|----------------|------|
| 昆明 | 25.0389, 102.7183 | |
| 大理 | 25.6065, 100.2681 | |
| 丽江 | 26.8721, 100.2299 | |
| 玉龙雪山 | 27.0983, 100.1753 | |
| 金沙江/虎跳峡 | 27.2013, 100.1412 | |
| 哈尔滨 | 45.8038, 126.5350 | |
| 沈阳 | 41.8057, 123.4316 | |
| 九江/庐山 | 29.7052, 116.0020 | |
| 九寨沟 | 33.2631, 103.9185 | **注意：region = 四川** |
| 上饶/婺源 | 28.4549, 117.9436 | |
| 武功山 | 27.4634, 114.1714 | |
| 洛阳 | 34.6197, 112.4539 | |
| 老君山 | 33.7267, 111.6466 | |
| 西安 | 34.3416, 108.9398 | |
| 首尔 | 37.5665, 126.9780 | |
| 三亚 | 18.2528, 109.5120 | |
| 重庆 | 29.4316, 106.9123 | |
| 乐山 | 29.5637, 103.7682 | |

### 8.5 前端 MapCore 组件接口约定

`MapCore.vue` 接收以下 Props：

```typescript
interface MapCoreProps {
  destinations: Destination[]   // 所有目的地（含 coordinates）
  activeRegion: string | null   // 当前筛选地区，null = 全部
}
```

触发以下 Events：

```typescript
interface MapCoreEmits {
  (e: 'markerClick', dest: Destination): void  // 点击标记
}
```

### 8.6 高德地图配置

| 环境变量 | 说明 | 前端使用位置 |
|---------|------|------------|
| `VITE_AMAP_KEY` | 高德 JSAPI Key | `src/utils/amap.ts` |
| `VITE_AMAP_SECRET` | 高德安全码 | `src/utils/amap.ts`（挂载 `_AMapSecurityConfig`） |

地图样式使用 `amap://styles/whitesmoke`（浅色地图），初始化中心 `[104.0, 35.0]`（中国中部），默认缩放级别 5。

---

## 9. 错误码约定

| HTTP 状态码 | code | 说明 |
|-------------|------|------|
| 200 | 200 | 成功 |
| 400 | 400 | 请求参数错误 |
| 401 | 401 | 未认证 / Token 无效或过期 |
| 403 | 403 | 无权限 |
| 404 | 404 | 资源不存在 |
| 500 | 500 | 服务器内部错误 |

---

## 附录 A：与前端 Store 的对应关系

| 前端 Store / API 方法 | 对应后端接口 | 使用页面 |
|-----------------|-------------|---------|
| `destinationApi.getAll()` | `GET /api/destinations` | JourneysPage, HomePage, SharedMap |
| `destinationApi.getById(id)` | `GET /api/destinations/{id}` | DestinationDetail |
| `destinationApi.getByRegion(region)` | `GET /api/destinations?region={region}` | JourneysPage (region filter) |
| `travelerApi.getById(id)` | `GET /api/travelers/{travelerId}` | JourneysPage |
| `statsApi.getStats()` | `GET /api/stats` | HomePage, SharedMap |
| `sharedApi.getShared()` | `GET /api/shared` | SharedMap (map markers + venn) |
| `auth.login(username, password)` | `POST /api/auth/login` | LoginModal |
| `store.addDestination(dest, owner)` | `POST /api/destinations` | AdminPage |
| `store.updateDestination(id, data, owner)` | `PUT /api/destinations/{id}` | AdminPage |
| `store.removeDestination(id)` | `DELETE /api/destinations/{id}` | AdminPage |

## 附录 B：当前 Mock 数据概况

| 数据 | 数量 |
|------|------|
| 男方目的地 | 15 个（云南 5、东北 2、江西 3、四川 1、河南 2、陕西 1、安徽/江苏/江西 visited 共 5） |
| 女方目的地 | 10 个（云南 2、东北 1、韩国 1、海南 1、重庆 1、四川 1、安徽/江苏/江西 visited 共 5） |
| 共同目的地（按名称） | 7 个（大理、昆明、哈尔滨、马鞍山、南京、无锡、苏州、南昌） |
| 已踏足（共用） | 5 个（马鞍山、南京、无锡、苏州、南昌） |
| 向往中（共用） | 2 个（大理、昆明、哈尔滨） |
| 覆盖国家 | 2 个（中国、韩国） |
| 覆盖地区 | 11 个 |

> **数据修正记录（2026-07-12）：** 九寨沟 `region` 从"江西"修正为"四川"。详见 `src/data/destinations.ts`。

---

## 附录 C：前端认证流程

```
用户输入用户名/密码
        │
        ▼
POST /api/auth/login ──成功──▶ localStorage.setItem('auth_token', token)
        │                      localStorage.setItem('travel_user', JSON.stringify(user))
        │
        │失败
        ▼
显示错误提示
```

后续请求自动通过 Axios 拦截器从 `localStorage` 读取 `auth_token` 附加 Bearer Token。

路由守卫：`/admin` 路由在 `onMounted` 中检查 `auth.isAuthenticated`（基于 `localStorage` 中的 `travel_user`），未登录重定向到 `/`。

---

## 附录 D：前端地图模块架构

```
src/
├── utils/
│   └── amap.ts                      # 高德 JSAPI 加载器（单例模式）
├── types/
│   └── amap.d.ts                    # AMap 全局类型声明
├── components/
│   └── map/
│       ├── MapCore.vue              # 地图核心：初始化 AMap、渲染标记、信息窗
│       └── MapLegend.vue            # 图例面板：毛玻璃覆盖层
└── views/
    └── SharedMap.vue                # 同行地图页面：组合上述组件
```

**依赖关系：**

```
SharedMap.vue
  ├── MapCore.vue        ← loadAMap() from utils/amap.ts
  │   └── 自定义 SVG 标记（data URI）
  │   └── AMap.InfoWindow（自定义内容）
  ├── MapLegend.vue      ← 纯展示组件
  ├── SectionTitle.vue   ← 复用
  └── DecorativeDivider.vue ← 复用
```

**npm 依赖新增：**

| 包名 | 用途 |
|------|------|
| `element-plus` | UI 组件库（Skeleton 加载态等） |
| `@element-plus/icons-vue` | Element Plus 图标 |
| `@amap/amap-jsapi-loader` | 高德地图 JSAPI 2.0 动态加载器 |
