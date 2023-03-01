module.exports = {
  semi: true, // 是否在句末使用分号 ;
  singleQuote: false, // 是否单引号
  bracketSpacing: true, // true:{ var } false:{var}
  printWidth: 160, // 当一行超过 80 个字符，折叠行
  tabWidth: 2, // 缩进空格数
  useTabs: false, // 是否使用 tab(制表位) 而非空格
  trailingComma: "es5", // 是否多行中输入尾逗号 none:无 es5:添加 es5 中支持的尾逗号 all:所有可能的地方添加尾逗号
  endOfLine: "lf", // 换行符
  alwaysParens: "avoid", // 是否为剪头函数参数添加括号 avoid: x => x always: (x) => x
  insertPragma: false, // 是否允许 Prettier 为已经格式化过的文件标记
  jsxBracketSameLine: true, // 是否在 jsx 中将 > 放在最后一行末尾
  vueIndentScriptAndStyle: true, // 是否在 vue 文件中的 script 和 style 标签内缩进代码
};
