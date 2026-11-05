# Security policy

This project treats source repositories, generated diagrams, and Maven artifacts as controlled trust boundaries.

## Supported versions

The project supports the current `main` branch and the latest released version. Security fixes are backported only for maintained releases.

## Reporting a vulnerability

Please report suspected vulnerabilities privately to the repository maintainers through the security contact mechanism in the GitHub repository. Do not disclose details publicly until a fix is confirmed and released.

## Security controls

- Java 17 is required and enforced through the Maven Enforcer plugin.
- Dependency convergence and upper-bound checks are enforced in the parent POM.
- SBOM generation is enabled via the CycloneDX Maven plugin.
- Dependency vulnerability scanning is executed during CI using the OWASP Dependency-Check plugin.
- Release artifacts are signed with GPG when the `release` profile is activated.
- CI gating requires successful tests and security scans before a release is approved.

## Trust boundary requirements

The plugin reads source code from a repository and generates architecture artifacts. The source tree must be treated as trusted input. The project must not be used to analyze untrusted repositories without explicit review and isolation controls.

## Release requirements

Production and release builds must:

1. pass unit tests;
2. generate an SBOM;
3. run dependency vulnerability scans;
4. sign artifacts with a release key; and
5. verify all security checks before publishing.
