package com.immersiveconvergence.core.integration.opencomputers;

import com.immersiveconvergence.api.compat.ICComputerTable;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.List;

public final class ICTableEnvironment<T> extends AbstractManagedEnvironment implements ManagedPeripheral, NamedBlock {
    private final ICOCBlockRef ref;
    private final ICComputerTable<T> table;
    private final ICOCReferences<T> references;

    public ICTableEnvironment(BlockEntity blockEntity, ICComputerTable<T> table) {
        this.ref = new ICOCBlockRef(blockEntity);
        this.table = table;
        this.references = new ICOCReferences<>(table::attach, table::detach);
        setNode(Network.newNode(this, Visibility.Network).create());
    }

    @Override public String[] methods() { return table.methodNames(); }

    @Override public boolean canUpdate() { return true; }

    @Override public void update() {
        if (references.isEmpty()) { return; }
        ref.refresh();
        reconcile();
    }

    @Override public Object[] invoke(String name, Context context, Arguments args) throws Exception {
        ref.requireServerThread();
        T object = table.locate(ref.resolve());
        if (object == null) { throw new IllegalStateException("Machine is not formed"); }
        references.reconcile(List.of(object));
        Object[] result = table.invoke(object, name, ICOCArguments.normalize(args));
        return result == null ? new Object[0] : result;
    }

    private void reconcile() {
        T object = table.locate(ref.current());
        references.reconcile(object == null ? List.of() : List.of(object));
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
            reconcile();
        }
    }

    @Override public void onMessage(Message message) {
        super.onMessage(message);
        if (!(message.source().host() instanceof Context)) { return; }
        if ("computer.started".equals(message.name())) { attach(message.source()); }
        else if ("computer.stopped".equals(message.name())) { detach(message.source()); }
    }

    private void attach(Node node) { if (references.attach(node.address())) { reconcile(); } }

    private void detach(Node node) { if (references.detach(node.address())) { reconcile(); } }

    @Override public String preferredName() { return table.type; }

    @Override public int priority() { return 1000; }
}
