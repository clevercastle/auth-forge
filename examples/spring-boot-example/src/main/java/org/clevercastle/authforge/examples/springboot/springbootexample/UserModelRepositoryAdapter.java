package org.clevercastle.authforge.examples.springboot.springbootexample;

import org.clevercastle.authforge.User;
import org.clevercastle.authforge.repository.rdsjpa.IUserModelRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserModelRepositoryAdapter extends IUserModelRepository, JpaRepository<User, String> {
}