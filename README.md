# Vaadin Routes Export Maven Plugin

A Maven plugin that **statically analyses** compiled [Vaadin](https://vaadin.com/) projects and exports a complete inventory of all `@Route`-annotated views, including:

* Resolved route paths
* Layout hierarchy
* Security annotations (`@RolesAllowed`, `@PermitAll`, `@DenyAll`, `@AnonymousAllowed`)

The output is a structured artifact (JSON, CSV, or YAML) suitable for documentation, security audits, and CI validation.

---

## Table of Contents

- [Quick Start](#quick-start)
- [Plugin Configuration](#plugin-configuration)
- [Demo Application](#demo-application)
- [Example Output](#example-output)
- [How It Works](#how-it-works)
- [Building from Source](#building-from-source)

---

## Quick Start

Add the plugin to your Vaadin project's `pom.xml`:

```xml
<plugin>
    <groupId>io.github.johannesrabauer</groupId>
    <artifactId>vaadin-routes-export-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <goals>
                <goal>export-routes</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <basePackage>com.yourcompany.app</basePackage>
        <outputFile>${project.build.directory}/vaadin-routes.json</outputFile>
    </configuration>
</plugin>
```

Then build your project:

```bash
mvn compile
```

The route export is generated automatically during the `process-classes` phase.
The output file will be at `target/vaadin-routes.json`.

---

## Plugin Configuration

| Parameter              | Type    | Required | Default                                    | Description                                                      |
|------------------------|---------|----------|--------------------------------------------|------------------------------------------------------------------|
| `basePackage`          | String  | **yes**  | —                                          | Root Java package to scan for `@Route` classes                   |
| `outputFile`           | String  | **yes**  | `${project.build.directory}/vaadin-routes.json` | Path of the generated export file                            |
| `outputFormat`         | Enum    | no       | `JSON`                                     | Output format: `JSON`, `CSV`, or `YAML`                          |
| `includeLayouts`       | boolean | no       | `true`                                     | Include the `RouterLayout` chain for each route                  |
| `includeAnonymousAccess` | boolean | no     | `true`                                     | Include publicly accessible routes in the output                 |
| `failOnMissingRoles`   | boolean | no       | `false`                                    | Fail the build if any route has no security annotation           |
| `scanDependencies`     | boolean | no       | `false`                                    | Also scan external JARs on the compile classpath                 |

### Full configuration example

```xml
<configuration>
    <basePackage>com.yourcompany.app</basePackage>
    <outputFile>${project.build.directory}/vaadin-routes.json</outputFile>
    <outputFormat>JSON</outputFormat>
    <includeLayouts>true</includeLayouts>
    <includeAnonymousAccess>true</includeAnonymousAccess>
    <failOnMissingRoles>true</failOnMissingRoles>
    <scanDependencies>false</scanDependencies>
</configuration>
```

---

## Demo Application

The [`demo-app`](demo-app/) module is a minimal **Vaadin 24.7.4** application that shows the plugin in action. It contains six views, each demonstrating a different security annotation:

| View            | Route path   | Security              | Layout        |
|-----------------|--------------|-----------------------|---------------|
| `HomeView`      | `/` (root)   | `@AnonymousAllowed`   | `MainLayout`  |
| `DashboardView` | `/dashboard` | `@PermitAll`          | `MainLayout`  |
| `AdminView`     | `/admin`     | `@RolesAllowed("ADMIN")` | `MainLayout` |
| `ProfileView`   | `/profile`   | `@RolesAllowed({"USER","ADMIN"})` | `MainLayout` |
| `InternalView`  | `/internal`  | `@DenyAll`            | `MainLayout`  |
| `LoginView`     | `/login`     | `@AnonymousAllowed`   | *(none)*      |

`AdminView` also declares a `@RouteAlias("admin-panel")`.

### Running the demo

```bash
# Clone the repository
git clone https://github.com/JohannesRabauer/vaadin-routes-export-maven-plugin.git
cd vaadin-routes-export-maven-plugin

# Build everything (plugin + demo)
mvn clean verify

# Inspect the generated route export
cat demo-app/target/vaadin-routes.json
```

---

## Example Output

After building the demo application, the plugin produces the following `vaadin-routes.json`:

```json
[
  {
    "path": "admin",
    "className": "io.github.johannesrabauer.demo.views.AdminView",
    "layouts": ["io.github.johannesrabauer.demo.layouts.MainLayout"],
    "roles": ["ADMIN"],
    "access": "RESTRICTED",
    "aliases": ["admin-panel"],
    "securitySource": "ANNOTATION"
  },
  {
    "path": "dashboard",
    "className": "io.github.johannesrabauer.demo.views.DashboardView",
    "layouts": ["io.github.johannesrabauer.demo.layouts.MainLayout"],
    "roles": ["*"],
    "access": "PUBLIC",
    "securitySource": "ANNOTATION"
  },
  {
    "path": "",
    "className": "io.github.johannesrabauer.demo.views.HomeView",
    "layouts": ["io.github.johannesrabauer.demo.layouts.MainLayout"],
    "roles": ["*"],
    "access": "PUBLIC",
    "securitySource": "ANNOTATION"
  },
  {
    "path": "internal",
    "className": "io.github.johannesrabauer.demo.views.InternalView",
    "layouts": ["io.github.johannesrabauer.demo.layouts.MainLayout"],
    "roles": [],
    "access": "DENIED",
    "securitySource": "ANNOTATION"
  },
  {
    "path": "login",
    "className": "io.github.johannesrabauer.demo.views.LoginView",
    "layouts": [],
    "roles": ["*"],
    "access": "PUBLIC",
    "securitySource": "ANNOTATION"
  },
  {
    "path": "profile",
    "className": "io.github.johannesrabauer.demo.views.ProfileView",
    "layouts": ["io.github.johannesrabauer.demo.layouts.MainLayout"],
    "roles": ["USER", "ADMIN"],
    "access": "RESTRICTED",
    "securitySource": "ANNOTATION"
  }
]
```

### Reading the output

| Field            | Meaning |
|------------------|---------|
| `path`           | The URL path of the route (empty string = root `/`) |
| `className`      | Fully qualified Java class name |
| `layouts`        | Ordered list of parent `RouterLayout` classes (outermost first) |
| `roles`          | `["ADMIN"]` = restricted; `["*"]` = public/permit-all; `[]` = deny-all; `null` = unknown |
| `access`         | `RESTRICTED`, `PUBLIC`, `DENIED`, or `UNKNOWN` |
| `aliases`        | Additional route paths (from `@RouteAlias`) |
| `securitySource` | `ANNOTATION` (direct) or `INFERRED` (heuristic) |

---

## How It Works

1. **Classpath resolution** – uses `project.getCompileClasspathElements()` to locate compiled `.class` files.
2. **Bytecode scanning** – [ClassGraph](https://github.com/classgraph/classgraph) scans for `@Route`, `@RouteAlias`, and security annotations *without* loading classes into the JVM.
3. **Route extraction** – extracts the route value, layout class, and any aliases.
4. **Layout hierarchy resolution** – recursively resolves the `RouterLayout` parent chain.
5. **Security annotation extraction** – maps `@RolesAllowed`, `@PermitAll`, `@DenyAll`, and `@AnonymousAllowed` to roles and access levels.
6. **Output generation** – writes JSON (default), CSV, or YAML.

> **Important:** The plugin does *not* start the Vaadin runtime. It operates entirely on compiled bytecode, which means programmatic or dynamic route registrations cannot be captured.

---

## Building from Source

**Prerequisites:** Java 17+, Maven 3.9+

```bash
git clone https://github.com/JohannesRabauer/vaadin-routes-export-maven-plugin.git
cd vaadin-routes-export-maven-plugin
mvn clean verify
```

This builds the plugin, runs all 15 unit tests, and generates the demo route export.

---

## License

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)