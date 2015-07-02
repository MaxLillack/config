package com.typesafe.config.impl;

import java.util.HashMap;
import java.util.Map;

import org.pcollections.HashPMap;
import org.pcollections.HashTreePMap;

/**
 * This exists because we have to memoize resolved substitutions as we go
 * through the config tree; otherwise we could end up creating multiple copies
 * of values or whole trees of values as we follow chains of substitutions.
 */
final class ResolveMemos {
    // note that we can resolve things to undefined (represented as Java null,
    // rather than ConfigNull) so this map can have null values.
    final private Map<MemoKey, AbstractConfigValue> memos;

    private ResolveMemos(Map<MemoKey, AbstractConfigValue> memos) {
        this.memos = memos;
    }

    ResolveMemos() {
        this(new HashMap<MemoKey, AbstractConfigValue>());
    }

    AbstractConfigValue get(MemoKey key) {
        return memos.get(key);
    }

    ResolveMemos put(MemoKey key, AbstractConfigValue value) {
        // completely inefficient, but so far nobody cares about resolve()
        // performance, we can clean it up someday...
        HashPMap<MemoKey, AbstractConfigValue> copy = HashTreePMap.<MemoKey, AbstractConfigValue>from(memos);
        copy = copy.plus(key, value);
        return new ResolveMemos(copy);
    }
}
