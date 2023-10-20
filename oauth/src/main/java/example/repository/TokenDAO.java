package example.repository;

import example.models.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public interface TokenDAO extends CrudRepository<Token, UUID> {
    Token findTokenById(UUID id);
    Token findTokenByValue(String value);
}
