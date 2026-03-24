Overview
--------
This is the main contributor guide for this repository.
Use it to get oriented, choose the right build/test workflow, and find the deeper subsystem docs that already exist elsewhere in the tree.

If you are looking for project policy, review expectations, or contribution etiquette, start with [CONTRIBUTING.md](CONTRIBUTING.md).
If you are looking for a short project overview, start with [README.md](README.md).

Start Here
----------
The repository has two main development surfaces:

* `tlatools/org.lamport.tlatools`: the command-line tools (`tla2tools.jar`), built and tested primarily with Ant.
* `toolbox/`: the Eclipse-based Toolbox IDE, built primarily with Maven/Tycho from the repository root `pom.xml`.

Local toolchain baseline:

* Java Development Kit (JDK) 11
* Apache Ant 1.9.8+ for `tlatools`
* Apache Maven 3.9.7+ for Toolbox and top-level builds
* Git 2.0+

CI also validates this repository on JDK 17.
If you develop locally on JDK 11 and see a version-specific issue, check the GitHub Actions workflow in `.github/workflows/pr.yml` before assuming your setup is wrong.

### Fastest Successful Local Paths

#### TLA+ Tools (`tlatools`)

```bash
cd tlatools/org.lamport.tlatools
ant -f customBuild.xml info compile compile-test dist
java -cp dist/tla2tools.jar tlc2.TLC test-model/pcal/Bakery.tla
ant -f customBuild.xml info test
```

What this gives you:

* `dist/tla2tools.jar`
* a known-good TLC smoke run against `test-model/pcal/Bakery.tla`
* the main Ant-driven unit test suite

#### Toolbox (`toolbox/`)

From the repository root:

```bash
mvn install -Dmaven.test.skip=true
mvn verify
```

The Toolbox distributables are written under `toolbox/org.lamport.tla.toolbox.product.product/target/products`.

If you are running the Toolbox build on Linux without a desktop session, note that CI uses `xvfb-run` for the Linux Maven/Tycho job.

#### VS Code Dev Container

This repository also ships an optional dev container in `.devcontainer/`.
It installs Java 11, Maven, Git, Ant, and Xvfb, then runs `ant -version && mvn -version` after container creation.

Use it if you want an isolated toolchain without configuring your host machine directly.

Repository Mental Model
-----------------------
At a high level:

* Ant is the source-of-truth build/test path for `tlatools/org.lamport.tlatools`.
* Maven/Tycho drives the Toolbox and the top-level multi-module build.
* The top-level `pom.xml` pulls together `tlatools`, Toolbox bundles, features, and packaging modules.
* Some parser sources are generated from JavaCC grammar and are checked into the repository.

Important top-level paths:

```text
/
├── README.md
├── CONTRIBUTING.md
├── DEVELOPING.md
├── pom.xml
├── .devcontainer/
├── .github/workflows/
├── tlatools/
│   ├── org.lamport.tlatools/
│   ├── org.lamport.tlatools.api/
│   ├── org.lamport.tlatools.consumer.distributed/
│   └── org.lamport.tlatools.impl.distributed/
└── toolbox/
```

Subsystem Map
-------------

### `tla2sany`: parser and semantic front end

Purpose:
parse TLA+ modules, build syntax/semantic structures, and surface parser/front-end behavior used by the rest of the tools.

Main locations:

* `tlatools/org.lamport.tlatools/src/tla2sany/`
* `tlatools/org.lamport.tlatools/javacc/tla+.jj`
* `tlatools/org.lamport.tlatools/src/tla2sany/parser/` generated parser output

Typical entrypoints:

* `tla2sany.SANY`
* `tla2sany.drivers.SANY`

Tests and fixtures:

* `tlatools/org.lamport.tlatools/test/tla2sany/`
* `tlatools/org.lamport.tlatools/test/tla2sany/corpus/`
* `tlatools/org.lamport.tlatools/test-model/sany/`

What usually lands here:

* grammar changes
* parse tree / semantic analysis changes
* front-end error reporting changes
* XML/export/front-end utility changes

### `tlc2`: model checker, REPL, and runtime support

Purpose:
the executable model checker, REPL, debugging support, built-in operators, state handling, liveness checking, and runtime value machinery.

Main locations:

* `tlatools/org.lamport.tlatools/src/tlc2/`
* `tlatools/org.lamport.tlatools/src/tlc2/tool/`
* `tlatools/org.lamport.tlatools/src/tlc2/module/`
* `tlatools/org.lamport.tlatools/src/tlc2/value/`

Typical entrypoints:

* `tlc2.TLC`
* `tlc2.REPL`

Tests and fixtures:

* `tlatools/org.lamport.tlatools/test/tlc2/`
* `tlatools/org.lamport.tlatools/test-model/tlc2/`
* `tlatools/org.lamport.tlatools/test-model/`

What usually lands here:

* state exploration and fingerprinting changes
* error trace behavior
* TLC modules and overrides
* simulator / liveness / debugger behavior

### `pcal`: PlusCal translator

