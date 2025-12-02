# Architecture

### I chose a 3 tiers architecture for a few reasons:
- Each layer is independant for a clearer code comprehension and an easier maintenance.
- Each layer has it's own role so you can isolate sensible data (you cannot access a database client side)
- Easier to test by testing each layer separately.
