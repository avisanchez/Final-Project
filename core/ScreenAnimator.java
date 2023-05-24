package core;

import javax.swing.JPanel;

import core.mydatastruct.Pair;

public class ScreenAnimator implements Runnable {
    private final int TARGET_FPS = 60;

    private JPanel target;

    private int FPS, maxFPS;
    private double startTime, deltaTime;
    private Pair<Integer, Double> avgFPS; // _0 = accumulated FPS, _1 = numRedraws

    public ScreenAnimator(JPanel target) {
        this.target = target;

        FPS = maxFPS = 0;
        startTime = deltaTime = 0.0;
        avgFPS = new Pair<Integer, Double>(0, 0.0);
    }

    @Override
    public void run() {
        while (true) {
            if (Thread.interrupted()) {
                break;
            }

            // update delta time
            deltaTime = (System.currentTimeMillis() - startTime) / 1000.0;
            startTime = System.currentTimeMillis();

            // update fps
            FPS = (int) (1.0 / deltaTime);

            // update max fps
            if (FPS > maxFPS) {
                maxFPS = FPS;
            }

            // update avg fps
            avgFPS._0 += FPS;
            avgFPS._1++;

            // run game at ~ target fps
            try {
                Thread.sleep((long) (1000.0 / TARGET_FPS));

            } catch (InterruptedException e) {
                e.printStackTrace();

            }

            target.repaint();

        }
    }

    public int getFPS() {
        return FPS;
    }

    public int getMaxFPS() {
        return maxFPS;
    }

    public int getAvgFPS() {
        return (int) (avgFPS._0 / avgFPS._1);
    }

    public double getDeltaTime() {
        return deltaTime;
    }
}