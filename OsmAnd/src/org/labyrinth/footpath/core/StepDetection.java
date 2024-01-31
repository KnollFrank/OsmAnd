package org.labyrinth.footpath.core;

import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.SECOND;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.labyrinth.coordinate.Angle;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

// FK-TODO: refactor
public class StepDetection {

    private static final Quantity<Time> INTERVAL = getQuantity(1000L / 30L, MILLI(SECOND));

    private final IStepListener stepListener;
    private final Consumer<Runnable> runInUIThread;

    private final SensorManager sensorManager;

    private static final int vhSize = 6;
    private final double[] values_history = new double[vhSize];
    private int vhPointer = 0;
    private final double a;
    private final double peak;
    private final int step_timeout_ms;
    private long last_step_ts = 0;
    private final double[] lastAcc = new double[]{0.0, 0.0, 0.0};
    private final Angle[] lastComp = new Angle[]{Angle.ZERO, Angle.ZERO, Angle.ZERO};
    private int round = 0;
    private Timer timer;

    private final SensorEventListener mySensorEventListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        }

        @Override
        public void onSensorChanged(final SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    // just update the oldest z value
                    lastAcc[0] = lowpassFilter(lastAcc[0], event.values[0], a);
                    lastAcc[1] = lowpassFilter(lastAcc[1], event.values[1], a);
                    lastAcc[2] = lowpassFilter(lastAcc[2], event.values[2], a);
                    break;
                case Sensor.TYPE_ORIENTATION:
                    lastComp[0] = new Angle(-event.values[0], Angle.Unit.DEGREES).wrap0To360Degrees();
                    lastComp[1] = new Angle(event.values[1], Angle.Unit.DEGREES);
                    lastComp[2] = new Angle(event.values[2], Angle.Unit.DEGREES);
                    break;
                default:
            }
        }

        private double lowpassFilter(final double old_value, final double new_value, final double a) {
            return old_value + a * (new_value - old_value);
        }
    };

    public StepDetection(final Consumer<Runnable> runInUIThread,
                         final SensorManager sensorManager,
                         final IStepListener stepListener,
                         final double a,
                         final double peak,
                         final Quantity<Time> stepTimeout) {
        this.runInUIThread = runInUIThread;
        this.sensorManager = sensorManager;
        this.stepListener = stepListener;
        this.a = a;
        this.peak = peak;
        this.step_timeout_ms = stepTimeout.to(MILLI(SECOND)).getValue().intValue();
    }

    public StepDetection(final Consumer<Runnable> runInUIThread, final SensorManager sensorManager, final IStepListener stepListener) {
        this(runInUIThread, sensorManager, stepListener, 0.5f, 0.5f, getQuantity(666, MILLI(SECOND)));
    }

    public void load() {
        unload();
        for (final Sensor sensor : sensorManager.getSensorList(Sensor.TYPE_ALL)) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER || sensor.getType() == Sensor.TYPE_ORIENTATION) {
                sensorManager.registerListener(mySensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME);
            }
        }

        timer = new Timer("UpdateData", false);
        timer.schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        updateData();
                    }
                },
                0,
                INTERVAL.to(MILLI(SECOND)).getValue().longValue());
    }

    public boolean isLoaded() {
        return timer != null;
    }

    public void unload() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        sensorManager.unregisterListener(mySensorEventListener);
    }

    private void updateData() {
        final long now_ms = System.currentTimeMillis();

        final double[] oldAcc = new double[3];
        System.arraycopy(lastAcc, 0, oldAcc, 0, 3);
        final Angle[] oldComp = new Angle[3];
        System.arraycopy(lastComp, 0, oldComp, 0, 3);
        final Angle lCompass = oldComp[0];
        final double lOld_z = oldAcc[2];

        addData(lOld_z);

        if ((now_ms - last_step_ts) > step_timeout_ms && checkForStep(peak)) {
            last_step_ts = now_ms;
            this.runInUIThread.accept(() -> stepListener.onStepDetected(lCompass));
            Log.i("FOOTPATH", "Detected step in direction " + lCompass + " in round = " + round + " @ " + now_ms);
        }
        round++;
    }

    private void addData(final double value) {
        values_history[vhPointer % vhSize] = value;
        vhPointer++;
        vhPointer = vhPointer % vhSize;
    }

    private boolean checkForStep(final double peakSize) {
        final int lookahead = 5;

        for (int t = 1; t <= lookahead; t++) {
            if ((values_history[(vhPointer - 1 - t + vhSize + vhSize) % vhSize] -
                    values_history[(vhPointer - 1 + vhSize) % vhSize]
                    > peakSize)) {
                Log.i("FOOTPATH",
                        "Detected step with t = " + t + ", diff = " + peakSize + " < " + (values_history[(vhPointer - 1 - t + vhSize + vhSize) % vhSize] -
                                values_history[(vhPointer - 1 + vhSize) % vhSize]));
                return true;
            }
        }
        return false;
    }
}
