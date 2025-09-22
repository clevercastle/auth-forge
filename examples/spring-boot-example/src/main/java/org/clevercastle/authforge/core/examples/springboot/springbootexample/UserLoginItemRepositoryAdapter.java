package org.clevercastle.authforge.core.examples.springboot.springbootexample;

import org.clevercastle.authforge.core.model.UserLoginItem;
import org.clevercastle.authforge.core.repository.rdsjpa.RdsJpaUserLoginItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginItemRepositoryAdapter extends RdsJpaUserLoginItemRepository, JpaRepository<UserLoginItem, String> {
}