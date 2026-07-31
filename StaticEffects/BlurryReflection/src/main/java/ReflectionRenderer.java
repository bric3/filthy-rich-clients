/*
 * Copyright (c) 2007, Romain Guy
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of the TimingFramework project nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/// A reflection renderer generates the reflection of a given picture. The
/// result can be either the reflection itself, or an image containing both the
/// source image and its reflection.
///
/// ## Reflection Properties
///
/// A reflection is defined by three properties:
///
///   - _opacity_: the opacity of the reflection. You will usually
///     change this valued according to the background color.
///   - _length_: the length of the reflection. The length is a fraction
///     of the height of the source image.
///   - _blur enabled_: perfect reflections are hardly natural. You can
///     blur the reflection to make it look a bit more natural.
///
/// You can set these properties using the provided mutaters or the appropriate
/// constructor. Here are two ways of creating a blurred reflection, with an
/// opacity of 50% and a length of 30% the height of the original image:
/// <pre>
/// ReflectionRenderer renderer = new ReflectionRenderer(0.5f, 0.3f, true);
/// // ..
/// renderer = new ReflectionRenderer();
/// renderer.setOpacity(0.5f);
/// renderer.setLength(0.3f);
/// renderer.setBlurEnabled(true);
/// </pre>
///
/// The default constructor provides the following default values:
///
///   - _opacity_: 35%
///   - _length_: 40%
///   - _blur enabled_: false
///
/// ## Generating Reflections
///
/// A reflection is generated as a `BufferedImage` from another
/// `BufferedImage`. Once the renderer is set up, you must call
/// [#createReflection(BufferedImage)] to actually generate
/// the reflection:
///
/// <pre>
/// ReflectionRenderer renderer = new ReflectionRenderer();
/// // renderer setup
/// BufferedImage reflection = renderer.createReflection(bufferedImage);
/// </pre>
///
/// The returned image contains only the reflection. You will have to append
/// it to the source image at painting time to get a realistic results. You can
/// also asks the rendered to return a picture composed of both the source image
/// and its reflection:
///
/// <pre>
/// ReflectionRenderer renderer = new ReflectionRenderer();
/// // renderer setup
/// BufferedImage reflection = renderer.appendReflection(bufferedImage);
/// </pre>
///
/// ## Properties Changes
///
/// This renderer allows to register property change listeners with
/// [#addPropertyChangeListener]. Listening to properties changes is very
/// useful when you emebed the renderer in a graphical component and give the API
/// user the ability to access the renderer. By listening to properties changes,
/// you can easily repaint the component when needed.
///
/// ## Threading Issues
///
/// `ReflectionRenderer` is not guaranteed to be thread-safe.
///
/// @author Romain Guy <romain.guy@mac.com></romain.guy@mac.com>
public class ReflectionRenderer {
    /// Identifies a change to the opacity used to render the reflection.
    public static final String OPACITY_CHANGED_PROPERTY = "reflection_opacity";

    /// Identifies a change to the length of the rendered reflection.
    public static final String LENGTH_CHANGED_PROPERTY = "reflection_length";

    /// Identifies a change to the blurring of the rendered reflection.
    public static final String BLUR_ENABLED_CHANGED_PROPERTY = "reflection_blur";

    // opacity of the reflection
    private float opacity;

    // length of the reflection
    private float length;

    // should the reflection be blurred?
    private boolean blurEnabled;

    // notifies listeners of properties changes
    private final PropertyChangeSupport changeSupport;
    private StackBlurFilter stackBlurFilter;

    /// Creates a default good looking reflections generator.
    /// The default reflection renderer provides the following default values:
    ///
    ///   - _opacity_: 35%
    ///   - _length_: 40%
    ///   - _blurring_: disabled with a radius of 1 pixel
    ///
    /// These properties provide a regular, good looking reflection.
    ///
    /// @see #getOpacity()
    /// @see #setOpacity(float)
    /// @see #getLength()
    /// @see #setLength(float)
    /// @see #isBlurEnabled()
    /// @see #setBlurEnabled(boolean)
    /// @see #getBlurRadius()
    /// @see #setBlurRadius(int)
    public ReflectionRenderer() {
        this(0.35f, 0.4f, false);
    }

    /// Creates a default good looking reflections generator with the
    /// specified opacity. The default reflection renderer provides the following
    /// default values:
    ///
    ///   - _length_: 40%
    ///   - _blurring_: disabled with a radius of 1 pixel
    ///
    /// @param opacity the opacity of the reflection, between 0.0 and 1.0
    /// @see #getOpacity()
    /// @see #setOpacity(float)
    /// @see #getLength()
    /// @see #setLength(float)
    /// @see #isBlurEnabled()
    /// @see #setBlurEnabled(boolean)
    /// @see #getBlurRadius()
    /// @see #setBlurRadius(int)
    public ReflectionRenderer(float opacity) {
        this(opacity, 0.4f, false);
    }

    /// Creates a reflections generator with the specified properties. Both
    /// opacity and length are numbers between 0.0 (0%) and 1.0 (100%). If the
    /// provided numbers are outside this range, they are clamped.
    ///
    /// Enabling the blur generates a different kind of reflections that might
    /// look more natural. The default blur radius is 1 pixel
    ///
    /// @param opacity     the opacity of the reflection
    /// @param length      the length of the reflection
    /// @param blurEnabled if true, the reflection is blurred
    /// @see #getOpacity(),#setOpacity(float),#getLength(),#setLength(float)
    /// @see #isBlurEnabled(),#setBlurEnabled(boolean)
    /// @see #getBlurRadius()
    /// @see #setBlurRadius(int)
    public ReflectionRenderer(float opacity, float length, boolean blurEnabled) {
        // noinspection ThisEscapedInObjectConstruction
        this.changeSupport = new PropertyChangeSupport(this);
        this.stackBlurFilter = new StackBlurFilter(1);

        setOpacity(opacity);
        setLength(length);
        setBlurEnabled(blurEnabled);
    }

    /// Add a PropertyChangeListener to the listener list. The listener is
    /// registered for all properties. The same listener object may be added
    /// more than once, and will be called as many times as it is added. If
    /// `listener` is null, no exception is thrown and no action
    /// is taken.
    ///
    /// @param listener the PropertyChangeListener to be added
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /// Remove a PropertyChangeListener from the listener list. This removes
    /// a PropertyChangeListener that was registered for all properties. If
    /// `listener` was added more than once to the same event source,
    /// it will be notified one less time after being removed. If
    /// `listener` is null, or was never added, no exception is thrown
    /// and no action is taken.
    ///
    /// @param listener the PropertyChangeListener to be removed
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /// Gets the opacity used by the factory to generate reflections.
    ///
    /// The opacity is comprised between 0.0f and 1.0f; 0.0f being fully
    /// transparent and 1.0f fully opaque.
    ///
    /// @return this factory's shadow opacity
    /// @see #getOpacity()
    /// @see #createReflection(BufferedImage)
    /// @see #appendReflection(BufferedImage)
    public float getOpacity() {
        return opacity;
    }

    /// Sets the opacity used by the factory to generate reflections.
    ///
    /// Consecutive calls to [#createReflection] will all use this
    /// opacity until it is set again.
    ///
    /// The opacity is comprised between 0.0f and 1.0f; 0.0f being fully
    /// transparent and 1.0f fully opaque. If you provide a value out of these
    /// boundaries, it will be restrained to the closest boundary.
    ///
    /// @param opacity the generated reflection opacity
    /// @see #setOpacity(float)
    /// @see #createReflection(BufferedImage)
    /// @see #appendReflection(BufferedImage)
    public void setOpacity(float opacity) {
        var oldOpacity = this.opacity;

        if (opacity < 0.0f) {
            opacity = 0.0f;
        } else if (opacity > 1.0f) {
            opacity = 1.0f;
        }

        if (oldOpacity != opacity) {
            this.opacity = opacity;
            changeSupport.firePropertyChange(OPACITY_CHANGED_PROPERTY,
                    oldOpacity,
                    this.opacity);
        }
    }

    /// Returns the length of the reflection. The result is a number between
    /// 0.0 and 1.0. This number is the fraction of the height of the source
    /// image that is used to compute the size of the reflection.
    ///
    /// @return the length of the reflection, as a fraction of the source image
    /// height
    /// @see #setLength(float)
    /// @see #createReflection(BufferedImage)
    /// @see #appendReflection(BufferedImage)
    public float getLength() {
        return length;
    }

    /// Sets the length of the reflection, as a fraction of the height of the
    /// source image.
    ///
    /// Consecutive calls to [#createReflection] will all use this
    /// opacity until it is set again.
    ///
    /// The opacity is comprised between 0.0f and 1.0f; 0.0f being fully
    /// transparent and 1.0f fully opaque. If you provide a value out of these
    /// boundaries, it will be restrained to the closest boundary.
    ///
    /// @param length the length of the reflection, as a fraction of the source
    ///               image height
    /// @see #getLength()
    /// @see #createReflection(BufferedImage)
    /// @see #appendReflection(BufferedImage)
    public void setLength(float length) {
        var oldLength = this.length;

        if (length < 0.0f) {
            length = 0.0f;
        } else if (length > 1.0f) {
            length = 1.0f;
        }

        if (oldLength != length) {
            this.length = length;
            changeSupport.firePropertyChange(LENGTH_CHANGED_PROPERTY,
                    oldLength,
                    this.length);
        }
    }

    /// Returns true if the blurring of the reflection is enabled, false
    /// otherwise. When blurring is enabled, the reflection is blurred to look
    /// more natural.
    ///
    /// @return true if blur is enabled, false otherwise
    /// @see #setBlurEnabled(boolean)
    /// @see #createReflection(BufferedImage)
    /// @see #appendReflection(BufferedImage)
    public boolean isBlurEnabled() {
        return blurEnabled;
    }

    /// Setting the blur to true will enable the blurring of the reflection
    /// when [#createReflection] is invoked.
    ///
    /// Enabling the blurring of the reflection can yield to more natural
    /// results which may or may not be better looking, depending on the source
    /// picture.
    ///
    /// Consecutive calls to [#createReflection] will all use this
    /// opacity until it is set again.
    ///
    /// @param blurEnabled true to enable the blur, false otherwise
    /// @see #isBlurEnabled()
    /// @see #createReflection(BufferedImage)
    /// @see #appendReflection(BufferedImage)
    public void setBlurEnabled(boolean blurEnabled) {
        if (blurEnabled != this.blurEnabled) {
            var oldBlur = this.blurEnabled;
            this.blurEnabled = blurEnabled;

            changeSupport.firePropertyChange(BLUR_ENABLED_CHANGED_PROPERTY,
                    oldBlur,
                    this.blurEnabled);
        }
    }

    /// Returns the effective radius, in pixels, of the blur used by this
    /// renderer when [#isBlurEnabled()] is true.
    ///
    /// @return the effective radius of the blur used when
    /// `isBlurEnabled` is true
    /// @see #isBlurEnabled()
    /// @see #setBlurEnabled(boolean)
    /// @see #setBlurRadius(int)
    /// @see #getBlurRadius()
    public int getEffectiveBlurRadius() {
        return stackBlurFilter.getEffectiveRadius();
    }

    /// Returns the radius, in pixels, of the blur used by this renderer when
    /// [#isBlurEnabled()] is true.
    ///
    /// @return the radius of the blur used when `isBlurEnabled`
    /// is true
    /// @see #isBlurEnabled()
    /// @see #setBlurEnabled(boolean)
    /// @see #setBlurRadius(int)
    /// @see #getEffectiveBlurRadius()
    public int getBlurRadius() {
        return stackBlurFilter.getRadius();
    }

    /// Sets the radius, in pixels, of the blur used by this renderer when
    /// [#isBlurEnabled()] is true. This radius changes the size of the
    /// generated image when blurring is applied.
    ///
    /// @param radius the radius, in pixels, of the blur
    /// @see #isBlurEnabled()
    /// @see #setBlurEnabled(boolean)
    /// @see #getBlurRadius()
    public void setBlurRadius(int radius) {
        this.stackBlurFilter = new StackBlurFilter(radius);
    }

    /// Returns the source image and its reflection. The appearance of the
    /// reflection is defined by the opacity, the length and the blur
    /// properties.
    ///
    /// \*
    ///
    /// The width of the generated image will be augmented when
    /// [#isBlurEnabled()] is true. The generated image will have the width
    /// of the source image plus twice the effective blur radius (see
    /// [#getEffectiveBlurRadius()]). The default blur radius is 1 so the
    /// width will be augmented by 6. You might need to take this into account
    /// at drawing time.
    ///
    /// The returned image height depends on the value returned by
    /// [#getLength()] and [#getEffectiveBlurRadius()]. For instance,
    /// if the length is 0.5 (or 50%) and the source image is 480 pixels high,
    /// then the reflection will be 246 (480 \* 0.5 + 3 \* 2) pixels high.
    ///
    /// You can create only the reflection by calling
    /// [#createReflection(BufferedImage)].
    ///
    /// @param image the source image
    /// @return the source image with its reflection below
    /// @see #createReflection(BufferedImage)
    public BufferedImage appendReflection(BufferedImage image) {
        var reflection = createReflection(image);
        var buffer = GraphicsUtilities.createCompatibleTranslucentImage(
                reflection.getWidth(), image.getHeight() + reflection.getHeight());
        var g2 = buffer.createGraphics();

        var effectiveRadius = isBlurEnabled() ? stackBlurFilter.getEffectiveRadius() : 0;
        g2.drawImage(image, effectiveRadius, 0, null);
        g2.drawImage(reflection, 0, image.getHeight() - effectiveRadius, null);

        g2.dispose();
        reflection.flush();

        return buffer;
    }

    /// Returns the reflection of the source image. The appearance of the
    /// reflection is defined by the opacity, the length and the blur
    /// properties.
    ///
    /// \*
    ///
    /// The width of the generated image will be augmented when
    /// [#isBlurEnabled()] is true. The generated image will have the width
    /// of the source image plus twice the effective blur radius (see
    /// [#getEffectiveBlurRadius()]). The default blur radius is 1 so the
    /// width will be augmented by 6. You might need to take this into account
    /// at drawing time.
    ///
    /// The returned image height depends on the value returned by
    /// [#getLength()] and [#getEffectiveBlurRadius()]. For instance,
    /// if the length is 0.5 (or 50%) and the source image is 480 pixels high,
    /// then the reflection will be 246 (480 \* 0.5 + 3 \* 2) pixels high.
    ///
    /// The returned image contains **only**
    /// the reflection. You will have to append it to the source image to produce
    /// the illusion of a reflective environement. The method
    /// [#appendReflection(BufferedImage)] provides an easy
    /// way to create an image containing both the source and the reflection.
    ///
    /// @param image the source image
    /// @return the reflection of the source image
    /// @see #appendReflection(BufferedImage)
    public BufferedImage createReflection(BufferedImage image) {
        if (length == 0.0f) {
            return GraphicsUtilities.createCompatibleTranslucentImage(1, 1);
        }

        var blurOffset = isBlurEnabled() ?
                stackBlurFilter.getEffectiveRadius() : 0;
        var height = (int) (image.getHeight() * length);

        var buffer =
                GraphicsUtilities.createCompatibleTranslucentImage(
                        image.getWidth() + blurOffset * 2,
                        height + blurOffset * 2);
        var g2 = buffer.createGraphics();

        g2.translate(0, image.getHeight());
        g2.scale(1.0, -1.0);

        g2.drawImage(image, blurOffset, -blurOffset, null);

        g2.scale(1.0, -1.0);
        g2.translate(0, -image.getHeight());

        g2.setComposite(AlphaComposite.DstIn);
        g2.setPaint(new GradientPaint(0.0f, 0.0f,
                new Color(0.0f, 0.0f, 0.0f, getOpacity()),
                0.0f, buffer.getHeight(),
                new Color(0.0f, 0.0f, 0.0f, 0.0f), true));
        g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

        g2.dispose();
        return isBlurEnabled() ? stackBlurFilter.filter(buffer, null) :
                buffer;
    }
}
