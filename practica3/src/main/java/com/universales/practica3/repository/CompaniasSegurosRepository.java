package com.universales.practica3.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.universales.practica3.entity.CompaniasSeguros;

@Repository("companiassegurosRepository")
public interface CompaniasSegurosRepository extends JpaRepository<CompaniasSeguros, Serializable> {

}