Purpose:
the PlusCal-to-TLA+ translator and its parser/translation pipeline.

Main locations:

* `tlatools/org.lamport.tlatools/src/pcal/`

Typical entrypoints:

* `pcal.trans`

Tests and fixtures:

* `tlatools/org.lamport.tlatools/test/pcal/`
* `tlatools/org.lamport.tlatools/test-model/pcal/`

What usually lands here:

* translator changes
* parser/tokenizer changes specific to PlusCal
* translation output and mapping changes

### `tla2tex`: pretty-printing and LaTeX output

Purpose:
formatting and typesetting support for TLA+ specifications.

Main locations:

* `tlatools/org.lamport.tlatools/src/tla2tex/`

Typical entrypoints:

* `tla2tex.TLA`
* `tla2tex.TeX`

Tests and fixtures:

* tests live under `tlatools/org.lamport.tlatools/test/` alongside the rest of the tools test tree

What usually lands here:

* formatting and pretty-print changes
* LaTeX generation changes

### Toolbox modules

Purpose:
the Eclipse-based IDE, editor integrations, TLC UI integration, product packaging, and plugin-based test bundles.

Common modules:

* `toolbox/org.lamport.tla.toolbox/`: core Toolbox plugin
* `toolbox/org.lamport.tla.toolbox.editor.basic/`: editor integration
* `toolbox/org.lamport.tla.toolbox.tool.tlc/`: TLC Toolbox-side integration
* `toolbox/org.lamport.tla.toolbox.tool.tlc.ui/`: TLC UI
* `toolbox/org.lamport.tla.toolbox.product.standalone/`: standalone application wiring
* `toolbox/org.lamport.tla.toolbox.product.product/`: product packaging
* `toolbox/org.lamport.tla.toolbox.test/`, `toolbox/org.lamport.tla.toolbox.tool.tlc.test/`, `toolbox/org.lamport.tla.toolbox.tool.tlc.ui.test/`: plugin test bundles

What usually lands here:

* editor behavior
* model editor / run UI behavior
* Toolbox launch, packaging, and product integration

Generated Sources and Build Boundaries
--------------------------------------

### JavaCC-generated parser sources

The JavaCC grammar lives at:

* `tlatools/org.lamport.tlatools/javacc/tla+.jj`

Running:

```bash
cd tlatools/org.lamport.tlatools
ant -f customBuild.xml generate
```

regenerates parser output in:

* `tlatools/org.lamport.tlatools/src/tla2sany/parser/`

CI explicitly checks that grammar changes and generated parser output stay in sync.
If you change `javacc/tla+.jj`, regenerate the parser files and commit the generated changes as part of the same diff.

### Ant vs Maven/Tycho

Use Ant for:

* compiling `tlatools`
* building `dist/tla2tools.jar`
* running focused or full `tlatools` unit tests

Use Maven/Tycho for:

* building Toolbox bundles, features, and packaged products
* running the top-level multi-module build from the repository root

Do not assume the top-level Maven build is a drop-in replacement for the Ant workflow when you are iterating on `tlatools`.
For command-line tools work, start with the Ant workflow first.

Change Guide: If You Changed X, Run Y
-------------------------------------

| You changed... | Primary build system | Minimum local validation | Before opening a PR | CI-specific gotcha |
| --- | --- | --- | --- | --- |
| `javacc/tla+.jj` or parser generation code | Ant | `ant -f customBuild.xml generate compile compile-test` then `ant -f customBuild.xml test-set -Dtest.testcases="tla2sany/parser/*"` | Run the broader SANY front-end tests with `ant -f customBuild.xml test-set -Dtest.testcases="tla2sany/**/*"` | CI fails if generated parser files are out of sync with the grammar |
| SANY / parser / semantic front-end code under `src/tla2sany/` | Ant | `ant -f customBuild.xml compile compile-test` then focused tests such as `ant -f customBuild.xml test-set -Dtest.testcases="tla2sany/**/*"` | Run `ant -f customBuild.xml info test` if the change crosses parser, semantic, or output packages | The PR workflow also runs a grammar/code sync check before the main tools test job |
| TLC runtime code under `src/tlc2/` | Ant | `ant -f customBuild.xml compile compile-test dist` then a focused package run such as `ant -f customBuild.xml test-set -Dtest.testcases="tlc2/tool/*"` or another package-level glob closer to your change | Run `ant -f customBuild.xml info test`; for packaging-sensitive changes also run `ant -f customBuild.xml test-dist` | CI also builds CommunityModules against the produced `tla2tools.jar` and runs examples integration tests in a separate workflow job |
| PlusCal translator code under `src/pcal/` | Ant | `ant -f customBuild.xml compile compile-test dist`; smoke-test the translator with `java -cp dist/tla2tools.jar pcal.trans -help`; run `ant -f customBuild.xml test-set -Dtest.testcases="pcal/*"` | Re-run a representative PlusCal-backed spec such as `java -cp dist/tla2tools.jar tlc2.TLC test-model/pcal/Bakery.tla` | PlusCal behavior is exercised indirectly by the tools test suite and examples jobs |
| Toolbox code under `toolbox/` | Maven/Tycho | From the repo root run `mvn install -Dmaven.test.skip=true` | Run `mvn verify` from the repo root; on headless Linux machines use `xvfb-run` as CI does | The Linux Toolbox CI job uses `xvfb-run`; macOS signs packaged artifacts only in non-fork contexts |
| Documentation only | none required | Manually verify commands, paths, and links against the repo | Re-read affected docs together to remove contradictions | There is no separate docs CI gate, so stale commands and broken links must be caught in review |

