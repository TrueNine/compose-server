# QA Knowledge Base

## Design Principles

This QA system follows these principles:
- **AI-Friendly**: Structured tags, clear context, reason explanations
- **Human-Friendly**: Clear categorization, easy search, practical examples

## Format Specification

```markdown
## [Category] Question Title

**Tags**: `tag1` `tag2` `tag3`
**File Association**: path/to/file.ext:line

### Question
Specific problem description and scenario

### Solution
Step-by-step solution

### Reason Explanation
Why do this, underlying logic and considerations

### Related References
- Related file links
- External documentation
```

---

## [Development Environment] How to Configure Project Code Formatting

**Tags**: `formatting` `eslint` `prettier` `development-setup`
**File Association**: .eslintrc.js, .prettierrc, package.json

### Question
New projects need unified code formatting standards to ensure consistent code style among team members, while allowing AI assistants to understand project code style preferences.

### Solution
1. Install and configure ESLint and Prettier
2. Add formatting scripts to package.json
3. Configure editor auto-formatting
4. Record formatting preferences in CLAUDE.md

### Reason Explanation
- **Consistency**: Unified formatting avoids format conflicts in PRs
- **AI Learning**: Structured config files help AI understand project preferences
- **Efficiency**: Auto-formatting reduces manual adjustment time
- **Quality**: ESLint rules catch potential issues

### Related References
- [ESLint Configuration Guide](https://eslint.org/docs/user-guide/configuring)
- docs/references/code-style.md

---

## [Git Workflow] When to Use Feature Branches vs Direct Main Branch Development

**Tags**: `git` `branching` `workflow` `collaboration`
**File Association**: .github/workflows/, docs/other/git-workflow.md

### Question
In personal and team projects, when should you create feature branches, and when can you develop directly on the main branch?

### Solution
**Personal Project (Small Changes)**:
- Develop directly on main branch
- Frequent commits to keep history clear

**Personal Project (Large Features)**:
- Create feature branch
- Merge to main when complete

**Team Projects**:
- Always use feature branches
- Code review through PRs

### Reason Explanation
- **Risk Control**: Feature branches isolate incomplete features, avoiding main line disruption
- **Collaboration Efficiency**: Branches allow parallel development of different features
- **Clear History**: Proper branching strategy makes git history more understandable
- **AI Understanding**: Clear branch patterns help AI assistants better understand project status

### Related References
- [Git Flow Workflow](https://nvie.com/posts/a-successful-git-branching-model/)
- docs/other/git-workflow.md

---

## [Dependency Management] How to Choose and Manage Third-Party Libraries

**Tags**: `dependencies` `npm` `security` `maintenance`
**File Association**: package.json, package-lock.json

### Question
When adding new functionality to a project, how to evaluate and choose third-party libraries? How to maintain dependency security and freshness?

### Solution
**Selection Criteria**:
1. Check library activity and maintenance status
2. Evaluate security and community trust
3. Consider package size impact on project
4. Review if API design fits project style

**Management Practices**:
1. Regularly run `npm audit` to check security vulnerabilities
2. Use Dependabot for automatic dependency updates
3. Record dependency selection reasons in CLAUDE.md

### Reason Explanation
- **Security**: Timely updates avoid known security vulnerabilities
- **Stability**: Choosing well-maintained libraries reduces future issues
- **Performance**: Package size control affects application load speed
- **AI Assistance**: Recording selection reasons helps AI understand project's technical decision preferences

### Related References
- [npm Security Best Practices](https://docs.npmjs.com/auditing-package-dependencies-for-security-vulnerabilities)
- docs/references/dependency-guidelines.md

---

## Search and Index

### Find by Tags
- `development-setup` - Development environment configuration
- `git` - Git workflow and version control
- `dependencies` - Dependency management
- `formatting` - Code formatting
- `security` - Security related
- `performance` - Performance optimization

### Find by Category
- **Development Environment** - Environment setup, tool configuration
- **Git Workflow** - Version control, branch management
- **Dependency Management** - Third-party library selection and maintenance

### Quick Search
Use Ctrl+F to search keywords like: `eslint`, `branch`, `security`, etc.
