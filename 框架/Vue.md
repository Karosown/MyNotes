# Vue

## 插值 {{ }}

$mont

## defineProperty

参数一为对象实例名，第二个为键，第三个为配置（.e.g:value ）

![image-20220426090415560](http://gd.7n.cdn.wzl1.top/typora/img/image-20220430213021227.png)

age是通过Object.defineProperty插入的，而你是每次读取的时候，会返回age的值等于number，你在set的时候会改，但在获取的是偶有改成了number了

## 数据代理  擦欧总 读/写

实现：defineProperty

## 事件处理

函数名(event)

函数名($event)

## 事件修饰符

1. prevent 阻止默认事件

2. stop阻止事件冒泡

3. once 事件只触发一次

4. self 只有event.target是当前操作元素才有效

   子组件调用函数，如果有event.target无法实现

   Vue中冒泡后event.target永远是被点击事件

![image-20220426131515193](http://gd.7n.cdn.wzl1.top/typora/img/image-20220426090415560.png)

![image-20220426131617706](http://gd.7n.cdn.wzl1.top/typora/img/image-20220426131832249.png)

![image-20220426131832249](http://gd.7n.cdn.wzl1.top/typora/img/image-20220426131617706.png)

## 计算属性

```javascript
new Vue({
    computed:{
        fullName:{
            //由于需要处理较大的数据，所以选择使用对象
            //getter和defineproperty一样
            //vue-getter在被读取的时候调用，当调用一次，在下次修改（所以来的数据）之前，会有一个cache，所以多次读取可能只会调用一次	
            get(){
                //若要调用data中的数据，请使用this指针（Vue优化）
            }
            //当fullname被修改的时候调用
            set(){
        
    }
        }
    }
})
```



## 监视属性

```javas
new Vue({
	watch:{
		varname:{
			//在varname发生改变时调用
			handler(newValue,oldValue){
			
			}
		}
	}
})
```

## 过滤器

后对前

filters

filter

v-bind，插值

## 内置指令

![image-20220430213021227](http://gd.7n.cdn.wzl1.top/typora/img/image-20220430232428170.png)

![image-20220430214647162](http://gd.7n.cdn.wzl1.top/typora/img/image-20220426131515193.png)

![image-20220430232428170](http://gd.7n.cdn.wzl1.top/typora/img/image-20220430232811696.png)

![image-20220430232617434](http://gd.7n.cdn.wzl1.top/typora/img/image-20220430214647162.png)

## 自定义指令

![image-20220430233713968](http://gd.7n.cdn.wzl1.top/typora/img/image-20220501105842800.png)

![image-20220501000752613](http://gd.7n.cdn.wzl1.top/typora/img/image-20220501000752613.png)![image-20220501105842800](http://gd.7n.cdn.wzl1.top/typora/img/image-20220502092424272.png)

指令性的this是window	

## 生命周期:

beforeCreated：生命周期、事件初始化 之后

Created：数据监测，数据代理之后，虚拟DOM解析之前

beforeMount（虚拟解析DOM完毕后）：在电影

![image-20220502092424272](http://gd.7n.cdn.wzl1.top/typora/img/image-20220430233713968.png)

## VueCompnoents的原型对象

![image-20220505204900831](http://gd.7n.cdn.wzl1.top/typora/img/image-20220505204900831.png)

## mixin混入

在单文件组件中，从mixin.js导入模块

mixins:[名称]

关于混入和原数据中，数据以原数据为主

**如果时生命周期钩子，则都要执行**