package com.immersiveconvergence.core.compat.opencomputers;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class ICOCReferences<K> {
    private final Set<String> computers = new HashSet<>();
    private final Map<K, Integer> held = new IdentityHashMap<>();
    private final Consumer<K> add;
    private final Consumer<K> remove;

    public ICOCReferences(Consumer<K> add, Consumer<K> remove) {
        this.add = add;
        this.remove = remove;
    }

    public boolean isEmpty() { return computers.isEmpty(); }

    public boolean attach(String address) { return computers.add(address); }

    public boolean detach(String address) { return computers.remove(address); }

    public void detachAll() { computers.clear(); }

    public void reconcile(Collection<? extends K> current) {
        if (computers.isEmpty() && held.isEmpty()) { return; }
        int target = computers.size();
        for (K key : current) { held.putIfAbsent(key, 0); }
        held.replaceAll((key, have) -> {
            for (; have < target; have++) { add.accept(key); }
            for (; have > target; have--) { remove.accept(key); }
            return have;
        });
        if (target == 0) { held.clear(); }
    }
}
