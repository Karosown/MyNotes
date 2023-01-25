# Gorm

# Go源设置

更换国内源

```bash
SETX GO111MODULE on
#然后我们需要进行更改Go的GOPROXY值

go env -w GOPROXY=https://goproxy.cn,direct
#1
SETX来设置一下Windows的环境变量

SETX GOPROXY=https://goproxy.cn,direct
```

# 拉取Gorm

```bash
go get -u gorm.io/gorm
go get -u github.com/go-sql-driver/mysql
go get -u gorm.io/driver/mysq
```

# 连接数据库

```go
package config

import (
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"
	"gorm.io/gorm/schema"
	"log"
	"tiktok/common"
)

func MysqlInit() {
	// 日志打印
	newLogger := logger.Default

	log.Println("Mysql:初始化！")
	dsn := "root:你的密码@tcp(127.0.0.1:3306)/tiktok?charset=utf8mb4&parseTime=True&loc=Local"
	v, err := gorm.Open(mysql.Open(dsn), &gorm.Config{
		Logger: newLogger,
		NamingStrategy: schema.NamingStrategy{
			//TablePrefix:   "t_", // 定义表前缀
			SingularTable: true, // true不在表后面+ s，
		},
	})
	if err != nil {
		log.Panic(err)
	}
	common.Db = v
}
```

```go
package main

import (
	"tiktok/config"
)

func main() {
	config.MysqlInit()
}
```

# 使用

通过一个工厂获得gorm.DB()

```go
package service

import (
	"gorm.io/gorm"
	"log"
	"tiktok/common"
	"tiktok/model"
)

var UserService = userService{}

// userService 业务层
type userService struct {
}

func (t userService) db() *gorm.DB {
	return common.Db
}

// List 分页列表
func (t userService) List(page, size int, v *model.User) map[string]interface{} {
	// 结果
	var lists []model.User
	t.db().Model(&v).Where(&v).Order("").Offset((page - 1) * size).Limit(size).Find(&lists)
	// 统计
	var total int64
	t.db().Model(&v).Where(&v).Count(&total)
	data := make(map[string]interface{})
	data["list"] = lists
	data["total"] = total
	return data
}

// One 根据主键Id查询记录
func (t userService) One(id interface{}) model.User {
	var v model.User
	db := t.db().Find(&v, id)
	if db.RowsAffected != 1 {
		log.Println("未找到数据！")
	}
	return v
}

// Update 修改记录 true -> 操作成功
func (t userService) Update(v model.User) bool {
	tx := t.db().Model(&v).Updates(v)
	if tx.Error != nil {
		log.Panicln(tx.Error.Error())
		return false
	}
	return true
}

// Insert 插入记录 true -> 操作成功 注: 主键也传递进来的话，那么就会执行更新或插入操作
func (t userService) Insert(v model.User) bool {
	tx := t.db().Save(&v)
	if tx.Error != nil {
		log.Panicln(tx.Error.Error())
		return false
	}
	return true
}

// Delete 根据主键删除 true -> 操作成功
func (t userService) Delete(ids []int) bool {
	tx := t.db().Delete(model.User{}, ids)
	if tx.Error != nil {
		log.Panicln(tx.Error.Error())
		return false
	}
	return true
}
```

> 感觉和MyBaitsPlus有点像，但是只有mapper，会不会也有GormPlus呢 :smile: