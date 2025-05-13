package org.clevercastle.authforge.examples.springboot.springbootexample;

import org.clevercastle.authforge.model.UserLoginItem;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaUserLoginItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginItemRepositoryAdapter extends RdsJpaUserLoginItemRepository, JpaRepository<UserLoginItem, String> {
}