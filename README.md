<p align="center">
  <img width="320" src="https://wpimg.wallstcn.com/ecc53a42-d79b-42e2-8852-5126b810a4c8.svg">
</p>

<p align="center">
  <a href="https://github.com/vuejs/vue">
    <img src="https://img.shields.io/badge/vue-2.6.10-brightgreen.svg" alt="vue">
  </a>
  <a href="https://github.com/ElemeFE/element">
    <img src="https://img.shields.io/badge/element--ui-2.7.0-brightgreen.svg" alt="element-ui">
  </a>
</p>

简体中文 

## 简介

[vue-iotequ-admin] 是一个前后端分离的综合解决方案，它基于 [vue](https://github.com/vuejs/vue) 和 [element-ui](https://github.com/ElemeFE/element)实现。它使用了最新的前端技术栈，内置了 i18n 国际化解决方案，动态路由，权限验证，提炼了典型的业务模型，提供了丰富的功能组件，它可以帮助你快速搭建企业级中后台产品原型。后台采用spring boot实现，集成spring security ，oauth2.0提供权限管理方案。同时提供基于数据库访问操作的代码生成工具(code-generator)，快速提供基本的数据操作的前后端代码。

- [在线预览](https://www.iotequ.top/admin)

- [使用文档](https://www.iotequ.top/admin/res/doc)

- [Donate](https://www.iotequ.top/admin/res/donate)

**该项目不支持低版本浏览器(如 ie)，有需求请自行添加 polyfill

**目前版本为 `v3.0.0` 基于 `vue-cli` 进行构建，若发现问题，欢迎提[issue](https://www.iotequ.top/admin/res/issues/new)。

## 前序准备

你需要在本地安装 [node.js](http://nodejs.org/) 。本项目技术栈基于 [ES2015+](http://es6.ruanyifeng.com/)、[vue](https://cn.vuejs.org/index.html)、[vuex](https://vuex.vuejs.org/zh-cn/)、[vue-router](https://router.vuejs.org/zh-cn/) 、[vue-cli](https://github.com/vuejs/vue-cli) 、[axios](https://github.com/axios/axios) 和 [element-ui](https://github.com/ElemeFE/element)，所有的请求数据都使用[Mock.js](https://github.com/nuysoft/Mock)进行模拟，提前了解和学习这些知识会对使用本项目有很大的帮助。

**如有问题请先看上述使用文档和文章，若不能满足，欢迎 issue 和 pr**


## 功能

```
- 登录 / 注销
  - 账号密码登录
  - 短线验证码登录
  - 第三方授权登录
  - 短信注册
  - 个人信息中心

- 权限验证
  - 路由权限
  - 后台访问控制
  - 角色权限
  - 部门权限
  - 权限配置

- 消息 / 任务
  - 日志
  - 消息
  - 事件
  - 任务

- 字典
  - 系统数据字典
  - 常量字典
  - sql选择数据字典
  - 视图检索字典
  - 动态条件检索
  - 多选、层级字典

- 流程控制
  - 有限状态机
  - 时间线
  - 消息与待办事务
  - 权限、人员、状态、迁移

- Excel
  - 导出excel
  - 导入excel
  - 前端本地化导出

- 列表
  - 动态表格
  - 拖拽表格
  - 内联编辑
  - 内嵌菜单
  - 右键菜单
  - 表格表单联动编辑

- 手机端
  - 自动适配
  - 统一的视图组件  
  - 适配手机的卡片表格
  - 下拉、上滑、左滑
  - 抽屉效果
  - actionSheet

- 組件
  - 图像上传
  - 文件上传
  - 二维码
  - 返回顶部
  - 拖拽Dialog
  - 列表拖拽
  - 可拖动分栏

- 页面框架
  - 国际化多语言
  - 消息
  - 动态侧边栏
  - 动态面包屑
  - 快捷导航(标签页)
  - Svg 图标
  - Screenfull全屏
  - 页脚  

- 多环境发布
  - dev sit stage prod

```

```bash

```

## 开发

```bash
# 克隆项目
git clone -b git@github.com:qinyoyo/framework.git

# 开发环境
idea / eclipse 导入maven项目

```
## 简化的后端java代码结构
#### 生成的文件目录结构：

- controller : controller文件，自定义代码请通过服务扩展实现
-	dao 数据访问接口
- pojo：所有的生成的实体类均实现接口 CgEntity，实现CgTableAnnotation标注接口
- service/impl：服务，ICgService的实现类。
  命名规则为：
  XxxController 注入 CgXxxService实例，CgXxxService为Cg自动生成文件
  自定义代码文件，请实现服务：
  @Service
  public class XxxService extends CgXxxService
  如果定义了自定义的XxxService，controller将注入该实例而不注入CgXxxService实例
  如果定义了流程控制，请实现服务
  public class XxxFlowService extends SysFlowProcessService<Xxx> 或
  public class XxxFlowService implements IFlowService<Xxx>
-	util目录请自行定义
-	resources/mybatis ： mapper定义文件
-	cg自定义代码原则：
  -	不要修改由cg生成的文件
  -	通过服务实现特定功能
  -	所有的url请求必须通过button的完整定义，以保持前后端一致。具体的功能实现在自定义服务代码中实现

Copyright (c) 2020-present Qinyoyo
