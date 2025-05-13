package org.clevercastle.authforge.examples.springboot.springbootexample;

import org.clevercastle.authforge.model.User;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaUserModelRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserModelRepositoryAdapter extends RdsJpaUserModelRepository, JpaRepository<User, String> {
}