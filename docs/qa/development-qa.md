# 开发实践问答

## [测试策略] 如何为React组件设置单元测试

**标签**: `测试` `react` `jest` `组件测试` `tdd`
**文件关联**: src/components/, __tests__/, jest.config.js

### 问题
在开发React应用时，如何结构化和实现组件的单元测试，以确保功能可靠性的同时让测试易于维护且对AI友好？

### 解决方案
1. **在组件旁边创建测试文件**：
  - 使用 `.test.tsx` 或 `.spec.tsx` 扩展名
  - 在 `__tests__/` 目录中镜像组件目录结构

2. **遵循AAA模式**（准备、执行、断言）：
   ```javascript
   test('当提供用户名时应该渲染用户名', () => {
     // 准备
     const props = { name: '张三', email: 'zhangsan@example.com' };

     // 执行
     render(<UserCard {...props} />);

     // 断言
     expect(screen.getByText('张三')).toBeInTheDocument();
   });
   ```

3. **测试行为，而非实现**：
   - 专注于用户交互和输出
   - 避免直接测试组件内部状态

### 原因解释
- **可靠性**: 单元测试在开发早期捕获回归问题
- **文档化**: 测试作为组件行为的活文档
- **AI理解**: 结构化的测试模式帮助AI助手理解预期的组件行为
- **重构安全**: 良好的测试使代码重构更加安全

### 相关参考
- [React Testing Library最佳实践](https://testing-library.com/docs/guiding-principles/)
- docs/references/testing-guidelines.md

---

## [状态管理] 在Vue3项目中如何选择状态管理方案

**标签**: `状态管理` `vue3` `pinia` `vuex` `组合式api`
**文件关联**: src/stores/, src/composables/, package.json

### 问题
在Vue3项目中，面对Pinia、Vuex、以及原生Composition API等多种状态管理选择，如何根据项目规模和复杂度选择合适的状态管理方案？

### 解决方案
**小型项目（单页面或简单应用）**：
- 使用Vue3的 `reactive` 和 `ref` 进行本地状态管理
- 通过 `provide/inject` 共享跨组件状态

**中型项目（多页面，中等复杂度）**：
- 选择Pinia作为状态管理库
- 按功能模块划分store
- 使用TypeScript获得更好的类型支持

**大型项目（复杂业务逻辑）**：
- 使用Pinia + 模块化设计
- 实现状态持久化
- 配置开发工具支持

### 原因解释
- **渐进式**: 从简单到复杂的渐进式状态管理策略
- **开发效率**: 选择合适的工具避免过度设计或功能不足
- **团队协作**: 统一的状态管理模式提高团队开发效率
- **AI辅助**: 清晰的状态管理模式帮助AI理解应用架构并提供更好的建议

### 相关参考
- [Pinia官方文档](https://pinia.vuejs.org/)
- [Vue3状态管理指南](https://vuejs.org/guide/scaling-up/state-management.html)
- docs/references/state-management.md

---

## [性能优化] 如何优化前端应用的首屏加载速度

**标签**: `性能优化` `首屏加载` `打包优化` `缓存策略` `用户体验`
**文件关联**: webpack.config.js, vite.config.js, src/main.js

### 问题
当前端应用变得复杂时，首屏加载时间增长影响用户体验。如何系统性地优化首屏加载速度，特别是在移动端网络环境下？

### 解决方案
1. **资源优化**：
   ```javascript
   // 代码分割 - 路由级别
   const Home = () => import('./views/Home.vue');
   const About = () => import('./views/About.vue');

   // 预加载关键资源
   <link rel="preload" href="/fonts/main.woff2" as="font" type="font/woff2" crossorigin>
   ```

2. **打包优化**：
   - 启用Gzip/Brotli压缩
   - 使用Tree Shaking移除未使用代码
   - 图片资源使用WebP格式并设置懒加载

3. **缓存策略**：
   - 静态资源设置长期缓存
   - 使用Service Worker实现离线缓存
   - API数据合理缓存

4. **关键渲染路径优化**：
   - 内联关键CSS
   - 延迟加载非关键JavaScript
   - 使用骨架屏提升感知性能

### 原因解释
- **用户体验**: 快速加载显著提升用户满意度和留存率
- **SEO优化**: 加载速度是搜索引擎排名的重要因素
- **转化率**: 每100ms的加载时间减少可提升1%的转化率
- **移动优先**: 移动端网络环境对加载速度要求更高
- **AI优化**: 结构化的性能优化方案帮助AI识别瓶颈并提供针对性建议

### 相关参考
- [Web性能优化指南](https://web.dev/performance/)
- [前端性能监控最佳实践](https://developer.mozilla.org/en-US/docs/Web/Performance)
- docs/references/performance-optimization.md
