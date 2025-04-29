package org.clevercastle.helper.login.examples.springboot.springbootexample;

import org.clevercastle.helper.login.User;
import org.clevercastle.helper.login.repository.rdsjpa.IUserModelRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserModelRepositoryAdapter extends IUserModelRepository, JpaRepository<User, String> {
}