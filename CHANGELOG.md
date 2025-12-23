# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]

## 2025-12-23 - Dependency Modernization Update

### Changed
- **Clojure**: 1.11.1 → 1.12.4 (latest stable)
- **clj-kondo**: 2022.12.11-SNAPSHOT → 2025.10.23 (latest stable)
- **ring**: 1.9.6 → 1.15.3 (requires Java 17+)
- **aleph**: 0.6.1 → 0.9.3
- **manifold**: 0.4.0 → 0.4.4
- **reitit**: 0.6.0 → 0.9.2
- **hiccup**: Explicitly added at 2.0.0
- **clojure-watch**: 0.1.14 → 0.1.15
- **test.check**: 1.1.1 → 1.1.2

### Added
- SLF4J simple binding (2.0.17) to eliminate logging warnings
- Development aliases: `:outdated` (antq), `:build` (tools.build)
- Comprehensive Requirements section in README
- Java 17+ requirement documented

### Removed
- project.clj (Leiningen configuration) - project now uses deps.edn exclusively
- build.clj (incomplete build stub)
- All SNAPSHOT dependencies

### Fixed
- Logging warnings from missing SLF4J binding
- Outdated dependency versions
- Documentation inconsistencies

### Technical
- Java 17+ now required (was Java 11)
- All dependency management via Clojure CLI tools (deps.edn)
- Updated clj-kondo usage from Leiningen plugin to CLI invocation
- Verified full compatibility with Clojure 1.12.4

**Breaking Changes**:
- Java 17+ now required (previously Java 11)
- project.clj removed (Leiningen users must migrate to deps.edn)

## [0.1.1] - 2023-01-03
### Changed
- Documentation on how to make the widgets.

### Removed
- `make-widget-sync` - we're all async, all the time.

### Fixed
- Fixed widget maker to keep working when daylight savings switches over.

## 0.1.0 - 2023-01-03
### Added
- Files from the new template.
- Widget maker public API - `make-widget-sync`.

[Unreleased]: https://github.com/your-name/clj-viz/compare/0.1.1...HEAD
[0.1.1]: https://github.com/your-name/clj-viz/compare/0.1.0...0.1.1
