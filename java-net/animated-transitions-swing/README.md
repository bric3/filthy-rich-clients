# Animated Transitions for Swing

This directory contains a source copy of the Animated Transitions library used by the examples in Chapter 18 of *Filthy
Rich Clients*.

## Why this directory exists

The library sources were not part of the original `filthy-rich-clients`
repository. The `ImageBrowser` and `SearchTransition` examples instead carried prebuilt copies of
`AnimatedTransitions.jar` and `TimingFramework-0.55.jar` in their respective `lib` directories.

Animated Transitions was developed as a separate project and hosted on the now-defunct java.net service at
`animatedtransitions.dev.java.net`. This fork added the sources under `java-net` while migrating the examples to Gradle.
The separate location makes their third-party provenance explicit and provides a place to:

- inspect and debug code that was previously available only as a checked-in binary;
- adapt the library to current Java and Timing Framework APIs;
- eventually replace the duplicated `AnimatedTransitions.jar` files with a Gradle project dependency.

## Current status

Gradle includes this directory as the
`:java-net:animated-transitions-swing` project. It builds against the maintained Timing Framework for Swing.

This module should be understood as a vendored migration copy, not as source that was present in the original examples
repository.

## Provenance and license

Animated Transitions was created by Chet Haase and is described in Chapter 18 of *Filthy Rich Clients*. The source files
retain their original 2007 Sun Microsystems three-clause BSD license headers; those headers must be preserved.

Historical context is available from:

- the original project address:
  <http://animatedtransitions.dev.java.net/>;
- the archived [*Filthy Rich
  Clients* website](https://web.archive.org/web/20220118193103/http://filthyrichclients.org/).
