# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 3.2.0 - 2024-09-23
### Changed
- Changeover to AGPL-3.0-only license

## 3.1.5 - 2023-04-12
### Fixed
- Missing encryption in the audit log

## 3.1.4 - 2022-11-22
### Fixed
- Committer and pushed by labels ([#56](https://github.com/scm-manager/scm-jira-plugin/pull/56))

## 3.1.3 - 2022-06-02
### Fixed
- Replace custom styling with link styled button ([#51](https://github.com/scm-manager/scm-jira-plugin/pull/51))

## 3.1.2 - 2022-05-13
### Fixed
- Replace custom styling with link styled button ([#51](https://github.com/scm-manager/scm-jira-plugin/pull/51))

## 3.1.1 - 2022-04-29
### Fixed
- Fix storing of changed transitions ([#47](https://github.com/scm-manager/scm-jira-plugin/pull/47))

## 3.1.0 - 2022-03-03
### Added
- Add option to disable state change by commit ([#43](https://github.com/scm-manager/scm-jira-plugin/pull/43))

## 3.0.2 - 2022-02-18
### Fixed
- Shrink input fields within table ([#36](https://github.com/scm-manager/scm-jira-plugin/pull/36))
- Floating of 'add mapping'-button ([#36](https://github.com/scm-manager/scm-jira-plugin/pull/36))

## 3.0.1 - 2021-04-26
### Fixed
- Swapping of transition and keywords after save ([#19](https://github.com/scm-manager/scm-jira-plugin/issues/19) and [#20](https://github.com/scm-manager/scm-jira-plugin/pull/20))

## 3.0.0 - 2021-04-22
### Fixed
- Repository specific configuration ([#17](https://github.com/scm-manager/scm-jira-plugin/pull/17))

### Added
- Support pull requests and comments ([#17](https://github.com/scm-manager/scm-jira-plugin/pull/17))

### Removed
- Resubmit functionality, it is now supported by issue tracker plugin ([#17](https://github.com/scm-manager/scm-jira-plugin/pull/17))
- Wrapping, monospace, branches and bookmarks from comment templates ([#17](https://github.com/scm-manager/scm-jira-plugin/pull/17))
- Support for soap api ([#17](https://github.com/scm-manager/scm-jira-plugin/pull/17))

## 2.3.0 - 2020-11-09
### Changed
- Set span kind for http requests (for Trace Monitor)

## 2.2.1 - 2020-10-14
### Fixed
- NPE on regex pattern cache ([#12](https://github.com/scm-manager/scm-jira-plugin/pull/12))

## 2.2.0 - 2020-10-09
### Added
- Commit message issue key validator ([#11](https://github.com/scm-manager/scm-jira-plugin/pull/11))

## 2.1.0 - 2020-07-03
### Added
- Documentation in German ([#4](https://github.com/scm-manager/scm-jira-plugin/pull/4))

### Changed
- Uses topic for mails ([#6](https://github.com/scm-manager/scm-jira-plugin/pull/6))

### Fixed
- NPE on resubmit comments ([#7](https://github.com/scm-manager/scm-jira-plugin/pull/7))

## 2.0.0 - 2020-06-04
### Added
- Add swagger rest annotations to generate openAPI specs for the scm-openapi-plugin. ([#2](https://github.com/scm-manager/scm-jira-plugin/pull/2))
- Rebuild for api changes from core

### Changed
- Changeover to MIT license ([#3](https://github.com/scm-manager/scm-jira-plugin/pull/3))

