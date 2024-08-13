package ru.netology.cloudstorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.entities.User;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    UserDetails findByLogin(String login);


}
