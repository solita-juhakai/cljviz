# cljviz

Cljviz turns your clojure project's clj-kondo analysis output to visual diagram.

![generated image](cljviz.svg)

>**NOTE**
>cljviz is **alpha** code and made as a clojure learning project.

## Requirements

- **Java**: 17 or later (tested with OpenJDK 17)
- **Clojure**: 1.12.4 (via Clojure CLI tools)
- **Graphviz**: For rendering diagrams (install `graphviz` package, `dot` command must be in PATH)
- **PlantUML**: Optional, for PlantUML output format
- **Image Viewer**: For viewing generated PNG diagrams

## Installation

Download code and see Usage/Options below.

## Usage

Input options for cljviz are clj-file or source directory and optionally output type (`pl`) | `gv` | `ws`).
Cljviz output (stdout) can be plantuml or graphviz (dot-language) description.
Without options static svg diagram will be created in http://localhost:3000 and with ws option there is automatically updating diagram in http://localhost:3000/ui.

### Browser output (static)

Change to download directory and run

    $ clojure -M -m cljviz.core <clojure project clj-file or src-dir>

Open http://localhost:3000 with browser. The diagram is based on code state at the moment of starting cljviz. 

### Browser output (live)

Change to download directory and run

    $ clojure -M -m cljviz.core  <clojure project clj-file or src-dir> ws

Open http://localhost:3000/ui with browser. The diagram should update when vars and namespaces are updated in code repo cljviz is watching.
Note that namespace view live update is TBA.

### Plantuml output

Change to download directory and run

    $ clojure -M -m cljviz.core  <clojure project clj-file or src-dir> pl > example.plantuml

Then plantuml is needed to turn output into e.g. a png image

    $ plantuml example.plantuml

Open resulting example.png image with image viewer.

### Graphviz output

Change to download directory and run

    $ clojure -M -m cljviz.core  <clojure project clj-file or src-dir> gv > example.gv

Then graphviz dot-command is needed to turn output into e.g. a png image

    $ dot -v -Tpng -oexample.png example.gv

Open resulting example.png image with image viewer.

## Options

Options cannot be combined
- (no options): static diagram for browsing
- pl: plantuml std output, runs and quits
- gv: graphviz std output, runs and quits
- ws: "live" updating diagram for browsing

## Known issues

- Generated diagram resembles an UML class diagram, which is obviously wrong in clojure context.

- If your project is big, plantuml may run out of memory and part of image will not be generated. Try to give more memory to plantuml with e.g.

    $ export PLANTUML_LIMIT_SIZE=12288

- Tested only in linux.

## Future plans

- move from plantuml to plain graphviz (added option)
- generate web output for browser usage, maybe imagemap based (added basic svg and live updates)
- proper cli arg support
- template based output format
- namespace only view

## Development

### About clj-kondo

Cljviz uses [clj-kondo](https://github.com/clj-kondo/clj-kondo) for static analysis. clj-kondo is included as a dependency in `deps.edn`.

How to run clj-kondo analysis from CLI:

    $ clojure -M -m clj-kondo.main --lint ./src --config '{:analysis true :output {:format :edn}}'

Analysis output can be redirected with standard shell output redirection. This analysis data is what cljviz uses to generate the visual diagrams.

### Testing

Tests can be run with:

    $ clojure -M:test

### Checking for Outdated Dependencies

Check for outdated dependencies using the `:outdated` alias:

    $ clojure -M:outdated

### Build Tools

Project uses `deps.edn` for dependency management. For JAR building (if needed), tools.build is configured:

    $ clojure -T:build <task>

## License

MIT License