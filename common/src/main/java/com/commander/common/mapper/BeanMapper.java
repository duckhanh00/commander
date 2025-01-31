package com.commander.common.mapper;

import org.mapstruct.MappingTarget;

import java.util.List;

public interface BeanMapper<T, R> {
    R map(T source);

    void mapTo(T source, @MappingTarget R target);

    default List<R> map(List<T> sources) {
        if (sources == null) {
            throw new IllegalArgumentException("sources must not be null");
        }
        return sources.stream().map(this::map).toList();
    }

    default void mapTo(List<T> sources, @MappingTarget List<R> targets) {
        if (sources == null || targets == null) {
            throw new IllegalArgumentException("sources and targets must not be null");
        }
        if (sources.size() != targets.size()) {
            throw new IllegalArgumentException("sources and targets must be same size");
        }
        for (int i = 0; i < sources.size(); ++i) {
            mapTo(sources.get(i), targets.get(i));
        }
    }
}
