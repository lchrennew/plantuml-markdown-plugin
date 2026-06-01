# PlantUML Markdown Preview - 跨平台实现指南

本文档说明如何将 IntelliJ IDEA 插件功能迁移到 VS Code 插件。

## 目录结构

### 项目根目录 (monorepo 风格)
```
plant-uml-plugin/
├── README.md              # 项目总 README
├── VSC_EXTENSION_README.md  # 本文档
├── src/                   # IntelliJ IDEA 插件源码
└── vscode-extension/      # VS Code 插件源码 (新增)
    ├── package.json
    ├── tsconfig.json
    ├── src/
    │   ├── extension.ts
    │   └── prerender.ts
    └── styles.css
```

## 架构对比

### 1. 平台特定的部分

| 功能 | IntelliJ IDEA | VS Code |
|------|--------------|---------|
| **插件入口点** | `META-INF/plugin.xml` + Java 类 | `package.json` + TypeScript |
| **Markdown 扩展机制** | `CodeFenceGeneratingProvider` | `markdown.previewScripts` + `markdown.previewStyles` |
| **依赖管理** | Maven (pom.xml) | npm (package.json) |
| **打包格式** | `.zip` (plugin) | `.vsix` |

### 2. 平台无关的核心逻辑

| 功能 | Java 实现 (IntelliJ) | TypeScript 实现 (VS Code) |
|------|---------------------|-------------------------|
| **PlantUML 代码块识别** | `PlantUmlCodeFenceProvider.isApplicable()` | `prerender.ts` 中的 DOM 查询 |
| **PlantUML 渲染逻辑** | `PlantUmlRenderer.renderToBase64Png()` | `prerender.ts` 中的编码 + 服务器调用 |
| **Smetana 引擎注入** | 直接修改源码字符串 | 同样逻辑，只是语言不同 |

## VS Code 插件使用说明

### 1. 开发环境准备

```bash
cd vscode-extension/
npm install
```

### 2. 构建和调试

```bash
# 编译 TypeScript
npm run compile

# 运行扩展 (在 VS Code 中按 F5)
```

### 3. 打包和发布

```bash
npm install -g @vscode/vsce
vsce package  # 生成 .vsix 文件
vsce publish  # 发布到 VS Code Marketplace
```

## 两种平台的实现原理对比

### IntelliJ IDEA 插件
- 运行在 IDEA 平台上的 JVM 中
- 直接使用 PlantUML Java 库 (`plantuml-1.2026.5.jar`)
- 通过内部 API 与 Markdown 预览集成
- 无需外部服务依赖，完全在本地运行

### VS Code 插件 (当前实现)
- 有两种主要方式：
  1. **方式 1 (当前)**: 浏览器端渲染，使用公共 PlantUML 服务器
  2. **方式 2 (更好)**: 使用 Markdown-it 插件 + 本地 PlantUML（通过 Node.js 调用 Java 或其他方式）

### VS Code 插件的优化建议

如果你想要一个更强大、无网络依赖的 VS Code 插件，可以考虑以下改进：

1. 使用 `plantuml-encoder` npm 包来正确编码
2. 集成本地 PlantUML 渲染器（通过 Node.js 子进程调用 Java）
3. 使用 VS Code 的 Markdown-it 扩展点，而非简单的 DOM 替换

### Markdown-it 插件方式示例

```typescript
// src/markdownItPlugin.ts
import * as markdown from 'markdown-it';

export default function plantumlPlugin(md: markdown) {
  const defaultRender = md.renderer.rules.fence;
  
  md.renderer.rules.fence = (tokens, idx, options, env, self) => {
    const token = tokens[idx];
    const info = token.info ? token.info.trim() : '';
    
    if (['plantuml', 'puml'].includes(info)) {
      const code = token.content;
      // 在这里实现 PlantUML 渲染逻辑
      return `<img src="...">`;
    }
    
    return defaultRender!(tokens, idx, options, env, self);
  };
}
```

## PlantUML 渲染逻辑共享 (未来)

如果希望真正共享核心渲染逻辑（避免在两种语言中重复实现），可以考虑：
1. 将 PlantUML 渲染逻辑封装在一个平台无关的核心库中
2. 在 VS Code 中通过 WebAssembly 或 Node.js 绑定调用 Java 代码
3. 使用 JavaScript 实现的轻量级 PlantUML 渲染库（如 `node-plantuml`）

## 参考资源

- [VS Code Extension API Documentation](https://code.visualstudio.com/api)
- [VS Code Markdown Extension Guide](https://code.visualstudio.com/api/language-extensions/markdown)
- [PlantUML Official Site](https://plantuml.com/)

---
License: Apache 2.0
