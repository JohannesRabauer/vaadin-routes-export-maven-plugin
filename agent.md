# Agent Instructions

## README maintenance

The `README.md` at the repository root is the primary user-facing documentation.
**Always update it when making code changes** that affect any of the following areas:

### Output formats

The README lists all supported output formats in three places:

1. **Introduction** (paragraph after the bullet list near the top) — the sentence
   "The output is a structured artifact (…)" must enumerate every value of
   `OutputFormat` enum
   (`vaadin-routes-export-maven-plugin/src/main/java/dev/rabauer/vaadin/routes/export/model/OutputFormat.java`).

2. **Plugin Configuration table** — the `outputFormat` row's *Description* column
   must list every valid enum value.

3. **How It Works → step 6** — the sentence starting "writes JSON (default)…"
   must include every format.

Whenever a value is added to or removed from `OutputFormat`, update all three
locations in `README.md`.

### Plugin configuration parameters

The **Plugin Configuration** table in `README.md` must stay in sync with the
`@Parameter`-annotated fields in `ExportRoutesMojo.java`
(`vaadin-routes-export-maven-plugin/src/main/java/dev/rabauer/vaadin/routes/export/ExportRoutesMojo.java`).

For every `@Parameter` field, the table must have a matching row with the correct:
- parameter name
- type
- required flag
- default value
- description

When a parameter is added, removed, or has its default/description changed,
update the corresponding row (or add/remove the row).

### Unit-test count

The **Building from Source** section contains a sentence of the form
"runs all N unit tests". Update `N` whenever tests are added or removed.
The current count can be verified by running:

```bash
JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64 mvn test -pl vaadin-routes-export-maven-plugin 2>&1 | grep "Tests run:"
```

### Model / data classes

If fields are added to or removed from `RouteDescriptor`, update the
**Reading the output** table in the **Example Output** section accordingly.
