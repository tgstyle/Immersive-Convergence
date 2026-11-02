package com.immersiveconvergence.core.integration.opencomputers.ie;

import com.immersiveconvergence.core.integration.opencomputers.ICOCArguments;
import com.immersiveconvergence.core.integration.opencomputers.ICOCBlockRef;
import com.immersiveconvergence.core.integration.opencomputers.ICOCReferences;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration.ExtraComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.utils.ComputerControlState;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackOwner;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerControllable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.EventWaiterResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class IEEnvironment<T> extends AbstractManagedEnvironment implements ManagedPeripheral, NamedBlock {
    private static final Map<CallbackOwner<?>, List<?>> CACHE = new ConcurrentHashMap<>();
    private final CallbackOwner<T> owner;
    private final ICOCBlockRef ref;
    private final Level level;
    private final String name;
    private final List<ComputerCallback<? super T>> callbacks;
    private final String[] methodNames;
    private final ICOCReferences<ComputerControlState> references = new ICOCReferences<>(ComputerControlState::addReference, ComputerControlState::removeReference);
    private T object;

    public IEEnvironment(CallbackOwner<T> owner, T object, BlockEntity blockEntity, Level level, String name) {
        this.owner = owner;
        this.object = object;
        this.ref = new ICOCBlockRef(blockEntity);
        this.level = level;
        this.name = name;
        this.callbacks = callbacks(owner);
        this.methodNames = callbacks.stream().map(ComputerCallback::getName).toArray(String[]::new);
        setNode(Network.newNode(this, Visibility.Network).create());
    }

    @SuppressWarnings("unchecked") private static <T> List<ComputerCallback<? super T>> callbacks(CallbackOwner<T> owner) {
        List<?> cached = CACHE.get(owner);
        if (cached == null) {
            try { cached = ComputerCallback.getInClass(owner, new IEOCConverter(), Function.identity()); }
            catch (IllegalAccessException e) { throw new IllegalStateException(e); }
            CACHE.put(owner, cached);
        }
        return (List<ComputerCallback<? super T>>) cached;
    }

    @Override public String[] methods() { return methodNames; }

    @Override public boolean canUpdate() { return true; }

    @Override public void update() {
        if (references.isEmpty()) { return; }
        ref.refresh();
        rebind();
        reconcile();
    }

    private void rebind() { object = owner.getCallbackType().cast(ref.current()); }

    @Override public Object[] invoke(String method, Context context, Arguments args) throws Exception {
        ref.requireServerThread();
        ref.resolve();
        rebind();
        reconcile();
        for (ComputerCallback<? super T> callback : callbacks) {
            if (!callback.getName().equals(method)) { continue; }
            Object[] result = call(callback, ICOCArguments.normalize(args));
            if (callback.isAsync() && result.length > 0 && result[0] instanceof EventWaiterResult waiter) {
                waiter.startAsync(() -> context.signal(waiter.name()));
                return new Object[0];
            }
            return result;
        }
        throw new IllegalArgumentException("No such method: " + method);
    }

    private Object[] call(ComputerCallback<? super T> callback, Object[] args) {
        try { return callback.invoke(args, new CallbackEnvironment<>(owner.preprocess(object), level)); }
        catch (RuntimeException e) { throw e; }
        catch (Throwable t) { throw new IllegalStateException("Unexpected error, check server log!", t); }
    }

    @Override public void onConnect(Node node) {
        super.onConnect(node);
        if (node.host() instanceof Context context && context.isRunning()) { attach(node); }
    }

    @Override public void onDisconnect(Node node) {
        super.onDisconnect(node);
        if (node.host() instanceof Context) { detach(node); }
        else if (node == node()) {
            references.detachAll();
            reconcileLater();
        }
    }

    @Override public void onMessage(Message message) {
        super.onMessage(message);
        if (!(message.source().host() instanceof Context)) { return; }
        if ("computer.started".equals(message.name())) { attach(message.source()); }
        else if ("computer.stopped".equals(message.name())) { detach(message.source()); }
    }

    private void attach(Node node) { if (references.attach(node.address())) { reconcileLater(); } }

    private void detach(Node node) { if (references.detach(node.address())) { reconcileLater(); } }

    private void reconcileLater() {
        if (object instanceof IMultiblockBE<?>) { ApiUtils.addFutureServerTask(level, this::reconcile); }
        else { reconcile(); }
    }

    private void reconcile() { references.reconcile(controlStates()); }

    private List<ComputerControlState> controlStates() {
        List<ComputerControlState> states = new ArrayList<>();
        if (object instanceof ComputerControllable controllable) { controllable.getAllComputerControlStates().forEach(states::add); }
        if (!(object instanceof IMultiblockBE<?> multiblockBE)) { return states; }
        IMultiblockBEHelper<?> helper = multiblockBE.getHelper();
        Object state = helper.getState();
        if (state == null) { return states; }
        for (ExtraComponent<?, ?> component : helper.getMultiblock().extraComponents()) {
            if (component.makeWrapper() instanceof RedstoneControl<?> control) { states.add(controlState(control, state)); }
        }
        return states;
    }

    @SuppressWarnings("unchecked") private static <S> ComputerControlState controlState(RedstoneControl<S> control, Object state) { return control.wrapState((S) state).getComputerControlState(); }

    @Override public String preferredName() { return name; }

    @Override public int priority() { return 1000; }
}
