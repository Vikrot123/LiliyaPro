LiliyaPro evolution step configuration

Required:
  VERSION
  TITLE
  MODE
  TARGETS

MODE:
  BUILD   new capability
  STITCH  integration between mature capabilities
  HARDEN  contracts, isolation, rollback, automation
  MOVE    close current vertical and move to another subsystem

Optional guards:
  READ_ONLY_PATHS
  FORBIDDEN_DIFF
  FORBIDDEN_REFERENCES

Run:
  ./tools/core-evolve.sh run tools/steps/<version>.conf
