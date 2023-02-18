# Gin

# 拉取 Gin

```bash
github.com/gin-gonic/gin
```

# 编写接口函数

```go
func Feed(ctx *gin.Context) {
   ctx.JSON(http.StatusOK, gin.H{
      "name": "zhangsan",
   })
}
```

# 路由设置

```go
package routers

import (
	"tiktok/controller"

	"github.com/gin-gonic/gin"
)

func RouterInit(router *gin.Engine) {
	apiRouter := router.Group("/douyin")
	{
		//basic api
		apiRouter.GET("/feed", logic.Feed)
	}
}
```

# 主函数载入

```go
package main

import (
	"tiktok/routers"
	"github.com/gin-gonic/gin"
)

func main() {
	r := gin.Default()
	routers.RouterInit(r)
	r.Run() //127.0.0.1:8080
}
```

