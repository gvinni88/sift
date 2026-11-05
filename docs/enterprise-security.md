# Enterprise security controls for SpringPoet

This document captures the enterprise-grade controls added for release readiness.

## 1. Dependency and supply-chain governance

- Maven Enforcer rules require Java 17, Maven 3.9+, and dependency convergence.
- SBOM generation is enabled with CycloneDX.
- OWASP Dependency-Check is run as part of CI to flag high-risk vulnerabilities before release.
- Dependabot monitors Maven and GitHub Actions dependency updates weekly.

## 2. Release signing and artifact integrity

The project includes a `release` Maven profile that attaches sources, Javadocs, and signs the generated artifacts with GPG. This protects the integrity of published artifacts and prevents unsigned or tampered release artifacts from reaching consumers.

## 3. CI security gates

The GitHub Actions workflow runs tests and security checks on every push and pull request. Release-only jobs run additional signing and deployment steps using repository secrets.

## 4. Trust boundaries and safe usage

The plugin generates UML and ADR artifacts from source directories. This is a controlled automation feature, not a general-purpose repository scanner for untrusted code. It must be run only against trusted repositories or inside explicitly isolated CI jobs.

## 5. Operational recommendations

For full enterprise approval, continue to track:

- vulnerability exception process;
- artifact attestation and provenance;
- periodic review of suppression records; and
- signed commit and release verification in CI.
