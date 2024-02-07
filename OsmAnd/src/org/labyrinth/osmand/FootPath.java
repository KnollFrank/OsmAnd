package org.labyrinth.osmand;

import net.osmand.data.ValueHolder;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.routing.IRouteInformationListener;

import org.labyrinth.footpath.graph.Path;

import java.util.Optional;
import java.util.function.Supplier;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class FootPath implements IRouteInformationListener {

    private final FootPathDelegate delegate;
    private final Supplier<Optional<Path>> getActualPath;

    public FootPath(final OsmandApplication app,
                    final Supplier<Optional<Path>> getActualPath,
                    final Optional<Quantity<Length>> stepLength,
                    final boolean enabled) {
        this.delegate =
                new FootPathDelegate(
                        app,
                        getActualPath.get(),
                        stepLength,
                        enabled);
        this.getActualPath = getActualPath;
    }

    @Override
    public void newRouteIsCalculated(final boolean newRoute, final ValueHolder<Boolean> showToast) {
        delegate.setPath(getActualPath.get());
    }

    @Override
    public void routeWasCancelled() {
        delegate.stopIfEnabled();
    }

    @Override
    public void routeWasFinished() {
        delegate.stopIfEnabled();
    }

    public void setEnabled(final boolean enabled) {
        delegate.setEnabled(enabled);
    }

    public boolean isEnabled() {
        return delegate.isEnabled();
    }

    public void setStepLength(final Quantity<Length> stepLength) {
        delegate.setStepLength(stepLength);
    }
}
