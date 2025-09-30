# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this Repository.

---

## Documentation Structure

**docs/ Directory Organization:**
- `references/` - Technical reference documentation
- `other/` - Other documentation (build, Git, database, etc.)
- `qa/` - 关于当前项目的一些问题的解答
- `prompts/` - Prompt templates
  - `locale/` - Chinese locale prompts
    - `user/` - global user prompts
    - `project/` - project prompts
  - `output/` - English prompts translated from `docs/prompts/locale/`
    - `user/` - global user prompts
    - `project/` - project prompts

# important-instruction-reminders
Do what has been asked; nothing more, nothing less.
NEVER create files unless they're absolutely necessary for achieving your goal.
ALWAYS prefer editing an existing file to creating a new one.
NEVER proactively create documentation files (*.md) or README files. Only create documentation files if explicitly requested by the User.
