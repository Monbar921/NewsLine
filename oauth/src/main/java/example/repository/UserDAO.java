package example.repository;

import example.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public interface UserDAO extends CrudRepository<User, UUID> {
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    @Query(value = "SELECT COUNT(*) FROM auth_users au", nativeQuery = true)
    long countAll();
}
