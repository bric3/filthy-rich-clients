/*
 * Copyright (c) 2006, Sun Microsystems, Inc
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <p>
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * * Neither the name of the TimingFramework project nor the names of its
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * <p>
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

package equation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

public class EquationDisplay extends JComponent implements PropertyChangeListener {
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_MAJOR_GRID = Color.GRAY.brighter();
    private static final Color COLOR_MINOR_GRID = new Color(220, 220, 220);
    private static final Color COLOR_AXIS = Color.BLACK;

    private static final float STROKE_AXIS = 1.2f;
    private static final float STROKE_GRID = 1.0f;

    private static final float COEFF_ZOOM = 1.1f;

    private final List<DrawableEquation> equations;

    protected double minX;
    protected double maxX;
    protected double minY;
    protected double maxY;

    private final double originX;
    private final double originY;

    private final double majorX;
    private final int minorX;
    private final double majorY;
    private final int minorY;

    private boolean drawText = true;

    private Point dragStart;

    private final NumberFormat formatter;
    private final ZoomHandler zoomHandler;
    private final PanMotionHandler panMotionHandler;
    private final PanHandler panHandler;

    public EquationDisplay(double originX, double originY,
                           double minX, double maxX,
                           double minY, double maxY,
                           double majorX, int minorX,
                           double majorY, int minorY) {
        if (minX >= maxX) {
            throw new IllegalArgumentException("minX must be < to maxX");
        }

        if (originX < minX || originX > maxX) {
            throw new IllegalArgumentException("originX must be between minX and maxX");
        }

        if (minY >= maxY) {
            throw new IllegalArgumentException("minY must be < to maxY");
        }

        if (originY < minY || originY > maxY) {
            throw new IllegalArgumentException("originY must be between minY and maxY");
        }

        if (minorX <= 0) {
            throw new IllegalArgumentException("minorX must be > 0");
        }

        if (minorY <= 0) {
            throw new IllegalArgumentException("minorY must be > 0");
        }

        if (majorX <= 0.0) {
            throw new IllegalArgumentException("majorX must be > 0.0");
        }

        if (majorY <= 0.0) {
            throw new IllegalArgumentException("majorY must be > 0.0");
        }

        this.originX = originX;
        this.originY = originY;

        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;

        this.majorX = majorX;
        this.minorX = minorX;
        this.majorY = majorY;
        this.minorY = minorY;

        this.equations = new LinkedList<>();

        this.formatter = NumberFormat.getInstance();
        this.formatter.setMaximumFractionDigits(2);

        panHandler = new PanHandler();
        addMouseListener(panHandler);
        panMotionHandler = new PanMotionHandler();
        addMouseMotionListener(panMotionHandler);
        zoomHandler = new ZoomHandler();
        addMouseWheelListener(zoomHandler);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            // super.setEnabled(enabled);

            if (enabled) {
                addMouseListener(panHandler);
                addMouseMotionListener(panMotionHandler);
                addMouseWheelListener(zoomHandler);
            } else {
                removeMouseListener(panHandler);
                removeMouseMotionListener(panMotionHandler);
                removeMouseWheelListener(zoomHandler);
            }
        }
    }

    public boolean isDrawText() {
        return drawText;
    }

    public void setDrawText(boolean drawText) {
        this.drawText = drawText;
    }

    public void addEquation(AbstractEquation equation, Color color) {
        if (equation != null && !equations.contains(equation)) {
            equation.addPropertyChangeListener(this);
            equations.add(new DrawableEquation(equation, color));
            repaint();
        }
    }

    public void removeEquation(AbstractEquation equation) {
        if (equation != null) {
            DrawableEquation toRemove = null;
            for (var drawable : equations) {
                if (drawable.getEquation() == equation) {
                    toRemove = drawable;
                    break;
                }
            }

            if (toRemove != null) {
                equation.removePropertyChangeListener(this);
                equations.remove(toRemove);
                repaint();
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
    }

    protected double yPositionToPixel(double position) {
        double height = getHeight();
        return height - ((position - minY) * height / (maxY - minY));
    }

    protected double xPositionToPixel(double position) {
        return (position - minX) * (double) getWidth() / (maxX - minX);
    }

    protected double xPixelToPosition(double pixel) {
        var axisV = xPositionToPixel(originX);
        return (pixel - axisV) * (maxX - minX) / (double) getWidth();
    }

    protected double yPixelToPosition(double pixel) {
        var axisH = yPositionToPixel(originY);
        return (getHeight() - pixel - axisH) * (maxY - minY) / (double) getHeight();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!isVisible()) {
            return;
        }

        var g2 = (Graphics2D) g;
        setupGraphics(g2);

        paintBackground(g2);
        drawGrid(g2);
        drawAxis(g2);

        drawEquations(g2);

        paintInformation(g2);
    }

    protected void paintInformation(Graphics2D g2) {
    }

    private void drawEquations(Graphics2D g2) {
        for (var drawable : equations) {
            g2.setColor(drawable.getColor());
            drawEquation(g2, drawable.getEquation());
        }
    }

    private void drawEquation(Graphics2D g2, AbstractEquation equation) {
        var x = 0.0f;
        var y = (float) yPositionToPixel(equation.compute(xPixelToPosition(0.0)));

        var path = new GeneralPath();
        path.moveTo(x, y);

        for (x = 0.0f; x < getWidth(); x += 1.0f) {
            var position = xPixelToPosition(x);
            y = (float) yPositionToPixel(equation.compute(position));
            path.lineTo(x, y);
        }

        g2.draw(path);
    }

    private void drawGrid(Graphics2D g2) {
        var stroke = g2.getStroke();

        drawVerticalGrid(g2);
        drawHorizontalGrid(g2);

        if (drawText) {
            drawVerticalLabels(g2);
            drawHorizontalLabels(g2);
        }

        g2.setStroke(stroke);
    }

    private void drawHorizontalLabels(Graphics2D g2) {
        var axisV = xPositionToPixel(originX);

        g2.setColor(COLOR_AXIS);
        for (var y = originY + majorY; y < maxY + majorY; y += majorY) {
            var position = (int) yPositionToPixel(y);
            g2.drawString(formatter.format(y), (int) axisV + 5, position);
        }

        for (var y = originY - majorY; y > minY - majorY; y -= majorY) {
            var position = (int) yPositionToPixel(y);
            g2.drawString(formatter.format(y), (int) axisV + 5, position);
        }
    }

    private void drawHorizontalGrid(Graphics2D g2) {
        var minorSpacing = majorY / minorY;
        var axisV = xPositionToPixel(originX);

        Stroke gridStroke = new BasicStroke(STROKE_GRID);
        Stroke axisStroke = new BasicStroke(STROKE_AXIS);

        for (var y = originY + majorY; y < maxY + majorY; y += majorY) {
            g2.setStroke(gridStroke);
            g2.setColor(COLOR_MINOR_GRID);
            for (var i = 0; i < minorY; i++) {
                var position = (int) yPositionToPixel(y - i * minorSpacing);
                g2.drawLine(0, position, getWidth(), position);
            }

            var position = (int) yPositionToPixel(y);
            g2.setColor(COLOR_MAJOR_GRID);
            g2.drawLine(0, position, getWidth(), position);

            g2.setStroke(axisStroke);
            g2.setColor(COLOR_AXIS);
            g2.drawLine((int) axisV - 3, position, (int) axisV + 3, position);
        }

        for (var y = originY - majorY; y > minY - majorY; y -= majorY) {
            g2.setStroke(gridStroke);
            g2.setColor(COLOR_MINOR_GRID);
            for (var i = 0; i < minorY; i++) {
                var position = (int) yPositionToPixel(y + i * minorSpacing);
                g2.drawLine(0, position, getWidth(), position);
            }

            var position = (int) yPositionToPixel(y);
            g2.setColor(COLOR_MAJOR_GRID);
            g2.drawLine(0, position, getWidth(), position);

            g2.setStroke(axisStroke);
            g2.setColor(COLOR_AXIS);
            g2.drawLine((int) axisV - 3, position, (int) axisV + 3, position);
        }
    }

    private void drawVerticalLabels(Graphics2D g2) {
        var axisH = yPositionToPixel(originY);
        var metrics = g2.getFontMetrics();

        g2.setColor(COLOR_AXIS);

        for (var x = originX + majorX; x < maxX + majorX; x += majorX) {
            var position = (int) xPositionToPixel(x);
            g2.drawString(formatter.format(x), position, (int) axisH + metrics.getHeight());
        }

        for (var x = originX - majorX; x > minX - majorX; x -= majorX) {
            var position = (int) xPositionToPixel(x);
            g2.drawString(formatter.format(x), position, (int) axisH + metrics.getHeight());
        }
    }

    private void drawVerticalGrid(Graphics2D g2) {
        var minorSpacing = majorX / minorX;
        var axisH = yPositionToPixel(originY);

        Stroke gridStroke = new BasicStroke(STROKE_GRID);
        Stroke axisStroke = new BasicStroke(STROKE_AXIS);

        for (var x = originX + majorX; x < maxX + majorX; x += majorX) {
            g2.setStroke(gridStroke);
            g2.setColor(COLOR_MINOR_GRID);
            for (var i = 0; i < minorX; i++) {
                var position = (int) xPositionToPixel(x - i * minorSpacing);
                g2.drawLine(position, 0, position, getHeight());
            }

            var position = (int) xPositionToPixel(x);
            g2.setColor(COLOR_MAJOR_GRID);
            g2.drawLine(position, 0, position, getHeight());

            g2.setStroke(axisStroke);
            g2.setColor(COLOR_AXIS);
            g2.drawLine(position, (int) axisH - 3, position, (int) axisH + 3);
        }

        for (var x = originX - majorX; x > minX - majorX; x -= majorX) {
            g2.setStroke(gridStroke);
            g2.setColor(COLOR_MINOR_GRID);
            for (var i = 0; i < minorX; i++) {
                var position = (int) xPositionToPixel(x + i * minorSpacing);
                g2.drawLine(position, 0, position, getHeight());
            }

            var position = (int) xPositionToPixel(x);
            g2.setColor(COLOR_MAJOR_GRID);
            g2.drawLine(position, 0, position, getHeight());

            g2.setStroke(axisStroke);
            g2.setColor(COLOR_AXIS);
            g2.drawLine(position, (int) axisH - 3, position, (int) axisH + 3);
        }
    }

    private void drawAxis(Graphics2D g2) {
        var axisH = yPositionToPixel(originY);
        var axisV = xPositionToPixel(originX);

        g2.setColor(COLOR_AXIS);
        var stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(STROKE_AXIS));

        g2.drawLine(0, (int) axisH, getWidth(), (int) axisH);
        g2.drawLine((int) axisV, 0, (int) axisV, getHeight());

        var metrics = g2.getFontMetrics();
        g2.drawString(formatter.format(0.0), (int) axisV + 5, (int) axisH + metrics.getHeight());

        g2.setStroke(stroke);
    }

    protected void setupGraphics(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
    }

    protected void paintBackground(Graphics2D g2) {
        g2.setColor(COLOR_BACKGROUND);
        g2.fill(g2.getClipBounds());
    }

    private static class DrawableEquation {
        private final AbstractEquation equation;
        private final Color color;

        DrawableEquation(AbstractEquation equation, Color color) {
            this.equation = equation;
            this.color = color;
        }

        AbstractEquation getEquation() {
            return equation;
        }

        Color getColor() {
            return color;
        }
    }

    private class ZoomHandler implements MouseWheelListener {
        public void mouseWheelMoved(MouseWheelEvent e) {
            var distanceX = maxX - minX;
            var distanceY = maxY - minY;

            var cursorX = minX + distanceX / 2.0;
            var cursorY = minY + distanceY / 2.0;

            var rotation = e.getWheelRotation();
            if (rotation < 0) {
                distanceX /= COEFF_ZOOM;
                distanceY /= COEFF_ZOOM;
            } else {
                distanceX *= COEFF_ZOOM;
                distanceY *= COEFF_ZOOM;
            }

            minX = cursorX - distanceX / 2.0;
            maxX = cursorX + distanceX / 2.0;
            minY = cursorY - distanceY / 2.0;
            maxY = cursorY + distanceY / 2.0;

            repaint();
        }
    }

    private class PanHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            dragStart = e.getPoint();
        }
    }

    private class PanMotionHandler extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            var dragEnd = e.getPoint();

            var distance = xPixelToPosition(dragEnd.getX()) -
                           xPixelToPosition(dragStart.getX());
            minX -= distance;
            maxX -= distance;

            distance = yPixelToPosition(dragEnd.getY()) -
                       yPixelToPosition(dragStart.getY());
            minY -= distance;
            maxY -= distance;

            repaint();
            dragStart = dragEnd;
        }
    }
}
