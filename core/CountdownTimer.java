package core;

public class CountdownTimer {
    private double duration;

    public CountdownTimer(double duration, TimeUnit unit) {
        switch (unit) {
            case MILLISECOND:

                break;
            case SECOND:
                break;
            case MINUTE:
                break;

        }
    }

    private boolean isDone;

    public void start() {
        isDone = false;

        Runnable masterCountdownRunner = new Runnable() {

            @Override
            public void run() {
                double startTime = System.currentTimeMillis();
                double currentTime = startTime;
                double timeElapsed = 0;

                while (timeElapsed < Settings.GAME_DURATION_MIN) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    currentTime = System.currentTimeMillis();
                    timeElapsed = currentTime - startTime;

                    System.out.println("Time in game: " + ((Settings.GAME_DURATION_MIN - timeElapsed) / 60000));

                }
                System.out.println("done");
            }

        };

    }
}

enum TimeUnit {
    MILLISECOND,
    SECOND,
    MINUTE,
}