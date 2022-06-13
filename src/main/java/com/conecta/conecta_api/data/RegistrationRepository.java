package com.conecta.conecta_api.data;

import com.conecta.conecta_api.domain.entities.CourseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<CourseRegistration, Long> {

}