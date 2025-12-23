# Cljviz Development Backlog

**Date Created**: 23 December 2025  
**Last Updated**: 23 December 2025

---

## Future Enhancements

### djblue/portal Integration

**Priority**: Nice to have  
**Status**: Deferred  
**Category**: Development Tooling

**Description**:
Add djblue/portal as a development dependency for enhanced REPL-driven data inspection and debugging.

**What is Portal**:
Portal is a sophisticated Clojure data inspector and navigation tool that provides a rich visual UI for exploring complex nested data structures at runtime. It integrates with Clojure's `tap>` system to send data to an interactive browser-based or editor-integrated UI.

**Key Features**:
- Visual data inspection with multiple viewers (table, tree, JSON, etc.)
- Navigation through nested data structures
- Search and filter capabilities
- Editor integration (VS Code, IntelliJ)
- Cross-platform support (JVM, ClojureScript, Babashka, CLR)

**Proposed Implementation**:

**File**: `deps.edn`

Add to `:aliases`:
```clojure
:dev {:extra-paths ["dev"]
      :extra-deps {djblue/portal {:mvn/version "0.62.1"}}}
```

**Usage Examples**:
```clojure
;; In REPL
(require '[portal.api :as p])
(def portal (p/open))           ; Opens the UI
(add-tap #'p/submit)            ; Connect tap> to Portal
(tap> {:my "data"})             ; Send data to inspect
(tap> (range 100))              ; Tap more values
(p/clear)                       ; Clear the UI
(p/close)                       ; Close when done
```

**Benefits**:
- Better debugging experience than `println`
- Interactive exploration of clj-kondo analysis results
- Visualization of graph structures before rendering
- Improved REPL-driven development workflow

**Resources**:
- GitHub: https://github.com/djblue/portal
- Documentation: https://cljdoc.org/d/djblue/portal/CURRENT
- Latest Version: 0.62.1 (released Dec 2025)
- Demo Video: https://www.youtube.com/watch?v=Tj-iyDo3bq0

**Related Work**:
- Similar in concept to REBL or Reveal
- Complements existing development workflow
- No conflicts with production dependencies

---

## Other Future Ideas

### tools.cli for Enhanced CLI Parsing

**Priority**: Low  
**Status**: Deferred  
**Category**: CLI Enhancement

**Description**:
Consider adding `org.clojure/tools.cli` if the command-line interface needs to support more complex options in the future.

**Current State**:
The CLI currently uses simple positional arguments:
- Arg 1: File/directory path (required)
- Arg 2: Output mode (optional: "pl", "gv", "ws", or default HTTP)

**When to Revisit**:
Add tools.cli if future requirements include:
- Named options (e.g., `--output svg`, `--port 8080`)
- Boolean flags (e.g., `--verbose`, `--watch`, `--help`)
- Input validation and error messages
- Auto-generated help text
- Multiple simultaneous options
- Configuration file support

**Proposed Implementation**:
```clojure
;; In deps.edn
org.clojure/tools.cli {:mvn/version "1.1.230"}

;; Example enhanced CLI
(def cli-options
  [["-p" "--port PORT" "Port number"
    :default 3000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be 0-65536"]]
   ["-o" "--output FORMAT" "Output format (pl|gv|svg)"
    :default "svg"]
   ["-h" "--help"]])
```

**Benefits**:
- Professional CLI experience with help text
- Input validation
- Better error messages
- Standard Unix-style options

**Current Workaround**:
Simple positional argument parsing with `(first args)` and `(second args)` is sufficient for current needs.

**Resources**:
- GitHub: https://github.com/clojure/tools.cli
- Documentation: https://github.com/clojure/tools.cli/wiki

---

*Add additional backlog items here as they come up*
