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
    private final Supplier<Optional<Path>> optionalPathSupplier;

    public FootPath(final OsmandApplication app,
                    final Supplier<Optional<Path>> optionalPathSupplier,
                    final Quantity<Length> pedestrianHeight,
                    final boolean enabled) {
        this.delegate =
                new FootPathDelegate(
                        app,
                        optionalPathSupplier.get(),
                        pedestrianHeight,
                        enabled);
        this.optionalPathSupplier = optionalPathSupplier;
    }

    @Override
    public void newRouteIsCalculated(final boolean newRoute, final ValueHolder<Boolean> showToast) {
        delegate.setPath(optionalPathSupplier.get());
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

    public void setPedestrianHeight(final Quantity<Length> pedestrianHeight) {
        delegate.setPedestrianHeight(pedestrianHeight);
    }
}
