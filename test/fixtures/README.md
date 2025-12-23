# Test Fixtures for cljviz

This directory contains minimal test fixtures for testing cljviz functionality.

## Structure

```
test/fixtures/
├── sample.clj              (single file fixture)
└── sample-project/         (multi-file project fixture)
    └── src/
        └── sample/
            ├── core.clj    (main entry point)
            └── util.clj    (utility functions)
```

## 1. Single File Fixture: `sample.clj`

A standalone Clojure file for testing single-file analysis.

**Namespace**: `fixtures.sample`

**Functions**:
- `helper-fn [x]` - Converts input to uppercase using `clojure.string`
- `main-fn [input]` - Calls `helper-fn` to process input

**Purpose**: Test cljviz with a simple single-file scenario.

## 2. Multi-File Project: `sample-project/`

A proper Clojure project structure demonstrating inter-namespace dependencies.

### `sample.core` (src/sample/core.clj)

Main entry point namespace.

**Dependencies**: 
- `sample.util` (internal)
- `clojure.string` (external)

**Functions**:
- `process-data [data]` - Pipeline that uses `util/clean-data` and `str/trim`
- `-main [& args]` - Entry point that prints and processes first argument

**Shows**: Function calls across namespaces, threading macro usage.

### `sample.util` (src/sample/util.clj)

Utility namespace.

**Dependencies**: `clojure.string`

**Functions**:
- `clean-data [data]` - Converts data to lowercase string
- `validate-data [data]` - Validates string is non-blank

**Shows**: Helper functions used by other namespaces.

## Design Rationale

This fixture project provides:

- **Minimal complexity**: Only 2 functions per file, easy to verify
- **Real relationships**: `sample.core` → `sample.util` dependency
- **Predictable output**: Simple string operations that are easy to test
- **Standard structure**: Follows typical `src/namespace/` layout
- **Multiple scenarios**: Both single-file and multi-file testing

## Expected Analysis Results

When cljviz analyzes this fixture:

- **Function definitions**: 5 functions across 3 files
- **Dependency arrows**: `process-data` → `util/clean-data`
- **Namespace dependencies**: `sample.core` → `sample.util`
- **External dependencies**: Both namespaces use `clojure.string`

## Usage in Tests

These fixtures are used in:
- `test/cljviz/core_test.clj` - Testing main entry point with different output formats
- `test/cljviz/lint_test.clj` - Testing clj-kondo analysis functions
- `test/cljviz/dotwriter_test.clj` - Testing graphviz output generation
- `test/cljviz/plwriter_test.clj` - Testing PlantUML output generation

The predictable structure and simple logic allow comprehensive testing of cljviz's analysis, parsing, and visualization generation without the complexity of a real project.
