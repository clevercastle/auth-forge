package org.clevercastle.helper.login.examples.springboot.springbootexample;

import org.clevercastle.helper.login.UserLoginItem;
import org.clevercastle.helper.login.repository.rdsjpa.UserLoginItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginItemRepositoryAdapter extends UserLoginItemRepository, JpaRepository<UserLoginItem, String> {
}