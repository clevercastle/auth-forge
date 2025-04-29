package org.clevercastle.authforge.examples.springboot.springbootexample;

import org.clevercastle.authforge.UserLoginItem;
import org.clevercastle.authforge.repository.rdsjpa.UserLoginItemJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginItemRepositoryAdapter extends UserLoginItemJpaRepository, JpaRepository<UserLoginItem, String> {
}