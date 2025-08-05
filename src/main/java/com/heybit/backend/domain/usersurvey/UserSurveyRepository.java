package com.heybit.backend.domain.usersurvey;

import com.heybit.backend.domain.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSurveyRepository extends JpaRepository<UserSurvey, Long> {

  Optional<UserSurvey> findByUser(User user);
}
