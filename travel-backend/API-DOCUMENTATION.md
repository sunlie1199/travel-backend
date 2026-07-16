# 山水记 · 旅游网站后端 API 接口文档

> **Base URL:** `http://localhost:3000/api`  
> **Version:** 1.0.0  
> **Auth:** Bearer Token (JWT)

---

## 通用约定

### 统一响应格式

所有接口返回统一 JSON 格式：

```json
{
  "code": 200,
  "data": { ... },
  "message": "success"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | `int` | 状态码，200 = 成功 |
| `data` | `T` / `null` | 响应数据 |
| `message` | `string` | 提示信息 |

### 错误码

| 状态码 | 含义 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证或 Token 无效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 认证方式

登录成功后获取 JWT Token，后续鉴权接口在请求头携带：

```
Authorization: Bearer <token>
```

---

## 模块一：认证 (Auth)

### 1. 登录

```
POST /auth/login
```

**权限：** 无需认证

**请求体：**

```json
{
  "username": "admin",
  "password": "admin123"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `username` | `string` | 是 | 用户名 |
| `password` | `string` | 是 | 密码 |

**成功响应：**

```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "username": "admin"
    }
  },
  "message": "success"
}
```

**错误响应示例：**

```json
{
  "code": 401,
  "data": null,
  "message": "用户名或密码错误"
}
```

> **默认管理员账号：** `admin` / `admin123`（首次启动自动创建）

---

## 模块二：目的地 (Destinations)

### 2. 获取目的地列表

```
GET /destinations
```

**权限：** 无需认证

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `region` | `string` | 否 | 按地区筛选，如 `华东`、`西南` |

**成功响应：**

```json
{
  "code": 200,
  "data": [
    {
      "id": "7583-a1b2c3d4",
      "name": "九寨沟",
      "region": "西南",
      "country": "中国",
      "description": "童话世界，人间天堂",
      "highlights": ["五彩池", "诺日朗瀑布", "树正群海"],
      "bestSeason": "秋季（9-11月）",
      "status": "visited",
      "images": [
        {
          "id": "img_01",
          "url": "https://example.com/jiuzhaigou.jpg",
          "alt": "九寨沟五彩池",
          "width": 1920,
          "height": 1080
        }
      ],
      "coordinates": {
        "lat": 33.2632,
        "lng": 103.9187
      },
      "rating": 4.8,
      "tags": ["自然风光", "世界遗产", "5A景区"]
    }
  ],
  "message": "success"
}
```

### 3. 获取单个目的地

```
GET /destinations/{id}
```

**权限：** 无需认证

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | `string` | 是 | 目的地 ID |

**成功响应：**

```json
{
  "code": 200,
  "data": {
    "id": "7583-a1b2c3d4",
    "name": "九寨沟",
    "region": "西南",
    "country": "中国",
    "description": "童话世界，人间天堂",
    "highlights": ["五彩池", "诺日朗瀑布"],
    "bestSeason": "秋季（9-11月）",
    "status": "visited",
    "images": [...],
    "coordinates": { "lat": 33.2632, "lng": 103.9187 },
    "rating": 4.8,
    "tags": ["自然风光", "世界遗产"]
  },
  "message": "success"
}
```

**错误响应：**

```json
{
  "code": 404,
  "data": null,
  "message": "目的地不存在"
}
```

### 4. 创建目的地

```
POST /destinations
```

**权限：** 需要认证  
**请求头：** `Authorization: Bearer <token>`

**请求体：**

```json
{
  "name": "九寨沟",
  "region": "西南",
  "country": "中国",
  "status": "visited",
  "description": "童话世界，人间天堂",
  "highlights": ["五彩池", "诺日朗瀑布", "树正群海"],
  "bestSeason": "秋季（9-11月）",
  "images": [
    {
      "id": "img_01",
      "url": "https://example.com/jiuzhaigou.jpg",
      "alt": "九寨沟五彩池",
      "width": 1920,
      "height": 1080
    }
  ],
  "latitude": 33.2632,
  "longitude": 103.9187,
  "rating": 4.8,
  "tags": ["自然风光", "世界遗产", "5A景区"],
  "owner": "both"
}
```

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `name` | `string` | 是 | — | 目的地名称 |
| `region` | `string` | 是 | — | 地区（如 `华东`、`西南`） |
| `country` | `string` | 是 | — | 国家 |
| `status` | `string` | 否 | `wishlist` | 状态：`wishlist` / `visited` |
| `description` | `string` | 是 | — | 描述 |
| `highlights` | `string[]` | 否 | — | 亮点列表 |
| `bestSeason` | `string` | 否 | — | 最佳旅行季节 |
| `images` | `ImageItem[]` | 否 | — | 图片列表 |
| `latitude` | `BigDecimal` | 否 | — | 纬度 |
| `longitude` | `BigDecimal` | 否 | — | 经度 |
| `rating` | `BigDecimal` | 否 | `4.5` | 评分 (0-5) |
| `tags` | `string[]` | 否 | — | 标签 |
| `owner` | `string` | 是 | — | 归属：`male` / `female` / `both` |

**`ImageItem` 结构：**

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `string` | 图片 ID |
| `url` | `string` | 图片 URL |
| `alt` | `string` | 替代文本 |
| `width` | `int` | 宽度 |
| `height` | `int` | 高度 |

**成功响应：** 返回创建的 `DestinationResponse` 对象，code = 200。

### 5. 更新目的地

```
PUT /destinations/{id}
```

**权限：** 需要认证

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | `string` | 是 | 目的地 ID |

**请求体：** 同创建接口。

**说明：** 更新时会先删除旧的旅行者绑定，再根据 `owner` 重新绑定。

**成功响应：** 返回更新后的 `DestinationResponse` 对象，code = 200。

### 6. 删除目的地

```
DELETE /destinations/{id}
```

**权限：** 需要认证

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | `string` | 是 | 目的地 ID |

**成功响应：**

```json
{
  "code": 200,
  "data": null,
  "message": "success"
}
```

---

## 模块三：旅行者 (Travelers)

### 7. 获取旅行者及其目的地

```
GET /travelers/{travelerId}
```

**权限：** 无需认证

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `travelerId` | `string` | 是 | `male` 或 `female` |

**成功响应：**

```json
{
  "code": 200,
  "data": {
    "id": "male",
    "name": "他的旅程",
    "avatar": "",
    "destinations": [
      {
        "id": "7583-a1b2c3d4",
        "name": "九寨沟",
        "region": "西南",
        "country": "中国",
        "description": "...",
        "highlights": [...],
        "bestSeason": "秋季（9-11月）",
        "status": "visited",
        "images": [...],
        "coordinates": { "lat": 33.2632, "lng": 103.9187 },
        "rating": 4.8,
        "tags": [...]
      }
    ]
  },
  "message": "success"
}
```

**错误响应：**

```json
{
  "code": 404,
  "data": null,
  "message": "旅行者不存在"
}
```

---

## 模块四：统计 (Stats)

### 8. 获取统计数据

```
GET /stats
```

**权限：** 无需认证

**成功响应：**

```json
{
  "code": 200,
  "data": {
    "maleCount": 15,
    "femaleCount": 12,
    "sharedCount": 8,
    "totalRegions": 6,
    "countries": ["中国", "日本", "泰国"],
    "visitedCount": 20,
    "wishlistCount": 7
  },
  "message": "success"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `maleCount` | `int` | 男性旅行者的目的地数量 |
| `femaleCount` | `int` | 女性旅行者的目的地数量 |
| `sharedCount` | `int` | 双方共同打卡的目的地数量 |
| `totalRegions` | `int` | 覆盖的地区总数 |
| `countries` | `string[]` | 覆盖的国家列表 |
| `visitedCount` | `int` | 已打卡数量 |
| `wishlistCount` | `int` | 愿望清单数量 |

---

## 模块五：共同足迹 (Shared)

### 9. 获取共同足迹

```
GET /shared
```

**权限：** 无需认证

**成功响应：**

```json
{
  "code": 200,
  "data": [
    {
      "name": "九寨沟",
      "maleVisited": true,
      "femaleVisited": true,
      "region": "西南",
      "coordinates": {
        "lat": 33.2632,
        "lng": 103.9187
      }
    },
    {
      "name": "黄山",
      "maleVisited": true,
      "femaleVisited": false,
      "region": "华东",
      "coordinates": {
        "lat": 30.1346,
        "lng": 118.1686
      }
    }
  ],
  "message": "success"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `name` | `string` | 目的地名称 |
| `maleVisited` | `boolean` | 男方是否打卡 |
| `femaleVisited` | `boolean` | 女方是否打卡 |
| `region` | `string` | 所属地区 |
| `coordinates` | `object` | 经纬度坐标 `{ lat, lng }` |

---

## 模块六：文件 (File)

### 10. 上传文件

```
POST /file/upload
```

**权限：** 需要认证  
**请求头：** `Authorization: Bearer <token>`  
**Content-Type：** `multipart/form-data`

**表单参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `file` | `file` | 是 | 上传的文件（最大 10MB） |

**成功响应：**

```json
{
  "code": 200,
  "data": {
    "url": "http://localhost:9000/travel-images/travel/a1b2c3d4-e5f6.jpg"
  },
  "message": "success"
}
```

**说明：** 文件上传到 MinIO 对象存储，路径为 `travel/<UUID><扩展名>`。返回的 URL 可直接在 `<img>` 标签中使用。

### 11. 删除文件

```
DELETE /file/delete?url=<图片URL>
```

**权限：** 需要认证  
**请求头：** `Authorization: Bearer <token>`

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `url` | `string` | 是 | 上传时返回的完整图片 URL（需 URL 编码） |

**成功响应：**

```json
{
  "code": 200,
  "data": null,
  "message": "success"
}
```

**错误响应：**

```json
{
  "code": 400,
  "data": null,
  "message": "文件删除失败: 非法的文件 URL"
}
```

**说明：** 仅允许删除本 bucket 下的文件，非法 URL 会被拒绝。

---

## 接口权限汇总

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| `POST` | `/auth/login` | 否 | 登录获取 Token |
| `GET` | `/destinations` | 否 | 目的地列表 |
| `GET` | `/destinations/{id}` | 否 | 目的地详情 |
| `POST` | `/destinations` | **是** | 创建目的地 |
| `PUT` | `/destinations/{id}` | **是** | 更新目的地 |
| `DELETE` | `/destinations/{id}` | **是** | 删除目的地 |
| `GET` | `/travelers/{travelerId}` | 否 | 旅行者信息 |
| `GET` | `/stats` | 否 | 统计数据 |
| `GET` | `/shared` | 否 | 共同足迹 |
| `POST` | `/file/upload` | **是** | 上传文件 |
| `DELETE` | `/file/delete` | **是** | 删除文件 |

---

## 数据库表结构

### destinations（目的地表）

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `VARCHAR(64)` | 主键，格式：`<hash>-<uuid8>` |
| `name` | `VARCHAR(100)` | 目的地名称 |
| `region` | `VARCHAR(50)` | 地区 |
| `country` | `VARCHAR(50)` | 国家 |
| `status` | `VARCHAR(10)` | `wishlist` / `visited` |
| `description` | `TEXT` | 描述 |
| `highlights` | `JSON` | 亮点 |
| `best_season` | `VARCHAR(100)` | 最佳季节 |
| `images` | `JSON` | 图片列表 |
| `latitude` | `DECIMAL(10,7)` | 纬度 |
| `longitude` | `DECIMAL(10,7)` | 经度 |
| `rating` | `DECIMAL(2,1)` | 评分 |
| `tags` | `JSON` | 标签 |
| `created_at` | `DATETIME` | 创建时间 |
| `updated_at` | `DATETIME` | 更新时间 |

### traveler_destinations（旅行者-目的地关联表）

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `BIGINT` | 主键自增 |
| `destination_id` | `VARCHAR(64)` | 目的地 ID（外键） |
| `traveler_id` | `VARCHAR(10)` | `male` / `female` |
| `created_at` | `DATETIME` | 创建时间 |

### users（管理员用户表）

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `BIGINT` | 主键自增 |
| `username` | `VARCHAR(50)` | 用户名（唯一） |
| `password` | `VARCHAR(255)` | BCrypt 加密密码 |
| `created_at` | `DATETIME` | 创建时间 |
| `updated_at` | `DATETIME` | 更新时间 |
