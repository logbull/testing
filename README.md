This repository is for manual libraries testing.

To use it, make sure you have the following directory structure:

```
logbull/ (main repository)

libraries/ (libraries repositories)
├── python
├── go
└── ... (other libraries)

testing/ (testing repositories)
├── python
├── go
└── ... (other testing)
```

Then copy .env.example to .env and fill in the values.

To run:

```bash
make install
make test
```
