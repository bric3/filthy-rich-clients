# Animation libraries

The animation examples use two related libraries: Timing Framework drives time and interpolation, while Animated
Transitions turns those values into transitions between Swing component-tree states.

## Dependency cleanup

Legacy animation JARs (Animated Transitions and Timing Framework) were removed from the examples. The affected 
builds now use:

- `org.bidib.net.java.timingframework:timingframework-swing:7.4.3` from Maven Central;
- the in-tree `:java-net:animated-transitions-swing` source module where screen transitions are required.

The sources were migrated from `org.jdesktop.animation.timing` to `org.jdesktop.core.animation.timing`, including the
builder, property setter, trigger, key-frame, interpolator, and Swing timing-source APIs. Old code had 
reflective access to Swing internals, this was removed.

The chapters dedicated to the original Timing Framework API still declare
`com.kenai.nbpwr:org-jdesktop-animation-timing:1.0-201002281504` so their source remains consistent with the book.
That artifact is resolved by Gradle rather than checked into the repository.

## Library responsibilities

|               | Animated Transitions                                                                               | Timing Framework                                                                                    |
|---------------|----------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|
| Level         | Swing screen-transition layer                                                                      | General timing and interpolation engine                                                             |
| Main API      | `ScreenTransition`                                                                                 | `Animator`                                                                                          |
| Behavior      | Captures old and new component states and renders appearing, disappearing, and changing components | Provides clocks, easing, key frames, property interpolation, repeat behavior, targets, and triggers |
| Swing support | Move, fade, scale, rotate, and composite component effects                                         | Swing timing source and event triggers                                                              |
| Distribution  | Vendored source adapted from the 2007 java.net project                                             | Maven dependency                                                                                    |

Timing Framework answers “what value should the animation have now?” Animated Transitions uses that value to paint the
transition between two Swing layouts.

## Maintenance

The former `net.java.timingframework:timingframework-swing:7.3.1` coordinate last published in February 2014. The same
project continued under `org.bidib.net.java.timingframework`; version 7.4.3 was published in January 2025 and its
[published POM](https://repo.maven.apache.org/maven2/org/bidib/net/java/timingframework/timingframework/7.4.3/timingframework-7.4.3.pom)
points to the [Timing Framework repository](https://github.com/akuhtz/timingframework).

Animated Transitions has no maintained, published drop-in replacement. So it's kept as an in-tree module.

## Plausible other options

The current status is to keep the code close to the book. However, other maintained alternative exist, some modern.

### Radiance Animation

[Radiance Animation](https://github.com/kirill-grouchnikov/radiance/blob/sunshine/docs/animation/animation.md) is a
modern alternative for low-level timelines, interpolation, scenarios, and key frames. It is not a drop-in replacement
for Animated Transitions because it does not provide the same component-tree snapshots, state comparison, or transition
layer.

A useful proof of concept would port one Dynamic Effects example first. Porting ImageBrowser or SearchTransition would
also require a small snapshot or overlay implementation for fading and scaling Swing components.

### FlatLaf animated snapshots

[FlatLaf Extras](https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-extras) provides
[`FlatAnimatedLafChange`](https://github.com/JFormDesigner/FlatLaf/blob/main/flatlaf-extras/src/main/java/com/formdev/flatlaf/extras/FlatAnimatedLafChange.java),
which crossfades snapshots of old and new Swing windows.

This could simplify the examples if a whole-window crossfade is sufficient, but it cannot reproduce component-aware
move, scale, and fade effects. Its public API is intended for look-and-feel changes, so arbitrary layout transitions
would need separate validation.

### `javax.swing.Timer`

The JDK's [Swing timer](https://docs.oracle.com/en/java/javase/25/docs/api/java.desktop/javax/swing/Timer.html) is
sufficient for small, single-property animations. It removes the external timing dependency at the cost of owning
elapsed-time calculations, easing, key frames, repeat behavior, and property updates.

## Recommendation

Keep the current setup. Evaluate Radiance or FlatLaf only as separate experiments where changing the examples'
behavior and educational focus is acceptable.

