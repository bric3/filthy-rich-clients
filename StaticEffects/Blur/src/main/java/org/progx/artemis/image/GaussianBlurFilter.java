package org.progx.artemis.image;

import org.progx.artemis.graphics.GraphicsUtilities;

import java.awt.image.BufferedImage;

public class GaussianBlurFilter extends AbstractFilter {
    private final int radius;

    /// Creates a new blur filter with a default radius of 3.
    public GaussianBlurFilter() {
        this(3);
    }

    /// Creates a new blur filter with the specified radius. If the radius
    /// is lower than 0, a radius of 0.1 will be used automatically.
    ///
    /// @param radius the radius, in pixels, of the blur
    public GaussianBlurFilter(int radius) {
        if (radius < 1) {
            radius = 1;
        }

        this.radius = radius;
    }

    /// Returns the radius used by this filter, in pixels.
    ///
    /// @return the radius of the blur
    public int getRadius() {
        return radius;
    }

    /// {@inheritDoc}
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        var width = src.getWidth();
        var height = src.getHeight();

        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
        }

        var srcPixels = new int[width * height];
        var dstPixels = new int[width * height];

        var kernel = createGaussianKernel(radius);

        GraphicsUtilities.getPixels(src, 0, 0, width, height, srcPixels);
        // horizontal pass
        blur(srcPixels, dstPixels, width, height, kernel, radius);
        // vertical pass
        // noinspection SuspiciousNameCombination
        blur(dstPixels, srcPixels, height, width, kernel, radius);
        // the result is now stored in srcPixels due to the 2nd pass
        GraphicsUtilities.setPixels(dst, 0, 0, width, height, srcPixels);

        return dst;
    }

    /// Blurs the source pixels into the destination pixels. The force of
    /// the blur is specified by the radius which must be greater than 0.
    ///
    /// The source and destination pixels arrays are expected to be in the
    /// INT\_ARGB format.
    ///
    /// After this method is executed, dstPixels contains a transposed and
    /// filtered copy of srcPixels.
    ///
    /// @param srcPixels the source pixels
    /// @param dstPixels the destination pixels
    /// @param width     the width of the source picture
    /// @param height    the height of the source picture
    /// @param kernel    the kernel of the blur effect
    /// @param radius    the radius of the blur effect
    static void blur(int[] srcPixels, int[] dstPixels,
                     int width, int height,
                     float[] kernel, int radius) {
        float a;
        float r;
        float g;
        float b;

        int ca;
        int cr;
        int cg;
        int cb;

        for (var y = 0; y < height; y++) {
            var index = y;
            var offset = y * width;

            for (var x = 0; x < width; x++) {
                a = r = g = b = 0.0f;

                for (var i = -radius; i <= radius; i++) {
                    var subOffset = x + i;
                    if (subOffset < 0 || subOffset >= width) {
                        subOffset = (x + width) % width;
                    }

                    var pixel = srcPixels[offset + subOffset];
                    var blurFactor = kernel[radius + i];

                    a += blurFactor * ((pixel >> 24) & 0xFF);
                    r += blurFactor * ((pixel >> 16) & 0xFF);
                    g += blurFactor * ((pixel >> 8) & 0xFF);
                    b += blurFactor * ((pixel) & 0xFF);
                }

                ca = (int) (a + 0.5f);
                cr = (int) (r + 0.5f);
                cg = (int) (g + 0.5f);
                cb = (int) (b + 0.5f);

                dstPixels[index] = ((Math.min(ca, 255)) << 24) |
                                   ((Math.min(cr, 255)) << 16) |
                                   ((Math.min(cg, 255)) << 8) |
                                   (Math.min(cb, 255));
                index += height;
            }
        }
    }

    static float[] createGaussianKernel(int radius) {
        if (radius < 1) {
            throw new IllegalArgumentException("Radius must be >= 1");
        }

        var data = new float[radius * 2 + 1];

        var sigma = radius / 3.0f;
        var twoSigmaSquare = 2.0f * sigma * sigma;
        var sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
        var total = 0.0f;

        for (var i = -radius; i <= radius; i++) {
            float distance = i * i;
            var index = i + radius;
            data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
            total += data[index];
        }

        for (var i = 0; i < data.length; i++) {
            data[i] /= total;
        }

        return data;
    }
}
