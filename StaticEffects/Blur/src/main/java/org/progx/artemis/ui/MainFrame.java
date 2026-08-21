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

package org.progx.artemis.ui;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.progx.artemis.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

public class MainFrame extends JFrame {
    private StepLabel stepLabel;
    private BufferedImage image;
    private final ProgressGlassPane waitPanel;
    private final SwingTimerTimingSource timingSource = createTimingSource();
    private Animator timer;
    private SizeStepPanel sizeStep;
    private DragAndDropStepPanel dragAndDropStep;
    private DoneStepPanel doneStep;
    private String fileName;

    public MainFrame() {
        super(Application.getResourceBundle().getString("frame.title"));

        setContentPane(new GradientPanel());
        setGlassPane(waitPanel = new ProgressGlassPane());
        buildContentPane();

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(640, 480);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private static SwingTimerTimingSource createTimingSource() {
        var timingSource = new SwingTimerTimingSource(33, TimeUnit.MILLISECONDS);
        timingSource.init();
        return timingSource;
    }

    private void buildContentPane() {
        buildStepLabel();
        add(stepLabel, BorderLayout.SOUTH);
        showDragAndDropStep();
    }

    private void buildStepLabel() {
        stepLabel = new StepLabel();
        stepLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 36, 0));
    }

    public void showDragAndDropStep() {
        if (image != null) {
            image.flush();
            image = null;
        }
        if (sizeStep != null) {
            remove(sizeStep);
            sizeStep = null;
        } else if (doneStep != null) {
            remove(doneStep);
            doneStep = null;
        }
        dragAndDropStep = new DragAndDropStepPanel();
        dragAndDropStep.setTransferHandler(new ImageTransferHandler());
        add(dragAndDropStep);
        setStepInfo(1, Application.getResourceBundle().getString("step.1.dropHere"));
        hideWaitGlassPane();
    }

    private void setStepInfo(int step, String message) {
        stepLabel.setStep(step);
        stepLabel.setText(message);
    }

    public void setImage(BufferedImage image, String fileName) {
        this.fileName = fileName;
        this.image = image;
        showSizeStep();
    }

    public BufferedImage getImage() {
        return image;
    }

    private void showSizeStep() {
        sizeStep = new SizeStepPanel();
        remove(dragAndDropStep);
        dragAndDropStep = null;
        add(sizeStep);
        setStepInfo(2, Application.getResourceBundle().getString(
                "step.2.chooseSize"));
    }

    public void showWaitGlassPane() {
        timer = new Animator.Builder(timingSource)
                .setDuration(2500, TimeUnit.MILLISECONDS)
                .setRepeatCount(Animator.INFINITE)
                .setRepeatBehavior(Animator.RepeatBehavior.REVERSE)
                .addTarget(PropertySetter.getTarget(waitPanel, "progress", 0, 100))
                .build();

        waitPanel.setProgress(0);
        waitPanel.setVisible(true);
        timer.start();
    }

    public void hideWaitGlassPane() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        waitPanel.setVisible(false);
    }

    public void showDoneStep() {
        doneStep = new DoneStepPanel();
        remove(sizeStep);
        sizeStep = null;
        add(doneStep);
        setStepInfo(3, Application.getResourceBundle().getString("step.3.done"));
        hideWaitGlassPane();
    }

    public String getFileName() {
        return fileName;
    }
}