Focused `test-set` runs are relative to `tlatools/org.lamport.tlatools/test`.
For example:

```bash
cd tlatools/org.lamport.tlatools
ant -f customBuild.xml test-set -Dtest.testcases="tla2sany/parser/*"
ant -f customBuild.xml test-set -Dtest.testcases="tlc2/util/*Vec*"
```

The `test-set` target runs tests in forked JVMs and exposes debug port `1044`, which is useful for debugging but means focused runs are best done one at a time.

Common Local Workflows
----------------------

### Build only the tools jar

```bash
cd tlatools/org.lamport.tlatools
ant -f customBuild.xml info compile compile-test dist
```

### Run all `tlatools` unit tests

```bash
cd tlatools/org.lamport.tlatools
ant -f customBuild.xml info test
```

### Run tests against the packaged jar

```bash
cd tlatools/org.lamport.tlatools
ant -f customBuild.xml compile compile-test dist test-dist
```

### Generate the parser after grammar changes

```bash
cd tlatools/org.lamport.tlatools
ant -f customBuild.xml generate
```

### Build and test the Toolbox

```bash
cd /path/to/repo
mvn install -Dmaven.test.skip=true
mvn verify
```

### What CI runs that you may not run locally every time

The PR workflow does more than the common local fast path:

* checks JavaCC grammar/generated-source sync
* runs the `tlatools` build and unit tests on Ubuntu and macOS
* builds CommunityModules against the produced `tla2tools.jar`
* builds and tests the Toolbox on Ubuntu and macOS
* clones `tlaplus/examples` and runs parser/model-check integration tests against it

For small changes you do not need to reproduce every CI job locally, but you should know those gates exist.

IDE and Container Options
-------------------------

### Eclipse for `tlatools`

If you prefer Eclipse, import `tlatools/org.lamport.tlatools` as a project and use the Ant view to run `customBuild.xml` targets from inside Eclipse.

This matters because running Ant externally can occasionally leave Eclipse showing stale compile errors until the project is refreshed.
If that happens, refresh the project manually.

### Eclipse for the Toolbox

For Eclipse-specific Toolbox setup, including the optional Oomph-based workspace setup, see [general/ide/README.md](general/ide/README.md).

Treat that document as Eclipse-specific setup guidance, not as the main contributor entrypoint.
This file (`DEVELOPING.md`) remains the primary hub.

### VS Code Dev Container

The dev container in `.devcontainer/` is the best option if you want:

* Java 11, Maven, Git, Ant, and Xvfb preinstalled
* a disposable local environment
* a setup path that does not depend on Eclipse

Contribution Reminders
----------------------

Keep these rules in mind while iterating:

* Read [CONTRIBUTING.md](CONTRIBUTING.md) before starting substantial work.
* Keep changes surgical and avoid unrelated cleanup in touched files.
* Prefer adding or improving tests before changing tricky behavior.
* Sign commits with DCO using `git commit -s`.

Further Reading by Subsystem
----------------------------

Start with the hub above, then use these narrower documents when you need more depth:

* Toolbox Eclipse setup: [general/ide/README.md](general/ide/README.md)
* `tlatools` test commands and notes: [tlatools/org.lamport.tlatools/README.md](tlatools/org.lamport.tlatools/README.md)
* SANY command-line notes and explorer mode: [tlatools/org.lamport.tlatools/src/tla2sany/README](tlatools/org.lamport.tlatools/src/tla2sany/README)
* Unified syntax corpus notes: [tlatools/org.lamport.tlatools/test/tla2sany/corpus/README.md](tlatools/org.lamport.tlatools/test/tla2sany/corpus/README.md)
* TLC trace-expression design doc: [tlatools/org.lamport.tlatools/spec/generate-te-spec-by-default.md](tlatools/org.lamport.tlatools/spec/generate-te-spec-by-default.md)
* TLC profiling with Java Flight Recorder: [tlatools/org.lamport.tlatools/jfr/README.md](tlatools/org.lamport.tlatools/jfr/README.md)

Code Style
----------

There is no single enforced formatting style across the entire codebase.
Follow the style of the surrounding code rather than mixing refactors with behavioral changes.

For new Java code we recommend:

* add Javadoc to public classes and methods when the behavior is not obvious
* do not mix tabs and spaces
* keep opening braces on the same line as the declaration or statement
* use braces for single-statement `if` and `while` bodies
* prefer lines under 120 characters

The project's Java source files are UTF-8 encoded and may contain mathematical characters.
