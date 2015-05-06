# ------- A Perf build configured for cross-compilation ----------------------

SRC_HC_OPTS     = -O -H64m
GhcStage1HcOpts = -O2
GhcStage2HcOpts = -O2 -fasm
GhcHcOpts       = -Rghc-timing
GhcLibHcOpts    = -O2
GhcLibWays      = v dyn
INTEGER_LIBRARY    = integer-simple
Stage1Only         = YES

HADDOCK_DOCS       = NO
BUILD_DOCBOOK_HTML = NO
BUILD_DOCBOOK_PS   = NO
BUILD_DOCBOOK_PDF  = NO

DYNAMIC_BY_DEFAULT   = NO
DYNAMIC_GHC_PROGRAMS = NO

# NoFib settings
NoFibWays =
STRIP_CMD = :

