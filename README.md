This is an exercise in TDD. It is a Payroll application as in Section #3 of @unclebob's (Robert Cecil Martin)'s 
Agile Software Development: Principles Patterns and Practices.

The project exhibits hexagonal architecture. I.e. core model of the project is domain models's one. 
It contains data classes and behaviour drafts (interfaces) only. Other project modules 
(including repository module) depend on it. In-Memory (RAM based) repository implementation 
is chosen for the sake of simplicity. 