package com.ltev.connected.repository;

import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * Interface exposes only db's read-only operations
 */
@NoRepositoryBean
public interface BaseViewRepository<T, K>{

    Optional<T> findById(K k);

    long count();
}