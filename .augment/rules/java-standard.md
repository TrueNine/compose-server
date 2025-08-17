---
type: "always_apply"
description: "**/*.java"
---
**Java Rules**

All JVM platform programming languages: java, kotlin, groovy, scala must follow these principles

1. Can use `import xxx.*` imports to reduce code volume, leave subsequent processing to IDE
2. Use JDK new features as much as possible
3. Variable declarations should use `final var` as much as possible
4. Actively use lambda expressions
5. Strictly prohibit using `System.out.println` for output logging
