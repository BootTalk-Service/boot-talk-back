package com.icandoit.boottalk.user.domain.repository;

import com.icandoit.boottalk.user.domain.entity.User;
import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, BigInteger> {

}
