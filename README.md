This is an exercise in TDD. It is a Payroll application as in Section #3 of @unclebob's (Robert Cecil Martin)'s 
Agile Software Development: Principles, Patterns and Practices.

The project exhibits hexagonal architecture. I.e. domain entities constitute a core module of the project. 
The module contains data classes and behaviour drafts (interfaces) only. Other project modules 
(including repository module) depend on it. So we do not care for the exact form domain entities
are persisted in repository.

In-Memory (RAM based) repository implementation is chosen for the sake of simplicity. 