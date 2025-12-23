# AI Assistant Instructions for cljviz Project

This document contains guidelines and best practices for AI assistants working on the cljviz project.

## Git Workflow

### ⚠️ IMPORTANT: Git Operations Policy
**NEVER commit, merge, or push as part of a longer task - ONLY when explicitly asked by the user.**

This includes:
- `git commit` - only run when user explicitly requests "commit" or "commit changes"
- `git push` - only run when user explicitly requests "push" or "push to remote"
- `git merge` - only run when user explicitly requests merge operations
- `git rebase` - only run when user explicitly requests rebase operations

**Rationale**: Users need control over when changes are committed and pushed to maintain their workflow, review changes, and manage git history.

### Commit Staging
**Use `git add .` instead of `git add -A`**

- `git add .` - stages all changes in current directory and subdirectories
- `git add -A` - stages all changes in entire repository from any location

When working from repository root, prefer `git add .` as it's more intentional and explicit about what's being staged.

### Branch Strategy
- Create feature branches for new work
- Use descriptive branch names (e.g., `improve-test-coverage`)
- Push branches to remote for backup and collaboration

### Commit Messages
- Use descriptive, multi-line commit messages
- Start with a summary line
- Add bullet points for detailed changes
- Include test results when relevant

Example:
```
Complete DTR-1: Fix test syntax, add core tests, create fixtures

- Fixed syntax errors in existing tests
- Added 4 new tests for -main function
- Created test fixtures
- Updated documentation

All tests pass: 4 core tests (10 assertions), 3 utils tests (4 assertions)
```

## Testing Guidelines

### Test Organization
- Use the testing improvement plan (`agent-docs/testing-improvement-plan.md`) as reference
- Each major section (DTR-1, DTR-2, etc.) represents a testable component
- Mark completed sections with ✅ checkmark

### Test Fixtures
- Location: `test/fixtures/`
- Keep fixtures minimal and focused
- Document fixture structure in `test/fixtures/README.md`
- Use fixtures consistently across tests

### Running Tests
```bash
# Run specific test namespace
clojure -M:test -n cljviz.core-test

# Run all tests
clojure -M:test

# Run from Calva (VS Code)
# Load the file first, then run tests
```

### Test File Conventions
- Test files use underscores: `core_test.clj`
- Namespaces use dashes: `cljviz.core-test`
- One `deftest` per logical test case
- Use descriptive test names: `main-with-no-args-test`

## Clojure Code Style

### Namespace Declarations
```clojure
;; Correct: ns form includes requires
(ns sample.core
  (:require [clojure.string :as str]))

;; Wrong: closing paren too early
(ns sample.core)
  (:require [clojure.string :as str]))
```

### Test Structure
```clojure
;; Correct: deftest without extra parens
(deftest my-test
  (testing "description"
    (is (= expected actual))))

;; Wrong: extra parens around deftest
((deftest my-test
   (testing "description"
     (is (= expected actual)))))
```

## Documentation

### Markdown Files
- Use clear hierarchical structure with `#`, `##`, `###`
- Include code examples with proper syntax highlighting
- Use checkboxes `- [ ]` and `✅` for tracking progress
- Add unique identifiers for sections that need referencing (e.g., DTR-1, DTR-2)

### Code Comments
- Add explanatory comments for complex logic
- Use `;; Note:` for important implementation notes
- Document why tests are skipped or deferred
- Reference relevant documentation sections

## File Management

### Creating Files
- Always use complete, valid Clojure syntax
- Include proper namespace declarations
- Add docstrings for public functions
- Create supporting documentation (README.md) for new directories

### Editing Files
- Read file first to understand current state
- Check for errors after edits using `get_errors` tool
- Run tests after significant changes
- Use `multi_replace_string_in_file` for multiple independent edits

## Error Handling

### Common Issues
1. **Unmatched brackets**: Check namespace declarations
2. **Unresolved symbols**: Ensure proper `:require` in namespace
3. **Test not running**: Check for extra parentheses around `deftest`

### Debugging Process
1. Check file for syntax errors: `get_errors` tool
2. Run tests from command line: `clojure -M:test -n namespace`
3. Verify fixture files are valid Clojure code
4. Check namespace/filename correspondence

## Project-Specific Guidelines

### Test Coverage Strategy
Follow the phased approach in `testing-improvement-plan.md`:
- **Phase 1**: Critical paths (P0) - entry points and core functionality
- **Phase 2**: High priority (P1) - supporting functions
- **Phase 3**: Medium priority (P2) - helpers and utilities
- **Phase 4**: Integration tests and WebSocket testing

### Priority Levels
- **P0 (Critical)**: Entry points, core analysis, security-critical functions
- **P1 (High)**: Important supporting functions
- **P2 (Medium)**: Helper functions with clear responsibilities
- **P3 (Low)**: Simple functions, already tested via memoization

### Integration Tests
- Server lifecycle tests (HTTP, WebSocket) are deferred to Phase 4
- Document why tests are not included inline
- Reference the appropriate DTR section for future implementation

## Working with Test Fixtures

### Structure
```
test/fixtures/
├── README.md              (always document structure)
├── sample.clj             (single file scenarios)
└── sample-project/        (multi-file scenarios)
    └── src/
        └── namespace/
            ├── core.clj
            └── util.clj
```

### Design Principles
- Minimal complexity (2-3 functions per file)
- Real inter-namespace dependencies
- Predictable, testable output
- Standard Clojure project structure

## Communication with Users

### When to Ask vs Act
- **Act**: Fixing obvious syntax errors, implementing defined tests
- **Ask**: Unclear requirements, architectural decisions, integration test approaches
- **Confirm**: After completing major sections, before moving to next phase

### Progress Reporting
- State what was completed
- Include test results (number of tests, assertions)
- Mention any issues or limitations
- Suggest next steps

## Tools and Commands

### Essential Git Commands
```bash
git status                    # Check current state
git checkout -b branch-name   # Create and switch to new branch
git add .                     # Stage changes in current directory
git commit -m "message"       # Commit with message
git push -u origin branch     # Push and track remote branch
```

### Testing Commands
```bash
clojure -M:test                      # Run all tests
clojure -M:test -n namespace         # Run specific namespace
clojure -M -e "(require 'ns) ..."    # REPL evaluation
```

### File Inspection
```bash
tree directory/              # Show directory structure
find path -type f           # List all files
cat file.clj                # View file contents
```

## References

- **Testing Plan**: `agent-docs/testing-improvement-plan.md`
- **Project Updates**: `agent-docs/update-plan.md`
- **Backlog**: `agent-docs/backlog.md`
- **Main README**: `README.md`

## Maintenance

This instruction file should be updated when:
- New conventions are established
- Common issues are discovered and resolved
- Tools or workflows change
- New phases of development begin

Last updated: 23 December 2025
